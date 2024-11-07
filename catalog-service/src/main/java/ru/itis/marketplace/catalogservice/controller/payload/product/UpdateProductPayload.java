package ru.itis.marketplace.catalogservice.controller.payload.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateProductPayload(
        @NotBlank
        @Size(min = 3, max = 150)
        String name,
        @NotNull
        @Positive
        BigDecimal price,
        @NotBlank
        @Size(max = 1000)
        String description,
        @NotNull
        @Positive
        Long categoryId,
        @NotNull
        @Positive
        Long brandId,
        @NotBlank
        String requestStatus
) {
}
