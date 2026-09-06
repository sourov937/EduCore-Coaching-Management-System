package com.educore.dao;

import com.educore.model.Director;
import com.educore.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DirectorDAO {

    /**
     * Authenticates a Director by email and password.
     * @param email The director's email
     * @param password The director's password
     * @return Director object if authentication is successful, null otherwise.
     */
    public Director authenticate(String email, String password) {
        String sql = "SELECT * FROM DIRECTOR WHERE Email = ? AND Password = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Director director = new Director();
                    director.setDirectorId(rs.getInt("Director_ID"));
                    director.setName(rs.getString("Name"));
                    director.setEmail(rs.getString("Email"));
                    director.setPhone(rs.getString("Phone"));
                    director.setPassword(rs.getString("Password"));
                    return director;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
