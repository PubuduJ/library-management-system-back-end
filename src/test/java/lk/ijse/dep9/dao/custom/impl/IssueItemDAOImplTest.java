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
        assertEquals(4, actualCount);
    }

    @Test
    void existsById() {
        IssueItemPK pk = new IssueItemPK(3, "978-3-16-148410-0");
        boolean isExist = issueItemDAOImpl.existsById(pk);
        assertTrue(isExist);
    }

    @Test
    void deleteById() {
        IssueItemPK pk = new IssueItemPK(3, "978-3-16-148410-0");
        assertThrows(ConstraintViolationException.class, () -> {
            issueItemDAOImpl.deleteById(pk);
        });
    }

    @Test
    void findAll() {
        List<IssueItem> list = issueItemDAOImpl.findAll();
        assertEquals(4, list.size());
    }

    @Test
    void findById() {
        IssueItemPK pk = new IssueItemPK(4, "978-3-16-148410-2");
        Optional<IssueItem> issueItem = issueItemDAOImpl.findById(pk);
        assertTrue(issueItem.isPresent());
    }

    @Test
    void save() {
        IssueItem issueItem = new IssueItem(4, "978-3-16-148412-6");
        assertThrows(RuntimeException.class, () -> {
            IssueItem savedIssueItem = issueItemDAOImpl.save(issueItem);
        });
    }
}