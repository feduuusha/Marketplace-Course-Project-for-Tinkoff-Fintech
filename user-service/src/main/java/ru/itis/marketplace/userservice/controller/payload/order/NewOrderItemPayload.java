package ru.itis.marketplace.userservice.controller.payload.order;

public record NewOrderItemPayload (
        Long productId,
        Long productSizeId,
        Long amount
) {
}
