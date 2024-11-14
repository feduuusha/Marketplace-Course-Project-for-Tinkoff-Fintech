package ru.itis.marketplace.userservice.controller.payload.order;

import java.util.List;

public record NewOrderPayload (
        String country,
        String region,
        String locality,
        String postalCode,
        String street,
        String houseNumber,
        String description,
        List<NewOrderItemPayload> orderItems
) {
}
