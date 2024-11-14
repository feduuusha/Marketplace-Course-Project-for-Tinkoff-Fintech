package ru.itis.marketplace.userservice.controller.payload.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record NewCartItemPayload(
        @NotNull
        @Positive
        Long productId,
        @NotNull
        @Positive
        Long sizeId,
        @NotNull
        @Min(1)
        Long quantity
) {
}
