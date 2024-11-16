package ru.itis.marketplace.userservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.userservice.exception.BadRequestException;
import ru.itis.marketplace.userservice.service.StripeWebHookService;
import ru.itis.marketplace.userservice.webhookhandler.WebHookHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StripeWebHookServiceImpl implements StripeWebHookService {

    private final ObjectMapper objectMapper;
    private final Map<String, WebHookHandler> eventHandlers;

    @Value("${payment.signing-secret}")
    private String signingSecret;

    public StripeWebHookServiceImpl(ObjectMapper objectMapper, List<WebHookHandler> webHookHandlerList) {
        this.objectMapper = objectMapper;
        this.eventHandlers = new HashMap<>();
        webHookHandlerList.forEach(it ->
                it.getSupportedEventTypes()
                        .forEach((eventType) -> eventHandlers.put(eventType, it)));
    }

    @Override
    public void handlePaymentIntentWebHook(String signature, String body) {
        Event event;
        try {
            event = Webhook.constructEvent(body, signature, signingSecret);
        } catch (SignatureVerificationException e) {
            throw new BadRequestException("Invalid Stripe Signature: " + e.getMessage());
        }

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            throw new BadRequestException("Event do not contains object");
        }
        try {
            var node = objectMapper.readTree(stripeObject.toJson());
            var paymentId = node.get("description");
            var eventType = event.getType();
            WebHookHandler handler = eventHandlers.get(eventType);
            if (handler == null) {
                throw new IllegalStateException("WebHook with event type: " + eventType + " is unsupported");
            }
            handler.handle(paymentId.asText());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Exception when parse stripe object: " + e.getMessage());
        }
    }
}
