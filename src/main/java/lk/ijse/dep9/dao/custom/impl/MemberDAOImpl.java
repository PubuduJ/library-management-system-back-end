package lk.ijse.dep9.dao.custom.impl;

import lk.ijse.dep9.dao.custom.MemberDAO;
import lk.ijse.dep9.dao.exception.ConstraintViolationException;
import lk.ijse.dep9.entity.Member;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemberDAOImpl implements MemberDAO {

    private final Connection connection;

    public MemberDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long count() {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT COUNT(id) FROM Member");
            ResultSet rst = stm.executeQuery();
            rst.next();
            return rst.getLong(1);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsById(String pk) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT id FROM Member WHERE id = ?");
            stm.setString(1, pk);
            ResultSet rst = stm.executeQuery();
            return rst.next();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(String pk) throws ConstraintViolationException {
        try {
            PreparedStatement stm = connection.prepareStatement("DELETE FROM Member WHERE id = ?");
            stm.setString(1, pk);
            stm.executeUpdate();
        }
        catch (SQLException e) {
            if (existsById(pk)) throw new ConstraintViolationException("Member ID still exists in other tables", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Member> findAll() {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM Member");
            ResultSet rst = stm.executeQuery();
            List<Member> memberList = new ArrayList<>();
            while (rst.next()) {
                String id = rst.getString("id");
                String name = rst.getString("name");
                String address = rst.getString("address");
                String contact = rst.getString("contact");
                Member member = new Member(id, name, address, contact);
                memberList.add(member);
            }
            return memberList;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Member> findById(String pk) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM Member WHERE id = ?");
            stm.setString(1, pk);
            ResultSet rst = stm.executeQuery();
            if (rst.next()) {
                String name = rst.getString("name");
                String address = rst.getString("address");
                String contact = rst.getString("contact");
                Member member = new Member(pk, name, address, contact);
                return Optional.of(member);
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
    public Member save(Member member) throws ConstraintViolationException {
        try {
            PreparedStatement stm = connection.prepareStatement("INSERT INTO Member (id, name, address, contact) VALUES (?, ?, ?, ?)");
            stm.setString(1, member.getId());
            stm.setString(2, member.getName());
            stm.setString(3, member.getAddress());
            stm.setString(4, member.getContact());
            if (stm.executeUpdate() == 1) {
                return member;
            }
            else {
                throw new SQLException("Failed to save the member");
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Member update(Member member) throws ConstraintViolationException {
        try {
            PreparedStatement stm = connection.prepareStatement("UPDATE Member SET name=?, address=?, contact=? WHERE id=?");
            stm.setString(1, member.getName());
            stm.setString(2, member.getAddress());
            stm.setString(3, member.getContact());
            stm.setString(4, member.getId());
            if (stm.executeUpdate() == 1) {
                return member;
            }
            else {
                throw new SQLException("Failed to update the member");
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Member> findAllMembers(int size, int page) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM Member LIMIT ? OFFSET ?");
            stm.setInt(1, size);
            stm.setInt(2, (page - 1) * size);
            ResultSet rst = stm.executeQuery();
            List<Member> memberList = new ArrayList<>();
            while (rst.next()) {
                String id = rst.getString("id");
                String name = rst.getString("name");
                String address = rst.getString("address");
                String contact = rst.getString("contact");
                Member member = new Member(id, name, address, contact);
                memberList.add(member);
            }
            return memberList;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Member> findMembersByQuery(String query) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM Member WHERE id LIKE ? OR name LIKE ? OR address LIKE ? OR contact LIKE ?");
            query = "%" + query + "%";
            stm.setString(1, query);
            stm.setString(2, query);
            stm.setString(3, query);
            stm.setString(4, query);
            ResultSet rst = stm.executeQuery();
            List<Member> memberList = new ArrayList<>();
            while (rst.next()) {
                String id = rst.getString("id");
                String name = rst.getString("name");
                String address = rst.getString("address");
                String contact = rst.getString("contact");
                Member member = new Member(id, name, address, contact);
                memberList.add(member);
            }
            return memberList;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Member> findMembersByQuery(String query, int size, int page) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM Member WHERE id LIKE ? OR name LIKE ? OR address LIKE ? OR contact LIKE ? LIMIT ? OFFSET ?");
            query = "%" + query + "%";
            stm.setString(1, query);
            stm.setString(2, query);
            stm.setString(3, query);
            stm.setString(4, query);
            stm.setInt(5, size);
            stm.setInt(6, (page - 1) * size);
            ResultSet rst = stm.executeQuery();
            List<Member> memberList = new ArrayList<>();
            while (rst.next()) {
                String id = rst.getString("id");
                String name = rst.getString("name");
                String address = rst.getString("address");
                String contact = rst.getString("contact");
                Member member = new Member(id, name, address, contact);
                memberList.add(member);
            }
            return memberList;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsByContact(String contact) {
        return false;
    }
}
