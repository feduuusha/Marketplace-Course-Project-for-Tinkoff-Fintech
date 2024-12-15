package ru.itis.marketplace.userservice.controller.payload.user_brand;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record NewUserBrandPayload(
        @NotNull
        @Positive
        Long brandId
) {
}
