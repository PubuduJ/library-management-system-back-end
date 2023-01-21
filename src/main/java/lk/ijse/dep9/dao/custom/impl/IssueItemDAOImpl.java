package lk.ijse.dep9.dao.custom.impl;

import lk.ijse.dep9.dao.custom.IssueItemDAO;
import lk.ijse.dep9.dao.exception.ConstraintViolationException;
import lk.ijse.dep9.entity.IssueItem;
import lk.ijse.dep9.entity.IssueItemPK;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IssueItemDAOImpl implements IssueItemDAO {

    private final Connection connection;

    public IssueItemDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long count() {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT COUNT(isbn) FROM IssueItem");
            ResultSet rst = stm.executeQuery();
            rst.next();
            return rst.getLong(1);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsById(IssueItemPK pk) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT isbn FROM IssueItem WHERE isbn = ? AND issue_id = ?");
            stm.setString(1, pk.getIsbn());
            stm.setInt(2, pk.getIssueId());
            ResultSet rst = stm.executeQuery();
            return rst.next();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(IssueItemPK pk) throws ConstraintViolationException {
        try {
            PreparedStatement stm = connection.prepareStatement("DELETE FROM IssueItem WHERE isbn = ? AND issue_id = ?");
            stm.setString(1, pk.getIsbn());
            stm.setInt(2, pk.getIssueId());
            stm.executeUpdate();
        }
        catch (SQLException e) {
            if (existsById(pk)) throw new ConstraintViolationException("Issue Item primary key still exists within other tables", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<IssueItem> findAll() {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM IssueItem");
            ResultSet rst = stm.executeQuery();
            List<IssueItem> issueItemList = new ArrayList<>();
            while (rst.next()) {
                int id = rst.getInt("issue_id");
                String isbn = rst.getString("isbn");
                IssueItem issueItem = new IssueItem(id, isbn);
                issueItemList.add(issueItem);
            }
            return issueItemList;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<IssueItem> findById(IssueItemPK pk) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM IssueItem WHERE isbn = ? AND issue_id = ?");
            stm.setString(1, pk.getIsbn());
            stm.setInt(2, pk.getIssueId());
            ResultSet rst = stm.executeQuery();
            if (rst.next()) {
                int issueId = rst.getInt("issue_id");
                String isbn = rst.getString("isbn");
                IssueItem issueItem = new IssueItem(issueId, isbn);
                return Optional.of(issueItem);
            }
            else {
                return Optional.empty();
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IssueItem save(IssueItem issueItem) {
        try {
            PreparedStatement stm = connection.prepareStatement("INSERT INTO IssueItem (issue_id, isbn) VALUES (?, ?)");
            stm.setInt(1, issueItem.getIssueItemPK().getIssueId());
            stm.setString(2, issueItem.getIssueItemPK().getIsbn());
            if (stm.executeUpdate() == 1) {
                return issueItem;
            }
            else {
                throw new SQLException("Failed to save the issue item");
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IssueItem update(IssueItem issueItem) {
        /* Primary key cannot update */
        return null;
    }
}
