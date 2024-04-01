package com.reireilla.data.validations;

import java.util.Arrays;

public enum Gender {
    MALE(1),
    FEMALE(2),
    OTHER(0);

    private final int gender;

    public static boolean isGenderContained(int givenGender) {
        return Arrays.stream(Gender.values()).anyMatch(e -> e.gender == givenGender);
    }

    Gender(int gender) {
        this.gender = gender;
    }
}
