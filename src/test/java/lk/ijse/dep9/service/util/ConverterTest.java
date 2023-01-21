package lk.ijse.dep9.service.util;

import lk.ijse.dep9.dto.BookDTO;
import lk.ijse.dep9.dto.IssueNoteDTO;
import lk.ijse.dep9.dto.MemberDTO;
import lk.ijse.dep9.entity.Book;
import lk.ijse.dep9.entity.IssueItem;
import lk.ijse.dep9.entity.IssueNote;
import lk.ijse.dep9.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConverterTest {

    private Converter converter;

    @BeforeEach
    void setUp() {
        converter = new Converter(new ModelMapper());
    }

    @Test
    void toBookDTO() {
        Book book = new Book("4523-5632", "Programming Fundamentals", "Mike", 5);
        BookDTO bookDTO = converter.toBookDTO(book);
        assertEquals(book.getIsbn(),bookDTO.getIsbn());
        assertEquals(book.getTitle(),bookDTO.getTitle());
        assertEquals(book.getAuthor(),bookDTO.getAuthor());
        assertEquals(book.getCopies(),bookDTO.getCopies());
    }

    @Test
    void toBookEntity() {
        BookDTO bookDTO = new BookDTO("4523-5632", "Programming Fundamentals", "Mike", 5);
        Book book = converter.toBookEntity(bookDTO);
        assertEquals(book.getIsbn(),bookDTO.getIsbn());
        assertEquals(book.getTitle(),bookDTO.getTitle());
        assertEquals(book.getAuthor(),bookDTO.getAuthor());
        assertEquals(book.getCopies(),bookDTO.getCopies());
    }

    @Test
    void toMemberDTO() {
        Member member = new Member("104ccff3-c584-4782-a582-8a06479b46f6", "Supun", "Moratuwa", "071-9652369");
        MemberDTO memberDTO = converter.toMemberDTO(member);
        assertEquals(member.getId(), memberDTO.getId());
        assertEquals(member.getName(), memberDTO.getName());
        assertEquals(member.getAddress(), memberDTO.getAddress());
        assertEquals(member.getContact(), memberDTO.getContact());
    }

    @Test
    void toMemberEntity() {
        MemberDTO memberDTO = new MemberDTO("104ccff3-c584-4782-a582-8a06479b46f6", "Supun", "Moratuwa", "071-9652369");
        Member member = converter.toMemberEntity(memberDTO);
        assertEquals(member.getId(), memberDTO.getId());
        assertEquals(member.getName(), memberDTO.getName());
        assertEquals(member.getAddress(), memberDTO.getAddress());
        assertEquals(member.getContact(), memberDTO.getContact());
    }

    @Test
    void toIssueNoteEntity() {
        ArrayList<String> books = new ArrayList<>();
        books.add("7896-5632");
        books.add("8542-8963");
        books.add("0213-4523");
        IssueNoteDTO issueNoteDTO = new IssueNoteDTO(1, LocalDate.now(), "104ccff3-c584-4782-a582-8a06479b46f6", books);
        IssueNote issueNote = converter.toIssueNoteEntity(issueNoteDTO);
        assertEquals(issueNote.getId(), issueNoteDTO.getId());
        assertEquals(issueNote.getDate().toString(), issueNoteDTO.getDate().toString());
        assertEquals(issueNote.getMemberId(), issueNoteDTO.getMemberId());
    }

    @Test
    void toIssueItemEntityList() {
        ArrayList<String> books = new ArrayList<>();
        books.add("7896-5632");
        books.add("8542-8963");
        books.add("0213-4523");
        IssueNoteDTO issueNoteDTO = new IssueNoteDTO(1, LocalDate.now(), "104ccff3-c584-4782-a582-8a06479b46f6", books);
        List<IssueItem> issueItemList = converter.toIssueItemEntityList(issueNoteDTO);
        for (int i = 0; i < issueItemList.size(); i++) {
            assertEquals(issueItemList.get(i).getIssueItemPK().getIssueId(), issueNoteDTO.getId());
            assertEquals(issueItemList.get(i).getIssueItemPK().getIsbn(), issueNoteDTO.getBooks().get(i));
        }
    }
}