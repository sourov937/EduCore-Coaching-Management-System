package com.educore.dao;

import com.educore.model.Result;
import com.educore.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultDAO {

    public boolean insert(Result result) {
        String sql = "INSERT INTO RESULT (Student_ID, Exam_ID, Marks_Obtained, Grade, Remarks) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, result.getStudentId());
            pstmt.setInt(2, result.getExamId());
            pstmt.setDouble(3, result.getMarksObtained());
            pstmt.setString(4, result.getGrade());
            pstmt.setString(5, result.getRemarks());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        result.setResultId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Result result) {
        String sql = "UPDATE RESULT SET Student_ID=?, Exam_ID=?, Marks_Obtained=?, Grade=?, Remarks=? WHERE Result_ID=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, result.getStudentId());
            pstmt.setInt(2, result.getExamId());
            pstmt.setDouble(3, result.getMarksObtained());
            pstmt.setString(4, result.getGrade());
            pstmt.setString(5, result.getRemarks());
            pstmt.setInt(6, result.getResultId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int resultId) {
        String sql = "DELETE FROM RESULT WHERE Result_ID=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, resultId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Result> getByStudentId(int studentId) {
        List<Result> results = new ArrayList<>();
        String sql = "SELECT * FROM RESULT WHERE Student_ID=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    results.add(extractResult(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public List<Result> getByExamId(int examId) {
        List<Result> results = new ArrayList<>();
        String sql = "SELECT * FROM RESULT WHERE Exam_ID=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, examId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    results.add(extractResult(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public List<Result> getAll() {
        List<Result> results = new ArrayList<>();
        String sql = "SELECT * FROM RESULT";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                results.add(extractResult(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    private Result extractResult(ResultSet rs) throws SQLException {
        Result result = new Result();
        result.setResultId(rs.getInt("Result_ID"));
        result.setStudentId(rs.getInt("Student_ID"));
        result.setExamId(rs.getInt("Exam_ID"));
        result.setMarksObtained(rs.getDouble("Marks_Obtained"));
        result.setGrade(rs.getString("Grade"));
        result.setRemarks(rs.getString("Remarks"));
        return result;
    }
}
