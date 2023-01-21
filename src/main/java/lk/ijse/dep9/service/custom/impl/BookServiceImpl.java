package lk.ijse.dep9.service.custom.impl;

import lk.ijse.dep9.dao.DAOFactory;
import lk.ijse.dep9.dao.DAOTypes;
import lk.ijse.dep9.dao.custom.BookDAO;
import lk.ijse.dep9.dto.BookDTO;
import lk.ijse.dep9.entity.Book;
import lk.ijse.dep9.service.custom.BookService;
import lk.ijse.dep9.service.exception.DuplicateException;
import lk.ijse.dep9.service.exception.NotFoundException;
import lk.ijse.dep9.service.util.Converter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookServiceImpl implements BookService {

    private final BookDAO bookDAO;

    private final Converter converter;

    public BookServiceImpl(Connection connection) {
        bookDAO = DAOFactory.getInstance().getDAO(connection, DAOTypes.BOOK);
        converter = new Converter();
    }

    @Override
    public List<BookDTO> getAllBooks() {
        List<BookDTO> bookDTOList = new ArrayList<>();
        List<Book> books = bookDAO.findAll();
        for (Book book : books) {
            BookDTO bookDTO = converter.toBookDTO(book);
            bookDTOList.add(bookDTO);
        }
        return bookDTOList;
    }

    @Override
    public List<BookDTO> getBooksByPage(int size, int page) {
        List<BookDTO> bookDTOList = new ArrayList<>();
        List<Book> books = bookDAO.findAllBooks(size, page);
        for (Book book : books) {
            BookDTO bookDTO = converter.toBookDTO(book);
            bookDTOList.add(bookDTO);
        }
        return bookDTOList;
    }

    @Override
    public List<BookDTO> findBooks(String query) {
        List<BookDTO> bookDTOList = new ArrayList<>();
        List<Book> books = bookDAO.findBooksByQuery(query);
        for (Book book : books) {
            BookDTO bookDTO = converter.toBookDTO(book);
            bookDTOList.add(bookDTO);
        }
        return bookDTOList;
    }

    @Override
    public List<BookDTO> findBooksByPage(String query, int size, int page) {
        List<BookDTO> bookDTOList = new ArrayList<>();
        List<Book> books = bookDAO.findBooksByQuery(query, size, page);
        for (Book book : books) {
            BookDTO bookDTO = converter.toBookDTO(book);
            bookDTOList.add(bookDTO);
        }
        return bookDTOList;
    }

    @Override
    public BookDTO getBookInfo(String isbn) throws NotFoundException {
        if (!bookDAO.existsById(isbn)) {
            throw new NotFoundException("Book doesn't exist");
        }
        Optional<Book> book = bookDAO.findById(isbn);
        return converter.toBookDTO(book.get());
    }

    @Override
    public BookDTO addNewBook(BookDTO bookDTO) throws DuplicateException {
        if (bookDAO.existsById(bookDTO.getIsbn())) {
            throw new DuplicateException("A book already exists with this isbn number");
        }
        Book book = converter.toBookEntity(bookDTO);
        Book savedBook = bookDAO.save(book);
        return converter.toBookDTO(savedBook);
    }

    @Override
    public BookDTO updateBookDetails(BookDTO bookDTO) throws NotFoundException {
        if (!bookDAO.existsById(bookDTO.getIsbn())) {
            throw new NotFoundException("Book doesn't exist");
        }
        Book book = converter.toBookEntity(bookDTO);
        Book updatedBook = bookDAO.update(book);
        return converter.toBookDTO(updatedBook);
    }
}
