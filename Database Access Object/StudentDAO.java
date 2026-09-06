package com.educore.dao;

import com.educore.model.Student;
import com.educore.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public boolean insert(Student student) {
        String sql = "INSERT INTO STUDENT (Name, DOB, Gender, Phone, Email, Password, Batch_ID, Guardian_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, student.getName());
            if (student.getDob() != null) {
                pstmt.setDate(2, student.getDob());
            } else {
                pstmt.setNull(2, Types.DATE);
            }
            pstmt.setString(3, student.getGender());
            pstmt.setString(4, student.getPhone());
            pstmt.setString(5, student.getEmail());
            pstmt.setString(6, student.getPassword());
            if (student.getBatchId() != null) {
                pstmt.setInt(7, student.getBatchId());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }
            if (student.getGuardianId() != null) {
                pstmt.setInt(8, student.getGuardianId());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        student.setStudentId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Student student) {
        String sql = "UPDATE STUDENT SET Name=?, DOB=?, Gender=?, Phone=?, Email=?, Password=?, Batch_ID=?, Guardian_ID=? WHERE Student_ID=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getName());
            if (student.getDob() != null) {
                pstmt.setDate(2, student.getDob());
            } else {
                pstmt.setNull(2, Types.DATE);
            }
            pstmt.setString(3, student.getGender());
            pstmt.setString(4, student.getPhone());
            pstmt.setString(5, student.getEmail());
            pstmt.setString(6, student.getPassword());
            if (student.getBatchId() != null) {
                pstmt.setInt(7, student.getBatchId());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }
            if (student.getGuardianId() != null) {
                pstmt.setInt(8, student.getGuardianId());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }
            pstmt.setInt(9, student.getStudentId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int studentId) {
        String sql = "DELETE FROM STUDENT WHERE Student_ID=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Student getById(int studentId) {
        String sql = "SELECT * FROM STUDENT WHERE Student_ID=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractStudent(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Student authenticate(String email, String password) {
        String sql = "SELECT * FROM STUDENT WHERE Email=? AND Password=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractStudent(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Student> getAll() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM STUDENT ORDER BY Name";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(extractStudent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public List<Student> getByBatchId(int batchId) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM STUDENT WHERE Batch_ID=? ORDER BY Name";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, batchId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(extractStudent(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public List<Student> getByGuardianId(int guardianId) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM STUDENT WHERE Guardian_ID=? ORDER BY Name";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, guardianId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(extractStudent(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public int getCount() {
        String sql = "SELECT COUNT(*) FROM STUDENT";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Student extractStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getInt("Student_ID"));
        student.setName(rs.getString("Name"));
        student.setDob(rs.getDate("DOB"));
        student.setGender(rs.getString("Gender"));
        student.setPhone(rs.getString("Phone"));
        student.setEmail(rs.getString("Email"));
        student.setPassword(rs.getString("Password"));
        int batchId = rs.getInt("Batch_ID");
        student.setBatchId(rs.wasNull() ? null : batchId);
        int guardianId = rs.getInt("Guardian_ID");
        student.setGuardianId(rs.wasNull() ? null : guardianId);
        return student;
    }
}
