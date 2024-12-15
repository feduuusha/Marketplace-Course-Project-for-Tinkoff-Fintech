package ru.itis.marketplace.userservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import ru.itis.marketplace.userservice.entity.Order;
import ru.itis.marketplace.userservice.exception.BadRequestException;
import ru.itis.marketplace.userservice.service.impl.StripeWebHookServiceImpl;
import ru.itis.marketplace.userservice.webhookhandler.WebHookHandler;
import ru.itis.marketplace.userservice.webhookhandler.impl.CancelEventsWebHookHandler;
import ru.itis.marketplace.userservice.webhookhandler.impl.SuccessfulEventsWebHookHandler;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {StripeWebHookServiceImpl.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class StripeWebHookServiceTests {

    @Autowired
    private WebHookService webHookService;

    @SpyBean
    private ObjectMapper objectMapper;
    @SpyBean
    private ArrayList<WebHookHandler> webHookHandlerList;
    @SpyBean
    private SuccessfulEventsWebHookHandler handler1;
    @SpyBean
    private CancelEventsWebHookHandler handler2;
    @MockBean
    private OrderService orderService;
    @MockBean
    private PaymentService paymentService;
    @MockBean
    private MeterRegistry meterRegistry;
    @MockBean
    private Counter counter;

    @Value("${payment.signing-secret}")
    private String signingSecret;

    @Test
    @DisplayName("handlePaymentIntentWebHook should correct handle webhook with SuccessfulWebHookHandler and do refund")
    void handlePaymentIntentWebHookSuccessfulWebHookHandlerSuccessfulMustBeRefundedTest() {
        // Arrange
        try (MockedStatic<Webhook> mocked = mockStatic(Webhook.class)) {
            String signature = "signature";
            String body = "body";
            Event event = mock();
            String json = "{\"description\" : \"uuid\", \"id\" : \"id\"}";
            String type = "payment_intent.succeeded";
            EventDataObjectDeserializer eventDataObjectDeserializer = mock();
            StripeObject stripeObject = mock();
            mocked.when(() -> Webhook.constructEvent(body, signature, signingSecret)).thenReturn(event);
            when(event.getDataObjectDeserializer()).thenReturn(eventDataObjectDeserializer);
            when(eventDataObjectDeserializer.getObject()).thenReturn(Optional.of(stripeObject));
            when(stripeObject.toJson()).thenReturn(json);
            when(event.getType()).thenReturn(type);
            when(meterRegistry.counter(any())).thenReturn(counter);
            Order order = mock();
            when(orderService.findByPaymentId(any())).thenReturn(order);
            when(order.getStatus()).thenReturn("must be refunded");

            // Act
            webHookService.handlePaymentIntentWebHook(signature, body);

            // Assert
            verify(paymentService).refundPayment(any());
            verify(orderService).updateOrderStatusAndPaymentIntentByPaymentId("uuid", "refunded", "id");
            verify(handler1).handle("uuid", "id");
        }
    }

    @Test
    @DisplayName("handlePaymentIntentWebHook should correct handle webhook with SuccessfulWebHookHandler and do not refund")
    void handlePaymentIntentWebHookSuccessfulWebHookHandlerSuccessfulNotRefundedTest() {
        // Arrange
        try (MockedStatic<Webhook> mocked = mockStatic(Webhook.class)) {
            String signature = "signature";
            String body = "body";
            Event event = mock();
            String json = "{\"description\" : \"uuid\", \"id\" : \"id\"}";
            String type = "payment_intent.succeeded";
            EventDataObjectDeserializer eventDataObjectDeserializer = mock();
            StripeObject stripeObject = mock();
            mocked.when(() -> Webhook.constructEvent(body, signature, signingSecret)).thenReturn(event);
            when(event.getDataObjectDeserializer()).thenReturn(eventDataObjectDeserializer);
            when(eventDataObjectDeserializer.getObject()).thenReturn(Optional.of(stripeObject));
            when(stripeObject.toJson()).thenReturn(json);
            when(event.getType()).thenReturn(type);
            when(meterRegistry.counter(any())).thenReturn(counter);
            Order order = mock();
            when(orderService.findByPaymentId(any())).thenReturn(order);
            when(order.getStatus()).thenReturn("any");

            // Act
            webHookService.handlePaymentIntentWebHook(signature, body);

            // Assert
            verify(orderService).updateOrderStatusAndPaymentIntentByPaymentId("uuid", "paid for", "id");
            verify(handler1).handle("uuid", "id");
        }
    }

    @Test
    @DisplayName("handlePaymentIntentWebHook should correct handle webhook with CancelEventsWebHookHandler")
    void handlePaymentIntentWebHookCancelEventsWebHookHandlerSuccessfulTest() {
        // Arrange
        try (MockedStatic<Webhook> mocked = mockStatic(Webhook.class)) {
            String signature = "signature";
            String body = "body";
            Event event = mock();
            String json = "{\"description\" : \"uuid\", \"id\" : \"id\"}";
            String type = "payment_intent.canceled";
            EventDataObjectDeserializer eventDataObjectDeserializer = mock();
            StripeObject stripeObject = mock();
            mocked.when(() -> Webhook.constructEvent(body, signature, signingSecret)).thenReturn(event);
            when(event.getDataObjectDeserializer()).thenReturn(eventDataObjectDeserializer);
            when(eventDataObjectDeserializer.getObject()).thenReturn(Optional.of(stripeObject));
            when(stripeObject.toJson()).thenReturn(json);
            when(event.getType()).thenReturn(type);
            when(meterRegistry.counter(any())).thenReturn(counter);

            // Act
            webHookService.handlePaymentIntentWebHook(signature, body);

            // Assert
            verify(orderService).updateOrderStatusAndPaymentIntentByPaymentId("uuid", "canceled", "id");
            verify(handler2).handle("uuid", "id");
        }
    }

    @Test
    @DisplayName("handlePaymentIntentWebHook should throw IllegalStateException, because handler for specified type not found")
    void handlePaymentIntentWebHookUnSuccessfulHandlerForTypeNotFoundTest() {
        // Arrange
        try (MockedStatic<Webhook> mocked = mockStatic(Webhook.class)) {
            String signature = "signature";
            String body = "body";
            Event event = mock();
            String json = "{\"description\" : \"uuid\", \"id\" : \"id\"}";
            String type = "random";
            EventDataObjectDeserializer eventDataObjectDeserializer = mock();
            StripeObject stripeObject = mock();
            mocked.when(() -> Webhook.constructEvent(body, signature, signingSecret)).thenReturn(event);
            when(event.getDataObjectDeserializer()).thenReturn(eventDataObjectDeserializer);
            when(eventDataObjectDeserializer.getObject()).thenReturn(Optional.of(stripeObject));
            when(stripeObject.toJson()).thenReturn(json);
            when(event.getType()).thenReturn(type);
            when(meterRegistry.counter(any())).thenReturn(counter);

            // Act
            // Assert
            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> webHookService.handlePaymentIntentWebHook(signature, body))
                    .withMessage("WebHook with event type: " + type + " is unsupported");
        }
    }

    @Test
    @DisplayName("handlePaymentIntentWebHook should throw IllegalStateException, because was exception while parsing stripe object")
    void handlePaymentIntentWebHookUnSuccessfulHandlerParseExceptionTest() {
        // Arrange
        try (MockedStatic<Webhook> mocked = mockStatic(Webhook.class)) {
            String signature = "signature";
            String body = "body";
            Event event = mock();
            String json = "{\"description\" : \"uuid\",}";
            EventDataObjectDeserializer eventDataObjectDeserializer = mock();
            StripeObject stripeObject = mock();
            mocked.when(() -> Webhook.constructEvent(body, signature, signingSecret)).thenReturn(event);
            when(event.getDataObjectDeserializer()).thenReturn(eventDataObjectDeserializer);
            when(eventDataObjectDeserializer.getObject()).thenReturn(Optional.of(stripeObject));
            when(stripeObject.toJson()).thenReturn(json);

            // Act
            // Assert
            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> webHookService.handlePaymentIntentWebHook(signature, body))
                    .withMessage("Exception when parse stripe object: Unexpected character ('}' (code 125)): was expecting double-quote to start field name\n at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); line: 1, column: 25]");
        }
    }

    @Test
    @DisplayName("handlePaymentIntentWebHook should throw BadRequestException, because deserializer do not have stripe object")
    void handlePaymentIntentWebHookUnSuccessfulNotFoundStripeObjectTest() {
        // Arrange
        try (MockedStatic<Webhook> mocked = mockStatic(Webhook.class)) {
            String signature = "signature";
            String body = "body";
            Event event = mock();
            EventDataObjectDeserializer eventDataObjectDeserializer = mock();
            mocked.when(() -> Webhook.constructEvent(body, signature, signingSecret)).thenReturn(event);
            when(event.getDataObjectDeserializer()).thenReturn(eventDataObjectDeserializer);
            when(eventDataObjectDeserializer.getObject()).thenReturn(Optional.empty());

            // Act
            // Assert
            assertThatExceptionOfType(BadRequestException.class)
                    .isThrownBy(() -> webHookService.handlePaymentIntentWebHook(signature, body))
                    .withMessage("Event do not contains object");
        }
    }

    @Test
    @DisplayName("handlePaymentIntentWebHook should throw BadRequestException, because request is unsigned")
    void handlePaymentIntentWebHookUnSuccessfulUnSignedRequestTest() {
        // Arrange
        try (MockedStatic<Webhook> mocked = mockStatic(Webhook.class)) {
            String signature = "signature";
            String body = "body";
            String message = "message";
            mocked.when(() -> Webhook.constructEvent(body, signature, signingSecret)).thenThrow(new SignatureVerificationException(message, signature));

            // Act
            // Assert
            assertThatExceptionOfType(BadRequestException.class)
                    .isThrownBy(() -> webHookService.handlePaymentIntentWebHook(signature, body))
                    .withMessage("Invalid Stripe Signature: " + message);
        }
    }
}
