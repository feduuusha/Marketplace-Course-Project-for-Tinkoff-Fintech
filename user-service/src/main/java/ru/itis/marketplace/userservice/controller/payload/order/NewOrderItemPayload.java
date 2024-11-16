package ru.itis.marketplace.userservice.controller.payload.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record NewOrderItemPayload (
        @NotNull
        @Positive
        Long productId,
        @NotNull
        @Positive
        Long productSizeId,
        @Positive
        Long quantity
) {
}
