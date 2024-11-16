package ru.itis.marketplace.catalogservice.controller.payload.product_photo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

public record NewProductPhotoPayload (
        @NotBlank
        @Length(max = 2048)
        String url,
        @Positive
        @NotNull
        Long sequenceNumber
) {}
