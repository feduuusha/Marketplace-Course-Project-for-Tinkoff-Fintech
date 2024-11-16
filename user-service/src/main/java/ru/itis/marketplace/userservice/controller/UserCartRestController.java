package ru.itis.marketplace.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.itis.marketplace.userservice.controller.payload.cart.NewCartItemPayload;
import ru.itis.marketplace.userservice.controller.payload.cart.UpdateCartItemPayload;
import ru.itis.marketplace.userservice.entity.CartItem;
import ru.itis.marketplace.userservice.service.UserCartService;

import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("api/v1/user-service/users/{userId:\\d+}/cart")
@RequiredArgsConstructor
public class UserCartRestController {

    private final UserCartService cartService;

    @GetMapping
    public List<CartItem> findAllCartItemsByUserId(@PathVariable Long userId,
                                                   @RequestParam(required = false, name = "sorted-by") String sortedBy) {
        return cartService.findCartItemsByUserId(userId, sortedBy);
    }

    @PostMapping
    public ResponseEntity<CartItem> createCartItem(@PathVariable Long userId,
                                                   @Valid @RequestBody NewCartItemPayload payload,
                                                   UriComponentsBuilder uriComponentsBuilder) {
        CartItem item = cartService.createCartItem(userId, payload.productId(), payload.sizeId(), payload.quantity());
        return ResponseEntity.created(
                uriComponentsBuilder
                        .replacePath("/api/v1/user-service/users/{userId}/cart/{cartItemId}")
                        .build(item.getUserId(), item.getId()))
                .body(item);
    }

    @PutMapping("/{cartItemId:\\d+}")
    public ResponseEntity<Void> updateCartItem(@PathVariable(name = "userId") Long ignoredUserId,
                                               @PathVariable Long cartItemId,
                                               @Valid @RequestBody UpdateCartItemPayload payload) {
        cartService.updateCartItem(cartItemId, payload.quantity());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cartItemId:\\d+}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable(name = "userId") Long ignoredUserId,
                                               @PathVariable Long cartItemId) {
        cartService.deleteCartItem(cartItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllItemFromUserCartById(@PathVariable Long userId) {
        cartService.deleteAllItemFromUserCartById(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total-quantity")
    public ResponseEntity<Map<String, Long>> findSumOfItemQuantitiesByUserId(@PathVariable Long userId) {
        Long sum = cartService.findSumOfItemQuantitiesByUserId(userId);
        return ResponseEntity.ok(Map.of("total-quantity", sum));
    }
}
