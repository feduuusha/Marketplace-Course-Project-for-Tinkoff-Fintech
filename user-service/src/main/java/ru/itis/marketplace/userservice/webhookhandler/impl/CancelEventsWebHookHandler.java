package ru.itis.marketplace.userservice.webhookhandler.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.itis.marketplace.userservice.service.OrderService;
import ru.itis.marketplace.userservice.webhookhandler.WebHookHandler;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CancelEventsWebHookHandler implements WebHookHandler {

    private final OrderService orderService;

    @Override
    public void handle(String paymentId) {
        orderService.updateOrderStatusByPaymentId(paymentId, "Order was canceled due to a payment error");
    }

    @Override
    public List<String> getSupportedEventTypes() {
        return List.of("payment_intent.canceled", "payment_intent.partially_funded", "payment_intent.payment_failed", "payment_intent.amount_capturable_updated");
    }
}
