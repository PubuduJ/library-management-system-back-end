package lk.ijse.dep9.dao;

import lk.ijse.dep9.dao.custom.impl.MemberDAOImpl;
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

import static org.junit.jupiter.api.Assertions.*;

class DAOFactoryTest {

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException, URISyntaxException, IOException {
        connection = DriverManager.getConnection("jdbc:h2:mem:");
        String dbScript = Files.readString(Paths.get(this.getClass().getResource("/lms_db.sql").toURI()));
        Statement stm = connection.createStatement();
        stm.execute(dbScript);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void getInstance() {
        DAOFactory instanceOne = DAOFactory.getInstance();
        DAOFactory instanceTwo = DAOFactory.getInstance();
        assertEquals(instanceOne, instanceTwo);
    }

    @Test
    void getDAO() {
        MemberDAOImpl memberDaoImpl = DAOFactory.getInstance().getDAO(connection, DAOTypes.MEMBER);
        long count = memberDaoImpl.count();
        assertEquals(3, count);
    }
}