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
import ru.itis.marketplace.userservice.controller.payload.user_brand.NewUserBrandPayload;
import ru.itis.marketplace.userservice.entity.Order;
import ru.itis.marketplace.userservice.entity.UserBrand;
import ru.itis.marketplace.userservice.service.OrderService;
import ru.itis.marketplace.userservice.service.UserBrandService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserBrandRestController.class})
@ActiveProfiles("test")
@Import(SecurityBeans.class)
class UserBrandRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserBrandService userBrandService;
    @MockBean
    private OrderService orderService;

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/user-service/users/{userId}/brands should declare brand to user")
    @WithMockUser(roles={"SERVICE"})
    void declareBrandOwnerSuccessfulTest() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        Long brandId = 2L;
        Long userId = 3L;
        UserBrand userBrand = new UserBrand(5L, userId, brandId);
        when(userBrandService.declareBrandOwner(userId, brandId)).thenReturn(userBrand);

        // Act
        // Assert
        String response = mockMvc.perform(post("/api/v1/user-service/users/{userId}/brands", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new NewUserBrandPayload(brandId))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        UserBrand actualUserBrand = mapper.readValue(response, UserBrand.class);
        assertThat(actualUserBrand).isEqualTo(userBrand);
        verify(userBrandService).declareBrandOwner(userId, brandId);
    }

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/user-service/users/{userId}/brands should return 400, because payload is incorrect")
    @WithMockUser(roles={"SERVICE"})
    void declareBrandOwnerUnSuccessfulTest() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        Long brandId = -2L;
        Long userId = 3L;

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/user-service/users/{userId}/brands", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new NewUserBrandPayload(brandId))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/user-service/users/{userId}/brands should return all user brands")
    @WithMockUser(roles={"SERVICE"})
    void findAllUserBrandsSuccessfulTest() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        Long userId = 3L;
        List<Long> brandIds = List.of(1L, 2L, 3L);
        when(userBrandService.findAllUserBrands(userId)).thenReturn(brandIds);

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/user-service/users/{userId}/brands", userId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Long> actualBrandIds = mapper.readValue(response, new TypeReference<>() {});
        assertThat(actualBrandIds).isEqualTo(brandIds);
        verify(userBrandService).findAllUserBrands(userId);
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/user-service/users/{userId}/brands/{brandId}/orders should return all brand orders")
    @WithMockUser(roles={"SERVICE"})
    void findAllBrandOrdersSuccessfulTest() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        Long brandId = 3L;
        Long userId = 2L;
        List<Order> orders = List.of(
                new Order(),
                new Order()
        );
        orders.get(0).setId(1L);
        orders.get(1).setId(2L);
        when(orderService.findOrdersByBrandId(brandId)).thenReturn(orders);

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/user-service/users/{userId}/brands/{brandId}/orders", userId, brandId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Order> actualOrders = mapper.readValue(response, new TypeReference<>() {});
        assertThat(actualOrders).isEqualTo(orders);
        verify(orderService).findOrdersByBrandId(brandId);
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/user-service/users/by-brand/{brandId:\\d+} should UserBrand by brandId")
    @WithMockUser(roles={"SERVICE"})
    void findUserIdOwnerOfBrandSuccessfulTest() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        Long brandId = 3L;
        UserBrand userBrand = new UserBrand(2L, 3L, 5L);
        when(userBrandService.findUserBrandByBrandId(brandId)).thenReturn(userBrand);

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/user-service/users/by-brand/{brandId:\\d+}", brandId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        UserBrand actualUserBrand = mapper.readValue(response, UserBrand.class);
        assertThat(actualUserBrand).isEqualTo(userBrand);
        verify(userBrandService).findUserBrandByBrandId(brandId);
    }

    @Test
    @DisplayName("Method: DELETE Endpoint: api/v1/user-service/users/{userId:\\d+}/brands/{brandId:\\d+} should delete UserBrand")
    @WithMockUser(roles={"SERVICE"})
    void deleteUserBrandSuccessfulTest() throws Exception {
        // Arrange
        Long brandId = 3L;
        Long userId = 10L;

        // Act
        // Assert
        mockMvc.perform(delete("/api/v1/user-service/users/{userId:\\d+}/brands/{brandId:\\d+}", userId, brandId))
                .andExpect(status().isNoContent());
        verify(userBrandService).deleteUserBrand(brandId);
    }
}
