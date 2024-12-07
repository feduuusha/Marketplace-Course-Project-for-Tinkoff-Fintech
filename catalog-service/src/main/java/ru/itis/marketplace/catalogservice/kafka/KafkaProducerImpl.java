package ru.itis.marketplace.catalogservice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import ru.itis.marketplace.catalogservice.kafka.message.ProductUpdateKafkaMessage;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class KafkaProducerImpl implements KafkaProducer{

    private final KafkaTemplate<String, List<Long>> kafkaTemplateForDeletion;
    private final KafkaTemplate<String, ProductUpdateKafkaMessage> kafkaTemplateForUpdate;

    @Value("${kafka.topics.size-deletion.name}")
    private String topicForSizeDeletionName;
    @Value("${kafka.topics.brand-deletion.name}")
    private String topicForBrandDeletionName;
    @Value("${kafka.topics.product-update.name}")
    private String topicForProductUpdateName;

    @Override
    public CompletableFuture<SendResult<String, List<Long>>> sendSizeIds(List<Long> sizeIds) {
        return kafkaTemplateForDeletion.send(topicForSizeDeletionName, sizeIds);
    }

    @Override
    public CompletableFuture<SendResult<String, List<Long>>> sendBrandIds(List<Long> brandIds) {
        return kafkaTemplateForDeletion.send(topicForBrandDeletionName, brandIds);
    }

    @Override
    public CompletableFuture<SendResult<String, ProductUpdateKafkaMessage>> sendProductUpdateMessage(Long productId, Long productNewBrandId) {
        return kafkaTemplateForUpdate.send(topicForProductUpdateName, new ProductUpdateKafkaMessage(productId, productNewBrandId));
    }
}
