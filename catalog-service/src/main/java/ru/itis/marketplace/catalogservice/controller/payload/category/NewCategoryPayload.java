package ru.itis.marketplace.catalogservice.controller.payload.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewCategoryPayload(
        @NotBlank
        @Size(max = 255)
        String name
) {
}
