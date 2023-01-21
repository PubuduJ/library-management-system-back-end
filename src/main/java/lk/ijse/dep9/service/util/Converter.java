package lk.ijse.dep9.service.util;

import lk.ijse.dep9.dto.BookDTO;
import lk.ijse.dep9.dto.IssueNoteDTO;
import lk.ijse.dep9.dto.MemberDTO;
import lk.ijse.dep9.entity.Book;
import lk.ijse.dep9.entity.IssueNote;
import lk.ijse.dep9.entity.Member;
import org.modelmapper.ModelMapper;

import java.sql.Date;

public class Converter {

    private ModelMapper modelMapper;

    public Converter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public BookDTO toBookDTO(Book book) {
        return modelMapper.map(book, BookDTO.class);
    }

    public Book toBookEntity(BookDTO bookDTO) {
        return modelMapper.map(bookDTO, Book.class);
    }

    public MemberDTO toMemberDTO(Member member) {
        return modelMapper.map(member, MemberDTO.class);
    }

    public Member toMemberEntity(MemberDTO memberDTO) {
        return modelMapper.map(memberDTO, Member.class);
    }

    public IssueNote toIssueNoteEntity(IssueNoteDTO issueNoteDTO) {
        return new IssueNote(issueNoteDTO.getId(), Date.valueOf(issueNoteDTO.getDate()),issueNoteDTO.getMemberId());
    }
}
