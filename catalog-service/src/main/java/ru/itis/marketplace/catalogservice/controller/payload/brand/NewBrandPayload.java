package ru.itis.marketplace.catalogservice.controller.payload.brand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewBrandPayload (
        @NotBlank
        @Size(min = 1, max = 63)
        String name,
        @NotBlank
        @Size(max = 1000)
        String description,
        @NotBlank
        String linkToLogo
) {
}
