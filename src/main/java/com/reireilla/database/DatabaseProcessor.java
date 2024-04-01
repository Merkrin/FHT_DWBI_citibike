package com.reireilla.database;

import com.reireilla.data.model.DataBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class DatabaseProcessor {
    private static final String USER_INSERT_SQL = "INSERT INTO users(gender, user_type, birth_year) VALUES(?, ?, ?) ON CONFLICT DO NOTHING";
    private static final String STATION_INSERT_SQL = "INSERT INTO stations(station_id, station_name) VALUES(?, ?) ON CONFLICT DO NOTHING";
    private static final String TRIP_INSERT_SQL = "INSERT INTO trips(start_date, end_date, duration, start_station, end_station, bike_user) VALUES(?, ?, ?, ?, ?, ?) ON CONFLICT DO NOTHING";

    private static final String TRANSFORMATION_SCRIPT_PATH = "sql/script.sql";

    private static final Logger logger = LogManager.getLogger(DatabaseProcessor.class);

    public static void insertDataBean(DataBean dataBean, Connection connection) {
        logger.debug("Started inserting {}...", dataBean);

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(USER_INSERT_SQL,
                    Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setInt(1, dataBean.getGender());
            preparedStatement.setString(2, dataBean.getUserType());
            preparedStatement.setInt(3, dataBean.getBirthYear());

            preparedStatement.executeUpdate();

            int userId = 0;

            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                userId = resultSet.getInt(1);
            }

            preparedStatement = connection.prepareStatement(STATION_INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, dataBean.getStartStationId());
            preparedStatement.setString(2, dataBean.getStartStationName());
            preparedStatement.executeUpdate();

            preparedStatement.setInt(1, dataBean.getEndStationId());
            preparedStatement.setString(2, dataBean.getEndStationName());
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement(TRIP_INSERT_SQL, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setDate(1, dataBean.getStartDate());
            preparedStatement.setDate(2, dataBean.getEndDate());
            preparedStatement.setInt(3, dataBean.getDuration());
            preparedStatement.setInt(4, dataBean.getStartStationId());
            preparedStatement.setInt(5, dataBean.getEndStationId());
            preparedStatement.setInt(6, userId);

            preparedStatement.executeUpdate();

            logger.debug("Ended inserting {}.", dataBean);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void runFinalTransformation(Connection connection) {
        ScriptExecutor.executeFile(TRANSFORMATION_SCRIPT_PATH, connection);
    }
}
