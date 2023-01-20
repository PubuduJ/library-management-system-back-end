package lk.ijse.dep9.dao.custom.impl;

import lk.ijse.dep9.dao.exception.ConstraintViolationException;
import lk.ijse.dep9.entity.Book;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BookDAOImplTest {

    private BookDAOImpl bookDAOImpl;

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException, URISyntaxException, IOException {
        connection = DriverManager.getConnection("jdbc:h2:mem:");
        String dbScript = Files.readString(Paths.get(this.getClass().getResource("/lms_db.sql").toURI()));
        Statement stm = connection.createStatement();
        stm.execute(dbScript);
        this.bookDAOImpl = new BookDAOImpl(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void count() {
        long actualCount = bookDAOImpl.count();
        assertEquals(9, actualCount);
    }

    @Test
    void existsById() {
        boolean isExist = bookDAOImpl.existsById("1234-1234");
        assertTrue(isExist);
    }

    @Test
    void deleteById() {
        assertThrows(ConstraintViolationException.class,() -> {
            bookDAOImpl.deleteById("1234-1234");
        });
    }

    @Test
    void findAll() {
        List<Book> list = bookDAOImpl.findAll();
        assertEquals(9, list.size());
    }

    @Test
    void findById() {
        Optional<Book> book = bookDAOImpl.findById("1234-4567");
        assertTrue(book.isPresent());
    }

    @Test
    void save() {
        Book book = new Book("7456-1010", "Programming fundamentals", "James Fowler", 4);
        Book savedBook = bookDAOImpl.save(book);
        assertEquals(book.toString(), savedBook.toString());
    }

    @Test
    void update() {
        Book book = new Book("1234-7891", "Clean update codes", "Robert James", 5);
        Book updatedBook = bookDAOImpl.update(book);
        assertEquals(book.toString(), updatedBook.toString());
    }

    @Test
    void findAllBooks() {
        List<Book> books = bookDAOImpl.findAllBooks(5, 1);
        assertEquals(5, books.size());
    }

    @Test
    void findBooksByQuery() {
        List<Book> books = bookDAOImpl.findBooksByQuery("SQL");
        assertEquals(1, books.size());
    }

    @Test
    void testFindBooksByQuery() {
        List<Book> booksByQuery = bookDAOImpl.findBooksByQuery("Java", 5, 1);
        assertEquals(2, booksByQuery.size());
    }
}