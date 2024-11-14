package ru.itis.marketplace.userservice.controller.payload.order;

public record UpdateOrderPayload(
    String country,
    String locality,
    String region,
    String postalCode,
    String street,
    String houseNumber,
    String description
) {
}
