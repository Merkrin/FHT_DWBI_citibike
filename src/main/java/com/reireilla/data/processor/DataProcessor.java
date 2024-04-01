package com.reireilla.data.processor;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.reireilla.data.exceptions.DataBeanValidationException;
import com.reireilla.data.exceptions.ProcessedDataFormatException;
import com.reireilla.data.model.DataBean;
import com.reireilla.data.model.UpdateBean;
import com.reireilla.data.model.utils.DataParser;
import com.reireilla.data.validations.DataValidator;
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
import java.util.List;

public class DataProcessor {
    private static final Logger logger = LogManager.getLogger(DataProcessor.class);

    @Getter
    private final List<Path> filePaths;

    public DataProcessor(List<Path> filePaths) {
        this.filePaths = filePaths;
    }

    public int getFilePathsAmount() {
        return CollectionUtils.size(filePaths);
    }

    public boolean areFilePathsFound() {
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
                        DataValidator.validateDataBean(dataBean);
                        processDataLine(dataBean, connection);
                    } catch (ProcessedDataFormatException e) {
                        logger.error("Data format violation for data bean {}", dataBean);
                    } catch (DataBeanValidationException e) {
                        logger.error("Data validation failed for data bean {}", dataBean);
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
                DataParser.parseDate(
                        dataBean.getStartDateTime().substring(0, dataBean.getStartDateTime().indexOf(" "))));
        dataBean.setEndDate(
                DataParser.parseDate(dataBean.getStopDateTime().substring(0, dataBean.getStopDateTime().indexOf(" "))));

        dataBean.setBirthYear(DataParser.parseBirthYear(dataBean.getStringBirthYear()));

        DatabaseProcessor.insertDataBean(dataBean, connection);
    }
}
