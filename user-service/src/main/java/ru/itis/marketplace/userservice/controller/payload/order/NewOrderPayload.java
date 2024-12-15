package ru.itis.marketplace.userservice.controller.payload.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public record NewOrderPayload (
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
        String description,
        @NotNull
        @Size(min = 1)
        List<NewOrderItemPayload> orderItems
) {
}
