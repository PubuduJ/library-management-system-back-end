package lk.ijse.dep9.dao.custom.impl;

import lk.ijse.dep9.dao.custom.BookDAO;
import lk.ijse.dep9.dao.exception.ConstraintViolationException;
import lk.ijse.dep9.entity.Book;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookDAOImpl implements BookDAO {

    private final Connection connection;

    public BookDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long count() {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT COUNT(isbn) FROM Book");
            ResultSet rst = stm.executeQuery();
            rst.next();
            return rst.getLong(1);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsById(String pk) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT isbn FROM Book WHERE isbn = ?");
            stm.setString(1, pk);
            ResultSet rst = stm.executeQuery();
            return rst.next();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(String pk) throws ConstraintViolationException {
        try {
            PreparedStatement stm = connection.prepareStatement("DELETE FROM Book WHERE isbn = ?");
            stm.setString(1, pk);
            stm.executeUpdate();
        }
        catch (SQLException e) {
            if (existsById(pk)) throw new ConstraintViolationException("Book ISBN still exists in other tables", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Book> findAll() {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM Book");
            ResultSet rst = stm.executeQuery();
            List<Book> bookList = new ArrayList<>();
            while (rst.next()) {
                String isbn = rst.getString("isbn");
                String title = rst.getString("title");
                String author = rst.getString("author");
                int copies = rst.getInt("copies");
                Book book = new Book(isbn, title, author, copies);
                bookList.add(book);
            }
            return bookList;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Book> findById(String pk) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM Book WHERE isbn = ?");
            stm.setString(1, pk);
            ResultSet rst = stm.executeQuery();
            if (rst.next()) {
                String title = rst.getString("title");
                String author = rst.getString("author");
                int copies = rst.getInt("copies");
                Book book = new Book(pk, title, author, copies);
                return Optional.of(book);
            }
            else {
                return Optional.empty();
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Book save(Book book) {
        try {
            PreparedStatement stm = connection.prepareStatement("INSERT INTO Book (isbn, title, author, copies) VALUES (?, ?, ?, ?)");
            stm.setString(1, book.getIsbn());
            stm.setString(2, book.getTitle());
            stm.setString(3, book.getAuthor());
            stm.setInt(4, book.getCopies());
            if (stm.executeUpdate() == 1) {
                return book;
            }
            else {
                throw new SQLException("Failed to save the book");
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Book update(Book book) {
        try {
            PreparedStatement stm = connection.prepareStatement("UPDATE Book SET title=?, author=?, copies=? WHERE isbn=?");
            stm.setString(1, book.getTitle());
            stm.setString(2, book.getAuthor());
            stm.setInt(3, book.getCopies());
            stm.setString(4, book.getIsbn());
            if (stm.executeUpdate() == 1) {
                return book;
            }
            else {
                throw new SQLException("Failed to update the book");
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Book> findAllBooks(int size, int page) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM Book LIMIT ? OFFSET ?");
            stm.setInt(1, size);
            stm.setInt(2, (page - 1) * size);
            ResultSet rst = stm.executeQuery();
            List<Book> bookList = new ArrayList<>();
            while (rst.next()) {
                String isbn = rst.getString("isbn");
                String title = rst.getString("title");
                String author = rst.getString("author");
                int copies = rst.getInt("copies");
                Book book = new Book(isbn, title, author, copies);
                bookList.add(book);
            }
            return bookList;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Book> findBooksByQuery(String query) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM Book WHERE isbn LIKE ? OR title LIKE ? OR author LIKE ?");
            query = "%" + query + "%";
            stm.setString(1, query);
            stm.setString(2, query);
            stm.setString(3, query);
            ResultSet rst = stm.executeQuery();
            List<Book> bookList = new ArrayList<>();
            while (rst.next()) {
                String isbn = rst.getString("isbn");
                String title = rst.getString("title");
                String author = rst.getString("author");
                int copies = rst.getInt("copies");
                Book book = new Book(isbn, title, author, copies);
                bookList.add(book);
            }
            return bookList;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Book> findBooksByQuery(String query, int size, int page) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM Book WHERE isbn LIKE ? OR title LIKE ? OR author LIKE ? LIMIT ? OFFSET ?");
            query = "%" + query + "%";
            stm.setString(1, query);
            stm.setString(2, query);
            stm.setString(3, query);
            stm.setInt(4, size);
            stm.setInt(5, (page - 1) * size);
            ResultSet rst = stm.executeQuery();
            List<Book> bookList = new ArrayList<>();
            while (rst.next()) {
                String isbn = rst.getString("isbn");
                String title = rst.getString("title");
                String author = rst.getString("author");
                int copies = rst.getInt("copies");
                Book book = new Book(isbn, title, author, copies);
                bookList.add(book);
            }
            return bookList;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
