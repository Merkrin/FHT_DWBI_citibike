package com.reireilla.data.validations;

import com.reireilla.data.exceptions.DataBeanValidationException;
import com.reireilla.data.exceptions.ProcessedDataFormatException;
import com.reireilla.data.model.DataBean;
import com.reireilla.data.model.utils.DataParser;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;

public class DataValidator {
    private static final int MINIMAL_START_YEAR = 2014;
    private static final int MINIMAL_BIRTH_YEAR = 1900;
    private static final int N_A_BIRTH_YEAR = -1;

    public static void validateDataBean(DataBean dataBean) throws DataBeanValidationException {
        boolean isDataBeanCorrect = isTripDurationValid(dataBean.getDuration());

        try {
            isDataBeanCorrect &= areTimesValid(dataBean.getStartDateTime(), dataBean.getStopDateTime());
        } catch (ProcessedDataFormatException e) {
            throw new DataBeanValidationException();
        }

        isDataBeanCorrect &= isStationIdValid(dataBean.getStartStationId());
        isDataBeanCorrect &= isStationIdValid(dataBean.getEndStationId());
        isDataBeanCorrect &= isUserTypeValid(dataBean.getUserType());
        isDataBeanCorrect &= isBirthYearValid(dataBean.getStringBirthYear());
        isDataBeanCorrect &= isGenderValid(dataBean.getGender());

        if (!isDataBeanCorrect) {
            throw new DataBeanValidationException();
        }
    }

    private static boolean isTripDurationValid(int tripDuration) {
        return tripDuration > 0;
    }

    private static boolean areTimesValid(String startTime, String endTime) throws ProcessedDataFormatException {
        Date startDateTime = DataParser.parseDate(startTime);
        Date endDateTime = DataParser.parseDate(endTime);

        return isDateValid(startDateTime) && isDateValid(endDateTime) && !endDateTime.before(startDateTime);
    }

    private static boolean isDateValid(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);

        return MINIMAL_START_YEAR <= year && year <= LocalDate.now().getYear();
    }

    private static boolean isStationIdValid(int stationId) {
        return stationId > 0;
    }

    private static boolean isUserTypeValid(String userType) {
        return UserType.isUserTypeContained(userType);
    }

    private static boolean isBirthYearValid(String stringBirthYear) {
        int birthYear = DataParser.parseBirthYear(stringBirthYear);

        return birthYear == N_A_BIRTH_YEAR || (MINIMAL_BIRTH_YEAR <= birthYear && birthYear <= LocalDate.now()
                .getYear());
    }

    private static boolean isGenderValid(int gender) {
        return Gender.isGenderContained(gender);
    }
}
