package ru.itis.marketplace.catalogservice.controller.payload.brand;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UpdateBrandStatusPayload(
        @NotBlank
        @Length(max = 255)
        String requestStatus
) {
}
