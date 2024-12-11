package ru.itis.marketplace.userservice.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import ru.itis.marketplace.userservice.client.ProductsRestClient;
import ru.itis.marketplace.userservice.controller.payload.order.NewOrderItemPayload;
import ru.itis.marketplace.userservice.entity.Order;
import ru.itis.marketplace.userservice.entity.OrderItem;
import ru.itis.marketplace.userservice.entity.User;
import ru.itis.marketplace.userservice.exception.BadRequestException;
import ru.itis.marketplace.userservice.exception.NotFoundException;
import ru.itis.marketplace.userservice.model.Product;
import ru.itis.marketplace.userservice.model.ProductSize;
import ru.itis.marketplace.userservice.repository.OrderItemRepository;
import ru.itis.marketplace.userservice.repository.OrderRepository;
import ru.itis.marketplace.userservice.repository.UserRepository;
import ru.itis.marketplace.userservice.service.impl.OrderServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@SuppressWarnings("ALL")
@SpringBootTest(classes = {OrderServiceImpl.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class OrderServiceTests {

    @Autowired
    private OrderService orderService;

    @MockBean
    private OrderRepository orderRepository;
    @MockBean
    private OrderItemRepository orderItemRepository;
    @MockBean
    private ProductsRestClient productsRestClient;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private PaymentService paymentService;

    @Test
    @DisplayName("findOrdersByUserIdAndOrderStatus should return list of Orders pageable and sortable")
    void findOrdersByUserIdAndOrderStatusSuccessfulPageableAndSortableTest() {
        // Arrange
        Long userId = 2L;
        String status = "status";
        int pageSize = 3;
        int page = 4;
        String sortedBy = "name";
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        Sort sort = Sort.by(sortedBy);
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        List<Order> orders = List.of(
                new Order(),
                new Order()
        );
        Page<Order> page1 = mock();
        when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page1);
        when(page1.toList()).thenReturn(orders);

        // Act
        List<Order> actualOrders = orderService.findOrdersByUserIdAndOrderStatus(userId, status, pageSize, page, sortedBy);

        // Assert
        assertThat(actualOrders).isEqualTo(orders);
        verify(orderRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("findOrdersByUserIdAndOrderStatus should return list of Orders, un paged and un sorted")
    void findOrdersByUserIdAndOrderStatusSuccessfulUnPagedAndUnSortedTest() {
        // Arrange
        Long userId = 2L;
        String status = "status";
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        Sort sort = Sort.unsorted();
        Pageable pageable = Pageable.unpaged(sort);
        List<Order> orders = List.of(
                new Order(),
                new Order()
        );
        Page<Order> page1 = mock();
        when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page1);
        when(page1.toList()).thenReturn(orders);

        // Act
        List<Order> actualOrders = orderService.findOrdersByUserIdAndOrderStatus(userId, status, null, null, null);

        // Assert
        assertThat(actualOrders).isEqualTo(orders);
        verify(orderRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("findOrdersByUserIdAndOrderStatus should throw NotFoundException, because user not found")
    void findOrdersByUserIdAndOrderStatusUnSuccessfulTest() {
        // Arrange
        Long userId = 2L;
        String status = "status";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> orderService.findOrdersByUserIdAndOrderStatus(userId, status, null, null, null))
                .withMessage("User with ID: " + userId + " not found");
    }

    @Test
    @DisplayName("findOrderById should return order")
    void findOrderByIdSuccessfulTest() {
        // Arrange
        Long orderId = 2L;
        Order order = new Order();
        order.setId(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        Order actualOrder = orderService.findOrderById(orderId);

        // Assert
        assertThat(actualOrder).isEqualTo(order);
        verify(orderRepository).findById(orderId);
    }

    @Test
    @DisplayName("findOrderById should throw NotFoundException, because order not found")
    void findOrderByIdUnSuccessfulTest() {
        // Arrange
        Long orderId = 2L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> orderService.findOrderById(orderId))
                .withMessage("Order with ID: " + orderId + " not found");
    }

    @Test
    @DisplayName("createOrder should create order")
    void createOrderSuccessfulTest() {
        // Arrange
        Long userId = 2L;
        String country = "country";
        String locality = "locality";
        String region = "region";
        String postalCode = "postalCode";
        String street = "street";
        String houseNumber = "phoneNumber";
        String description = "desc";
        Long orderId = 5L;
        String url = "url";
        List<NewOrderItemPayload> orderItems = List.of(
                new NewOrderItemPayload(10L, 110L, 12L),
                new NewOrderItemPayload(20L, 220L, 6L),
                new NewOrderItemPayload(30L, 330L, 1L)
        );
        List<Product> products = List.of(
            new Product(10L, null, null, null, null, null, null, null, List.of(new ProductSize(110L, null)), null, null),
            new Product(20L, null, null, null, null, null, null, null, List.of(new ProductSize(220L, null)), null, null),
            new Product(30L, null, null, null, null, null, null, null, List.of(new ProductSize(330L, null)), null, null)
        );
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(productsRestClient.findProductsByIds(List.of(10L, 20L, 30L))).thenReturn(products);
        when(paymentService.createPayment(any(), any(), any())).thenReturn(url);
        Order order = new Order();
        order.setId(orderId);
        when(orderRepository.save(any())).thenReturn(order);

        // Act
        Order actualOrder = orderService.createOrder(userId, country, locality, region, postalCode, street, houseNumber, description, orderItems);

        // Assert
        assertThat(actualOrder).isEqualTo(order);
        verify(orderRepository).save(any());
        verify(orderItemRepository).saveAll(any());
    }

    @Test
    @DisplayName("createOrder should throw BadRequestException, because one of size id is not property of product")
    void createOrderUnSuccessfulSizeIsNotPropertyOfProductTest() {
        // Arrange
        Long userId = 2L;
        String country = "country";
        String locality = "locality";
        String region = "region";
        String postalCode = "postalCode";
        String street = "street";
        String houseNumber = "phoneNumber";
        String description = "desc";
        List<NewOrderItemPayload> orderItems = List.of(
                new NewOrderItemPayload(10L, 110L, 12L),
                new NewOrderItemPayload(20L, 221L, 6L),
                new NewOrderItemPayload(30L, 330L, 1L)
        );
        List<Product> products = List.of(
                new Product(10L, null, null, null, null, null, null, null, List.of(new ProductSize(110L, null)), null, null),
                new Product(20L, null, null, null, null, null, null, null, List.of(new ProductSize(220L, null)), null, null),
                new Product(30L, null, null, null, null, null, null, null, List.of(new ProductSize(330L, null)), null, null)
        );
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(productsRestClient.findProductsByIds(List.of(10L, 20L, 30L))).thenReturn(products);

        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> orderService.createOrder(userId, country, locality, region, postalCode, street, houseNumber, description, orderItems))
                .withMessage("Product with ID: 20 does not have a size with ID: 221");
    }

    @Test
    @DisplayName("createOrder should throw BadRequestException, because one of product do not exist in catalog")
    void createOrderUnSuccessfulProductDoNotExistInCatalogTest() {
        // Arrange
        Long userId = 2L;
        String country = "country";
        String locality = "locality";
        String region = "region";
        String postalCode = "postalCode";
        String street = "street";
        String houseNumber = "phoneNumber";
        String description = "desc";
        List<NewOrderItemPayload> orderItems = List.of(
                new NewOrderItemPayload(11L, 110L, 12L),
                new NewOrderItemPayload(20L, 220L, 6L),
                new NewOrderItemPayload(30L, 330L, 1L)
        );
        List<Product> products = List.of(
                new Product(10L, null, null, null, null, null, null, null, List.of(new ProductSize(110L, null)), null, null),
                new Product(20L, null, null, null, null, null, null, null, List.of(new ProductSize(220L, null)), null, null),
                new Product(30L, null, null, null, null, null, null, null, List.of(new ProductSize(330L, null)), null, null)
        );
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(productsRestClient.findProductsByIds(List.of(10L, 20L, 30L))).thenReturn(products);

        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> orderService.createOrder(userId, country, locality, region, postalCode, street, houseNumber, description, orderItems))
                .withMessage("Product with ID: " + 11L + " does not exist");
    }

    @Test
    @DisplayName("createOrder should throw BadRequestException, because user not found")
    void createOrderUnSuccessfulUserNotFoundTest() {
        // Arrange
        Long userId = 2L;
        String country = "country";
        String locality = "locality";
        String region = "region";
        String postalCode = "postalCode";
        String street = "street";
        String houseNumber = "phoneNumber";
        String description = "desc";
        List<NewOrderItemPayload> orderItems = List.of(
                new NewOrderItemPayload(11L, 110L, 12L),
                new NewOrderItemPayload(20L, 220L, 6L),
                new NewOrderItemPayload(30L, 330L, 1L)
        );
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> orderService.createOrder(userId, country, locality, region, postalCode, street, houseNumber, description, orderItems))
                .withMessage("User with ID: " + userId + " not found");
    }

    @Test
    @DisplayName("updateOrderById should update order")
    void updateOrderByIdSuccessfulTest() {
        // Arrange
        Long orderId = 2L;
        String country = "country";
        String locality = "locality";
        String region = "region";
        String postalCode = "postalCode";
        String street = "street";
        String houseNumber = "phoneNumber";
        String description = "desc";
        Order order = mock();
        order.setId(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        orderService.updateOrderById(orderId, country, locality, region, postalCode, street, houseNumber, description);

        // Assert
        verify(order).setCountry(country);
        verify(order).setLocality(locality);
        verify(order).setRegion(region);
        verify(order).setPostalCode(postalCode);
        verify(order).setStreet(street);
        verify(order).setHouseNumber(houseNumber);
        verify(order).setDescription(description);
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("updateOrderById should throw NotFoundException, because order not found")
    void updateOrderByIdUnSuccessfulTest() {
        // Arrange
        Long orderId = 2L;
        String country = "country";
        String locality = "locality";
        String region = "region";
        String postalCode = "postalCode";
        String street = "street";
        String houseNumber = "phoneNumber";
        String description = "desc";
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> orderService.updateOrderById(orderId, country, locality, region, postalCode, street, houseNumber, description))
                .withMessage("Order with ID: " + orderId + " not found");
    }

    @Test
    @DisplayName("updateOrderStatusById should update order status")
    void updateOrderStatusByIdSuccessfulTest() {
        // Arrange
        Long orderId = 2L;
        String status = "status";
        Order order = mock();
        order.setId(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        orderService.updateOrderStatusById(orderId, status);

        // Assert
        verify(order).setStatus(status);
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("updateOrderStatusById should throw NotFoundException, because order not found")
    void updateOrderStatusByIdUnSuccessfulTest() {
        // Arrange
        Long orderId = 2L;
        String status = "status";
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> orderService.updateOrderStatusById(orderId, status))
                .withMessage("Order with ID: " + orderId + " not found");
    }

    @Test
    @DisplayName("findOrdersByBrandId should return list of orders that contains products from specified brand")
    void findOrdersByBrandIdSuccessfulTest() {
        // Arrange
        Long brandId = 2L;
        List<Order> orders = List.of(
                new Order(1L, null, null, null, null, null,
                        null, null, null, null, null,
                        List.of(new OrderItem(10L, 10L, 32L, 2L, 5L, 1L),
                                new OrderItem(10L, 12L, 16L, 1L, 1L, 1L)),
                        null, null, null, null),
                new Order(2L, null, null, null, null, null,
                        null, null, null, null, null,
                        List.of(new OrderItem(12L, 99L, 55L, 2L, 10L, 1L),
                                new OrderItem(8L, 9L, 32L, 1L, 5L, 1L)),
                        null, null, null, null)
        );
        List<Order> expectedOrders = List.of(
                new Order(1L, null, null, null, null, null,
                        null, null, null, null, null,
                        List.of(new OrderItem(10L, 10L, 32L, 2L, 5L, 1L)),
                        null, null, null, null),
                new Order(2L, null, null, null, null, null,
                        null, null, null, null, null,
                        List.of(new OrderItem(12L, 99L, 55L, 2L, 10L, 1L)),
                        null, null, null, null)
        );
        when(orderRepository.findOrderThatContainsItemsWithSpecifiedBrandId(brandId)).thenReturn(orders);

        // Act
        List<Order> actualOrders = orderService.findOrdersByBrandId(brandId);

        // Assert
        assertThat(actualOrders).isEqualTo(expectedOrders);
    }

    @Test
    @DisplayName("updateOrderStatusAndPaymentIntentByPaymentId should update order by paymentId")
    void updateOrderStatusAndPaymentIntentByPaymentIdSuccessfulTest() {
        // Arrange
        String status = "status";
        String paymentId = "uuid";
        String paymentIntentId = "id";
        Order order = mock();
        when(orderRepository.findByPaymentId(paymentId)).thenReturn(Optional.of(order));

        // Act
        orderService.updateOrderStatusAndPaymentIntentByPaymentId(paymentId, status, paymentIntentId);

        // Assert
        verify(order).setStatus(status);
        verify(order).setPaymentIntentId(paymentIntentId);
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("updateOrderStatusAndPaymentIntentByPaymentId should throw BadRequestException, because order not found")
    void updateOrderStatusAndPaymentIntentByPaymentIdUnSuccessfulTest() {
        // Arrange
        String status = "status";
        String paymentId = "uuid";
        String paymentIntentId = "id";
        when(orderRepository.findByPaymentId(paymentId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> orderService.updateOrderStatusAndPaymentIntentByPaymentId(paymentId, status, paymentIntentId))
                .withMessage("Order with payment ID: " + paymentId + " not found");
    }

    @Test
    @DisplayName("findOrderThatContainsSizeIds should call orderRepository.findOrderThatContainsSizeIds")
    void findOrderThatContainsSizeIdsSuccessfulTest() {
        // Arrange
        List<Long> sizeIds = List.of(1L, 2L, 3L, 4L, 5L);
        List<Order> orders = List.of(
                new Order(),
                new Order()
        );
        when(orderRepository.findOrderThatContainsSizeIds(sizeIds)).thenReturn(orders);

        // Act
        List<Order> actualOrders = orderService.findOrderThatContainsSizeIds(sizeIds);

        // Assert
        assertThat(actualOrders).isEqualTo(orders);
        verify(orderRepository).findOrderThatContainsSizeIds(sizeIds);
    }

    @Test
    @DisplayName("findByPaymentId should call orderRepository.findByPaymentId")
    void findByPaymentIdSuccessfulTest() {
        // Arrange
        String paymentId = "String";
        Order order = new Order();
        order.setId(2L);
        when(orderRepository.findByPaymentId(paymentId)).thenReturn(Optional.of(order));

        // Act
        Order actualOrder = orderService.findByPaymentId(paymentId);

        // Assert
        assertThat(actualOrder).isEqualTo(order);
        verify(orderRepository).findByPaymentId(paymentId);
    }

    @Test
    @DisplayName("findByPaymentId should throw NotFoundException, because order not found")
    void findByPaymentIdUnSuccessfulTest() {
        // Arrange
        String paymentId = "String";
        when(orderRepository.findByPaymentId(paymentId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> orderService.findByPaymentId(paymentId))
                .withMessage("Order with paymentId: " + paymentId + " not found");
    }

    @Test
    @DisplayName("updateOrderItemsSetNewBrandIdForProductId should call orderItemRepository.updateBrandIdForProductWithId")
    void updateOrderItemsSetNewBrandIdForProductIdSuccessfulTest() {
        // Arrange
        Long productId = 2L;
        Long brandId = 4L;

        // Act
        orderService.updateOrderItemsSetNewBrandIdForProductId(productId, brandId);

        // Assert
        verify(orderItemRepository).updateBrandIdForProductWithId(productId, brandId);
    }

    @Test
    @DisplayName("deleteAllOrderItemsByOrderId should call orderItemRepository.deleteByOrderId")
    void deleteAllOrderItemsByOrderIdSuccessfulTest() {
        // Arrange
        Long orderId = 228L;

        // Act
        orderService.deleteAllOrderItemsByOrderId(orderId);

        // Assert
        verify(orderItemRepository).deleteByOrderId(orderId);
    }
}
