package ru.itis.marketplace.catalogservice.controller.payload.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProductStatusPayload(
        @NotBlank
        @Size(max = 255)
        String requestStatus
) {
}
