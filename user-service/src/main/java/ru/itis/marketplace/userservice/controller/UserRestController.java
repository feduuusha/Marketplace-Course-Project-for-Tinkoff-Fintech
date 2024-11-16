package ru.itis.marketplace.userservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.itis.marketplace.userservice.controller.payload.user.NewUserPayload;
import ru.itis.marketplace.userservice.controller.payload.user.UpdateUserPayload;
import ru.itis.marketplace.userservice.service.UserService;
import ru.itis.marketplace.userservice.entity.User;

@Validated
@RestController
@RequestMapping("api/v1/user-service")
@AllArgsConstructor
public class UserRestController {

    private final UserService userService;

    @GetMapping("/users/by-username/{username}")
    public User findUserByUsername(@NotBlank @PathVariable String username) {
        return userService.findUserByUsername(username);
    }

    @GetMapping("/users/by-email/{email}")
    public User findUserByEmail(@NotBlank @PathVariable String email) {
        return userService.findUserByEmail(email);
    }

    @GetMapping("/users/by-phone-number/{phoneNumber}")
    public User findUserByPhoneNumber(@NotBlank @PathVariable String phoneNumber) {
        return userService.findUserByPhoneNumber(phoneNumber);
    }

    @GetMapping("/users/{userId:\\d+}")
    public User findUserById(@PathVariable Long userId) {
        return userService.findUserById(userId);
    }

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

    @PutMapping("/users/{userId:\\d+}")
    public ResponseEntity<Void> updateUserById(@PathVariable Long userId,
                                               @Valid @RequestBody UpdateUserPayload payload) {
        userService.updateUserById(userId, payload.email(), payload.phoneNumber(), payload.firstName(),
                payload.lastName(), payload.username(), payload.password(), payload.roles());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{userId:\\d+}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }

}
