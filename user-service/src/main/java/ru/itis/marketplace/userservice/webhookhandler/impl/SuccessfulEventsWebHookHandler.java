package ru.itis.marketplace.userservice.webhookhandler.impl;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.itis.marketplace.userservice.service.OrderService;
import ru.itis.marketplace.userservice.service.PaymentService;
import ru.itis.marketplace.userservice.webhookhandler.WebHookHandler;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SuccessfulEventsWebHookHandler implements WebHookHandler {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final MeterRegistry meterRegistry;

    @Override
    public void handle(String paymentId, String paymentIntentId) {
        var order = orderService.findByPaymentId(paymentId);
        if (order.getStatus().equals("must be refunded")) {
            paymentService.refundPayment(paymentIntentId);
            orderService.updateOrderStatusAndPaymentIntentByPaymentId(paymentId, "refunded", paymentIntentId);
        } else {
            orderService.updateOrderStatusAndPaymentIntentByPaymentId(paymentId, "paid for", paymentIntentId);
            meterRegistry.counter("count of successful payments").increment();
        }
    }

    @Override
    public List<String> getSupportedEventTypes() {
        return List.of("payment_intent.succeeded");
    }
}
