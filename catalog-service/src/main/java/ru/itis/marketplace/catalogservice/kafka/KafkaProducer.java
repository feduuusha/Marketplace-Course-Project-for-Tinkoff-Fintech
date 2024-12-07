package ru.itis.marketplace.catalogservice.kafka;

import org.springframework.kafka.support.SendResult;
import ru.itis.marketplace.catalogservice.kafka.message.ProductUpdateKafkaMessage;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface KafkaProducer {

    CompletableFuture<SendResult<String, List<Long>>> sendSizeIds(List<Long> sizeIds);

    CompletableFuture<SendResult<String, List<Long>>> sendBrandIds(List<Long> brandIds);

    CompletableFuture<SendResult<String, ProductUpdateKafkaMessage>> sendProductUpdateMessage(Long productId, Long productNewBrandId);
}
