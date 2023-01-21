package lk.ijse.dep9.service.custom.impl;

import lk.ijse.dep9.dto.IssueNoteDTO;
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

class IssueServiceImplTest {

    private IssueServiceImpl issueServiceImpl;

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException, URISyntaxException, IOException {
        connection = DriverManager.getConnection("jdbc:h2:mem:");
        String dbScript = Files.readString(Paths.get(this.getClass().getResource("/lms_db.sql").toURI()));
        Statement stm = connection.createStatement();
        stm.execute(dbScript);
        this.issueServiceImpl = new IssueServiceImpl(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void placeNewIssueNote() {
        ArrayList<String> books1 = new ArrayList<>();
        books1.add("1234-1234");
        books1.add("1234-7891");
        IssueNoteDTO issueNoteDTO1 = new IssueNoteDTO(null, null, "104ccff3-c584-4782-a582-8a06479b46f6", books1);
        issueServiceImpl.placeNewIssueNote(issueNoteDTO1);

        ArrayList<String> books2 = new ArrayList<>();
        books2.add("1234-7891");
        IssueNoteDTO issueNoteDTO2 = new IssueNoteDTO(null, null, "104ccff3-c584-4782-a582-8a06479b46f6", books2);
        issueServiceImpl.placeNewIssueNote(issueNoteDTO2);
    }
}