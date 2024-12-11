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
import ru.itis.marketplace.userservice.controller.payload.order.NewOrderItemPayload;
import ru.itis.marketplace.userservice.controller.payload.order.NewOrderPayload;
import ru.itis.marketplace.userservice.controller.payload.order.UpdateOrderPayload;
import ru.itis.marketplace.userservice.entity.Order;
import ru.itis.marketplace.userservice.service.OrderService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {OrderRestController.class})
@ActiveProfiles("test")
@Import(SecurityBeans.class)
class OrderRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/user-service/users/{userId}/orders should return all user orders with pageable")
    @WithMockUser(roles={"SERVICE"})
    void findAllUserOrdersSuccessfulTest() throws Exception {
        // Arrange
        Long userId = 2L;
        String status = "status";
        Integer pageSize = 5;
        Integer page = 1;
        String sortedBy = "name";
        ObjectMapper mapper = new ObjectMapper();
        List<Order> orders = List.of(
                new Order("paymentId1", "country", "locality", "region", "postalCode", "street", "houseNumber", null, null, null, null),
                new Order("paymentId2", "country", "locality", "region", "postalCode", "street", "houseNumber", null, null, null, null),
                new Order("paymentId3", "country", "locality", "region", "postalCode", "street", "houseNumber", null, null, null, null)
        );
        orders = new ArrayList<>(orders);
        orders.get(0).setId(1L);
        orders.get(1).setId(2L);
        orders.get(2).setId(3L);
        when(orderService.findOrdersByUserIdAndOrderStatus(userId, status, pageSize, page, sortedBy)).thenReturn(orders);

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/user-service/users/{userId}/orders" +
                        "?status={status}&page={page}&page-size={pageSize}&sorted-by={sortBy}", userId, status, page, pageSize, sortedBy))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Order> actualOrders = mapper.readValue(response, new TypeReference<>() {});
        assertThat(actualOrders).isEqualTo(orders);
        verify(orderService).findOrdersByUserIdAndOrderStatus(userId, status, pageSize, page, sortedBy);
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/user-service/users/{userId}/orders should return all user orders")
    @WithMockUser(roles={"SERVICE"})
    void findAllUserOrdersSuccessfulWithOutSortingPageableTest() throws Exception {
        // Arrange
        Long userId = 2L;
        ObjectMapper mapper = new ObjectMapper();
        List<Order> orders = List.of(
                new Order("paymentId1", "country", "locality", "region", "postalCode", "street", "houseNumber", null, null, null, null),
                new Order("paymentId2", "country", "locality", "region", "postalCode", "street", "houseNumber", null, null, null, null),
                new Order("paymentId3", "country", "locality", "region", "postalCode", "street", "houseNumber", null, null, null, null)
        );
        orders = new ArrayList<>(orders);
        orders.get(0).setId(1L);
        orders.get(1).setId(2L);
        orders.get(2).setId(3L);
        when(orderService.findOrdersByUserIdAndOrderStatus(userId, null, null, null, null)).thenReturn(orders);

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/user-service/users/{userId}/orders", userId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Order> actualOrders = mapper.readValue(response, new TypeReference<>() {});
        assertThat(actualOrders).isEqualTo(orders);
        verify(orderService).findOrdersByUserIdAndOrderStatus(userId, null, null, null, null);
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/user-service/users/{userId}/orders/{orderId} should return order by id")
    @WithMockUser(roles={"SERVICE"})
    void findOrderByIdSuccessfulTest() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        Long userId = 2L;
        Long orderId = 15L;
        Order order = new Order();
        order.setId(orderId);
        when(orderService.findOrderById(orderId)).thenReturn(order);

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/user-service/users/{userId}/orders/{orderId}", userId, orderId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Order actualOrder = mapper.readValue(response, Order.class);
        assertThat(actualOrder).isEqualTo(order);
        verify(orderService).findOrderById(orderId);
    }

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/user-service/users/{userId}/orders should create order")
    @WithMockUser(roles={"SERVICE"})
    void createOrderSuccessfulTest() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        Long userId = 2L;
        String country = "country";
        String locality = "locality";
        String region = "region";
        String postalCode = "postalCode";
        String street = "street";
        String houseNumber = "houseNumber";
        String description = "description";
        List<NewOrderItemPayload> orderItems = List.of(
                new NewOrderItemPayload(20L, 30L, 40L),
                new NewOrderItemPayload(40L, 30L, 20L)
        );
        Order order = new Order();
        order.setId(2L);
        order.setUserId(userId);
        when(orderService.createOrder(userId, country, locality, region, postalCode, street, houseNumber, description, orderItems)).thenReturn(order);

        // Act
        // Assert
        String response = mockMvc.perform(post("/api/v1/user-service/users/{userId}/orders", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new NewOrderPayload(country, region, locality, postalCode, street, houseNumber, description, orderItems))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/user-service/users/2/orders/2"))
                .andReturn().getResponse().getContentAsString();
        Order actualOrder = mapper.readValue(response, Order.class);
        assertThat(actualOrder).isEqualTo(order);
        verify(orderService).createOrder(userId, country, locality, region, postalCode, street, houseNumber, description, orderItems);
    }

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/user-service/users/{userId}/orders should return 400, because payload is incorrect")
    @WithMockUser(roles={"SERVICE"})
    void createOrderUnSuccessfulTest() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        Long userId = 2L;
        String country = "country";
        String locality = "locality";
        String region = "region";
        String postalCode = "postalCode";
        String street = "street";
        String houseNumber = "houseNumber";
        String description = "description";
        Order order = new Order();
        List<NewOrderItemPayload> orderItems = List.of();
        order.setId(2L);
        order.setUserId(userId);

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/user-service/users/{userId}/orders", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new NewOrderPayload(country, region, locality, postalCode, street, houseNumber, description, orderItems))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Method: PUT Endpoint: api/v1/user-service/users/{userId}/orders/{orderId} should fully update order by id")
    @WithMockUser(roles={"SERVICE"})
    void updateOrderByIdSuccessfulTest() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        Long userId = 2L;
        Long orderId = 15L;
        String country = "country";
        String locality = "locality";
        String region = "region";
        String postalCode = "postalCode";
        String street = "street";
        String houseNumber = "houseNumber";
        String description = "description";
        Order order = new Order();
        order.setId(orderId);

        // Act
        // Assert
        mockMvc.perform(put("/api/v1/user-service/users/{userId}/orders/{orderId}", userId, orderId)
                        .content(mapper.writeValueAsString(new UpdateOrderPayload(country, region, locality, postalCode, street, houseNumber, description)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(orderService).updateOrderById(orderId, country, locality, region, postalCode, street, houseNumber, description);
    }

    @Test
    @DisplayName("Method: PUT Endpoint: api/v1/user-service/users/{userId}/orders/{orderId} should return 400, because incorrect payload")
    @WithMockUser(roles={"SERVICE"})
    void updateOrderByIdUnSuccessfulTest() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        Long userId = 2L;
        Long orderId = 15L;
        String locality = "locality";
        String region = "region";
        String postalCode = "postalCode";
        String street = "street";
        String houseNumber = "houseNumber";
        String description = "description";
        Order order = new Order();
        order.setId(orderId);

        // Act
        // Assert
        mockMvc.perform(put("/api/v1/user-service/users/{userId}/orders/{orderId}", userId, orderId)
                        .content(mapper.writeValueAsString(new UpdateOrderPayload(null, region, locality, postalCode, street, houseNumber, description)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Method: PATCH Endpoint: api/v1/user-service/users/{userId}/orders/{orderId} should update order status by id")
    @WithMockUser(roles={"SERVICE"})
    void updateOrderStatusByIdSuccessfulTest() throws Exception {
        // Arrange
        Long userId = 2L;
        Long orderId = 15L;
        String status = "status";
        Order order = new Order();
        order.setId(orderId);

        // Act
        // Assert
        mockMvc.perform(patch("/api/v1/user-service/users/{userId}/orders/{orderId}?status={status}", userId, orderId, status))
                .andExpect(status().isNoContent());
        verify(orderService).updateOrderStatusById(orderId, status);
    }

    @Test
    @DisplayName("Method: PATCH Endpoint: api/v1/user-service/users/{userId}/orders/{orderId} should return 400, because parameter not provided")
    @WithMockUser(roles={"SERVICE"})
    void updateOrderStatusByIdUnSuccessfulTest() throws Exception {
        // Arrange
        Long userId = 2L;
        Long orderId = 15L;
        Order order = new Order();
        order.setId(orderId);

        // Act
        // Assert
        mockMvc.perform(put("/api/v1/user-service/users/{userId}/orders/{orderId}", userId, orderId))
                .andExpect(status().isBadRequest());
    }
}
