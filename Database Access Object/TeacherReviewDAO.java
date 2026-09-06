package com.educore.dao;

import com.educore.model.TeacherReview;
import com.educore.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeacherReviewDAO {

    public boolean insert(TeacherReview review) {
        String sql = "INSERT INTO TEACHER_REVIEW (Student_ID, Teacher_ID, Rating, Comment, Date_Submitted) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, review.getStudentId());
            pstmt.setInt(2, review.getTeacherId());
            pstmt.setInt(3, review.getRating());
            pstmt.setString(4, review.getComment());
            pstmt.setDate(5, review.getDateSubmitted());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        review.setReviewId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int reviewId) {
        String sql = "DELETE FROM TEACHER_REVIEW WHERE Review_ID=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reviewId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<TeacherReview> getByTeacherId(int teacherId) {
        List<TeacherReview> reviews = new ArrayList<>();
        String sql = "SELECT * FROM TEACHER_REVIEW WHERE Teacher_ID=? ORDER BY Date_Submitted DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teacherId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(extractReview(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public List<TeacherReview> getByStudentId(int studentId) {
        List<TeacherReview> reviews = new ArrayList<>();
        String sql = "SELECT * FROM TEACHER_REVIEW WHERE Student_ID=? ORDER BY Date_Submitted DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(extractReview(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public double getAverageRating(int teacherId) {
        String sql = "SELECT AVG(Rating) FROM TEACHER_REVIEW WHERE Teacher_ID=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teacherId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private TeacherReview extractReview(ResultSet rs) throws SQLException {
        TeacherReview review = new TeacherReview();
        review.setReviewId(rs.getInt("Review_ID"));
        review.setStudentId(rs.getInt("Student_ID"));
        review.setTeacherId(rs.getInt("Teacher_ID"));
        review.setRating(rs.getInt("Rating"));
        review.setComment(rs.getString("Comment"));
        review.setDateSubmitted(rs.getDate("Date_Submitted"));
        return review;
    }
}
