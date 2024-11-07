package ru.itis.marketplace.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.userservice.client.BrandsRestClient;
import ru.itis.marketplace.userservice.client.ProductsRestClient;
import ru.itis.marketplace.userservice.controller.payload.order.NewOrderItemPayload;
import ru.itis.marketplace.userservice.entity.CustomerOrder;
import ru.itis.marketplace.userservice.entity.OrderItem;
import ru.itis.marketplace.userservice.exception.BadRequestException;
import ru.itis.marketplace.userservice.model.Product;
import ru.itis.marketplace.userservice.repository.MarketPlaceUserRepository;
import ru.itis.marketplace.userservice.repository.OrderRepository;
import ru.itis.marketplace.userservice.service.OrderService;
import ru.itis.marketplace.userservice.service.PaymentService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductsRestClient productsRestClient;
    private final MarketPlaceUserRepository userRepository;
    private final PaymentService paymentService;
    private final BrandsRestClient brandsRestClient;

    @Override
    public List<CustomerOrder> findAllOrdersByUserIdAndOrderStatus(Long userId, String status) {
        userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User with ID: " + userId + " not found"));
        if (status == null || status.isBlank()) {
            return this.orderRepository.findByUserId(userId);
        } else {
            return this.orderRepository.findByUserIdAndStatus(userId, status);
        }
    }

    @Override
    public CustomerOrder findOrderByUserIdAndByOrderId(Long userId, Long orderId) {
        return this.orderRepository.findById(orderId).orElseThrow(() -> new NoSuchElementException("Order with ID: " + orderId + " not found"));
    }

    @Override
    public CustomerOrder createOrder(Long userId, String country, String locality, String region, String postalCode,
                                     String street, String houseNumber, String description, List<NewOrderItemPayload> orderItems) {
        var user = this.userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User with ID: " + userId + " not found"));
        CustomerOrder order = new CustomerOrder(null, null, UUID.randomUUID().toString(), country, locality, region, postalCode, street, houseNumber, user, new ArrayList<>(), "Awaiting payment", description, null, null);

        List<Long> productIds = orderItems
                .stream()
                .map(NewOrderItemPayload::productId)
                .toList();

        Map<Long, Product> products = this.productsRestClient.findProductsByIds(productIds)
                .stream()
                .collect(Collectors.toMap(Product::id, product -> product));

        for (NewOrderItemPayload orderItem : orderItems) {
            if (products.containsKey(orderItem.productId())) {
                var product = products.get(orderItem.productId());
                if (product.sizes().stream().anyMatch(productSize -> productSize.id().equals(orderItem.productSizeId()))) {
                    order.getOrderItems().add(new OrderItem(null, orderItem.productId(), orderItem.productSizeId(), product.brand().id(), orderItem.amount(), order));
                } else {
                    throw new BadRequestException("Product Size with ID: " + orderItem.productSizeId() + " was not found on Product with id: " + orderItem.productId());
                }
            } else {
                throw new BadRequestException("Product with ID: " + orderItem.productId() + " do not exist");
            }
        }
        order.setPaymentLink(this.paymentService.createPayment(order.getPaymentId(), products, order.getOrderItems()));
        return this.orderRepository.save(order);
    }

    @Override
    public void updateOrderById(Long userId, Long orderId, String country, String locality, String region, String postalCode, String street, String houseNumber, String description) {
        this.userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User with ID: " + userId + " not found"));
        var order = this.orderRepository.findById(orderId).orElseThrow(() -> new NoSuchElementException("Order with ID: " + orderId + " not found"));
        order.setCountry(country);
        order.setLocality(locality);
        order.setRegion(region);
        order.setPostalCode(postalCode);
        order.setStreet(street);
        order.setHouseNumber(houseNumber);
        order.setDescription(description);
        this.orderRepository.save(order);
    }

    @Override
    public void updateOrderStatusById(Long userId, Long orderId, String status) {
        this.userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User with ID: " + userId + " not found"));
        var order = this.orderRepository.findById(orderId).orElseThrow(() -> new NoSuchElementException("Order with ID: " + orderId + " not found"));
        order.setStatus(status);
        this.orderRepository.save(order);
    }

    @Override
    public List<CustomerOrder> findOrdersByBrandId(Long userId, Long brandId) {
        this.userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User with ID: " + userId + " not found"));
        if (this.brandsRestClient.brandWithIdExist(brandId)) {
            var orders = this.orderRepository.findCustomerOrderThatContainsOrderItemsWithSpecifiedBrandId(brandId);
            for (var order : orders) {
                order.setOrderItems(order.getOrderItems()
                        .stream()
                        .filter(orderItem -> Objects.equals(orderItem.getBrandId(), brandId))
                        .toList());
            }
            return orders;
        } else {
            throw new NoSuchElementException("Brand with ID: " + brandId + " not found");
        }
    }

    @Override
    public void updateOrderStatusByPaymentId(String paymentId, String orderStatus) {
        var order = this.orderRepository.findByPaymentId(paymentId).orElseThrow(() -> new NoSuchElementException("Order with paymentId: " + paymentId + " not found"));
        order.setStatus(orderStatus);
        orderRepository.save(order);
    }
}
