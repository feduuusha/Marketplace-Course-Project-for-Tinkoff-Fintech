package ru.itis.marketplace.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.itis.marketplace.userservice.service.WebHookService;

@RestController
@RequestMapping("api/v1/webhooks/orders")
@RequiredArgsConstructor
public class StripeWebHookRestController {

    private final WebHookService webHookService;

    @PostMapping("/change-order-status")
    public void catchPaymentIntentWebHook(@RequestHeader(name = "Stripe-Signature") String signature,
                                          @RequestBody String body) {
        webHookService.handlePaymentIntentWebHook(signature, body);
    }
}
