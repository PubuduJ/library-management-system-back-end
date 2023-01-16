package lk.ijse.dep9.api;

import jakarta.annotation.Resource;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import lk.ijse.dep9.api.util.NewHttpServlet;
import lk.ijse.dep9.dto.BookDTO;
import lk.ijse.dep9.exception.ResponseStatusException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
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
        try (Connection connection = pool.getConnection()) {
            Statement stmCount = connection.createStatement();
            ResultSet rstCount = stmCount.executeQuery("SELECT COUNT(isbn) FROM Book");
            rstCount.next();
            int totalBooks = rstCount.getInt(1);
            response.addIntHeader("X-Total-Count", totalBooks);

            PreparedStatement stmData = connection.prepareStatement("SELECT * FROM Book LIMIT ? OFFSET ?");
            stmData.setInt(1, size);
            stmData.setInt(2, (page - 1) * size);
            ResultSet rstData = stmData.executeQuery();

            ArrayList<BookDTO> paginatedBooks = new ArrayList<>();

            while (rstData.next()) {
                String isbn = rstData.getString("isbn");
                String title = rstData.getString("title");
                String author = rstData.getString("author");
                int copies = rstData.getInt("copies");
                BookDTO dto = new BookDTO(isbn, title, author, copies);
                paginatedBooks.add(dto);
            }

            response.setContentType("application/json");
            JsonbBuilder.create().toJson(paginatedBooks, response.getWriter());
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void searchBooks(String query, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM Book WHERE isbn LIKE ? OR title LIKE ? OR author LIKE ? OR copies LIKE ?");
            query = "%" + query + "%";
            stm.setString(1, query);
            stm.setString(2, query);
            stm.setString(3, query);
            stm.setString(4, query);
            ResultSet rst = stm.executeQuery();

            ArrayList<BookDTO> searchedBooks = new ArrayList<>();
            while (rst.next()) {
                String isbn = rst.getString("isbn");
                String title = rst.getString("title");
                String author = rst.getString("author");
                int copies = rst.getInt("copies");
                BookDTO dto = new BookDTO(isbn, title, author, copies);
                searchedBooks.add(dto);
            }

            response.setContentType("application/json");
            JsonbBuilder.create().toJson(searchedBooks, response.getWriter());
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void searchBooksByPage(String query, int size, int page, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            query = "%" + query + "%";
            PreparedStatement stmCount = connection.prepareStatement("SELECT COUNT(isbn) FROM Book WHERE isbn LIKE ? OR title LIKE ? OR author LIKE ? OR copies LIKE ?");
            stmCount.setString(1, query);
            stmCount.setString(2, query);
            stmCount.setString(3, query);
            stmCount.setString(4, query);
            ResultSet rstCount = stmCount.executeQuery();
            rstCount.next();
            int searchedBookCount = rstCount.getInt(1);
            response.addIntHeader("X-Total-Count", searchedBookCount);

            PreparedStatement stmData = connection.prepareStatement("SELECT * FROM Book WHERE isbn LIKE ? OR title LIKE ? OR author LIKE ? OR copies LIKE ? LIMIT ? OFFSET ?");
            stmData.setString(1,query);
            stmData.setString(2,query);
            stmData.setString(3,query);
            stmData.setString(4,query);
            stmData.setInt(5,size);
            stmData.setInt(6,(page - 1) * size);
            ResultSet rstData = stmData.executeQuery();

            ArrayList<BookDTO> searchPaginatedBooks = new ArrayList<>();
            while (rstData.next()) {
                String isbn = rstData.getString("isbn");
                String title = rstData.getString("title");
                String author = rstData.getString("author");
                int copies = rstData.getInt("copies");
                BookDTO dto = new BookDTO(isbn, title, author, copies);
                searchPaginatedBooks.add(dto);
            }

            response.setContentType("application/json");
            JsonbBuilder.create().toJson(searchPaginatedBooks, response.getWriter());
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void getBookDetails(String isbnNumber, HttpServletResponse response) throws IOException {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM Book WHERE isbn=?");
            stm.setString(1, isbnNumber);
            ResultSet rst = stm.executeQuery();

            if (rst.next()) {
                String isbn = rst.getString("isbn");
                String title = rst.getString("title");
                String author = rst.getString("author");
                int copies = rst.getInt("copies");
                BookDTO book = new BookDTO(isbn, title, author, copies);

                response.setContentType("application/json");
                JsonbBuilder.create().toJson(book, response.getWriter());
            }
            else {
                throw new ResponseStatusException(404, "Book ISBN doesn't exist in the database");
            }
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
            PreparedStatement stm = connection.prepareStatement("INSERT INTO Book (isbn, title, author, copies) VALUES (?, ?, ?, ?)");
            stm.setString(1, bookDTO.getIsbn());
            stm.setString(2, bookDTO.getTitle());
            stm.setString(3, bookDTO.getAuthor());
            stm.setInt(4, bookDTO.getCopies());

            int affectedRows = stm.executeUpdate();
            if (affectedRows == 1) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setContentType("application/json");
                JsonbBuilder.create().toJson(bookDTO, response.getWriter());
            }
            else {
                throw new ResponseStatusException(500);
            }
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
            PreparedStatement stm = connection.prepareStatement("UPDATE Book SET title=?, author=?, copies=? WHERE isbn=?");
            stm.setString(1, bookDTO.getTitle());
            stm.setString(2, bookDTO.getAuthor());
            stm.setInt(3, bookDTO.getCopies());
            stm.setString(4, bookDTO.getIsbn());

            int affectedRows = stm.executeUpdate();
            if (affectedRows == 1) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setContentType("application/json");
                JsonbBuilder.create().toJson(bookDTO, response.getWriter());
            }
            else {
                throw new ResponseStatusException(404, "Book does not exist");
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
