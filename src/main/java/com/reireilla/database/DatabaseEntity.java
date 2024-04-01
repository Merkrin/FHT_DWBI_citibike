package com.reireilla.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseEntity {
    public static Connection connect() throws SQLException {
        String jdbcUrl = DatabaseConfig.getDbUrl();
        String user = DatabaseConfig.getDbUsername();
        String password = DatabaseConfig.getDbPassword();

        // Open connection.
        return DriverManager.getConnection(jdbcUrl, user, password);
    }
}
