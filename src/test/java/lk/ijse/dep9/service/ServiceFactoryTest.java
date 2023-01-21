package lk.ijse.dep9.service;

import lk.ijse.dep9.dto.MemberDTO;
import lk.ijse.dep9.service.custom.impl.MemberServiceImpl;
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

class ServiceFactoryTest {

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
        ServiceFactory instanceOne = ServiceFactory.getInstance();
        ServiceFactory instanceTwo = ServiceFactory.getInstance();
        assertEquals(instanceOne, instanceTwo);
    }

    @Test
    void getService() {
        MemberServiceImpl memberServiceImpl = ServiceFactory.getInstance().getService(connection, ServiceTypes.MEMBER);
        List<MemberDTO> allMembers = memberServiceImpl.getAllMembers();
        assertEquals(3, allMembers.size());
    }
}