package com.educore.dao;

import com.educore.model.Attendance;
import com.educore.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    public boolean insert(Attendance attendance) {
        String sql = "INSERT INTO ATTENDANCE (Student_ID, Batch_ID, Date, Status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, attendance.getStudentId());
            pstmt.setInt(2, attendance.getBatchId());
            pstmt.setDate(3, attendance.getDate());
            pstmt.setString(4, attendance.getStatus());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        attendance.setAttendanceId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Attendance attendance) {
        String sql = "UPDATE ATTENDANCE SET Student_ID=?, Batch_ID=?, Date=?, Status=? WHERE Attendance_ID=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, attendance.getStudentId());
            pstmt.setInt(2, attendance.getBatchId());
            pstmt.setDate(3, attendance.getDate());
            pstmt.setString(4, attendance.getStatus());
            pstmt.setInt(5, attendance.getAttendanceId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int attendanceId) {
        String sql = "DELETE FROM ATTENDANCE WHERE Attendance_ID=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, attendanceId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Attendance> getByStudentId(int studentId) {
        List<Attendance> records = new ArrayList<>();
        String sql = "SELECT * FROM ATTENDANCE WHERE Student_ID=? ORDER BY Date DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    records.add(extractAttendance(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    public List<Attendance> getByBatchAndDate(int batchId, Date date) {
        List<Attendance> records = new ArrayList<>();
        String sql = "SELECT * FROM ATTENDANCE WHERE Batch_ID=? AND Date=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, batchId);
            pstmt.setDate(2, date);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    records.add(extractAttendance(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    public List<Attendance> getByBatchId(int batchId) {
        List<Attendance> records = new ArrayList<>();
        String sql = "SELECT * FROM ATTENDANCE WHERE Batch_ID=? ORDER BY Date DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, batchId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    records.add(extractAttendance(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    private Attendance extractAttendance(ResultSet rs) throws SQLException {
        Attendance attendance = new Attendance();
        attendance.setAttendanceId(rs.getInt("Attendance_ID"));
        attendance.setStudentId(rs.getInt("Student_ID"));
        attendance.setBatchId(rs.getInt("Batch_ID"));
        attendance.setDate(rs.getDate("Date"));
        attendance.setStatus(rs.getString("Status"));
        return attendance;
    }
}
