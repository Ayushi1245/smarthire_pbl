package com.smarthire.util;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Backward-compatible wrapper delegating to DBConnection.
 */
public class DatabaseUtil {

    public static Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }
}
