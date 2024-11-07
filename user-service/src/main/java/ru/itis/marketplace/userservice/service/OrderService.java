package ru.itis.marketplace.userservice.service;

import ru.itis.marketplace.userservice.controller.payload.order.NewOrderItemPayload;
import ru.itis.marketplace.userservice.entity.CustomerOrder;

import java.util.List;

public interface OrderService {
    List<CustomerOrder> findAllOrdersByUserIdAndOrderStatus(Long userId, String status);

    CustomerOrder findOrderByUserIdAndByOrderId(Long userId, Long orderId);

    CustomerOrder createOrder(Long userId, String country, String locality, String region, String postalCode, String street, String houseNumber, String description, List<NewOrderItemPayload> orderItems);

    void updateOrderById(Long userId, Long orderId, String country, String locality, String region, String postalCode, String street, String houseNumber, String description);

    void updateOrderStatusById(Long userId, Long orderId, String status);

    List<CustomerOrder> findOrdersByBrandId(Long userId, Long brandId);

    void updateOrderStatusByPaymentId(String paymentId, String orderStatus);
}
