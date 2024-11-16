package ru.itis.marketplace.catalogservice.controller.payload.brand;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record NewBrandPayload (
        @NotBlank
        @Length(max = 255)
        String name,
        @NotBlank
        String description,
        @NotBlank
        @Length(max = 2048)
        String linkToLogo
) {
}
