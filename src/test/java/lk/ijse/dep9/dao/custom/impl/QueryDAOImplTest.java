package lk.ijse.dep9.dao.custom.impl;

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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class QueryDAOImplTest {

    private QueryDAOImpl queryDAOImpl;

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException, URISyntaxException, IOException {
        connection = DriverManager.getConnection("jdbc:h2:mem:");
        String dbScript = Files.readString(Paths.get(this.getClass().getResource("/lms_db.sql").toURI()));
        Statement stm = connection.createStatement();
        stm.execute(dbScript);
        this.queryDAOImpl = new QueryDAOImpl(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void getAvailableBookCopies() {
        Optional<Integer> actualCount = queryDAOImpl.getAvailableBookCopies("1234-1234");
        Integer count = actualCount.get();
        assertEquals(1, count);
    }

    @Test
    void isAlreadyIssued() {
        boolean isIssued = queryDAOImpl.isAlreadyIssued("1234-4567", "104ccff3-c584-4782-a582-8a06479b46f6");
        assertTrue(isIssued);
    }

    @Test
    void availableBookLimit() {
        Optional<Integer> bookLimit = queryDAOImpl.availableBookLimit("2714641a-301e-43d5-9d31-ad916d075ba6");
        Integer limit = bookLimit.get();
        assertEquals(3, limit);
    }

    @Test
    void isValidIssueItem() {
        boolean isValid = queryDAOImpl.isValidIssueItem("2714641a-301e-43d5-9d31-ad916d075ba7", 3, "1234-1234");
        assertTrue(isValid);
    }
}