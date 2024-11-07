package ru.itis.marketplace.userservice.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
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

    @Value("${payment.success-url}")
    private String successUrl;
    @Value("${payment.cancel-url}")
    private String cancelUrl;
    @Value("${payment.api-key}")
    private String apiKey;
    @Value("${payment.currencyCode}")
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
                            .setPaymentIntentData(SessionCreateParams.PaymentIntentData.builder().setDescription(paymentId).build())
                            .setClientReferenceId(paymentId);
            for (var orderItem : orderItems) {
                var product = products.get(orderItem.getProductId());
                BigDecimal bd = product.price().setScale(2, RoundingMode.HALF_UP);
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
            return Session.create(params.build()).getUrl();
        } catch (StripeException exception) {
            throw new UnavailableServiceException(exception.getMessage());
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }
}
