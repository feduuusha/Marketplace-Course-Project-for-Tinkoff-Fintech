package ru.itis.marketplace.userservice.service;

import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.exception.CardException;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import ru.itis.marketplace.userservice.entity.OrderItem;
import ru.itis.marketplace.userservice.exception.UnavailableServiceException;
import ru.itis.marketplace.userservice.model.Product;
import ru.itis.marketplace.userservice.model.ProductPhoto;
import ru.itis.marketplace.userservice.model.ProductSize;
import ru.itis.marketplace.userservice.service.impl.PaymentServiceImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {PaymentServiceImpl.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class PaymentServiceTests {

    @Autowired
    private PaymentService paymentService;

    @MockBean
    private MeterRegistry meterRegistry;
    @MockBean
    private Counter counter;
    @MockBean
    private DistributionSummary summary;

    @DynamicPropertySource
    static DynamicPropertyRegistry dynamicPropertyRegistry(DynamicPropertyRegistry propertyRegistry) {
        propertyRegistry.add("payment.success-url", () -> "success-url");
        propertyRegistry.add("payment.cancel-url", () -> "cancel-url");
        propertyRegistry.add("payment.api-key", () -> "test-api-key");
        propertyRegistry.add("payment.currency-code", () -> "eth");
        propertyRegistry.add("payment.file-service-base-url", () -> "some-url");
        return propertyRegistry;
    }

    @Test
    @DisplayName("createPayment should create payment and return payment url")
    void createPaymentSuccessfulTest() {
        // Arrange
        try (MockedStatic<Session> mocked = mockStatic(Session.class)) {
            String paymentId = "paymentId";
            List<Product> listProducts = List.of(
                new Product(1L, "name1", BigDecimal.valueOf(100), "desc1", "status1", null, 2L, List.of(new ProductPhoto(12L, "url", 2L)), List.of(new ProductSize(12L, "name")), null, null),
                new Product(2L, "name2", BigDecimal.valueOf(200), "desc2", "status2", null, 3L, List.of(), List.of(new ProductSize(13L, "name")), null, null),
                new Product(3L, "name3", BigDecimal.valueOf(300), "desc3", "status3", null, 2L, List.of(), List.of(new ProductSize(14L, "name")), null, null)
            );
            Map<Long, Product> products = listProducts.stream()
                    .collect(Collectors.toMap(Product::id, Function.identity()));
            List<OrderItem> orderItems = List.of(
                    new OrderItem(32L, 1L, 12L, 2L, 5L, 22L),
                    new OrderItem(33L, 2L, 13L, 3L, 1L, 22L),
                    new OrderItem(34L, 3L, 14L, 2L, 2L, 22L)
            );
            String expectedUrl = "url";
            Session sessionMock = mock();
            mocked.when(() -> Session.create(any(SessionCreateParams.class))).thenReturn(sessionMock);
            when(sessionMock.getUrl()).thenReturn(expectedUrl);
            when(meterRegistry.summary(any())).thenReturn(summary);
            when(meterRegistry.counter(any())).thenReturn(counter);

            // Act
            String url = paymentService.createPayment(paymentId, products, orderItems);

            // Assert
            assertThat(url).isEqualTo(expectedUrl);
            mocked.verify(() -> Session.create(any(SessionCreateParams.class)));
        }
    }

    @Test
    @DisplayName("createPayment should throw StripeException, and re-throw UnavailableServiceException")
    void createPaymentUnSuccessfulUnavailableServiceExceptionTest() {
        // Arrange
        try (MockedStatic<Session> mocked = mockStatic(Session.class)) {
            String paymentId = "paymentId";
            List<Product> listProducts = List.of(
                    new Product(1L, "name1", BigDecimal.valueOf(100), "desc1", "status1", null, 2L, List.of(), List.of(new ProductSize(12L, "name")), null, null),
                    new Product(2L, "name2", BigDecimal.valueOf(200), "desc2", "status2", null, 3L, List.of(), List.of(new ProductSize(13L, "name")), null, null),
                    new Product(3L, "name3", BigDecimal.valueOf(300), "desc3", "status3", null, 2L, List.of(), List.of(new ProductSize(14L, "name")), null, null)
            );
            Map<Long, Product> products = listProducts.stream()
                    .collect(Collectors.toMap(Product::id, Function.identity()));
            List<OrderItem> orderItems = List.of(
                    new OrderItem(32L, 1L, 12L, 2L, 5L, 22L),
                    new OrderItem(33L, 2L, 13L, 3L, 1L, 22L),
                    new OrderItem(34L, 3L, 14L, 2L, 2L, 22L)
            );
            String message = "message";
            mocked.when(() -> Session.create(any(SessionCreateParams.class))).thenThrow(new CardException(message, null, null, null, null, null, null, null));
            when(meterRegistry.summary(any())).thenReturn(summary);
            when(meterRegistry.counter(any())).thenReturn(counter);

            // Act
            // Assert
            assertThatExceptionOfType(UnavailableServiceException.class)
                    .isThrownBy(() -> paymentService.createPayment(paymentId, products, orderItems))
                    .withMessage("Stipe payment is unavailable: " + message);
        }
    }

    @Test
    @DisplayName("createPayment should throw unexpected Exception, and re-throw IllegalStateException")
    void createPaymentUnSuccessfulIllegalStateExceptionTest() {
        // Arrange
        try (MockedStatic<Session> mocked = mockStatic(Session.class)) {
            String paymentId = "paymentId";
            List<Product> listProducts = List.of(
                    new Product(1L, "name1", BigDecimal.valueOf(100), "desc1", "status1", null, 2L, List.of(), List.of(new ProductSize(12L, "name")), null, null),
                    new Product(2L, "name2", BigDecimal.valueOf(200), "desc2", "status2", null, 3L, List.of(), List.of(new ProductSize(13L, "name")), null, null),
                    new Product(3L, "name3", BigDecimal.valueOf(300), "desc3", "status3", null, 2L, List.of(), List.of(new ProductSize(14L, "name")), null, null)
            );
            Map<Long, Product> products = listProducts.stream()
                    .collect(Collectors.toMap(Product::id, Function.identity()));
            List<OrderItem> orderItems = List.of(
                    new OrderItem(32L, 1L, 12L, 2L, 5L, 22L),
                    new OrderItem(33L, 2L, 13L, 3L, 1L, 22L),
                    new OrderItem(34L, 3L, 14L, 2L, 2L, 22L)
            );
            String message = "message";
            mocked.when(() -> Session.create(any(SessionCreateParams.class))).thenThrow(new RuntimeException(message));
            when(meterRegistry.summary(any())).thenReturn(summary);
            when(meterRegistry.counter(any())).thenReturn(counter);

            // Act
            // Assert
            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> paymentService.createPayment(paymentId, products, orderItems))
                    .withMessage("java.lang.RuntimeException: " + message);
        }
    }

    @Test
    @DisplayName("refundPayment should refund payment ")
    void refundPaymentSuccessfulTest() {
        // Arrange
        try (MockedStatic<Refund> mocked = mockStatic(Refund.class)) {
            String paymentIntentId = "paymentIntentId";
            mocked.when(() -> Refund.create(any(RefundCreateParams.class))).thenReturn(null);
            when(meterRegistry.counter(any())).thenReturn(counter);

            // Act
            paymentService.refundPayment(paymentIntentId);

            // Assert
            mocked.verify(() -> Refund.create(any(RefundCreateParams.class)));
        }
    }

    @Test
    @DisplayName("refundPayment should re-throw UnavailableServiceException on all StripeException in method ")
    void refundPaymentUnSuccessfulTest() {
        // Arrange
        try (MockedStatic<Refund> mocked = mockStatic(Refund.class)) {
            String paymentIntentId = "paymentIntentId";
            String message = "message";
            mocked.when(() -> Refund.create(any(RefundCreateParams.class))).thenThrow(new CardException(message, null, null, null, null, null, null, null));

            // Act
            // Assert
            assertThatExceptionOfType(UnavailableServiceException.class)
                    .isThrownBy(() -> paymentService.refundPayment(paymentIntentId))
                    .withMessage("Stipe payment is unavailable: " + message);
        }
    }
}
