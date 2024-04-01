package com.reireilla.database;

import com.reireilla.data.model.DataBean;
import com.reireilla.data.model.UpdateBean;
import com.reireilla.utils.SqlPropertiesConnector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class DatabaseProcessor {
    private static final String STATION_INSERT_SQL_PROPERTY_NAME = "sql.insert.station";
    private static final String USER_INSERT_SQL_PROPERTY_NAME = "sql.insert.user";
    private static final String TRIP_INSERT_SQL_PROPERTY_NAME = "sql.insert.trip";

    private static final String STATION_UPDATE_SQL_PROPERTY_NAME = "sql.update.station";

    private static final String TRANSFORMATION_SCRIPT_PATH_PROPERTY_NAME = "sql.path.script.transformation";

    private static final Logger logger = LogManager.getLogger(DatabaseProcessor.class);

    public static void insertDataBean(DataBean dataBean, Connection connection) {
        logger.debug("Started inserting {}...", dataBean);

        try {
            insertStations(dataBean, connection);

            insertTrip(insertUser(dataBean, connection), dataBean, connection);

            logger.debug("Ended inserting {}.", dataBean);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void runFinalTransformation(Connection connection) {
        ScriptExecutor.executeFile(
                SqlPropertiesConnector.getSqlPropertyByName(TRANSFORMATION_SCRIPT_PATH_PROPERTY_NAME), connection);
    }

    public static void updateStationByUpdateBean(UpdateBean updateBean, Connection connection) {
        logger.debug("Started updating {}...", updateBean);

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    SqlPropertiesConnector.getSqlPropertyByName(STATION_UPDATE_SQL_PROPERTY_NAME),
                    Statement.NO_GENERATED_KEYS);

            preparedStatement.setString(1, updateBean.getNewStationName());
            preparedStatement.setInt(2, updateBean.getNewStationId());
            preparedStatement.setString(3, updateBean.getOldStationName());
            preparedStatement.setInt(4, updateBean.getOldStationId());

            preparedStatement.executeUpdate();

            logger.debug("Ended updating {}.", updateBean);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void insertStations(DataBean dataBean, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SqlPropertiesConnector.getSqlPropertyByName(
                STATION_INSERT_SQL_PROPERTY_NAME), Statement.NO_GENERATED_KEYS);
        preparedStatement.setInt(1, dataBean.getStartStationId());
        preparedStatement.setString(2, dataBean.getStartStationName());
        preparedStatement.executeUpdate();

        preparedStatement.setInt(1, dataBean.getEndStationId());
        preparedStatement.setString(2, dataBean.getEndStationName());
        preparedStatement.executeUpdate();
    }

    private static int insertUser(DataBean dataBean, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SqlPropertiesConnector.getSqlPropertyByName(
                USER_INSERT_SQL_PROPERTY_NAME), Statement.RETURN_GENERATED_KEYS);

        preparedStatement.setInt(1, dataBean.getGender());
        preparedStatement.setString(2, dataBean.getUserType());
        preparedStatement.setInt(3, dataBean.getBirthYear());

        preparedStatement.executeUpdate();

        int userId = 0;

        ResultSet resultSet = preparedStatement.getGeneratedKeys();

        if (resultSet.next()) {
            userId = resultSet.getInt(1);
        }

        return userId;
    }

    private static void insertTrip(int userId, DataBean dataBean, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SqlPropertiesConnector.getSqlPropertyByName(
                TRIP_INSERT_SQL_PROPERTY_NAME), Statement.NO_GENERATED_KEYS);

        preparedStatement.setDate(1, dataBean.getStartDate());
        preparedStatement.setDate(2, dataBean.getEndDate());
        preparedStatement.setInt(3, dataBean.getDuration());
        preparedStatement.setInt(4, dataBean.getStartStationId());
        preparedStatement.setInt(5, dataBean.getEndStationId());
        preparedStatement.setInt(6, userId);

        preparedStatement.executeUpdate();
    }
}
