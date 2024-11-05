package ru.itis.marketplace.userservice.controller.payload.user;

import jakarta.validation.constraints.*;

public record UpdateUserPayload(
        @Email
        String email,
        @Pattern(regexp = "^[0-9]+$")
        @NotBlank
        @NotNull
        String phoneNumber,
        @NotBlank
        @NotNull
        String firstName,
        @NotBlank
        @NotNull
        String lastName,
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9]+$")
        @NotNull
        String username,
        @Size(min = 8)
        @NotBlank
        String password,
        @Positive
        @NotNull
        Long role
) {
}
