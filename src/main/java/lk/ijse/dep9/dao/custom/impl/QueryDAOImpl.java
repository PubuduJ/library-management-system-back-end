package lk.ijse.dep9.dao.custom.impl;

import lk.ijse.dep9.dao.custom.QueryDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class QueryDAOImpl implements QueryDAO {

    private final Connection connection;

    public QueryDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<Integer> getAvailableBookCopies(String isbn) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT (B.copies - COUNT(II.isbn) + COUNT(R.isbn)) AS `available_copies`\n" +
                    "FROM IssueItem II\n" +
                    "LEFT JOIN `Return` R ON II.issue_id = R.issue_id AND II.isbn = R.isbn\n" +
                    "RIGHT JOIN Book B ON II.isbn = B.isbn\n" +
                    "WHERE B.isbn = ?\n" +
                    "GROUP BY B.isbn");
            stm.setString(1, isbn);
            ResultSet rst = stm.executeQuery();
            if (!rst.next()) return Optional.empty();
            int availableCopies = rst.getInt(1);
            return Optional.of(availableCopies);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isAlreadyIssued(String isbn, String memberId) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT II.isbn\n" +
                    "FROM IssueItem II\n" +
                    "INNER JOIN `Return` R ON NOT (II.issue_id = R.issue_id AND II.isbn = R.isbn)\n" +
                    "INNER JOIN IssueNote `IN` ON II.issue_id = `IN`.id\n" +
                    "INNER JOIN Book B ON II.isbn = B.isbn\n" +
                    "WHERE `IN`.member_id = ? AND B.isbn = ?");
            stm.setString(1, memberId);
            stm.setString(2, isbn);
            ResultSet rst = stm.executeQuery();
            return rst.next();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Integer> availableBookLimit(String memberId) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT M.id, M.name, 3 - COUNT(`IN`.id) as available\n" +
                    "FROM Member M\n" +
                    "LEFT JOIN IssueNote `IN` ON M.id = `IN`.member_id\n" +
                    "LEFT JOIN IssueItem II ON `IN`.id = II.issue_id\n" +
                    "LEFT JOIN `Return` R ON II.issue_id = R.issue_id AND II.isbn = R.isbn\n" +
                    "WHERE R.date IS NULL AND M.id = ? GROUP BY M.id");
            stm.setString(1, memberId);
            ResultSet rst = stm.executeQuery();
            if (!rst.next()) return Optional.empty();
            int availableBookLimit = rst.getInt("available");
            return Optional.of(availableBookLimit);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isValidIssueItem(String memberId, int issueId, String isbn) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT *\n" +
                    "FROM IssueItem II\n" +
                    "INNER JOIN IssueNote `IN` ON II.issue_id = `IN`.id\n" +
                    "WHERE `IN`.member_id = ? AND II.issue_id = ? AND II.isbn = ?");
            stm.setString(1, memberId);
            stm.setInt(2, issueId);
            stm.setString(3, isbn);
            ResultSet rst = stm.executeQuery();
            return rst.next();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
