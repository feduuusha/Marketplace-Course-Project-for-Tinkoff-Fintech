package ru.itis.marketplace.catalogservice.controller.payload.brand_link;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewBrandLinkPayload(
        @NotBlank
        @Size(max = 2048)
        String url,
        @NotBlank
        @Size(max = 255)
        String name
        ) {
}
