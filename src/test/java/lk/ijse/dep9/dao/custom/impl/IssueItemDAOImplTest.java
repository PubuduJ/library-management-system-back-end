package lk.ijse.dep9.dao.custom.impl;

import lk.ijse.dep9.dao.exception.ConstraintViolationException;
import lk.ijse.dep9.entity.IssueItem;
import lk.ijse.dep9.entity.IssueItemPK;
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

class IssueItemDAOImplTest {

    private IssueItemDAOImpl issueItemDAOImpl;

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException, URISyntaxException, IOException {
        connection = DriverManager.getConnection("jdbc:h2:mem:");
        String dbScript = Files.readString(Paths.get(this.getClass().getResource("/lms_db.sql").toURI()));
        Statement stm = connection.createStatement();
        stm.execute(dbScript);
        this.issueItemDAOImpl = new IssueItemDAOImpl(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void count() {
        long actualCount = issueItemDAOImpl.count();
        assertEquals(9, actualCount);
    }

    @Test
    void existsById() {
        IssueItemPK pk = new IssueItemPK(3, "1234-1234");
        boolean isExist = issueItemDAOImpl.existsById(pk);
        assertTrue(isExist);
    }

    @Test
    void deleteById() {
        IssueItemPK pk = new IssueItemPK(10, "1234-7891");
        assertThrows(ConstraintViolationException.class, () -> {
            issueItemDAOImpl.deleteById(pk);
        });
    }

    @Test
    void findAll() {
        List<IssueItem> list = issueItemDAOImpl.findAll();
        assertEquals(9, list.size());
    }

    @Test
    void findById() {
        IssueItemPK pk = new IssueItemPK(10, "1234-7891");
        Optional<IssueItem> issueItem = issueItemDAOImpl.findById(pk);
        assertTrue(issueItem.isPresent());
    }

    @Test
    void save() {
        IssueItem issueItem = new IssueItem(11, "5623-0001");
        assertThrows(RuntimeException.class, () -> {
            IssueItem savedIssueItem = issueItemDAOImpl.save(issueItem);
        });
    }
}