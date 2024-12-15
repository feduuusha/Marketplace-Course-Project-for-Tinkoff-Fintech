package ru.itis.marketplace.userservice.controller.payload.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateCartItemPayload(
        @Min(1)
        @NotNull
        Long quantity
) {
}
