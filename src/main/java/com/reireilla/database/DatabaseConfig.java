package com.reireilla.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private static final String DB_PROPERTIES_NAME = "db.properties";

    private static final String DB_URL_PROPERTY = "db.url";
    private static final String DB_USERNAME_PROPERTY = "db.username";
    private static final String DB_PASSWORD_PROPERTY = "db.password";

    private static final Properties PROPERTIES = new Properties();

    public static void loadConfig() {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream(DB_PROPERTIES_NAME)) {
            if (input == null) {
                throw new RuntimeException("No database properties file found");
            }

            // Load the properties file
            PROPERTIES.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getDbUrl() {
        return PROPERTIES.getProperty(DB_URL_PROPERTY);
    }

    public static String getDbUsername() {
        return PROPERTIES.getProperty(DB_USERNAME_PROPERTY);
    }

    public static String getDbPassword() {
        return PROPERTIES.getProperty(DB_PASSWORD_PROPERTY);
    }
}
