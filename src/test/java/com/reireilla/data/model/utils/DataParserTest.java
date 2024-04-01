package com.reireilla.data.model.utils;

import com.reireilla.data.exceptions.ProcessedDataFormatException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class DataParserTest {
    @Test
    void testInvalidDate() {
        String invalidDate = "2000-02-31";

        Exception exception = assertThrows(ProcessedDataFormatException.class, () -> DataParser.parseDate(invalidDate));

        Assertions.assertNotNull(exception);
    }
}