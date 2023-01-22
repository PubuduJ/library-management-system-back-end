package lk.ijse.dep9.api;

import jakarta.annotation.Resource;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import lk.ijse.dep9.api.exception.ResponseStatusException;
import lk.ijse.dep9.api.util.NewHttpServlet;
import lk.ijse.dep9.dto.BookDTO;
import lk.ijse.dep9.service.ServiceFactory;
import lk.ijse.dep9.service.ServiceTypes;
import lk.ijse.dep9.service.custom.BookService;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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
            BookService bookService = ServiceFactory.getInstance().getService(connection, ServiceTypes.BOOK);
            List<BookDTO> allBooks = bookService.getAllBooks();
            response.setContentType("application/json");
            JsonbBuilder.create().toJson(allBooks, response.getWriter());
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void loadBooksByPage(int size, int page, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            BookService bookService = ServiceFactory.getInstance().getService(connection, ServiceTypes.BOOK);
            List<BookDTO> paginatedBooks = bookService.getBooksByPage(size, page);
            response.setContentType("application/json");
            JsonbBuilder.create().toJson(paginatedBooks, response.getWriter());
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void searchBooks(String query, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            BookService bookService = ServiceFactory.getInstance().getService(connection, ServiceTypes.BOOK);
            List<BookDTO> searchedBooks = bookService.findBooks(query);
            response.setContentType("application/json");
            JsonbBuilder.create().toJson(searchedBooks, response.getWriter());
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void searchBooksByPage(String query, int size, int page, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            BookService bookService = ServiceFactory.getInstance().getService(connection, ServiceTypes.BOOK);
            List<BookDTO> searchPaginatedBooks = bookService.findBooksByPage(query, size, page);
            response.setContentType("application/json");
            JsonbBuilder.create().toJson(searchPaginatedBooks, response.getWriter());
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void getBookDetails(String isbn, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            BookService bookService = ServiceFactory.getInstance().getService(connection, ServiceTypes.BOOK);
            BookDTO book = bookService.getBookInfo(isbn);
            response.setContentType("application/json");
            JsonbBuilder.create().toJson(book, response.getWriter());
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getPathInfo() == null || request.getPathInfo().equals("/")) {
            if (request.getContentType() == null || !request.getContentType().startsWith("application/json")) {
                throw new ResponseStatusException(400, "Invalid Content Type");
            }
            else {
                try {
                    BookDTO bookDTO = JsonbBuilder.create().fromJson(request.getReader(), BookDTO.class);
                    saveNewBook(bookDTO, response);
                }
                catch (JsonbException e) {
                    throw new ResponseStatusException(400, "Member JSON format is incorrect");
                }
            }
        }
        else {
            throw new ResponseStatusException(501);
        }
    }

    private void saveNewBook(BookDTO bookDTO, HttpServletResponse response) throws IOException {
        if (bookDTO.getIsbn() == null || !bookDTO.getIsbn().matches("^\\d{3}-\\d-\\d{2}-\\d{6}-\\d$")) {
            throw new ResponseStatusException(400, "ISBN is empty or invalid");
        }
        else if (bookDTO.getTitle() == null || !bookDTO.getTitle().matches("^[A-Za-z][A-Za-z. ]+$")) {
            throw new ResponseStatusException(400, "Title is empty or invalid");
        }
        else if (bookDTO.getAuthor() == null || !bookDTO.getAuthor().matches("^[A-Za-z][A-Za-z. ]+$")) {
            throw new ResponseStatusException(400, "Author is empty or invalid");
        }
        else if (bookDTO.getCopies() == null || bookDTO.getCopies() < 1) {
            throw new ResponseStatusException(400, "Copies are empty or invalid");
        }

        try (Connection connection = pool.getConnection()) {
            BookService bookService = ServiceFactory.getInstance().getService(connection, ServiceTypes.BOOK);
            bookService.addNewBook(bookDTO);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType("application/json");
            JsonbBuilder.create().toJson(bookDTO, response.getWriter());

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getPathInfo() == null || request.getPathInfo().equals("/")) {
            throw new ResponseStatusException(501);
        }
        String pathInfo = request.getPathInfo();
        Matcher matcher = Pattern.compile("^/(\\d{3}-\\d-\\d{2}-\\d{6}-\\d)/?$").matcher(pathInfo);
        if (matcher.matches()) {
            if (request.getContentType() == null || !request.getContentType().startsWith("application/json")) {
                throw new ResponseStatusException(400, "Invalid Content Type");
            }
            try {
                String isbn = matcher.group(1);
                BookDTO bookDTO = JsonbBuilder.create().fromJson(request.getReader(), BookDTO.class);
                if (!isbn.equals(bookDTO.getIsbn())) {
                    throw new ResponseStatusException(400, "JSON object Book isbn is not match with the url pattern book isbn");
                }
                updateBook(bookDTO, response);
            }
            catch (JsonbException e) {
                throw new ResponseStatusException(400, "Member JSON format is incorrect");
            }
        }
        else {
            throw new ResponseStatusException(400, "Invalid book isbn");
        }
    }

    private void updateBook(BookDTO bookDTO, HttpServletResponse response) throws IOException {
        if (bookDTO.getTitle() == null || !bookDTO.getTitle().matches("^[A-Za-z][A-Za-z. ]+$")) {
            throw new ResponseStatusException(400, "Title is empty or invalid");
        }
        else if (bookDTO.getAuthor() == null || !bookDTO.getAuthor().matches("^[A-Za-z][A-Za-z. ]+$")) {
            throw new ResponseStatusException(400, "Author is empty or invalid");
        }
        else if (bookDTO.getCopies() == null || bookDTO.getCopies() < 1) {
            throw new ResponseStatusException(400, "Copies are empty or invalid");
        }
        try (Connection connection = pool.getConnection()) {
            BookService bookService = ServiceFactory.getInstance().getService(connection, ServiceTypes.BOOK);
            bookService.updateBookDetails(bookDTO);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType("application/json");
            JsonbBuilder.create().toJson(bookDTO, response.getWriter());
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
