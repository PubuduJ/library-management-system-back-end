package lk.ijse.dep9.service.custom.impl;

import lk.ijse.dep9.dto.MemberDTO;
import lk.ijse.dep9.service.exception.InUseException;
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
        List<MemberDTO> members = memberServiceImpl.findMembers("Pubudu");
        assertEquals(1, members.size());
    }

    @Test
    void findMembersByPage() {
        List<MemberDTO> members = memberServiceImpl.findMembersByPage("Pubudu", 1, 1);
        assertEquals(1, members.size());
    }

    @Test
    void getMemberInfo() {
        MemberDTO memberInfo = memberServiceImpl.getMemberInfo("104ccff3-c584-4782-a582-8a06479b4600");
        assertEquals("104ccff3-c584-4782-a582-8a06479b4600", memberInfo.getId());
    }

    @Test
    void addNewMember() {
        MemberDTO memberDTO = new MemberDTO(null, "Kasun", "Kaluthara", "071-5623698");
        memberServiceImpl.addNewMember(memberDTO);
    }

    @Test
    void deleteMember() {
        memberServiceImpl.deleteMember("2714641a-301e-43d5-9d31-ad916d075800");
        assertThrows(InUseException.class, () -> {
            memberServiceImpl.deleteMember("104ccff3-c584-4782-a582-8a06479b4600");
        });
    }

    @Test
    void updateMemberDetails() {
        MemberDTO memberDTO = new MemberDTO("2714641a-301e-43d5-9d31-ad916d075700", "Lilan Sachintha", "Rathmalana", "071-5623698");
        memberServiceImpl.updateMemberDetails(memberDTO);
    }
}