package ru.itis.marketplace.userservice.controller.payload.user;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

public record UpdateUserPayload(
        @Email
        @Length(max = 255)
        @NotBlank
        String email,
        @Length(max=1+15)
        @Pattern(regexp = "^\\+[0-9]+$")
        @NotBlank
        String phoneNumber,
        @NotBlank
        @Length(max = 255)
        String firstName,
        @NotBlank
        @Length(max = 255)
        String lastName,
        @NotBlank
        @Pattern(regexp = "^(?=.*[a-zA-Z])[a-zA-Z0-9]+$")
        @Length(max = 255)
        String username,
        @Length(min = 8, max = 72)
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])[a-zA-Z0-9]+$")
        @NotBlank
        String password,
        @NotNull
        @Size(min = 1)
        Set<String> roles
) {
}
