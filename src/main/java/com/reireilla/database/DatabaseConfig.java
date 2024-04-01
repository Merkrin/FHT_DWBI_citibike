package com.reireilla.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    public static final String DB_URL_PROPERTY = "db.url";
    public static final String DB_USERNAME_PROPERTY = "db.username";
    public static final String DB_PASSWORD_PROPERTY = "db.password";

    private static final Properties properties = new Properties();

    public static void loadConfig() {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("No database properties file found");
            }

            // Load the properties file
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getDbUrl() {
        return properties.getProperty(DB_URL_PROPERTY);
    }

    public static String getDbUsername() {
        return properties.getProperty(DB_USERNAME_PROPERTY);
    }

    public static String getDbPassword() {
        return properties.getProperty(DB_PASSWORD_PROPERTY);
    }
}
