package com.smarthire.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for the database connection.
 *
 * <p><strong>Skipped in CI by default.</strong> To run this test locally,
 * ensure MySQL is running and set the following environment variables:</p>
 * <ul>
 *   <li>{@code SMARTHIRE_DB_URL}      – e.g. {@code jdbc:mysql://localhost:3306/smarthire}</li>
 *   <li>{@code SMARTHIRE_DB_USER}     – MySQL username</li>
 *   <li>{@code SMARTHIRE_DB_PASSWORD} – MySQL password</li>
 * </ul>
 */
@Disabled("Requires a running MySQL instance. Set SMARTHIRE_DB_URL, SMARTHIRE_DB_USER, " +
          "SMARTHIRE_DB_PASSWORD and remove @Disabled to run locally.")
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
