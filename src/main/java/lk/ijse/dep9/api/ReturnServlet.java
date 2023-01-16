package lk.ijse.dep9.api;

import jakarta.annotation.Resource;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import lk.ijse.dep9.api.util.NewHttpServlet;
import lk.ijse.dep9.dto.ReturnDTO;
import lk.ijse.dep9.dto.ReturnItemDTO;
import lk.ijse.dep9.exception.ResponseStatusException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@WebServlet(name = "return-servlet", value = "/returns/*")
public class ReturnServlet extends NewHttpServlet {

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
                    ReturnDTO returnDTO = JsonbBuilder.create().fromJson(request.getReader(), ReturnDTO.class);
                    addReturnItems(returnDTO, response);
                }
                catch (JsonbException e) {
                    throw new ResponseStatusException(400, "Return Note JSON format is incorrect");
                }
            }
        }
        else {
            throw new ResponseStatusException(501);
        }
    }

    private void addReturnItems(ReturnDTO returnDTO, HttpServletResponse response) throws IOException {
        if (returnDTO.getMemberId() == null || !returnDTO.getMemberId().matches("^[A-Fa-f\\d]{8}(-[A-Fa-f\\d]{4}){3}-[A-Fa-f\\d]{12}$")) {
            throw new ResponseStatusException(400, "Member ID is invalid or empty");
        }
        else if (returnDTO.getReturnItems().isEmpty()) {
            throw new ResponseStatusException(400, "No return items found");
        }
        Set<Integer> checkDuplicates = new HashSet<>();
        for (ReturnItemDTO returnItem : returnDTO.getReturnItems()) {
            if (returnItem == null) {
                throw new ResponseStatusException(400, "Null items have been found in the list");
            }
            else if (returnItem.getIssueNoteId() == null || returnItem.getIsbn() == null || !returnItem.getIsbn().matches("^\\d{3}-\\d-\\d{2}-\\d{6}-\\d$")) {
                throw new ResponseStatusException(400, "Some items are invalid");
            }
            checkDuplicates.add(returnItem.getIssueNoteId());
        }
        if (checkDuplicates.size() != returnDTO.getReturnItems().size()) {
            throw new ResponseStatusException(400, "Duplicate issue note Ids are found");
        }

        try (Connection connection = pool.getConnection()) {
            PreparedStatement stmOne = connection.prepareStatement("SELECT *\n" +
                    "FROM IssueItem II\n" +
                    "INNER JOIN IssueNote `IN` on II.issue_id = `IN`.id\n" +
                    "WHERE `IN`.member_id = ? AND II.issue_id = ? AND II.isbn = ?");
            stmOne.setString(1, returnDTO.getMemberId());

            PreparedStatement stmTwo = connection.prepareStatement("SELECT * FROM `Return` WHERE isbn = ? AND issue_id = ?");
            PreparedStatement stmThree = connection.prepareStatement("INSERT INTO `Return` (date, issue_id, isbn) VALUES (?, ?, ?)");

            try {
                connection.setAutoCommit(false);
                for (ReturnItemDTO returnItem : returnDTO.getReturnItems()) {
                    stmOne.setInt(2, returnItem.getIssueNoteId());
                    stmOne.setString(3, returnItem.getIsbn());

                    stmTwo.setInt(1, returnItem.getIssueNoteId());
                    stmTwo.setString(2, returnItem.getIsbn());

                    if (!stmOne.executeQuery().next()) {
                        throw new ResponseStatusException(400, String.format("Either member: %s, issue note id: %s, isbn: %s don't exist or this return item is not belong to this member",
                                returnDTO.getMemberId(),
                                returnItem.getIssueNoteId(),
                                returnItem.getIsbn()));
                    }
                    if (stmTwo.executeQuery().next()) {
                        throw new ResponseStatusException(400, "This " + returnItem.getIsbn() +  " have been already returned");
                    }

                    stmThree.setDate(1, Date.valueOf(LocalDate.now()));
                    stmThree.setInt(2, returnItem.getIssueNoteId());
                    stmThree.setString(3, returnItem.getIsbn());

                    int affectedRows = stmThree.executeUpdate();
                    if (affectedRows != 1) {
                        throw new SQLException("Fail to insert a return item");
                    }
                }

                connection.commit();
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setContentType("application/json");
                JsonbBuilder.create().toJson(returnDTO, response.getWriter());
            }
            catch (Throwable t) {
                connection.rollback();
                if (t instanceof ResponseStatusException) throw t;
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
