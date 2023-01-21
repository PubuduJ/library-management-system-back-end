package lk.ijse.dep9.dao.custom.impl;

import lk.ijse.dep9.dao.exception.ConstraintViolationException;
import lk.ijse.dep9.entity.Member;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MemberDAOImplTest {

    private MemberDAOImpl memberDAOImpl;

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException, URISyntaxException, IOException {
        connection = DriverManager.getConnection("jdbc:h2:mem:");
        String dbScript = Files.readString(Paths.get(this.getClass().getResource("/lms_db.sql").toURI()));
        Statement stm = connection.createStatement();
        stm.execute(dbScript);
        this.memberDAOImpl = new MemberDAOImpl(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void count() {
        long actualCount = memberDAOImpl.count();
        assertEquals(3, actualCount);
    }

    @Test
    void existsById() {
        boolean isExist = memberDAOImpl.existsById("104ccff3-c584-4782-a582-8a06479b46f6");
        assertTrue(isExist);
    }

    @Test
    void deleteById() {
        assertThrows(ConstraintViolationException.class,() -> {
            memberDAOImpl.deleteById("104ccff3-c584-4782-a582-8a06479b46f6");
        });
    }

    @Test
    void findAll() {
        List<Member> list = memberDAOImpl.findAll();
        assertEquals(3, list.size());
    }

    @Test
    void findById() {
        Optional<Member> member = memberDAOImpl.findById("2714641a-301e-43d5-9d31-ad916d075ba6");
        assertTrue(member.isPresent());
    }

    @Test
    void save() {
        Member member = new Member("2714641a-301e-43d5-9d31-ad916d075ba9", "Pubudu Janith", "Horana", "077-4523698");
        Member savedMember = memberDAOImpl.save(member);
        assertEquals(member.toString(), savedMember.toString());
    }

    @Test
    void update() {
        Member member = new Member("2714641a-301e-43d5-9d31-ad916d075ba7", "Supun Silva", "Moratuwa", "071-4523698");
        Member updatedMember = memberDAOImpl.update(member);
        assertEquals(member.toString(), updatedMember.toString());
    }

    @Test
    void findAllMembers() {
        List<Member> allMembers = memberDAOImpl.findAllMembers(1, 1);
        assertEquals(1, allMembers.size());
    }

    @Test
    void findMembersByQuery() {
        List<Member> members = memberDAOImpl.findMembersByQuery("Tharindu");
        assertEquals(1, members.size());
    }

    @Test
    void testFindMembersByQuery() {
        List<Member> membersByQuery = memberDAOImpl.findMembersByQuery("Tharindu", 5, 1);
        assertEquals(1, membersByQuery.size());
    }

    @Test
    void existsByContact() {
        boolean isExist = memberDAOImpl.existsByContact("078-1234567");
        assertTrue(isExist);
    }
}