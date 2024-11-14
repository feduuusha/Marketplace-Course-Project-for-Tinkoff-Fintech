package ru.itis.marketplace.userservice.service;

import ru.itis.marketplace.userservice.entity.CartItem;

import java.util.List;

public interface UserCartService {
    List<CartItem> findAllCartItemsByUserId(Long userId);

    CartItem createCartItem(Long userId, Long productId, Long sizeId, Long quantity);

    void updateCartItem(Long userId, Long cartItemId, Long quantity);

    void deleteCartItem(Long userId, Long cartItemId);

    Long findSumOfItemQuantitiesByUserId(Long userId);

    void deleteAllItemFromUserCartById(Long userId);
}
