package com.reireilla.data.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UpdateBean {
    @CsvBindByName(column = "old station id")
    private int oldStationId;

    @CsvBindByName(column = "old station name")
    private String oldStationName;

    @CsvBindByName(column = "new station id")
    private int newStationId;

    @CsvBindByName(column = "new station name")
    private String newStationName;
}
