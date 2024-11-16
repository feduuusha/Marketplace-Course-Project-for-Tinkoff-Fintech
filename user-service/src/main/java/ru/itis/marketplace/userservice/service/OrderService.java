package ru.itis.marketplace.userservice.service;

import ru.itis.marketplace.userservice.controller.payload.order.NewOrderItemPayload;
import ru.itis.marketplace.userservice.entity.Order;

import java.util.List;

public interface OrderService {
    List<Order> findOrdersByUserIdAndOrderStatus(Long userId, String status, Integer pageSize, Integer page, String sortedBy);

    Order findOrderById(Long orderId);

    Order createOrder(Long userId, String country, String locality, String region, String postalCode, String street, String houseNumber, String description, List<NewOrderItemPayload> orderItems);

    void updateOrderById(Long orderId, String country, String locality, String region, String postalCode, String street, String houseNumber, String description);

    void updateOrderStatusById(Long orderId, String status);

    List<Order> findOrdersByBrandId(Long brandId);

    void updateOrderStatusByPaymentId(String paymentId, String orderStatus);
}
