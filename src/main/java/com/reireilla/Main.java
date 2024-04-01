package com.reireilla;

import com.reireilla.data.processor.DataProcessor;
import com.reireilla.data.reader.PathsLoader;
import com.reireilla.database.DatabaseConfig;
import com.reireilla.database.DatabaseEntity;
import com.reireilla.utils.SqlPropertiesConnector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        DatabaseConfig.loadConfig();
        SqlPropertiesConnector.loadConfig();

        try (Connection connection = DatabaseEntity.connect()) {
            DataProcessor dataProcessor = new DataProcessor(
                    PathsLoader.loadAllPathsFromFolderWithExtension("csv", "data"));
            logger.debug("{} files with data were found.", dataProcessor.getFilePathsAmount());

            if (dataProcessor.areFilePathsFound()) {
                dataProcessor.loadAndProcessData(connection);
            }

            dataProcessor = new DataProcessor(
                    PathsLoader.loadAllPathsFromFolderWithExtension("csv", "updates"));
            logger.debug("{} files with updates were found.", dataProcessor.getFilePathsAmount());

            if (dataProcessor.areFilePathsFound()) {
                dataProcessor.loadAndProcessUpdates(connection);
            }
        } catch (SQLException | IOException | URISyntaxException e) {
            logger.error(e);
        }
    }
}
