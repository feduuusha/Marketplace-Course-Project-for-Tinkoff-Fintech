package ru.itis.marketplace.catalogservice.controller.payload.product_size;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record NewProductSizePayload(
        @NotBlank
        @Length(max = 255)
        String name
) {
}
