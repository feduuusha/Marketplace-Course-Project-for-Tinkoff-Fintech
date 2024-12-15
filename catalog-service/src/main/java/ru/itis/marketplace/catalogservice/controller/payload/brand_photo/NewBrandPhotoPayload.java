package ru.itis.marketplace.catalogservice.controller.payload.brand_photo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

public record NewBrandPhotoPayload(
        @NotBlank
        @Length(max = 2048)
        String url,
        @Positive
        Long sequenceNumber
) {
}
