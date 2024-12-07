package ru.itis.marketplace.userservice.service;

import ru.itis.marketplace.userservice.entity.CartItem;

import java.util.List;

public interface UserCartService {
    List<CartItem> findCartItemsByUserId(Long userId, String sortedBy);
    CartItem createCartItem(Long userId, Long productId, Long sizeId, Long quantity);
    void updateCartItem(Long cartItemId, Long quantity);
    void deleteCartItem(Long cartItemId);
    Long findSumOfItemQuantitiesByUserId(Long userId);
    void deleteAllItemFromUserCartById(Long userId);
    void deleteCartItemsBySizeIds(List<Long> sizeIds);
}
