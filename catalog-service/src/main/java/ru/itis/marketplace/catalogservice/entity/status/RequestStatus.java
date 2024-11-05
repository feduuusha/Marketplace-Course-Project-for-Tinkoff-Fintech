package ru.itis.marketplace.catalogservice.entity.status;

import java.util.Arrays;

public enum RequestStatus {
    APPROVED,
    REJECTED,
    UNDER_CONSIDERATION;

    public static boolean statusIsValid(String status) {
        String finalStatus = status.toUpperCase();
        return Arrays.stream(RequestStatus.values()).anyMatch((reqStatus) -> reqStatus.name().equals(finalStatus));
    }
}
