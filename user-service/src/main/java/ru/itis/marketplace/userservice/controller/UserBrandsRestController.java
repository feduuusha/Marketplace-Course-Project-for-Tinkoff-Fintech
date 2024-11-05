package ru.itis.marketplace.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.itis.marketplace.userservice.service.UserBrandService;
import ru.itis.marketplace.userservice.entity.CustomerOrder;
import ru.itis.marketplace.userservice.entity.UserBrand;
import ru.itis.marketplace.userservice.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("api/v1/user/users")
@RequiredArgsConstructor
public class UserBrandsRestController {

    private final UserBrandService userBrandService;
    private final OrderService orderService;

    @PostMapping("/{userId:\\d+}/brands")
    public ResponseEntity<UserBrand> addBrandToUser(@PathVariable Long userId,
                                                    @RequestParam Long brandId,
                                                    UriComponentsBuilder uriComponentsBuilder) {
        UserBrand userBrand = this.userBrandService.addBrandToUser(userId, brandId);
        return ResponseEntity
                .created(uriComponentsBuilder
                        .replacePath("api/v1/user/{userId}/brands/{brandId}")
                        .build(userId, brandId))
                .body(userBrand);
    }

    @GetMapping("/{userId:\\d+}/brands")
    public List<Long> findAllUserBrands(@PathVariable Long userId) {
        return this.userBrandService.findAllUserBrands(userId);
    }

    @DeleteMapping("/{userId:\\d+}/brands/{brandId:\\d+}")
    public ResponseEntity<Void> deleteUserBrand(@PathVariable Long userId,
                                                @PathVariable Long brandId) {
        this.userBrandService.deleteUserBrand(userId, brandId);
        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/{userId:\\d+}/brands/{brandId:\\d+}/orders")
    public List<CustomerOrder> findOrdersByBrandId(@PathVariable Long userId, @PathVariable Long brandId) {
        return this.orderService.findOrdersByBrandId(userId, brandId);
    }
}
