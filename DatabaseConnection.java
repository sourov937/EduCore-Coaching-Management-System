package com.educore.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    
    private static final String URL = "jdbc:mysql://localhost:3307/educore?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String USER = "root"; 
    private static final String PASSWORD = ""; 

    private static DatabaseConnection instance;

    private DatabaseConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.err.println("Database Connection Creation Failed : " + ex.getMessage());
        }
    }

    /**
     * Returns a NEW connection each time. Callers are responsible for closing it.
     * This is safe for use with try-with-resources in DAO methods.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
}
