package ru.itis.marketplace.userservice.controller.payload.user;

import jakarta.validation.constraints.*;

public record NewUserPayload(
    @Email
    String email,
    @Size(max=1+15)
    @Pattern(regexp = "^\\+[0-9]+$")
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
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]+$")
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
