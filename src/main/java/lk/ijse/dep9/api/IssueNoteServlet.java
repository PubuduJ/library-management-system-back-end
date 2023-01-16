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
    }
}
