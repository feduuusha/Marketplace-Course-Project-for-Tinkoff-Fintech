package ru.itis.marketplace.userservice.service;

public interface StripeWebHookService {
    void handlePaymentIntentWebHook(String signature, String body);
}
