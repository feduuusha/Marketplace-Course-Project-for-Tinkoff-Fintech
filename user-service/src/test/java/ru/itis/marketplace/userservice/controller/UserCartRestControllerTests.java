package ru.itis.marketplace.userservice.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.itis.marketplace.userservice.config.SecurityBeans;
import ru.itis.marketplace.userservice.controller.payload.cart.NewCartItemPayload;
import ru.itis.marketplace.userservice.controller.payload.cart.UpdateCartItemPayload;
import ru.itis.marketplace.userservice.entity.CartItem;
import ru.itis.marketplace.userservice.service.UserCartService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserCartRestController.class})
@ActiveProfiles("test")
@Import(SecurityBeans.class)
class UserCartRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserCartService userCartService;

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/user-service/users/{userId}/cart should return sorted user cart items")
    @WithMockUser(roles={"SERVICE"})
    void findAllCartItemsByUserIdSuccessfulTest() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String sortedBy = "sort";
        Long userId = 3L;
        List<CartItem> cartItems = List.of(
            new CartItem(1L, userId, 2L, 3L, 5L, null),
            new CartItem(2L, userId, 3L, 6L, 7L, null)
        );
        cartItems = new ArrayList<>(cartItems);
        when(userCartService.findCartItemsByUserId(userId, sortedBy)).thenReturn(cartItems);

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/user-service/users/{userId}/cart?sorted-by={sortedBy}", userId, sortedBy))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<CartItem> actualCartItems = mapper.readValue(response, new TypeReference<>() {});
        assertThat(actualCartItems).isEqualTo(cartItems);
        verify(userCartService).findCartItemsByUserId(userId, sortedBy);
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/user-service/users/{userId}/cart should return user cart items")
    @WithMockUser(roles={"SERVICE"})
    void findAllCartItemsByUserIdSuccessfulUnsortedTest() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        Long userId = 3L;
        List<CartItem> cartItems = List.of(
                new CartItem(1L, userId, 2L, 3L, 5L, null),
                new CartItem(2L, userId, 3L, 6L, 7L, null)
        );
        cartItems = new ArrayList<>(cartItems);
        when(userCartService.findCartItemsByUserId(userId, null)).thenReturn(cartItems);

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/user-service/users/{userId}/cart", userId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<CartItem> actualCartItems = mapper.readValue(response, new TypeReference<>() {});
        assertThat(actualCartItems).isEqualTo(cartItems);
        verify(userCartService).findCartItemsByUserId(userId, null);
    }

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/user-service/users/{userId}/cart should create cart item")
    @WithMockUser(roles={"SERVICE"})
    void createCartItemSuccessfulTest() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        Long productId = 5L;
        Long sizeId = 6L;
        Long quantity = 10L;
        Long userId = 12L;
        CartItem cartItem = new CartItem(2L, userId, productId, sizeId, quantity, null);
        when(userCartService.createCartItem(userId, productId, sizeId, quantity)).thenReturn(cartItem);

        // Act
        // Assert
        String response = mockMvc.perform(post("/api/v1/user-service/users/{userId}/cart", userId)
                        .content(mapper.writeValueAsString(new NewCartItemPayload(productId, sizeId, quantity)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/user-service/users/12/cart/2"))
                .andReturn().getResponse().getContentAsString();
        CartItem actualCartItem = mapper.readValue(response, CartItem.class);
        assertThat(actualCartItem).isEqualTo(cartItem);
        verify(userCartService).createCartItem(userId, productId, sizeId, quantity);
    }

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/user-service/users/{userId}/cart should return 400, because incorrect payload")
    @WithMockUser(roles={"SERVICE"})
    void createCartItemUnSuccessfulTest() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        Long productId = 5L;
        Long sizeId = 6L;
        Long quantity = -10L;
        Long userId = 12L;

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/user-service/users/{userId}/cart", userId)
                        .content(mapper.writeValueAsString(new NewCartItemPayload(productId, sizeId, quantity)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Method: PUT Endpoint: api/v1/user-service/users/{userId}/cart/{cartItemId} should update cart item by id")
    @WithMockUser(roles={"SERVICE"})
    void updateCartItemByIdSuccessfulTest() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        Long cartItemId = 13L;
        Long quantity = 10L;
        Long userId = 12L;

        // Act
        // Assert
        mockMvc.perform(put("/api/v1/user-service/users/{userId}/cart/{cartItemId}", userId, cartItemId)
                        .content(mapper.writeValueAsString(new UpdateCartItemPayload(quantity)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(userCartService).updateCartItem(cartItemId, quantity);
    }

    @Test
    @DisplayName("Method: PUT Endpoint: api/v1/user-service/users/{userId}/cart/{cartItemId} should return 400, because incorrect payload")
    @WithMockUser(roles={"SERVICE"})
    void updateCartItemByIdUnSuccessfulTest() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        Long cartItemId = 13L;
        Long quantity = -10L;
        Long userId = 12L;

        // Act
        // Assert
        mockMvc.perform(put("/api/v1/user-service/users/{userId}/cart/{cartItemId}", userId, cartItemId)
                        .content(mapper.writeValueAsString(new UpdateCartItemPayload(quantity)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Method: DELETE Endpoint: api/v1/user-service/users/{userId}/cart/{cartItemId} should delete cart item by id")
    @WithMockUser(roles={"SERVICE"})
    void deleteCartItemSuccessfulTest() throws Exception {
        // Arrange
        Long cartItemId = 13L;
        Long userId = 2L;

        // Act
        // Assert
        mockMvc.perform(delete("/api/v1/user-service/users/{userId}/cart/{cartItemId}", userId, cartItemId))
                .andExpect(status().isNoContent());
        verify(userCartService).deleteCartItem(cartItemId);
    }

    @Test
    @DisplayName("Method: DELETE Endpoint: api/v1/user-service/users/{userId}/cart should delete all cart items of user by id")
    @WithMockUser(roles={"SERVICE"})
    void deleteAllItemFromUserCartByIdSuccessfulTest() throws Exception {
        // Arrange
        Long userId = 2L;

        // Act
        // Assert
        mockMvc.perform(delete("/api/v1/user-service/users/{userId}/cart", userId))
                .andExpect(status().isNoContent());
        verify(userCartService).deleteAllItemFromUserCartById(userId);
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/user-service/users/{userId}/cart/total-quantity should return total quantity of cart")
    @WithMockUser(roles={"SERVICE"})
    void findSumOfItemQuantitiesByUserIdSuccessfulTest() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        Long userId = 12L;
        int totalSum = 15;
        when(userCartService.findSumOfItemQuantitiesByUserId(userId)).thenReturn(Long.valueOf(totalSum));

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/user-service/users/{userId}/cart/total-quantity", userId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Map actualTotalSum = mapper.readValue(response, Map.class);
        assertThat(actualTotalSum.get("total-quantity")).isEqualTo(totalSum);
        verify(userCartService).findSumOfItemQuantitiesByUserId(userId);
    }
}
