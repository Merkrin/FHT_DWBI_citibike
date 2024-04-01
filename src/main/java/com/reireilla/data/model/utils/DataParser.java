package com.reireilla.data.model.utils;

import com.reireilla.data.exceptions.ProcessedDataFormatException;
import lombok.experimental.UtilityClass;

import java.sql.Date;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.regex.Pattern;

@UtilityClass
public class DataParser {
    private final Pattern sqlDatePattern = Pattern.compile("^\\d{4}-\\d{1,2}-\\d{1,2}$");
    private final Pattern americanDatePattern = Pattern.compile("^\\d{1,2}/\\d{1,2}/\\d{4}$");

    public static Date parseDate(String dateTime) throws ProcessedDataFormatException {
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

        try {
            return Date.valueOf(LocalDate.of(year, month, day));
        } catch (DateTimeException e) {
            throw new ProcessedDataFormatException();
        }
    }

    public static int parseBirthYear(String stringBirthYear) {
        int result;

        try {
            result = Integer.parseInt(stringBirthYear);
        } catch (NumberFormatException e) {
            result = -1;
        }

        return result;
    }
}
