package ru.itis.marketplace.userservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.itis.marketplace.userservice.controller.payload.user.NewUserPayload;
import ru.itis.marketplace.userservice.controller.payload.user.UpdateUserPayload;
import ru.itis.marketplace.userservice.service.MarketPlaceUserService;
import ru.itis.marketplace.userservice.entity.MarketPlaceUser;

import java.util.Map;

@RestController
@RequestMapping("api/v1/user")
@AllArgsConstructor
public class MarketPlaceUserRestController {
    private final MarketPlaceUserService userService;
    @GetMapping("/users/{username:.*[a-zA-Z].*}")
    public MarketPlaceUser findUserByUsername(@PathVariable String username) {
        return this.userService.findUserByUsername(username);
    }

    @GetMapping("/users/{userId:\\d+}")
    public MarketPlaceUser findUserById(@PathVariable Long userId) {
        return this.userService.findUserById(userId);
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@Valid @RequestBody NewUserPayload payload,
                                     BindingResult bindingResult,
                                     UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        } else {
            MarketPlaceUser user = this.userService.createUser(payload.email(), payload.phoneNumber(),
                    payload.firstName(), payload.lastName(), payload.username(), payload.password(), payload.role());
            return ResponseEntity
                    .created(uriComponentsBuilder
                            .replacePath("api/v1/user/users/{username}")
                            .build(Map.of("username", user.getUsername())))
                    .body(user);
        }
    }

    @PutMapping("/users/{userId:\\d+}")
    public ResponseEntity<Void> updateUserById(@PathVariable Long userId,
                                               @Valid @RequestBody UpdateUserPayload payload,
                                               BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        } else {
            this.userService.updateUserById(userId, payload.email(), payload.phoneNumber(), payload.firstName(),
                    payload.lastName(), payload.username(), payload.password(), payload.role());
            return ResponseEntity.noContent().build();
        }

    }

    @DeleteMapping("/users/{userId:\\d+}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long userId) {
        this.userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }

}
