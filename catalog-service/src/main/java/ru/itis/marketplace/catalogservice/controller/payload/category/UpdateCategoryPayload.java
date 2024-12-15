package ru.itis.marketplace.catalogservice.controller.payload.category;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UpdateCategoryPayload(
        @NotBlank
        @Length(max = 255)
        String name
) {
}
