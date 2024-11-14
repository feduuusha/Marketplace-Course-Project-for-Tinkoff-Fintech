package ru.itis.marketplace.catalogservice.controller.payload.brand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateBrandStatusPayload(
        @NotBlank
        @Size(max = 255)
        String requestStatus
) {
}
