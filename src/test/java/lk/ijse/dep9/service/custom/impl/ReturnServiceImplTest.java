package lk.ijse.dep9.service.custom.impl;

import lk.ijse.dep9.dto.ReturnDTO;
import lk.ijse.dep9.dto.ReturnItemDTO;
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
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ReturnServiceImplTest {

    private ReturnServiceImpl returnServiceImpl;

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException, URISyntaxException, IOException {
        connection = DriverManager.getConnection("jdbc:h2:mem:");
        String dbScript = Files.readString(Paths.get(this.getClass().getResource("/lms_db.sql").toURI()));
        Statement stm = connection.createStatement();
        stm.execute(dbScript);
        this.returnServiceImpl = new ReturnServiceImpl(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void updateReturnStatus() {
        ArrayList<ReturnItemDTO> returnItems = new ArrayList<>();
        returnItems.add(new ReturnItemDTO(3, "978-3-16-148410-1"));
        ReturnDTO returnDTO = new ReturnDTO("104ccff3-c584-4782-a582-8a06479b4600", returnItems);
        returnServiceImpl.updateReturnStatus(returnDTO);
    }
}