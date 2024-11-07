package ru.itis.marketplace.userservice.model;

public record Brand (
        Long id,
        String name,
        String description,
        String linkToLogo,
        String requestStatus
) {
}
