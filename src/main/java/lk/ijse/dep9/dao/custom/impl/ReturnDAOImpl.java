package lk.ijse.dep9.dao.custom.impl;

import lk.ijse.dep9.dao.custom.ReturnDAO;
import lk.ijse.dep9.dao.exception.ConstraintViolationException;
import lk.ijse.dep9.entity.Return;
import lk.ijse.dep9.entity.ReturnPK;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReturnDAOImpl implements ReturnDAO {

    private final Connection connection;

    public ReturnDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long count() {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT COUNT(isbn) FROM `Return`");
            ResultSet rst = stm.executeQuery();
            rst.next();
            return rst.getLong(1);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsById(ReturnPK pk) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT isbn FROM `Return` WHERE issue_id=? AND isbn=?");
            stm.setInt(1, pk.getIssueId());
            stm.setString(2, pk.getIsbn());
            ResultSet rst = stm.executeQuery();
            return rst.next();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(ReturnPK pk) throws ConstraintViolationException {
        try {
            PreparedStatement stm = connection.prepareStatement("DELETE FROM `Return` WHERE isbn = ? AND issue_id = ?");
            stm.setString(1, pk.getIsbn());
            stm.setInt(2, pk.getIssueId());
            stm.executeUpdate();
        }
        catch (SQLException e) {
            if (existsById(pk)) throw new ConstraintViolationException("Return note primary key still exists within other tables", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Return> findAll() {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM `Return`");
            ResultSet rst = stm.executeQuery();
            List<Return> returnList = new ArrayList<>();
            while (rst.next()) {
                Date date = rst.getDate("date");
                int issueId = rst.getInt("issue_id");
                String isbn = rst.getString("isbn");
                Return aReturn = new Return(date, issueId, isbn);
                returnList.add(aReturn);
            }
            return returnList;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Return> findById(ReturnPK pk) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM `Return` WHERE issue_id = ? AND isbn = ?");
            stm.setInt(1, pk.getIssueId());
            stm.setString(2, pk.getIsbn());
            ResultSet rst = stm.executeQuery();
            if (rst.next()) {
                Date date = rst.getDate("date");
                int issueId = rst.getInt("issue_id");
                String isbn = rst.getString("isbn");
                Return aReturn = new Return(date, issueId, isbn);
                return Optional.of(aReturn);
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
    public Return save(Return aReturn) throws ConstraintViolationException {
        try {
            PreparedStatement stm = connection.prepareStatement("INSERT INTO `Return` (date, issue_id, isbn) VALUES (?, ?, ?)");
            stm.setDate(1, aReturn.getDate());
            stm.setInt(2, aReturn.getReturnPK().getIssueId());
            stm.setString(3, aReturn.getReturnPK().getIsbn());
            if (stm.executeUpdate() == 1) {
                return aReturn;
            }
            else {
                throw new SQLException("Failed to save the return note");
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Return update(Return aReturn) throws ConstraintViolationException {
        try {
            PreparedStatement stm = connection.prepareStatement("UPDATE `Return` SET date = ? WHERE issue_id=? AND isbn=?");
            stm.setDate(1, aReturn.getDate());
            stm.setInt(2, aReturn.getReturnPK().getIssueId());
            stm.setString(3, aReturn.getReturnPK().getIsbn());
            if (stm.executeUpdate() == 1) {
                return aReturn;
            }
            else {
                throw new SQLException("Failed to update the return note");
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
