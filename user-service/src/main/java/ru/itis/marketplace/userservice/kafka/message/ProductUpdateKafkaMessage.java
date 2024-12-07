package ru.itis.marketplace.userservice.kafka.message;

public record ProductUpdateKafkaMessage (
        Long productId,
        Long brandId
) {

}
