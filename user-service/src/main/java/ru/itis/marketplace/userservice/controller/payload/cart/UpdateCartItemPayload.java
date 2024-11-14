package ru.itis.marketplace.userservice.controller.payload.cart;

import jakarta.validation.constraints.Min;

public record UpdateCartItemPayload(
        @Min(1)
        Long quantity
) {
}
