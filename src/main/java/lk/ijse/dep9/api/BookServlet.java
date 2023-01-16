package lk.ijse.dep9.api;

import jakarta.annotation.Resource;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import lk.ijse.dep9.api.util.NewHttpServlet;
import lk.ijse.dep9.dto.BookDTO;
import lk.ijse.dep9.exception.ResponseStatusException;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "book-servlet", value = "/books/*")
public class BookServlet extends NewHttpServlet {

    @Resource(lookup = "java:/comp/env/jdbc/lms_db")
    private DataSource pool;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getPathInfo() == null || request.getPathInfo().equals("/")) {
            String query = request.getParameter("q");
            String size = request.getParameter("size");
            String page = request.getParameter("page");
            if (query != null && size != null && page != null) {
                if (!size.matches("\\d+") || !page.matches("\\d++")) {
                    throw new ResponseStatusException(400, "Invalid page or size");
                }
                else {
                    searchBooksByPage(query, Integer.parseInt(size), Integer.parseInt(page), response);
                }
            }
            else if (query != null && size == null && page == null) {
                searchBooks(query, response);
            }
            else if (query == null && size != null && page != null) {
                if (!size.matches("\\d+") || !page.matches("\\d+")) {
                    throw new ResponseStatusException(400, "Invalid page or size");
                }
                else {
                    loadBooksByPage(Integer.parseInt(size), Integer.parseInt(page), response);
                }
            }
            else {
                loadAllBooks(response);
            }
        }
        else {
            String pathInfo = request.getPathInfo();
            Matcher matcher = Pattern.compile("^/(\\d{3}-\\d-\\d{2}-\\d{6}-\\d)/?$").matcher(pathInfo);
            if (matcher.matches()) {
                String isbn = matcher.group(1);
                getBookDetails(isbn, response);
            }
            else {
                throw new ResponseStatusException(501);
            }
        }

    }

    private void loadAllBooks(HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM Book");
            ArrayList<BookDTO> allBooks = new ArrayList<>();

            while (rst.next()) {
                String isbn = rst.getString("isbn");
                String title = rst.getString("title");
                String author = rst.getString("author");
                int copies = rst.getInt("copies");
                BookDTO dto = new BookDTO(isbn, title, author, copies);
                allBooks.add(dto);
            }

            response.setContentType("application/json");
            JsonbBuilder.create().toJson(allBooks, response.getWriter());
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void loadBooksByPage(int size, int page, HttpServletResponse response) throws IOException {
        response.getWriter().println("load books by page");
    }

    private void searchBooks(String query, HttpServletResponse response) throws IOException {
        response.getWriter().println("search books");
    }

    private void searchBooksByPage(String query, int size, int page, HttpServletResponse response) throws IOException {
        response.getWriter().println("search books by page");
    }

    private void getBookDetails(String isbn, HttpServletResponse response) throws IOException {
        response.getWriter().println("get books details");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
