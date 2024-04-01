package com.reireilla.data.processor;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.reireilla.data.exceptions.ProcessedDataFormatException;
import com.reireilla.data.model.DataBean;
import com.reireilla.data.model.UpdateBean;
import com.reireilla.database.DatabaseProcessor;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

public class DataProcessor {
    private static final Logger logger = LogManager.getLogger(DataProcessor.class);

    @Getter
    private final List<Path> filePaths;

    private final Pattern sqlDatePattern = Pattern.compile("^\\d{4}-\\d{1,2}-\\d{1,2}$");
    private final Pattern americanDatePattern = Pattern.compile("^\\d{1,2}/\\d{1,2}/\\d{4}$");

    public DataProcessor(List<Path> filePaths) {
        this.filePaths = filePaths;
    }

    public int getFilePathsAmount(){
        return CollectionUtils.size(filePaths);
    }

    public boolean areFilePathsFound(){
        return CollectionUtils.isNotEmpty(filePaths);
    }

    public void loadAndProcessData(Connection connection) {
        for (Path filePath : filePaths) {
            try (Reader reader = Files.newBufferedReader(filePath)) {
                CsvToBean<DataBean> csvBean = new CsvToBeanBuilder<DataBean>(reader).withType(DataBean.class).build();

                List<DataBean> dataBeans = csvBean.parse();
                logger.debug("{} data beans were found in file {}.", dataBeans.size(), filePath);

                for (DataBean dataBean : dataBeans) {
                    try {
                        processDataLine(dataBean, connection);
                    } catch (ProcessedDataFormatException e) {
                        logger.error("Data format violation for data bean {}", dataBean);
                    }
                }

                DatabaseProcessor.runFinalTransformation(connection);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void loadAndProcessUpdates(Connection connection) {
        for (Path filePath : filePaths) {
            try (Reader reader = Files.newBufferedReader(filePath)) {
                CsvToBean<UpdateBean> csvBean = new CsvToBeanBuilder<UpdateBean>(reader).withType(UpdateBean.class)
                        .build();

                List<UpdateBean> updateBeans = csvBean.parse();
                logger.debug("{} data beans were found in file {}.", updateBeans.size(), filePath);

                for (UpdateBean updateBean : updateBeans) {
                    DatabaseProcessor.updateStationByUpdateBean(updateBean, connection);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void processDataLine(DataBean dataBean, Connection connection) throws ProcessedDataFormatException {
        dataBean.setStartDate(
                parseDate(dataBean.getStartDateTime().substring(0, dataBean.getStartDateTime().indexOf(" "))));
        dataBean.setEndDate(
                parseDate(dataBean.getStopDateTime().substring(0, dataBean.getStopDateTime().indexOf(" "))));

        dataBean.setBirthYear(parseBirthYear(dataBean.getStringBirthYear()));

        DatabaseProcessor.insertDataBean(dataBean, connection);
    }

    private Date parseDate(String dateTime) throws ProcessedDataFormatException {
        int day;
        int month;
        int year;

        if (sqlDatePattern.matcher(dateTime).find()) {
            int fistDividerIndex = dateTime.indexOf("-");
            int lastDividerIndex = dateTime.lastIndexOf("-");

            day = Integer.parseInt(dateTime.substring(lastDividerIndex + 1));
            month = Integer.parseInt(dateTime.substring(fistDividerIndex + 1, lastDividerIndex));
            year = Integer.parseInt(dateTime.substring(0, fistDividerIndex));
        } else if (americanDatePattern.matcher(dateTime).find()) {
            int fistDividerIndex = dateTime.indexOf("/");
            int lastDividerIndex = dateTime.lastIndexOf("/");

            day = Integer.parseInt(dateTime.substring(lastDividerIndex + 1));
            month = Integer.parseInt(dateTime.substring(0, fistDividerIndex));
            year = Integer.parseInt(dateTime.substring(fistDividerIndex + 1, lastDividerIndex));
        } else {
            throw new ProcessedDataFormatException();
        }

        return Date.valueOf(LocalDate.of(day, month, year));
    }

    private int parseBirthYear(String stringBirthYear) {
        int result;

        try {
            result = Integer.parseInt(stringBirthYear);
        } catch (NumberFormatException e) {
            result = -1;
        }

        return result;
    }
}
