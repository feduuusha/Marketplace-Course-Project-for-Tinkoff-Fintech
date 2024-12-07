package ru.itis.marketplace.userservice.webhookhandler;

import java.util.List;

public interface WebHookHandler {
    void handle(String paymentId, String paymentIntentId);
    List<String> getSupportedEventTypes();
}
