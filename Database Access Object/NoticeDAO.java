package com.educore.dao;

import com.educore.model.Notice;
import com.educore.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoticeDAO {

    public boolean insert(Notice notice) {
        String sql = "INSERT INTO NOTICE (Title, Content, Date_Posted, Target_Role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, notice.getTitle());
            pstmt.setString(2, notice.getContent());
            pstmt.setDate(3, notice.getDatePosted());
            pstmt.setString(4, notice.getTargetRole());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        notice.setNoticeId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Notice notice) {
        String sql = "UPDATE NOTICE SET Title=?, Content=?, Date_Posted=?, Target_Role=? WHERE Notice_ID=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, notice.getTitle());
            pstmt.setString(2, notice.getContent());
            pstmt.setDate(3, notice.getDatePosted());
            pstmt.setString(4, notice.getTargetRole());
            pstmt.setInt(5, notice.getNoticeId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int noticeId) {
        String sql = "DELETE FROM NOTICE WHERE Notice_ID=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, noticeId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Notice> getAll() {
        List<Notice> notices = new ArrayList<>();
        String sql = "SELECT * FROM NOTICE ORDER BY Date_Posted DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                notices.add(extractNotice(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notices;
    }

    public List<Notice> getByTargetRole(String role) {
        List<Notice> notices = new ArrayList<>();
        String sql = "SELECT * FROM NOTICE WHERE Target_Role=? OR Target_Role='All' ORDER BY Date_Posted DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, role);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notices.add(extractNotice(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notices;
    }

    private Notice extractNotice(ResultSet rs) throws SQLException {
        Notice notice = new Notice();
        notice.setNoticeId(rs.getInt("Notice_ID"));
        notice.setTitle(rs.getString("Title"));
        notice.setContent(rs.getString("Content"));
        notice.setDatePosted(rs.getDate("Date_Posted"));
        notice.setTargetRole(rs.getString("Target_Role"));
        return notice;
    }
}
