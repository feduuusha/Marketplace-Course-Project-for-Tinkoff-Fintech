package ru.itis.marketplace.catalogservice.controller.payload.brand_link;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record NewBrandLinkPayload(
        @NotBlank
        @Length(max = 2048)
        String url,
        @NotBlank
        @Length(max = 255)
        String name
        ) {
}
