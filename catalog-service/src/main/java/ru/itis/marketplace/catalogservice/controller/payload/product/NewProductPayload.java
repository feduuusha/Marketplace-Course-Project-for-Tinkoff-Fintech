package ru.itis.marketplace.catalogservice.controller.payload.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

public record NewProductPayload (
        @NotBlank
        @Length(max = 255)
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
        Long brandId
) {
}
