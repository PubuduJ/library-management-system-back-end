package lk.ijse.dep9.service.custom.impl;

import lk.ijse.dep9.dto.BookDTO;
import lk.ijse.dep9.service.exception.DuplicateException;
import lk.ijse.dep9.service.exception.NotFoundException;
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

import static org.junit.jupiter.api.Assertions.*;

class BookServiceImplTest {

    private BookServiceImpl bookServiceImpl;

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException, URISyntaxException, IOException {
        connection = DriverManager.getConnection("jdbc:h2:mem:");
        String dbScript = Files.readString(Paths.get(this.getClass().getResource("/lms_db.sql").toURI()));
        Statement stm = connection.createStatement();
        stm.execute(dbScript);
        this.bookServiceImpl = new BookServiceImpl(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void getAllBooks() {
        List<BookDTO> allBooks = bookServiceImpl.getAllBooks();
        assertEquals(8, allBooks.size());
    }

    @Test
    void getBooksByPage() {
        List<BookDTO> booksByPage = bookServiceImpl.getBooksByPage(5, 1);
        assertEquals(5, booksByPage.size());
    }

    @Test
    void findBooks() {
        List<BookDTO> books = bookServiceImpl.findBooks("Java");
        assertEquals(2, books.size());
    }

    @Test
    void findBooksByPage() {
        List<BookDTO> books = bookServiceImpl.findBooksByPage("Clean Code", 5, 1);
        assertEquals(1, books.size());
    }

    @Test
    void getBookInfo() {
        BookDTO bookInfo = bookServiceImpl.getBookInfo("978-3-16-148410-0");
        assertEquals("978-3-16-148410-0", bookInfo.getIsbn());
    }

    @Test
    void addNewBook() {
        BookDTO bookDTO1 = new BookDTO("978-3-16-148410-8", "Java Basics", "James", 5);
        BookDTO bookDTO2 = new BookDTO("978-3-16-148410-4", "Python fundamentals", "Mike", 3);
        BookDTO addedBook1 = bookServiceImpl.addNewBook(bookDTO1);
        assertEquals(bookDTO1.getIsbn(), addedBook1.getIsbn());
        assertEquals(bookDTO1.getTitle(), addedBook1.getTitle());
        assertEquals(bookDTO1.getAuthor(), addedBook1.getAuthor());
        assertEquals(bookDTO1.getCopies(), addedBook1.getCopies());
        assertThrows(DuplicateException.class, () -> {
            BookDTO addedBook2 = bookServiceImpl.addNewBook(bookDTO1);
        });
    }

    @Test
    void updateBookDetails() {
        BookDTO bookDTO1 = new BookDTO("978-3-16-148410-9", "Python Fundamentals", "Mike", 5);
        assertThrows(NotFoundException.class, () -> {
            BookDTO updatedBook1 = bookServiceImpl.updateBookDetails(bookDTO1);
        });
        BookDTO bookDTO2 = new BookDTO("978-3-16-148410-1", "MongoDB Specification", "Jim", 2);
        BookDTO updatedBook2 = bookServiceImpl.updateBookDetails(bookDTO2);
        assertEquals(bookDTO2.getIsbn(), updatedBook2.getIsbn());
        assertEquals(bookDTO2.getTitle(), updatedBook2.getTitle());
        assertEquals(bookDTO2.getAuthor(), updatedBook2.getAuthor());
        assertEquals(bookDTO2.getCopies(), updatedBook2.getCopies());
    }
}