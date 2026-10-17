package com.smarthire.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/smarthire?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "asa7053";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found. Add the MySQL connector dependency.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = System.getenv("DB_URL");
        if (url == null || url.isBlank()) {
            url = System.getenv("SMARTHIRE_DB_URL");
        }
        if (url == null || url.isBlank()) {
            url = DEFAULT_URL;
        }

        String user = System.getenv("DB_USER");
        if (user == null || user.isBlank()) {
            user = System.getenv("SMARTHIRE_DB_USER");
        }
        if (user == null || user.isBlank()) {
            user = DEFAULT_USER;
        }

        String password = System.getenv("DB_PASSWORD");
        if (password == null || password.isBlank()) {
            password = System.getenv("SMARTHIRE_DB_PASSWORD");
        }
        if (password == null || password.isBlank()) {
            password = DEFAULT_PASSWORD;
        }

        return DriverManager.getConnection(url, user, password);
    }
}
