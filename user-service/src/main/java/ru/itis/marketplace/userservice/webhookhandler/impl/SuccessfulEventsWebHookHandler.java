package ru.itis.marketplace.userservice.webhookhandler.impl;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.itis.marketplace.userservice.service.OrderService;
import ru.itis.marketplace.userservice.webhookhandler.WebHookHandler;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SuccessfulEventsWebHookHandler implements WebHookHandler {

    private final OrderService orderService;
    private final MeterRegistry meterRegistry;

    @Override
    public void handle(String paymentId) {
        orderService.updateOrderStatusByPaymentId(paymentId, "Order has been successfully paid for");
        meterRegistry.counter("count of successful payments").increment();
    }

    @Override
    public List<String> getSupportedEventTypes() {
        return List.of("payment_intent.succeeded");
    }
}
