package lk.ijse.dep9.service.util;

import lk.ijse.dep9.dto.*;
import lk.ijse.dep9.entity.*;
import org.modelmapper.ModelMapper;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Converter {

    private ModelMapper modelMapper;

    public Converter() {
        this.modelMapper = new ModelMapper();
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
        return new IssueNote(0, Date.valueOf(LocalDate.now()),issueNoteDTO.getMemberId());
    }

    public List<IssueItem> toIssueItemEntityList(IssueNoteDTO issueNoteDTO) {
        ArrayList<String> books = issueNoteDTO.getBooks();
        ArrayList<IssueItem> issueItemList = new ArrayList<>();
        for (String isbn : books) {
            IssueItem issueItem = new IssueItem(issueNoteDTO.getId(), isbn);
            issueItemList.add(issueItem);
        }
        return issueItemList;
    }

    public Return toReturnEntity(ReturnItemDTO returnItemDTO) {
        return new Return(Date.valueOf(LocalDate.now()), returnItemDTO.getIssueNoteId(), returnItemDTO.getIsbn());
    }
}
