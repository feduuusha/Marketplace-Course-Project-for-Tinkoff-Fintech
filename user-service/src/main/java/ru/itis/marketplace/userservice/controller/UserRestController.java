package ru.itis.marketplace.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.itis.marketplace.userservice.controller.payload.user.NewUserPayload;
import ru.itis.marketplace.userservice.controller.payload.user.UpdateUserPayload;
import ru.itis.marketplace.userservice.service.UserService;
import ru.itis.marketplace.userservice.entity.User;

@Tag(name = "User Rest Controller", description = "CRUD operations for user")
@Validated
@RestController
@RequestMapping("api/v1/user-service")
@AllArgsConstructor
public class UserRestController {

    private final UserService userService;

    @Operation(
            summary = "Endpoint for getting user by username, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with user", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
                    @ApiResponse(description = "User not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping("/users/by-username/{username}")
    public User findUserByUsername(@NotBlank @PathVariable String username) {
        return userService.findUserByUsername(username);
    }

    @Operation(
            summary = "Endpoint for getting user by email, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with user", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
                    @ApiResponse(description = "User not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping("/users/by-email/{email}")
    public User findUserByEmail(@NotBlank @PathVariable String email) {
        return userService.findUserByEmail(email);
    }

    @Operation(
            summary = "Endpoint for getting user by phone number, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with user", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
                    @ApiResponse(description = "User not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping("/users/by-phone-number/{phoneNumber}")
    public User findUserByPhoneNumber(@NotBlank @PathVariable String phoneNumber) {
        return userService.findUserByPhoneNumber(phoneNumber);
    }

    @Operation(
            summary = "Endpoint for getting user by ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with user", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
                    @ApiResponse(description = "User not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping("/users/{userId:\\d+}")
    public User findUserById(@PathVariable Long userId) {
        return userService.findUserById(userId);
    }

    @Operation(
            summary = "Endpoint for creating user, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with created user", headers = @Header(name = "Location", description = "URL of the created User"), responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
                    @ApiResponse(description = "Incorrect payload or some parameters are not specified or one of fields: username, email, phone-number is occupied", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody NewUserPayload payload,
                                           UriComponentsBuilder uriComponentsBuilder) {
        User user = userService.createUser(payload.email(), payload.phoneNumber(),
                payload.firstName(), payload.lastName(), payload.username(), payload.password(), payload.roles());
        return ResponseEntity
                .created(uriComponentsBuilder
                        .replacePath("api/v1/user-service/users/{userId}")
                        .build(user.getId()))
                .body(user);
    }

    @Operation(
            summary = "Endpoint for updating user by ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response, user updated", responseCode = "204", useReturnTypeSchema = true),
                    @ApiResponse(description = "Incorrect payload or some parameters are not specified or one of fields: username, email, phone-number is occupied", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "User not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PutMapping("/users/{userId:\\d+}")
    public ResponseEntity<Void> updateUserById(@PathVariable Long userId,
                                               @Valid @RequestBody UpdateUserPayload payload) {
        userService.updateUserById(userId, payload.email(), payload.phoneNumber(), payload.firstName(),
                payload.lastName(), payload.username(), payload.password(), payload.roles());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Endpoint for deleting user by ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response when user is deleted ", responseCode = "204", useReturnTypeSchema = true),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @DeleteMapping("/users/{userId:\\d+}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }

}
