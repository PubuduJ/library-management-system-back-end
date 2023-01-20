package lk.ijse.dep9.dao.custom.impl;

import lk.ijse.dep9.dao.exception.ConstraintViolationException;
import lk.ijse.dep9.entity.IssueNote;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class IssueNoteDAOImplTest {

    private IssueNoteDAOImpl issueNoteDAOImpl;

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException, URISyntaxException, IOException {
        connection = DriverManager.getConnection("jdbc:h2:mem:");
        String dbScript = Files.readString(Paths.get(this.getClass().getResource("/lms_db.sql").toURI()));
        Statement stm = connection.createStatement();
        stm.execute(dbScript);
        this.issueNoteDAOImpl = new IssueNoteDAOImpl(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void count() {
        long actualCount = issueNoteDAOImpl.count();
        assertEquals(5, actualCount);
    }

    @Test
    void existsById() {
        boolean isExist = issueNoteDAOImpl.existsById(1);
        assertTrue(isExist);
    }

    @Test
    void deleteById() {
        assertThrows(ConstraintViolationException.class,() -> {
            issueNoteDAOImpl.deleteById(1);
        });
    }

    @Test
    void findAll() {
        List<IssueNote> list = issueNoteDAOImpl.findAll();
        assertEquals(5, list.size());
    }

    @Test
    void findById() {
        Optional<IssueNote> issueNote = issueNoteDAOImpl.findById(3);
        assertTrue(issueNote.isPresent());
    }

    @Test
    void save() {
        IssueNote issueNote = new IssueNote(Date.valueOf(LocalDate.now()), "2714641a-301e-43d5-9d31-ad916d075ba6");
        assertThrows(RuntimeException.class, () -> {
            IssueNote savedIssueNote = issueNoteDAOImpl.save(issueNote);
        });
    }

    @Test
    void update() {
        IssueNote issueNote = new IssueNote(3, Date.valueOf(LocalDate.now()), "2714641a-301e-43d5-9d31-ad916d075ba7");
        IssueNote updatedIssueNote = issueNoteDAOImpl.update(issueNote);
        assertEquals(issueNote.toString(), updatedIssueNote.toString());
    }
}