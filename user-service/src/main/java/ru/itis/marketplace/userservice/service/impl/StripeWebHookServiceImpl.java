package ru.itis.marketplace.userservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.userservice.exception.BadRequestException;
import ru.itis.marketplace.userservice.service.OrderService;
import ru.itis.marketplace.userservice.service.StripeWebHookService;

@Service
@RequiredArgsConstructor
public class StripeWebHookServiceImpl implements StripeWebHookService {

    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    @Value("${payment.signing-secret}")
    private String signingSecret;

    @Override
    public void handlePaymentIntentWebHook(String signature, String body) {
        Event event;
        try {
            event = Webhook.constructEvent(
                    body, signature, signingSecret
            );
        } catch (SignatureVerificationException e) {
            throw new BadRequestException(e.getMessage());
        }

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            throw new IllegalStateException("Event do not contains object");
        }
        try {
            var node = objectMapper.readTree(stripeObject.toJson());
            var paymentId = node.get("description");
            var eventType = event.getType();
            if (eventType.equals("payment_intent.succeeded")) {
                orderService.updateOrderStatusByPaymentId(paymentId.asText(), "Order has been successfully paid for");
            } else if (eventType.equals("payment_intent.canceled") || eventType.equals("payment_intent.partially_funded")
                    || eventType.equals("payment_intent.payment_failed") || eventType.equals("payment_intent.amount_capturable_updated")){
                orderService.updateOrderStatusByPaymentId(paymentId.asText(), "Order was canceled due to a payment error");
            }
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Exception when parse stripe object: " + e.getMessage());
        }
    }
}
