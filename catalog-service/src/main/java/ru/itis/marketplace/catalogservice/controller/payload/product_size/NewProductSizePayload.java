package ru.itis.marketplace.catalogservice.controller.payload.product_size;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewProductSizePayload(
        @NotBlank
        @Size(max = 1000)
        String name
) {
}
