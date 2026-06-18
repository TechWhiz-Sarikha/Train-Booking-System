package dao;

import model.Customer;
import util.DBConnection;

import java.sql.*;

public class CustomerDAO {

    // ─── US006 : Register customer ────────────────────────────────────────────
    public boolean register(Customer c) throws SQLException {
        String sql = "INSERT INTO customers (name, email, phone, address, password, is_active) VALUES (?,?,?,?,?,1)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getAddress());
            ps.setString(5, c.getPassword());
            ps.executeUpdate();
            return true;
        }
    }

    // ─── US007 : Update customer details ──────────────────────────────────────
    public boolean update(Customer c) throws SQLException {
        String sql = "UPDATE customers SET name=?, email=?, phone=?, address=? WHERE customer_id=? AND is_active=1";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getAddress());
            ps.setInt   (5, c.getCustomerId());
            return ps.executeUpdate() > 0;
        }
    }

    // ─── US008 : Soft delete (deactivate) ─────────────────────────────────────
    public boolean deactivate(int customerId) throws SQLException {
        String sql = "UPDATE customers SET is_active=0 WHERE customer_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, customerId);
            return ps.executeUpdate() > 0;
        }
    }

    // ─── Find by email ────────────────────────────────────────────────────────
    public Customer findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM customers WHERE email=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    // ─── Find by ID ───────────────────────────────────────────────────────────
    public Customer findById(int id) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customer_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    // ─── Email exists check ───────────────────────────────────────────────────
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM customers WHERE email=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, email);
            return ps.executeQuery().next();
        }
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerId(rs.getInt   ("customer_id"));
        c.setName      (rs.getString("name"));
        c.setEmail     (rs.getString("email"));
        c.setPhone     (rs.getString("phone"));
        c.setAddress   (rs.getString("address"));
        c.setPassword  (rs.getString("password"));
        c.setActive    (rs.getInt   ("is_active") == 1);
        return c;
    }
}
