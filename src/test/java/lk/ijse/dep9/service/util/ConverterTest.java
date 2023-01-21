package lk.ijse.dep9.service.util;

import lk.ijse.dep9.dto.BookDTO;
import lk.ijse.dep9.dto.MemberDTO;
import lk.ijse.dep9.entity.Book;
import lk.ijse.dep9.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

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
}