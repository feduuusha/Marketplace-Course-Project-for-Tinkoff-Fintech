package ru.itis.marketplace.catalogservice.controller.payload.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateProductPayload(
        @NotBlank
        @Size(max = 255)
        String name,
        @NotNull
        @Positive
        BigDecimal price,
        @NotBlank
        String description,
        @NotNull
        @Positive
        Long categoryId,
        @NotNull
        @Positive
        Long brandId,
        @NotBlank
        @Size(max = 255)
        String requestStatus
) {
}
