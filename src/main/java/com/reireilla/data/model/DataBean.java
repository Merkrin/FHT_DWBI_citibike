package com.reireilla.data.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;

@Getter
@Setter
@ToString
public class DataBean {
    @CsvBindByName(column = "tripduration")
    private int duration;

    @CsvBindByName(column = "starttime")
    private String startDateTime;

    @CsvBindByName(column = "stoptime")
    private String stopDateTime;

    @CsvBindByName(column = "start station id")
    private int startStationId;

    @CsvBindByName(column = "start station name")
    private String startStationName;

    @CsvBindByName(column = "end station id")
    private int endStationId;

    @CsvBindByName(column = "end station name")
    private String endStationName;

    @CsvBindByName(column = "usertype")
    private String userType;

    @CsvBindByName(column = "birth year")
    private String stringBirthYear;

    @CsvBindByName(column = "gender")
    private int gender;

    private Date startDate;
    private Date endDate;

    private int birthYear;
}
