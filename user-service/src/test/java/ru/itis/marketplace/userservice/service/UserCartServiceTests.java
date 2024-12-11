package ru.itis.marketplace.userservice.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import ru.itis.marketplace.userservice.client.ProductsRestClient;
import ru.itis.marketplace.userservice.entity.CartItem;
import ru.itis.marketplace.userservice.entity.User;
import ru.itis.marketplace.userservice.exception.BadRequestException;
import ru.itis.marketplace.userservice.exception.NotFoundException;
import ru.itis.marketplace.userservice.repository.UserCartRepository;
import ru.itis.marketplace.userservice.repository.UserRepository;
import ru.itis.marketplace.userservice.service.impl.UserCartServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {UserCartServiceImpl.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class UserCartServiceTests {

    @Autowired
    private UserCartService userCartService;

    @MockBean
    private UserCartRepository cartRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ProductsRestClient productsRestClient;
    @MockBean
    private MeterRegistry meterRegistry;
    @MockBean
    private Counter counter;

    @Test
    @DisplayName("findCartItemsByUserId should return cart item")
    void findCartItemsByUserIdSuccessfulTest() {
        // Arrange
        Long userId = 2L;
        String sortedBy = "name";
        Sort sort = Sort.by(sortedBy);
        List<CartItem> cartItems = List.of(
                new CartItem(1L, userId, null, null, null, null),
                new CartItem(2L, userId, null, null, null, null),
                new CartItem(3L, userId, null, null, null, null)
        );
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(cartRepository.findAllByUserId(userId, sort)).thenReturn(cartItems);


        // Act
        List<CartItem> actualCartItems = userCartService.findCartItemsByUserId(userId, sortedBy);

        // Assert
        assertThat(actualCartItems).isEqualTo(cartItems);
        verify(cartRepository).findAllByUserId(userId, sort);
    }

    @Test
    @DisplayName("findCartItemsByUserId should return cart item")
    void findCartItemsByUserIdSuccessfulUnsortedTest() {
        // Arrange
        Long userId = 2L;
        Sort sort = Sort.unsorted();
        List<CartItem> cartItems = List.of(
                new CartItem(1L, userId, null, null, null, null),
                new CartItem(2L, userId, null, null, null, null),
                new CartItem(3L, userId, null, null, null, null)
        );
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(cartRepository.findAllByUserId(userId, sort)).thenReturn(cartItems);


        // Act
        List<CartItem> actualCartItems = userCartService.findCartItemsByUserId(userId, null);

        // Assert
        assertThat(actualCartItems).isEqualTo(cartItems);
        verify(cartRepository).findAllByUserId(userId, sort);
    }

    @Test
    @DisplayName("findCartItemsByUserId should throw NotFoundException, user with id not found")
    void findCartItemsByUserIdUnSuccessfulTest() {
        // Arrange
        Long userId = 2L;
        String sortedBy = "name";
        Sort sort = Sort.by(sortedBy);
        List<CartItem> cartItems = List.of(
                new CartItem(1L, userId, null, null, null, null),
                new CartItem(2L, userId, null, null, null, null),
                new CartItem(3L, userId, null, null, null, null)
        );
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(cartRepository.findAllByUserId(userId, sort)).thenReturn(cartItems);


        // Act
        List<CartItem> actualCartItems = userCartService.findCartItemsByUserId(userId, sortedBy);

        // Assert
        assertThat(actualCartItems).isEqualTo(cartItems);
        verify(cartRepository).findAllByUserId(userId, sort);
    }

    @Test
    @DisplayName("createCartItem should create CartItem")
    void createCartItemSuccessfulTest() {
        // Arrange
        Long userId = 2L;
        Long productId = 3L;
        Long sizeId = 4L;
        Long quantity = 5L;
        CartItem cartItem = new CartItem(2L, userId, productId, sizeId, quantity, null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(productsRestClient.productWithIdAndWithSizeIdExist(productId, sizeId)).thenReturn(true);
        when(cartRepository.findByUserIdAndProductIdAndSizeId(userId, productId, sizeId)).thenReturn(Optional.empty());
        when(cartRepository.save(any())).thenReturn(cartItem);
        when(meterRegistry.counter(any())).thenReturn(counter);

        // Act
        CartItem actualCartItem = userCartService.createCartItem(userId, productId, sizeId, quantity);

        // Assert
        assertThat(actualCartItem).isEqualTo(cartItem);
        verify(cartRepository).save(any());
    }

    @Test
    @DisplayName("createCartItem should throw NotFoundException, because user not found")
    void createCartItemUnSuccessfulNotFoundUserTest() {
        // Arrange
        Long userId = 2L;
        Long productId = 3L;
        Long sizeId = 4L;
        Long quantity = 5L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> userCartService.createCartItem(userId, productId, sizeId, quantity))
                .withMessage("User with ID: " + userId + " not found");
    }

    @Test
    @DisplayName("createCartItem should throw BadRequestException, because size id with specified product id not found")
    void createCartItemUnSuccessfulSizeWithSpecifiedIdNotFoundTest() {
        // Arrange
        Long userId = 2L;
        Long productId = 3L;
        Long sizeId = 4L;
        Long quantity = 5L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(productsRestClient.productWithIdAndWithSizeIdExist(productId, sizeId)).thenReturn(false);

        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> userCartService.createCartItem(userId, productId, sizeId, quantity))
                .withMessage("Product with ID: " + productId
                        + " does not exist or this product does not have a size with ID: " + sizeId);
    }

    @Test
    @DisplayName("createCartItem should throw BadRequestException, because cart item already exist")
    void createCartItemUnSuccessfulCartItemAlreadyExistTest() {
        // Arrange
        Long userId = 2L;
        Long productId = 3L;
        Long sizeId = 4L;
        Long quantity = 5L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(productsRestClient.productWithIdAndWithSizeIdExist(productId, sizeId)).thenReturn(true);
        when(cartRepository.findByUserIdAndProductIdAndSizeId(userId, productId, sizeId)).thenReturn(Optional.of(new CartItem()));

        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> userCartService.createCartItem(userId, productId, sizeId, quantity))
                .withMessage("Cart Item with user ID: " + userId + ", with product ID: "
                        + productId + ", with size ID: " + sizeId + " already exist");
    }

    @Test
    @DisplayName("updateCartItem should update CartItem")
    void updateCartItemSuccessfulTest() {
        // Arrange
        Long cartItemId = 2L;
        Long quantity = 3L;
        CartItem cartItem = new CartItem(2L, null, null, null, null, null);
        cartItem = spy(cartItem);
        when(cartRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));

        // Act
        userCartService.updateCartItem(cartItemId, quantity);

        // Assert
        verify(cartItem).setQuantity(quantity);
        verify(cartRepository).save(any());
    }

    @Test
    @DisplayName("updateCartItem should throw NotFoundException, because cart item with specified id not found")
    void updateCartItemUnSuccessfulTest() {
        // Arrange
        Long cartItemId = 2L;
        Long quantity = 3L;
        when(cartRepository.findById(cartItemId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> userCartService.updateCartItem(cartItemId, quantity))
                .withMessage("Cart Item with ID: " + cartItemId + " not found");
    }

    @Test
    @DisplayName("deleteCartItem should call cartRepository.deleteById")
    void deleteCartItemSuccessfulTest() {
        // Arrange
        Long cartItemId = 2L;

        // Act
        userCartService.deleteCartItem(cartItemId);

        // Assert
        verify(cartRepository).deleteById(cartItemId);
    }

    @Test
    @DisplayName("findSumOfItemQuantitiesByUserId should call cartRepository.findSumOfItemQuantitiesByUserId")
    void findSumOfItemQuantitiesByUserIdSuccessfulTest() {
        // Arrange
        Long userId = 2L;

        // Act
        userCartService.findSumOfItemQuantitiesByUserId(userId);

        // Assert
        verify(cartRepository).findSumOfItemQuantitiesByUserId(userId);
    }

    @Test
    @DisplayName("deleteAllItemFromUserCartById should call cartRepository.deleteByUserId")
    void deleteAllItemFromUserCartByIdSuccessfulTest() {
        // Arrange
        Long userId = 2L;

        // Act
        userCartService.deleteAllItemFromUserCartById(userId);

        // Assert
        verify(cartRepository).deleteByUserId(userId);
    }

    @Test
    @DisplayName("deleteCartItemsBySizeIds should call cartRepository.deleteAllBySizeIds")
    void deleteCartItemsBySizeIdsSuccessfulTest() {
        // Arrange
        List<Long> sizeIds = List.of(1L, 2L, 3L, 4L);

        // Act
        userCartService.deleteCartItemsBySizeIds(sizeIds);

        // Assert
        verify(cartRepository).deleteAllBySizeIds(sizeIds);
    }
}
