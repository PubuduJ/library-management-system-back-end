package lk.ijse.dep9.service.custom;

import lk.ijse.dep9.dto.MemberDTO;
import lk.ijse.dep9.service.SuperService;
import lk.ijse.dep9.service.exception.DuplicateException;
import lk.ijse.dep9.service.exception.InUseException;
import lk.ijse.dep9.service.exception.NotFoundException;

import java.util.List;

public interface MemberService extends SuperService {

    List<MemberDTO> getAllMembers();

    List<MemberDTO> getMembersByPage(int size, int page);

    List<MemberDTO> findMembers(String query);

    List<MemberDTO> findMembersByPage(String query, int size, int page);

    MemberDTO getMemberInfo(String memberId) throws NotFoundException;

    void addNewMember(MemberDTO memberDTO) throws DuplicateException;

    void deleteMember(String memberId) throws NotFoundException, InUseException;

    void updateMemberDetails(MemberDTO memberDTO) throws NotFoundException;
}
