package ru.itis.marketplace.catalogservice.controller.payload.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewCategoryPayload(
        @NotBlank
        @Size(min = 3, max = 50)
        String name
) {
}
