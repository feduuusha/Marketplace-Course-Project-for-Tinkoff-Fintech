package ru.itis.marketplace.userservice.webhookhandler.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.itis.marketplace.userservice.service.OrderService;
import ru.itis.marketplace.userservice.webhookhandler.WebHookHandler;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SuccessfulEventsWebHookHandler implements WebHookHandler {

    private final OrderService orderService;

    @Override
    public void handle(String paymentId) {
        orderService.updateOrderStatusByPaymentId(paymentId, "Order has been successfully paid for");
    }

    @Override
    public List<String> getSupportedEventTypes() {
        return List.of("payment_intent.succeeded");
    }
}
