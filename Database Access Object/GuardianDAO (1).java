package com.educore.dao;

import com.educore.model.Guardian;
import com.educore.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuardianDAO {

    public boolean insert(Guardian guardian) {
        String sql = "INSERT INTO GUARDIAN (Name, Phone, Email, Password, Address) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, guardian.getName());
            pstmt.setString(2, guardian.getPhone());
            pstmt.setString(3, guardian.getEmail());
            pstmt.setString(4, guardian.getPassword());
            pstmt.setString(5, guardian.getAddress());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        guardian.setGuardianId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Guardian guardian) {
        String sql = "UPDATE GUARDIAN SET Name=?, Phone=?, Email=?, Password=?, Address=? WHERE Guardian_ID=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, guardian.getName());
            pstmt.setString(2, guardian.getPhone());
            pstmt.setString(3, guardian.getEmail());
            pstmt.setString(4, guardian.getPassword());
            pstmt.setString(5, guardian.getAddress());
            pstmt.setInt(6, guardian.getGuardianId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int guardianId) {
        String sql = "DELETE FROM GUARDIAN WHERE Guardian_ID=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, guardianId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Guardian getById(int guardianId) {
        String sql = "SELECT * FROM GUARDIAN WHERE Guardian_ID=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, guardianId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractGuardian(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Guardian authenticate(String email, String password) {
        String sql = "SELECT * FROM GUARDIAN WHERE Email=? AND Password=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractGuardian(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Guardian> getAll() {
        List<Guardian> guardians = new ArrayList<>();
        String sql = "SELECT * FROM GUARDIAN ORDER BY Name";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                guardians.add(extractGuardian(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guardians;
    }

    private Guardian extractGuardian(ResultSet rs) throws SQLException {
        Guardian guardian = new Guardian();
        guardian.setGuardianId(rs.getInt("Guardian_ID"));
        guardian.setName(rs.getString("Name"));
        guardian.setPhone(rs.getString("Phone"));
        guardian.setEmail(rs.getString("Email"));
        guardian.setPassword(rs.getString("Password"));
        guardian.setAddress(rs.getString("Address"));
        return guardian;
    }
}
