package ru.itis.marketplace.userservice.service;

import ru.itis.marketplace.userservice.entity.OrderItem;
import ru.itis.marketplace.userservice.model.Product;

import java.util.List;
import java.util.Map;

public interface PaymentService {
    String createPayment(String paymentId, Map<Long, Product> products, List<OrderItem> orderItems);
}
