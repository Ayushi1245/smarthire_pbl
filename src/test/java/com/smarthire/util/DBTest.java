package com.smarthire.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Database Connection Test")
class DBTest {

    @Test
    @DisplayName("Should successfully connect to MySQL smarthire database and query tables")
    void testConnection() {
        assertDoesNotThrow(() -> {
            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SHOW TABLES;")) {
                assertNotNull(conn);
                assertTrue(rs.next(), "smarthire schema should have tables");
            }
        });
    }
}
