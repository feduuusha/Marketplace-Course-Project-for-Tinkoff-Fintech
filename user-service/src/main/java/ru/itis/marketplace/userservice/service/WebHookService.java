package ru.itis.marketplace.userservice.service;

public interface WebHookService {
    void handlePaymentIntentWebHook(String signature, String body);
}
