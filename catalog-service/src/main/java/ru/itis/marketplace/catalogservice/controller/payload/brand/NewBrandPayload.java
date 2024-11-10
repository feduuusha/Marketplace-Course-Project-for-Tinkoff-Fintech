package ru.itis.marketplace.catalogservice.controller.payload.brand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewBrandPayload (
        @NotBlank
        @Size(max = 255)
        String name,
        @NotBlank
        String description,
        @NotBlank
        @Size(max = 2048)
        String linkToLogo
) {
}
