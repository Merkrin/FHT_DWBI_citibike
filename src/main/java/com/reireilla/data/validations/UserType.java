package com.reireilla.data.validations;

import java.util.Arrays;

public enum UserType {
    SUBSCRIBER,
    CUSTOMER;

    public static boolean isUserTypeContained(String userType) {
        return Arrays.stream(UserType.values()).anyMatch(e -> e.name().equalsIgnoreCase(userType));
    }
}
