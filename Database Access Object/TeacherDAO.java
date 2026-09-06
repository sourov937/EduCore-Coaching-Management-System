package com.educore.dao;

import com.educore.model.Teacher;
import com.educore.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeacherDAO {

    public boolean insert(Teacher teacher) {
        String sql = "INSERT INTO TEACHER (Name, Subject, Phone, Email, Password) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, teacher.getName());
            pstmt.setString(2, teacher.getSubject());
            pstmt.setString(3, teacher.getPhone());
            pstmt.setString(4, teacher.getEmail());
            pstmt.setString(5, teacher.getPassword());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        teacher.setTeacherId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Teacher teacher) {
        String sql = "UPDATE TEACHER SET Name=?, Subject=?, Phone=?, Email=?, Password=? WHERE Teacher_ID=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, teacher.getName());
            pstmt.setString(2, teacher.getSubject());
            pstmt.setString(3, teacher.getPhone());
            pstmt.setString(4, teacher.getEmail());
            pstmt.setString(5, teacher.getPassword());
            pstmt.setInt(6, teacher.getTeacherId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int teacherId) {
        String sql = "DELETE FROM TEACHER WHERE Teacher_ID=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teacherId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Teacher getById(int teacherId) {
        String sql = "SELECT * FROM TEACHER WHERE Teacher_ID=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teacherId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractTeacher(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Teacher authenticate(String email, String password) {
        String sql = "SELECT * FROM TEACHER WHERE Email=? AND Password=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractTeacher(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Teacher> getAll() {
        List<Teacher> teachers = new ArrayList<>();
        String sql = "SELECT * FROM TEACHER ORDER BY Name";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                teachers.add(extractTeacher(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teachers;
    }

    private Teacher extractTeacher(ResultSet rs) throws SQLException {
        Teacher teacher = new Teacher();
        teacher.setTeacherId(rs.getInt("Teacher_ID"));
        teacher.setName(rs.getString("Name"));
        teacher.setSubject(rs.getString("Subject"));
        teacher.setPhone(rs.getString("Phone"));
        teacher.setEmail(rs.getString("Email"));
        teacher.setPassword(rs.getString("Password"));
        return teacher;
    }
}
