package ru.itis.marketplace.catalogservice.kafka.message;

public record ProductUpdateKafkaMessage (
        Long productId,
        Long brandId
) {

}
