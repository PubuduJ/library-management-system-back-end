package lk.ijse.dep9.api;

import jakarta.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import lk.ijse.dep9.api.util.NewHttpServlet;

import javax.sql.DataSource;
import java.io.IOException;

@WebServlet(name = "issue-note-servlet", value = "/issue-notes/*")
public class IssueNoteServlet extends NewHttpServlet {

    @Resource(lookup = "java:/comp/env/jdbc/lms_db")
    private DataSource pool;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
