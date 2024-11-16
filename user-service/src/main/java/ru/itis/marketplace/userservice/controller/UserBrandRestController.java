package ru.itis.marketplace.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.itis.marketplace.userservice.controller.payload.user_brand.NewUserBrandPayload;
import ru.itis.marketplace.userservice.service.UserBrandService;
import ru.itis.marketplace.userservice.entity.Order;
import ru.itis.marketplace.userservice.entity.UserBrand;
import ru.itis.marketplace.userservice.service.OrderService;

import java.util.List;

@Validated
@RestController
@RequestMapping("api/v1/user-service/users")
@RequiredArgsConstructor
public class UserBrandRestController {

    private final UserBrandService userBrandService;
    private final OrderService orderService;

    @PostMapping("/{userId:\\d+}/brands")
    public ResponseEntity<UserBrand> declareBrandOwner(@PathVariable Long userId,
                                                       @Valid @RequestBody NewUserBrandPayload  payload,
                                                       UriComponentsBuilder uriComponentsBuilder) {
        UserBrand userBrand = userBrandService.declareBrandOwner(userId, payload.brandId());
        return ResponseEntity
                .created(uriComponentsBuilder
                        .replacePath("api/v1/user-service/users/{userId}/brands/{brandId}")
                        .build(userId, payload.brandId()))
                .body(userBrand);
    }

    @GetMapping("/{userId:\\d+}/brands")
    public List<Long> findAllUserBrands(@PathVariable Long userId) {
        return userBrandService.findAllUserBrands(userId);
    }

    @DeleteMapping("/{userId:\\d+}/brands/{brandId:\\d+}")
    public ResponseEntity<Void> deleteUserBrand(@PathVariable Long userId,
                                                @PathVariable Long brandId) {
        userBrandService.deleteUserBrand(userId, brandId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId:\\d+}/brands/{brandId:\\d+}/orders")
    public List<Order> findOrdersByBrandId(@PathVariable(name ="userId") Long ignoredUserId, @PathVariable Long brandId) {
        return orderService.findOrdersByBrandId(brandId);
    }

    @GetMapping("/by-brand/{brandId:\\d+}")
    public UserBrand findUserIdOwnerOfBrand(@PathVariable Long brandId) {
        return userBrandService.findUserBrandByBrandId(brandId);
    }
}
