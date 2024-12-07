package ru.itis.marketplace.userservice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.itis.marketplace.userservice.kafka.message.ProductUpdateKafkaMessage;
import ru.itis.marketplace.userservice.service.OrderService;
import ru.itis.marketplace.userservice.service.PaymentService;
import ru.itis.marketplace.userservice.service.UserBrandService;
import ru.itis.marketplace.userservice.service.UserCartService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final UserCartService userCartService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final UserBrandService userBrandService;

    @KafkaListener(topics = "size-topic", containerFactory = "deletionKafkaListenerContainerFactory")
    public void onSizeDeletion(@Payload List<Long> sizeIds) {
        userCartService.deleteCartItemsBySizeIds(sizeIds);
        var orders = orderService.findOrderThatContainsSizeIds(sizeIds);
        orders.forEach(order -> {
            if (order.getPaymentIntentId() != null) {
                if (!(order.getStatus().equals("shipped") || order.getStatus().equals("delivered"))) {
                    paymentService.refundPayment(order.getPaymentIntentId());
                    orderService.updateOrderStatusById(order.getId(), "refunded");
                    orderService.deleteAllOrderItemsByOrderId(order.getId());
                }
            } else {
                orderService.updateOrderStatusById(order.getId(), "must be refunded");
                orderService.deleteAllOrderItemsByOrderId(order.getId());
            }
        });
    }

    @KafkaListener(topics = "brand-topic", containerFactory = "deletionKafkaListenerContainerFactory")
    public void onBrandDeletion(@Payload List<Long> brandIds) {

        for (Long brandId : brandIds) {
            userBrandService.deleteUserBrand(brandId);
        }
    }

    @KafkaListener(topics = "product-topic", containerFactory = "updateKafkaListenerContainerFactory")
    public void onProductUpdate(@Payload ProductUpdateKafkaMessage message) {
        orderService.updateOrderItemsSetNewBrandIdForProductId(message.productId(), message.brandId());
    }
}