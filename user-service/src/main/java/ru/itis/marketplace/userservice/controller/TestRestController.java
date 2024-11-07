package ru.itis.marketplace.userservice.controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import java.util.UUID;

public class TestRestController {

    public static void main(String[] args) throws StripeException {

        String successUrl = "http://localhost:8080/success";
        String cancelUrl = "http://localhost:8080/cancel";
        String currencyCode = "RUB";

        // This is your test secret API key.
        Stripe.apiKey = "sk_test_51Q8HJYE8Tb4u66ySvWNUNAOTt5bxJlZgmRnbXoZnD4XPhGH66sESOpUHojoNqhDDPAYBIxrPCTeQ6eITxXlFliga0098TR1x84";

        String id = UUID.randomUUID().toString();
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(successUrl)
                        .setCancelUrl(cancelUrl)
                        .setClientReferenceId(id)
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(5L)
                                        .setPriceData(SessionCreateParams.LineItem.PriceData
                                                .builder()
                                                .setCurrency(currencyCode)
                                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder().setName("name").build())
                                                .setUnitAmount(10099L)
                                                .build()
                                        )
                                        .build())
                        .build();
        Session session = Session.create(params);
        session.setAfterExpiration(new Session.AfterExpiration());
        System.out.println(id);
        System.out.println(session.getUrl());
//        while (true) {
//            System.out.println(session.getStatus());
////        }
    }
}