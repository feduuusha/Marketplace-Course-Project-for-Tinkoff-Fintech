package ru.itis.marketplace.catalogservice.controller.payload.product_photo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record NewProductPhotoPayload (
        @NotBlank
        @Size(max = 1000)
        String url,
        @Positive
        @NotNull
        Long sequenceNumber
) {}
