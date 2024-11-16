package ru.itis.marketplace.catalogservice.controller.payload.product;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UpdateProductStatusPayload(
        @NotBlank
        @Length(max = 255)
        String requestStatus
) {
}
