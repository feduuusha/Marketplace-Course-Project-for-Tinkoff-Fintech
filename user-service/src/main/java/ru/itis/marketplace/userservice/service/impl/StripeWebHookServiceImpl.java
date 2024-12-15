package ru.itis.marketplace.userservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.userservice.exception.BadRequestException;
import ru.itis.marketplace.userservice.service.WebHookService;
import ru.itis.marketplace.userservice.webhookhandler.WebHookHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StripeWebHookServiceImpl implements WebHookService {

    private final ObjectMapper objectMapper;
    private final Map<String, WebHookHandler> eventHandlers;
    private final MeterRegistry meterRegistry;

    @Value("${payment.signing-secret}")
    private String signingSecret;

    public StripeWebHookServiceImpl(ObjectMapper objectMapper, List<WebHookHandler> webHookHandlerList, MeterRegistry meterRegistry) {
        this.objectMapper = objectMapper;
        this.eventHandlers = new HashMap<>();
        webHookHandlerList.forEach(it ->
                it.getSupportedEventTypes()
                        .forEach((eventType) -> eventHandlers.put(eventType, it)));
        this.meterRegistry = meterRegistry;
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
        StripeObject stripeObject = dataObjectDeserializer.getObject().orElseThrow(() ->  new BadRequestException("Event do not contains object"));
        try {
            var node = objectMapper.readTree(stripeObject.toJson());
            var paymentId = node.get("description");
            var paymentIntentId = node.get("id");
            var eventType = event.getType();
            WebHookHandler handler = eventHandlers.get(eventType);
            if (handler == null) {
                throw new IllegalStateException("WebHook with event type: " + eventType + " is unsupported");
            }
            handler.handle(paymentId.asText(), paymentIntentId.asText());
            meterRegistry.counter("count of handled webhooks").increment();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Exception when parse stripe object: " + e.getMessage());
        }
    }
}
