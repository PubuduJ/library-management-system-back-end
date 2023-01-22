package lk.ijse.dep9.service.custom;

import lk.ijse.dep9.dto.BookDTO;
import lk.ijse.dep9.service.SuperService;
import lk.ijse.dep9.service.exception.DuplicateException;
import lk.ijse.dep9.service.exception.NotFoundException;

import java.util.List;

public interface BookService extends SuperService {

    List<BookDTO> getAllBooks();

    List<BookDTO> getBooksByPage(int size, int page);

    List<BookDTO> findBooks(String query);

    List<BookDTO> findBooksByPage(String query, int size, int page);

    BookDTO getBookInfo(String isbn) throws NotFoundException;

    void addNewBook(BookDTO bookDTO) throws DuplicateException;

    void updateBookDetails(BookDTO bookDTO) throws NotFoundException;
}
