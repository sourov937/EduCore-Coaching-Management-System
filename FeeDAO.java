package com.educore.dao;

import com.educore.model.Fee;
import com.educore.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeeDAO {

    public boolean insert(Fee fee) {
        String sql = "INSERT INTO FEE (Student_ID, Amount, Due_Date, Payment_Date, Status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, fee.getStudentId());
            pstmt.setDouble(2, fee.getAmount());
            pstmt.setDate(3, fee.getDueDate());
            pstmt.setDate(4, fee.getPaymentDate());
            pstmt.setString(5, fee.getStatus());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        fee.setFeeId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Fee fee) {
        String sql = "UPDATE FEE SET Student_ID=?, Amount=?, Due_Date=?, Payment_Date=?, Status=? WHERE Fee_ID=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, fee.getStudentId());
            pstmt.setDouble(2, fee.getAmount());
            pstmt.setDate(3, fee.getDueDate());
            pstmt.setDate(4, fee.getPaymentDate());
            pstmt.setString(5, fee.getStatus());
            pstmt.setInt(6, fee.getFeeId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int feeId) {
        String sql = "DELETE FROM FEE WHERE Fee_ID=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, feeId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Fee> getAll() {
        List<Fee> fees = new ArrayList<>();
        String sql = "SELECT * FROM FEE ORDER BY Due_Date DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                fees.add(extractFee(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fees;
    }

    public List<Fee> getByStudentId(int studentId) {
        List<Fee> fees = new ArrayList<>();
        String sql = "SELECT * FROM FEE WHERE Student_ID=? ORDER BY Due_Date DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    fees.add(extractFee(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fees;
    }

    public List<Fee> getByStatus(String status) {
        List<Fee> fees = new ArrayList<>();
        String sql = "SELECT * FROM FEE WHERE Status=? ORDER BY Due_Date DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    fees.add(extractFee(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fees;
    }

    public double getTotalCollected() {
        String sql = "SELECT COALESCE(SUM(Amount), 0) FROM FEE WHERE Status='Paid'";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Fee extractFee(ResultSet rs) throws SQLException {
        Fee fee = new Fee();
        fee.setFeeId(rs.getInt("Fee_ID"));
        fee.setStudentId(rs.getInt("Student_ID"));
        fee.setAmount(rs.getDouble("Amount"));
        fee.setDueDate(rs.getDate("Due_Date"));
        fee.setPaymentDate(rs.getDate("Payment_Date"));
        fee.setStatus(rs.getString("Status"));
        return fee;
    }
}
