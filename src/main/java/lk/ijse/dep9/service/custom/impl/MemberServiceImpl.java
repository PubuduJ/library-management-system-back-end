package lk.ijse.dep9.service.custom.impl;

import lk.ijse.dep9.dao.DAOFactory;
import lk.ijse.dep9.dao.DAOTypes;
import lk.ijse.dep9.dao.custom.MemberDAO;
import lk.ijse.dep9.dao.exception.ConstraintViolationException;
import lk.ijse.dep9.dto.MemberDTO;
import lk.ijse.dep9.entity.Member;
import lk.ijse.dep9.service.custom.MemberService;
import lk.ijse.dep9.service.exception.DuplicateException;
import lk.ijse.dep9.service.exception.InUseException;
import lk.ijse.dep9.service.exception.NotFoundException;
import lk.ijse.dep9.service.util.Converter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MemberServiceImpl implements MemberService {

    private final MemberDAO memberDAO;
    private final Converter converter;

    public MemberServiceImpl(Connection connection) {
        memberDAO = DAOFactory.getInstance().getDAO(connection, DAOTypes.MEMBER);
        converter = new Converter();
    }

    @Override
    public List<MemberDTO> getAllMembers() {
        List<MemberDTO> memberDTOList = new ArrayList<>();
        List<Member> members = memberDAO.findAll();
        for (Member member : members) {
            MemberDTO memberDTO = converter.toMemberDTO(member);
            memberDTOList.add(memberDTO);
        }
        return memberDTOList;
    }

    @Override
    public List<MemberDTO> getMembersByPage(int size, int page) {
        List<MemberDTO> memberDTOList = new ArrayList<>();
        List<Member> members = memberDAO.findAllMembers(size, page);
        for (Member member : members) {
            MemberDTO memberDTO = converter.toMemberDTO(member);
            memberDTOList.add(memberDTO);
        }
        return memberDTOList;
    }

    @Override
    public List<MemberDTO> findMembers(String query) {
        List<MemberDTO> memberDTOList = new ArrayList<>();
        List<Member> members = memberDAO.findMembersByQuery(query);
        for (Member member : members) {
            MemberDTO memberDTO = converter.toMemberDTO(member);
            memberDTOList.add(memberDTO);
        }
        return memberDTOList;
    }

    @Override
    public List<MemberDTO> findMembersByPage(String query, int size, int page) {
        List<MemberDTO> memberDTOList = new ArrayList<>();
        List<Member> members = memberDAO.findMembersByQuery(query, size, page);
        for (Member member : members) {
            MemberDTO memberDTO = converter.toMemberDTO(member);
            memberDTOList.add(memberDTO);
        }
        return memberDTOList;
    }

    @Override
    public MemberDTO getMemberInfo(String memberId) throws NotFoundException {
        if (!memberDAO.existsById(memberId)) {
            throw new NotFoundException("Member doesn't exist");
        }
        Optional<Member> member = memberDAO.findById(memberId);
        return converter.toMemberDTO(member.get());
    }

    @Override
    public void addNewMember(MemberDTO memberDTO) throws DuplicateException {
        if (memberDAO.existsByContact(memberDTO.getContact())) {
            throw new DuplicateException("A member already exists with this contact number");
        }
        memberDTO.setId(UUID.randomUUID().toString());
        Member member = converter.toMemberEntity(memberDTO);
        memberDAO.save(member);
    }

    @Override
    public void deleteMember(String memberId) throws NotFoundException, InUseException {
        if (!memberDAO.existsById(memberId)) {
            throw new NotFoundException("Member doesn't exist");
        }
        try {
            memberDAO.deleteById(memberId);
        } catch (ConstraintViolationException e) {
            throw new InUseException("Member details are still in use", e);
        }
    }

    @Override
    public void updateMemberDetails(MemberDTO memberDTO) throws NotFoundException {
        if (!memberDAO.existsById(memberDTO.getId())) {
            throw new NotFoundException("Member doesn't exist");
        }
        Member member = converter.toMemberEntity(memberDTO);
        memberDAO.update(member);
    }
}
