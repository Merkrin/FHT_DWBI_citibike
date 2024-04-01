package com.reireilla.utils;

import com.reireilla.database.DatabaseConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SqlPropertiesConnector {
    private static final String SQL_PROPERTIES_NAME = "sql.properties";

    private static final Properties PROPERTIES = new Properties();

    public static void loadConfig() {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream(SQL_PROPERTIES_NAME)) {
            if (input == null) {
                throw new RuntimeException("No sql properties file found");
            }

            // Load the properties file
            PROPERTIES.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getSqlPropertyByName(String name) {
        return PROPERTIES.getProperty(name);
    }
}
