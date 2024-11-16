package ru.itis.marketplace.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.userservice.client.ProductsRestClient;
import ru.itis.marketplace.userservice.controller.payload.order.NewOrderItemPayload;
import ru.itis.marketplace.userservice.entity.Order;
import ru.itis.marketplace.userservice.entity.OrderItem;
import ru.itis.marketplace.userservice.exception.BadRequestException;
import ru.itis.marketplace.userservice.exception.NotFoundException;
import ru.itis.marketplace.userservice.model.Product;
import ru.itis.marketplace.userservice.repository.UserRepository;
import ru.itis.marketplace.userservice.repository.OrderRepository;
import ru.itis.marketplace.userservice.service.OrderService;
import ru.itis.marketplace.userservice.service.PaymentService;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductsRestClient productsRestClient;
    private final UserRepository userRepository;
    private final PaymentService paymentService;

    @Override
    public List<Order> findOrdersByUserIdAndOrderStatus(Long userId, String status, Integer pageSize, Integer page, String sortedBy) {
        userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID: " + userId + " not found"));
        Sort sort = sortedBy != null ? Sort.by(sortedBy) : Sort.unsorted();
        Pageable pageable = Pageable.unpaged(sort);
        if (page != null && pageSize != null) {
            pageable = PageRequest.of(page, pageSize, sort);
        }
        Specification<Order> specification = OrderRepository.buildSpecification(userId, status);
        return orderRepository.findAll(specification, pageable).toList();
    }

    @Override
    public Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order with ID: " + orderId + " not found"));
    }

    @Override
    public Order createOrder(Long userId, String country, String locality, String region, String postalCode,
                             String street, String houseNumber, String description, List<NewOrderItemPayload> orderItems) {
        userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID: " + userId + " not found"));
        Order order = new Order(UUID.randomUUID().toString(), country, locality, region, postalCode, street, houseNumber, userId, new ArrayList<>(orderItems.size()), "Awaiting payment", description);

        List<Long> productIds = orderItems
                .stream()
                .map(NewOrderItemPayload::productId)
                .toList();

        Map<Long, Product> products = productsRestClient.findProductsByIds(productIds)
                .stream()
                .collect(Collectors.toMap(Product::id, Function.identity()));

        for (NewOrderItemPayload orderItem : orderItems) {
            if (!products.containsKey(orderItem.productId())) {
                throw new BadRequestException("Product with ID: " + orderItem.productId() + " does not exist");
            }
            var product = products.get(orderItem.productId());
            var productSize = product.sizes()
                    .stream()
                    .filter((size) -> size.id().equals(orderItem.productSizeId()))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException("Product with ID: " + product.id() + " does not have a size with ID: " + orderItem.productSizeId()));
            order.getOrderItems().add(new OrderItem(product.id(), productSize.id(), product.brandId(), orderItem.quantity(), order.getId()));
        }
        order.setPaymentLink(paymentService.createPayment(order.getPaymentId(), products, order.getOrderItems()));
        return orderRepository.save(order);
    }

    @Override
    public void updateOrderById(Long orderId, String country, String locality, String region, String postalCode, String street, String houseNumber, String description) {
        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order with ID: " + orderId + " not found"));
        order.setCountry(country);
        order.setLocality(locality);
        order.setRegion(region);
        order.setPostalCode(postalCode);
        order.setStreet(street);
        order.setHouseNumber(houseNumber);
        order.setDescription(description);
        orderRepository.save(order);
    }

    @Override
    public void updateOrderStatusById(Long orderId, String status) {
        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order with ID: " + orderId + " not found"));
        order.setStatus(status);
        orderRepository.save(order);
    }

    @Override
    public List<Order> findOrdersByBrandId(Long brandId) {
        List<Order> orders = orderRepository.findOrderThatContainsItemsWithSpecifiedBrandId(brandId);
        orders.forEach((order) ->
            order.setOrderItems(
                    order.getOrderItems()
                            .stream()
                            .filter((item) -> brandId.equals(item.getBrandId()))
                            .toList())
        );
        return orders;
    }

    @Override
    public void updateOrderStatusByPaymentId(String paymentId, String orderStatus) {
        Order order = orderRepository
                .findByPaymentId(paymentId)
                .orElseThrow(() -> new BadRequestException("Order with payment ID: " + paymentId + " not found"));
        order.setStatus(orderStatus);
        orderRepository.save(order);
    }
}
