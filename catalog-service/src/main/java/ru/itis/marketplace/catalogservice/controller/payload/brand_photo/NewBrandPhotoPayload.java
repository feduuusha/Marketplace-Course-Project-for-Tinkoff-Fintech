package ru.itis.marketplace.catalogservice.controller.payload.brand_photo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record NewBrandPhotoPayload(
        @NotBlank
        @Size(max = 1000)
        String url,
        @Positive
        Long sequenceNumber
) {
}
