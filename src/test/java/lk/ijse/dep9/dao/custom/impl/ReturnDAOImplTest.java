package lk.ijse.dep9.dao.custom.impl;

import lk.ijse.dep9.entity.Return;
import lk.ijse.dep9.entity.ReturnPK;
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

class ReturnDAOImplTest {

    private ReturnDAOImpl returnDAOImpl;

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException, URISyntaxException, IOException {
        connection = DriverManager.getConnection("jdbc:h2:mem:");
        String dbScript = Files.readString(Paths.get(this.getClass().getResource("/lms_db.sql").toURI()));
        Statement stm = connection.createStatement();
        stm.execute(dbScript);
        this.returnDAOImpl = new ReturnDAOImpl(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void count() {
        long actualCount = returnDAOImpl.count();
        assertEquals(2, actualCount);
    }

    @Test
    void existsById() {
        ReturnPK pk = new ReturnPK(3, "978-3-16-148410-0");
        boolean isExist = returnDAOImpl.existsById(pk);
        assertTrue(isExist);
    }

    @Test
    void deleteById() {
        ReturnPK pk = new ReturnPK(3, "978-3-16-148410-0");
        returnDAOImpl.deleteById(pk);
    }

    @Test
    void findAll() {
        List<Return> list = returnDAOImpl.findAll();
        assertEquals(2, list.size());
    }

    @Test
    void findById() {
        ReturnPK pk = new ReturnPK(3, "978-3-16-148410-0");
        Optional<Return> aReturn = returnDAOImpl.findById(pk);
        assertTrue(aReturn.isPresent());
    }

    @Test
    void save() {
        Return aReturn = new Return(Date.valueOf(LocalDate.now()), 5, "978-3-18-148410-0");
        assertThrows(RuntimeException.class, () -> {
            Return save = returnDAOImpl.save(aReturn);
        });
    }

    @Test
    void update() {
        Return aReturn = new Return(Date.valueOf(LocalDate.of(2023, 01, 15)), 3, "978-3-16-148410-0");
        Return updatedReturnNote = returnDAOImpl.update(aReturn);
        assertEquals(aReturn.toString(), updatedReturnNote.toString());
    }
}