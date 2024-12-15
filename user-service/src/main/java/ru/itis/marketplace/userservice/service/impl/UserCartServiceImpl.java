package ru.itis.marketplace.userservice.service.impl;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.userservice.entity.CartItem;
import ru.itis.marketplace.userservice.exception.NotFoundException;
import ru.itis.marketplace.userservice.repository.UserRepository;
import ru.itis.marketplace.userservice.repository.UserCartRepository;
import ru.itis.marketplace.userservice.service.UserCartService;
import ru.itis.marketplace.userservice.client.ProductsRestClient;
import ru.itis.marketplace.userservice.exception.BadRequestException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCartServiceImpl implements UserCartService {

    private final UserCartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductsRestClient productsRestClient;
    private final MeterRegistry meterRegistry;

    @Override
    public List<CartItem> findCartItemsByUserId(Long userId, String sortedBy) {
        userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID: " + userId + " not found"));
        return cartRepository.findAllByUserId(userId, sortedBy == null ? Sort.unsorted() : Sort.by(sortedBy));
    }

    @Override
    @Transactional
    public CartItem createCartItem(Long userId, Long productId, Long sizeId, Long quantity) {
        userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID: " + userId + " not found"));
        if (!productsRestClient.productWithIdAndWithSizeIdExist(productId, sizeId)) {
            throw new BadRequestException("Product with ID: " + productId
                    + " does not exist or this product does not have a size with ID: " + sizeId);
        }
        if (cartRepository.findByUserIdAndProductIdAndSizeId(userId, productId, sizeId).isPresent()) {
            throw new BadRequestException("Cart Item with user ID: " + userId + ", with product ID: "
                    + productId + ", with size ID: " + sizeId + " already exist");
        }
        var cartItem = cartRepository.save(new CartItem(userId, productId, sizeId, quantity));
        meterRegistry.counter("count of created cart items").increment();
        return cartItem;
    }

    @Override
    @Transactional
    public void updateCartItem(Long cartItemId, Long quantity) {
        CartItem cartItem = cartRepository
                .findById(cartItemId)
                .orElseThrow(() -> new NotFoundException("Cart Item with ID: " + cartItemId + " not found"));
        cartItem.setQuantity(quantity);
        cartRepository.save(cartItem);
    }

    @Override
    public void deleteCartItem(Long cartItemId) {
        cartRepository.deleteById(cartItemId);
    }

    @Override
    public Long findSumOfItemQuantitiesByUserId(Long userId) {
        return cartRepository.findSumOfItemQuantitiesByUserId(userId);
    }

    @Override
    public void deleteAllItemFromUserCartById(Long userId) {
        cartRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteCartItemsBySizeIds(List<Long> sizeIds) {
        cartRepository.deleteAllBySizeIds(sizeIds);
    }
}
