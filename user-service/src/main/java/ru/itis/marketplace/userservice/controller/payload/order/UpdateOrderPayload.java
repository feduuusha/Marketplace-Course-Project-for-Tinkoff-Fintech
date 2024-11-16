package ru.itis.marketplace.userservice.controller.payload.order;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record UpdateOrderPayload(
        @NotNull
        @Length(max = 255)
        String country,
        @NotNull
        @Length(max = 255)
        String region,
        @NotNull
        @Length(max = 255)
        String locality,
        @NotNull
        @Length(max = 255)
        String postalCode,
        @NotNull
        @Length(max = 255)
        String street,
        @NotNull
        @Length(max = 255)
        String houseNumber,
        String description
) {
}
