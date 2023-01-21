package lk.ijse.dep9.service.custom.impl;

import lk.ijse.dep9.dto.MemberDTO;
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

class MemberServiceImplTest {

    private MemberServiceImpl memberServiceImpl;

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException, URISyntaxException, IOException {
        connection = DriverManager.getConnection("jdbc:h2:mem:");
        String dbScript = Files.readString(Paths.get(this.getClass().getResource("/lms_db.sql").toURI()));
        Statement stm = connection.createStatement();
        stm.execute(dbScript);
        this.memberServiceImpl = new MemberServiceImpl(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void getAllMembers() {
        List<MemberDTO> allMembers = memberServiceImpl.getAllMembers();
        assertEquals(3, allMembers.size());
    }

    @Test
    void getMembersByPage() {
        List<MemberDTO> membersByPage = memberServiceImpl.getMembersByPage(1, 1);
        assertEquals(1, membersByPage.size());
    }

    @Test
    void findMembers() {
        List<MemberDTO> members = memberServiceImpl.findMembers("Tharindu");
        assertEquals(1, members.size());
    }

    @Test
    void findMembersByPage() {
        List<MemberDTO> members = memberServiceImpl.findMembersByPage("Tharindu", 1, 1);
        assertEquals(1, members.size());
    }

    @Test
    void getMemberInfo() {
        MemberDTO memberInfo = memberServiceImpl.getMemberInfo("2714641a-301e-43d5-9d31-ad916d075ba6");
        assertEquals("2714641a-301e-43d5-9d31-ad916d075ba6", memberInfo.getId());
    }

    @Test
    void addNewMember() {
        MemberDTO memberDTO = new MemberDTO(null, "Kasun", "Kaluthara", "071-5623698");
        MemberDTO savedMember = memberServiceImpl.addNewMember(memberDTO);
        assertEquals(memberDTO.getName(), savedMember.getName());
        assertEquals(memberDTO.getAddress(), savedMember.getAddress());
        assertEquals(memberDTO.getContact(), savedMember.getContact());
    }

    @Test
    void deleteMember() {
        memberServiceImpl.deleteMember("2714641a-301e-43d5-9d31-ad916d075ba6");
        assertThrows(NotFoundException.class, () -> {
            memberServiceImpl.deleteMember("2714641a-301e-43d5-5632-ad916d075ba6");
        });
    }

    @Test
    void updateMemberDetails() {
        MemberDTO memberDTO = new MemberDTO("2714641a-301e-43d5-9d31-ad916d075ba6", "Kasun", "Kaluthara", "071-5623698");
        MemberDTO updatedMember = memberServiceImpl.updateMemberDetails(memberDTO);
        assertEquals(memberDTO.getId(), updatedMember.getId());
        assertEquals(memberDTO.getName(), updatedMember.getName());
        assertEquals(memberDTO.getAddress(), updatedMember.getAddress());
        assertEquals(memberDTO.getContact(), updatedMember.getContact());
    }
}