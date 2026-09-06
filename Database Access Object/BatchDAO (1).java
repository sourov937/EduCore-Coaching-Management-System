package com.educore.dao;

import com.educore.model.Batch;
import com.educore.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BatchDAO {

    public boolean insert(Batch batch) {
        String sql = "INSERT INTO BATCH (Batch_Name, Subject, Schedule, Teacher_ID) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, batch.getBatchName());
            pstmt.setString(2, batch.getSubject());
            pstmt.setString(3, batch.getSchedule());
            if (batch.getTeacherId() != null) {
                pstmt.setInt(4, batch.getTeacherId());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        batch.setBatchId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Batch batch) {
        String sql = "UPDATE BATCH SET Batch_Name=?, Subject=?, Schedule=?, Teacher_ID=? WHERE Batch_ID=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, batch.getBatchName());
            pstmt.setString(2, batch.getSubject());
            pstmt.setString(3, batch.getSchedule());
            if (batch.getTeacherId() != null) {
                pstmt.setInt(4, batch.getTeacherId());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            pstmt.setInt(5, batch.getBatchId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int batchId) {
        String sql = "DELETE FROM BATCH WHERE Batch_ID=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, batchId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Batch getById(int batchId) {
        String sql = "SELECT * FROM BATCH WHERE Batch_ID=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, batchId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractBatch(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Batch> getAll() {
        List<Batch> batches = new ArrayList<>();
        String sql = "SELECT * FROM BATCH ORDER BY Batch_Name";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                batches.add(extractBatch(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return batches;
    }

    public List<Batch> getByTeacherId(int teacherId) {
        List<Batch> batches = new ArrayList<>();
        String sql = "SELECT * FROM BATCH WHERE Teacher_ID=? ORDER BY Batch_Name";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teacherId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    batches.add(extractBatch(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return batches;
    }

    public int getCount() {
        String sql = "SELECT COUNT(*) FROM BATCH";
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

    private Batch extractBatch(ResultSet rs) throws SQLException {
        Batch batch = new Batch();
        batch.setBatchId(rs.getInt("Batch_ID"));
        batch.setBatchName(rs.getString("Batch_Name"));
        batch.setSubject(rs.getString("Subject"));
        batch.setSchedule(rs.getString("Schedule"));
        int teacherId = rs.getInt("Teacher_ID");
        batch.setTeacherId(rs.wasNull() ? null : teacherId);
        return batch;
    }
}
