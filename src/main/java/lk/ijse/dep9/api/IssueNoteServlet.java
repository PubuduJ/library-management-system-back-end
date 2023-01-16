package lk.ijse.dep9.api;

import jakarta.annotation.Resource;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import lk.ijse.dep9.api.util.NewHttpServlet;
import lk.ijse.dep9.dto.IssueNoteDTO;
import lk.ijse.dep9.exception.ResponseStatusException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@WebServlet(name = "issue-note-servlet", value = "/issue-notes/*")
public class IssueNoteServlet extends NewHttpServlet {

    @Resource(lookup = "java:/comp/env/jdbc/lms_db")
    private DataSource pool;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getPathInfo() == null || request.getPathInfo().equals("/")) {
            if (request.getContentType() == null || !request.getContentType().startsWith("application/json")) {
                throw new ResponseStatusException(400, "Invalid Content Type");
            }
            else {
                try {
                    IssueNoteDTO issueNote = JsonbBuilder.create().fromJson(request.getReader(), IssueNoteDTO.class);
                    createIssueNote(issueNote, response);
                }
                catch (JsonbException e) {
                    throw new ResponseStatusException(400, "Issue Note JSON format is incorrect");
                }
            }
        }
        else {
            throw new ResponseStatusException(501);
        }
    }

    private void createIssueNote(IssueNoteDTO issueNote, HttpServletResponse response) {
        if (issueNote.getMemberId() == null || !issueNote.getMemberId().matches("^[A-Fa-f\\d]{8}(-[A-Fa-f\\d]{4}){3}-[A-Fa-f\\d]{12}$")) {
            throw new ResponseStatusException(400, "Member id is empty or invalid");
        }
        else if (issueNote.getBooks().isEmpty()) {
            throw new ResponseStatusException(400, "Can't place an issue note without books");
        }
        else if (issueNote.getBooks().size() > 3) {
            throw new ResponseStatusException(400, "Cannot issue more than 3 books");
        }
        Set<String> checkDuplicates = new HashSet<>();
        for (String isbn : issueNote.getBooks()) {
            if (isbn == null || !isbn.matches("^\\d{3}-\\d-\\d{2}-\\d{6}-\\d$")) throw new JsonbException("Invalid isbn");
            checkDuplicates.add(isbn);
        }
        if (checkDuplicates.size() != issueNote.getBooks().size()) {
            throw new ResponseStatusException(400, "Duplicate isbn are found");
        }

        try (Connection connection = pool.getConnection()) {
            PreparedStatement stmMemberExist = connection.prepareStatement("SELECT id FROM Member WHERE id = ?");
            stmMemberExist.setString(1, issueNote.getMemberId());
            ResultSet rstMemberExist = stmMemberExist.executeQuery();
            if (!rstMemberExist.next()) {
                throw new ResponseStatusException(400, "Member doesn't exist");
            }

            PreparedStatement stmOne = connection.prepareStatement("SELECT B.isbn, B.title, B.copies, ((B.copies - COUNT(II.isbn)) > 0) AS availability\n" +
                    "FROM IssueItem II\n" +
                    "INNER JOIN `Return` R ON NOT (II.issue_id = R.issue_id AND II.isbn = R.isbn)\n" +
                    "RIGHT JOIN Book B ON II.isbn = B.isbn\n" +
                    "WHERE B.isbn = ? GROUP BY B.isbn");

            PreparedStatement stmTwo = connection.prepareStatement("SELECT *, B.title\n" +
                    "FROM IssueItem II\n" +
                    "INNER JOIN `Return` R ON NOT (II.issue_id = R.issue_id and II.isbn = R.isbn)\n" +
                    "INNER JOIN Book B ON II.isbn = B.isbn\n" +
                    "INNER JOIN IssueNote `IN` ON II.issue_id = `IN`.id\n" +
                    "WHERE `IN`.member_id = ? AND B.isbn = ?");

            stmTwo.setString(1, issueNote.getMemberId());

            for (String isbn : issueNote.getBooks()) {
                stmOne.setString(1, isbn);
                stmTwo.setString(2, isbn);

                ResultSet rstOne = stmOne.executeQuery();
                ResultSet rstTwo = stmTwo.executeQuery();

                if (!rstOne.next()) throw new ResponseStatusException(400, "Book doesn't exist");
                boolean availability = rstOne.getBoolean("availability");
                if (!availability) throw new ResponseStatusException(400, "ISBN: " + isbn + " book is not available at the moment");
                if (rstTwo.next()) throw new ResponseStatusException(400, "Book has been already issued to a member");
            }

            PreparedStatement stmAvailable = connection.prepareStatement("SELECT M.name, 3 - COUNT(R.issue_id) AS available\n" +
                    "FROM IssueNote `IN`\n" +
                    "INNER JOIN IssueItem II ON `IN`.id = II.issue_id\n" +
                    "INNER JOIN `Return` R ON NOT (II.issue_id = R.issue_id and II.isbn = R.isbn)\n" +
                    "RIGHT JOIN Member M on `IN`.member_id = M.id\n" +
                    "WHERE M.id = ? GROUP BY M.id");
            stmAvailable.setString(1,issueNote.getMemberId());
            ResultSet rstAvailable = stmAvailable.executeQuery();
            rstAvailable.next();
            int available = rstAvailable.getInt("available");
            if (issueNote.getBooks().size() > available) throw new ResponseStatusException(400, "Member can borrow only " + available + " books");

            /* Begin transactions */
            try {
                connection.setAutoCommit(false);
                PreparedStatement stmIssueNote = connection.prepareStatement("INSERT INTO IssueNote (date, member_id) VALUES (?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                stmIssueNote.setDate(1, Date.valueOf(LocalDate.now()));
                stmIssueNote.setString(2, issueNote.getMemberId());
                if (stmIssueNote.executeUpdate() != 1) {
                    throw new SQLException("Fail to insert the issue note");
                }
                ResultSet generatedKeys = stmIssueNote.getGeneratedKeys();
                generatedKeys.next();
                int issueNoteId = generatedKeys.getInt(1);

                PreparedStatement stmIssueItem = connection.prepareStatement("INSERT INTO IssueItem (issue_id, isbn) VALUES (?, ?)");
                stmIssueItem.setInt(1, issueNoteId);
                for (String isbn : issueNote.getBooks()) {
                    stmIssueItem.setString(2, isbn);
                    if (stmIssueItem.executeUpdate() != 1) {
                        throw new SQLException("Fail to insert an issue item");
                    }
                }

                connection.commit();

                issueNote.setId(issueNoteId);
                issueNote.setDate(LocalDate.now());
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setContentType("application/json");
                JsonbBuilder.create().toJson(issueNote, response.getWriter());
            }
            catch (Throwable t) {
                connection.rollback();
                throw new RuntimeException(t);
            }
            finally {
                connection.setAutoCommit(true);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
