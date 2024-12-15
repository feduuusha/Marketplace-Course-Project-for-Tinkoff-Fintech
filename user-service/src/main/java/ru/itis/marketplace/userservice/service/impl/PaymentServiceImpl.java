package ru.itis.marketplace.userservice.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.userservice.entity.OrderItem;
import ru.itis.marketplace.userservice.exception.UnavailableServiceException;
import ru.itis.marketplace.userservice.model.Product;
import ru.itis.marketplace.userservice.service.PaymentService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final MeterRegistry meterRegistry;

    @Value("${payment.success-url}")
    private String successUrl;
    @Value("${payment.cancel-url}")
    private String cancelUrl;
    @Value("${payment.api-key}")
    private String apiKey;
    @Value("${payment.currency-code}")
    private String currencyCode;
    @Value("${payment.file-service-base-url}")
    private String fileServiceBaseUrl;

    @Override
    public String createPayment(String paymentId, Map<Long, Product> products, List<OrderItem> orderItems) {
        try {
            Stripe.apiKey = apiKey;
            var params =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setSuccessUrl(successUrl)
                            .setCancelUrl(cancelUrl)
                            .setPaymentIntentData(SessionCreateParams.PaymentIntentData
                                    .builder()
                                    .setDescription(paymentId)
                                    .build());
            DistributionSummary numberOfItems = meterRegistry.summary("number of different items in order");
            numberOfItems.record(orderItems.size());
            for (var orderItem : orderItems) {
                var product = products.get(orderItem.getProductId());
                BigDecimal bd = product.price().setScale(2, RoundingMode.HALF_UP);
                DistributionSummary orderProductPrice = meterRegistry.summary("order product price");
                DistributionSummary quantityOfProduct = meterRegistry.summary("quantity of a specific product");
                quantityOfProduct.record(orderItem.getQuantity());
                orderProductPrice.record(bd.doubleValue());
                var productData = SessionCreateParams.LineItem.PriceData.ProductData
                        .builder()
                        .setName(product.name())
                        .setDescription(product.description());
                if (!product.photos().isEmpty()) {
                    productData = productData
                            .addAllImage(product.photos()
                                    .stream()
                                    .map(productPhoto -> fileServiceBaseUrl + productPhoto.url())
                                    .toList());
                }

                params = params.addLineItem(
                        SessionCreateParams.LineItem
                                .builder()
                                .setQuantity(orderItem.getQuantity())
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData
                                                .builder()
                                                .setCurrency(currencyCode)
                                                .setProductData(productData.build())
                                                .setUnitAmount(Long.parseLong(String.format("%.2f", bd.doubleValue()).replace(".", "").replace(",", "")))
                                                .build()
                                )
                                .build()
                );
            }
            var sessionUrl = Session.create(params.build()).getUrl();
            meterRegistry.counter("count of payments").increment();
            return sessionUrl;
        } catch (StripeException exception) {
            throw new UnavailableServiceException("Stipe payment is unavailable: " + exception.getMessage());
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    @Override
    public void refundPayment(String paymentIntentId) {
        try {
            Stripe.apiKey = apiKey;

            RefundCreateParams params =
                    RefundCreateParams.builder()
                            .setPaymentIntent(paymentIntentId)
                            .build();

            Refund.create(params);
            meterRegistry.counter("count of refunds").increment();
        } catch (StripeException exception) {
            throw new UnavailableServiceException("Stipe payment is unavailable: " + exception.getMessage());
        }
    }
}
