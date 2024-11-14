package ru.itis.marketplace.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.itis.marketplace.userservice.controller.payload.cart.NewCartItemPayload;
import ru.itis.marketplace.userservice.controller.payload.cart.UpdateCartItemPayload;
import ru.itis.marketplace.userservice.entity.CartItem;
import ru.itis.marketplace.userservice.service.UserCartService;

import java.util.List;

@RestController
@RequestMapping("api/v1/user/users/{userId:\\d+}/cart")
@RequiredArgsConstructor
public class UserCartRestController {

    private final UserCartService cartService;

    @GetMapping
    public List<CartItem> findAllCartItemsByUserId(@PathVariable Long userId) {
        return this.cartService.findAllCartItemsByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<CartItem> createCartItem(@PathVariable Long userId,
                                                   @Valid @RequestBody NewCartItemPayload payload,
                                                   BindingResult bindingResult,
                                                   UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        } else {
            CartItem item = this.cartService.createCartItem(userId, payload.productId(), payload.sizeId(), payload.quantity());
            return ResponseEntity.created(
                    uriComponentsBuilder
                            .replacePath("/api/v1/user/users/{userId}/cart/{cartItemId}")
                            .build(item.getUser().getId(), item.getId()))
                    .body(item);
        }
    }

    @PutMapping("/{cartItemId:\\d+}")
    public ResponseEntity<Void> updateCartItem(@PathVariable Long userId,
                                               @PathVariable Long cartItemId,
                                               @Valid @RequestBody UpdateCartItemPayload payload,
                                               BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        } else {
            this.cartService.updateCartItem(userId, cartItemId, payload.quantity());
            return ResponseEntity.noContent().build();
        }
    }

    @DeleteMapping("/{cartItemId:\\d+}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long userId,
                                               @PathVariable Long cartItemId) {
        this.cartService.deleteCartItem(userId, cartItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllItemFromUserCartById(@PathVariable Long userId) {
        this.cartService.deleteAllItemFromUserCartById(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/size")
    public ResponseEntity<Long> findSumOfItemQuantitiesByUserId(@PathVariable Long userId) {
        Long sum = this.cartService.findSumOfItemQuantitiesByUserId(userId);
        return ResponseEntity.ok(sum);
    }
}
