package com.educore.dao;

import com.educore.model.Exam;
import com.educore.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExamDAO {

    public boolean insert(Exam exam) {
        String sql = "INSERT INTO EXAM (Exam_Name, Subject, Exam_Date, Total_Marks, Batch_ID) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, exam.getExamName());
            pstmt.setString(2, exam.getSubject());
            pstmt.setDate(3, exam.getExamDate());
            pstmt.setDouble(4, exam.getTotalMarks());
            if (exam.getBatchId() != null) {
                pstmt.setInt(5, exam.getBatchId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        exam.setExamId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Exam exam) {
        String sql = "UPDATE EXAM SET Exam_Name=?, Subject=?, Exam_Date=?, Total_Marks=?, Batch_ID=? WHERE Exam_ID=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, exam.getExamName());
            pstmt.setString(2, exam.getSubject());
            pstmt.setDate(3, exam.getExamDate());
            pstmt.setDouble(4, exam.getTotalMarks());
            if (exam.getBatchId() != null) {
                pstmt.setInt(5, exam.getBatchId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            pstmt.setInt(6, exam.getExamId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int examId) {
        String sql = "DELETE FROM EXAM WHERE Exam_ID=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, examId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Exam getById(int examId) {
        String sql = "SELECT * FROM EXAM WHERE Exam_ID=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, examId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractExam(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Exam> getAll() {
        List<Exam> exams = new ArrayList<>();
        String sql = "SELECT * FROM EXAM ORDER BY Exam_Date DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                exams.add(extractExam(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exams;
    }

    public List<Exam> getByBatchId(int batchId) {
        List<Exam> exams = new ArrayList<>();
        String sql = "SELECT * FROM EXAM WHERE Batch_ID=? ORDER BY Exam_Date DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, batchId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    exams.add(extractExam(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exams;
    }

    private Exam extractExam(ResultSet rs) throws SQLException {
        Exam exam = new Exam();
        exam.setExamId(rs.getInt("Exam_ID"));
        exam.setExamName(rs.getString("Exam_Name"));
        exam.setSubject(rs.getString("Subject"));
        exam.setExamDate(rs.getDate("Exam_Date"));
        exam.setTotalMarks(rs.getDouble("Total_Marks"));
        int batchId = rs.getInt("Batch_ID");
        exam.setBatchId(rs.wasNull() ? null : batchId);
        return exam;
    }
}
