package ru.itis.marketplace.userservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.userservice.entity.CartItem;
import ru.itis.marketplace.userservice.repository.MarketPlaceUserRepository;
import ru.itis.marketplace.userservice.repository.UserCartRepository;
import ru.itis.marketplace.userservice.service.UserCartService;
import ru.itis.marketplace.userservice.client.ProductsRestClient;
import ru.itis.marketplace.userservice.entity.MarketPlaceUser;
import ru.itis.marketplace.userservice.exception.BadRequestException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserCartServiceImpl implements UserCartService {

    private final UserCartRepository cartRepository;
    private final MarketPlaceUserRepository userRepository;
    private final ProductsRestClient productsRestClient;

    @Override
    public List<CartItem> findAllCartItemsByUserId(Long userId) {
        Optional<MarketPlaceUser> userOptional = this.userRepository.findById(userId);
        if (userOptional.isPresent()) {
            return this.cartRepository.findAllByUserId(userId);
        } else {
            throw new NoSuchElementException("User with ID: " + userId + " dont exist");
        }
    }

    @Override
    @Transactional
    public CartItem createCartItem(Long userId, Long productId, Long sizeId, Long quantity) {
        Optional<MarketPlaceUser> userOptional = this.userRepository.findById(userId);
        boolean productExist = this.productsRestClient.productWithIdAndWithSizeIdExist(productId, sizeId);
        if (userOptional.isPresent() && productExist) {
            if (this.cartRepository.findByUserIdAndProductId(userId, productId).isEmpty()) {
                return this.cartRepository.save(new CartItem(userOptional.get(), productId, sizeId, quantity));
            } else {
                throw new BadRequestException("CartItem with userId: " + userId + " productId: " + productId + " is already exist");
            }
        } else {
            if (userOptional.isEmpty()) {
                throw new NoSuchElementException("User with ID: " + userId + " do not exist");
            } else {
                throw new BadRequestException("Product with ID: " + productId + " or Product Size with ID: " + sizeId + " do not exist");
            }
        }
    }

    @Override
    @Transactional
    public void updateCartItem(Long userId, Long productId, Long quantity) {
        Optional<CartItem> optionalCartItem = this.cartRepository.findByUserIdAndProductId(userId, productId);
        if (optionalCartItem.isPresent()) {
            CartItem cartItem = optionalCartItem.get();
            cartItem.setQuantity(quantity);
            this.cartRepository.save(cartItem);
        } else {
            throw new NoSuchElementException("Cart item with userId:" + userId + " productId:" + productId + " dont exist");
        }
    }

    @Override
    @Transactional
    public void deleteCartItem(Long userId, Long productId) {
        this.cartRepository.deleteByUserIdAndProductId(userId, productId);
    }

    @Override
    public Long findSumOfItemQuantitiesByUserId(Long userId) {
        return this.cartRepository.findSumOfItemQuantitiesByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteAllItemFromUserCartById(Long userId) {
        this.cartRepository.deleteByUserId(userId);
    }
}
