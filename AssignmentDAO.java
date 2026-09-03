package com.educore.dao;

import com.educore.model.Assignment;
import com.educore.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssignmentDAO {

    public boolean insert(Assignment assignment) {
        String sql = "INSERT INTO ASSIGNMENT (Title, Description, Due_Date, Batch_ID, Teacher_ID) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, assignment.getTitle());
            pstmt.setString(2, assignment.getDescription());
            pstmt.setDate(3, assignment.getDueDate());
            pstmt.setInt(4, assignment.getBatchId());
            if (assignment.getTeacherId() != null) {
                pstmt.setInt(5, assignment.getTeacherId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        assignment.setAssignmentId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Assignment assignment) {
        String sql = "UPDATE ASSIGNMENT SET Title=?, Description=?, Due_Date=?, Batch_ID=?, Teacher_ID=? WHERE Assignment_ID=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, assignment.getTitle());
            pstmt.setString(2, assignment.getDescription());
            pstmt.setDate(3, assignment.getDueDate());
            pstmt.setInt(4, assignment.getBatchId());
            if (assignment.getTeacherId() != null) {
                pstmt.setInt(5, assignment.getTeacherId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            pstmt.setInt(6, assignment.getAssignmentId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int assignmentId) {
        String sql = "DELETE FROM ASSIGNMENT WHERE Assignment_ID=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, assignmentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Assignment> getAll() {
        List<Assignment> assignments = new ArrayList<>();
        String sql = "SELECT * FROM ASSIGNMENT ORDER BY Due_Date DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                assignments.add(extractAssignment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assignments;
    }

    public List<Assignment> getByBatchId(int batchId) {
        List<Assignment> assignments = new ArrayList<>();
        String sql = "SELECT * FROM ASSIGNMENT WHERE Batch_ID=? ORDER BY Due_Date DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, batchId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    assignments.add(extractAssignment(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assignments;
    }

    public List<Assignment> getByTeacherId(int teacherId) {
        List<Assignment> assignments = new ArrayList<>();
        String sql = "SELECT * FROM ASSIGNMENT WHERE Teacher_ID=? ORDER BY Due_Date DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teacherId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    assignments.add(extractAssignment(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assignments;
    }

    private Assignment extractAssignment(ResultSet rs) throws SQLException {
        Assignment assignment = new Assignment();
        assignment.setAssignmentId(rs.getInt("Assignment_ID"));
        assignment.setTitle(rs.getString("Title"));
        assignment.setDescription(rs.getString("Description"));
        assignment.setDueDate(rs.getDate("Due_Date"));
        assignment.setBatchId(rs.getInt("Batch_ID"));
        int teacherId = rs.getInt("Teacher_ID");
        assignment.setTeacherId(rs.wasNull() ? null : teacherId);
        return assignment;
    }
}
