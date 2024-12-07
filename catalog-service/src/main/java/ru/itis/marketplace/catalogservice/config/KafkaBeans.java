package ru.itis.marketplace.catalogservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.itis.marketplace.catalogservice.kafka.message.ProductUpdateKafkaMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class KafkaBeans {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Value("${kafka.topics.size-deletion.name}")
    private String topicForSizeDeletionName;
    @Value("${kafka.topics.size-deletion.partitions}")
    private Integer topicForSizeDeletionPartitions;
    @Value("${kafka.topics.brand-deletion.name}")
    private String topicForBrandDeletionName;
    @Value("${kafka.topics.brand-deletion.partitions}")
    private Integer topicForBrandDeletionPartitions;
    @Value("${kafka.topics.product-update.name}")
    private String topicForProductUpdateName;
    @Value("${kafka.topics.product-update.partitions}")
    private Integer topicForProductUpdatePartitions;

    private Map<String, Object> configProps() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "1");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 10);
        configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);
        return configProps;
    }

    @Bean
    public ProducerFactory<String, List<Long>> producerFactoryForDeletion() {
        return new DefaultKafkaProducerFactory<>(configProps());
    }

    @Bean
    public ProducerFactory<String, ProductUpdateKafkaMessage> producerFactoryForUpdate() {
        return new DefaultKafkaProducerFactory<>(configProps());
    }

    @Bean
    public KafkaTemplate<String, List<Long>> kafkaTemplateForDeletion() {
        return new KafkaTemplate<>(producerFactoryForDeletion());
    }

    @Bean
    public KafkaTemplate<String, ProductUpdateKafkaMessage> kafkaTemplateForUpdate() {
        return new KafkaTemplate<>(producerFactoryForUpdate());
    }

    @Bean
    public NewTopic topicForSizeDeletion() {
        return TopicBuilder.name(topicForSizeDeletionName).partitions(topicForSizeDeletionPartitions).build();
    }

    @Bean
    public NewTopic topicForBrandDeletion() {
        return TopicBuilder.name(topicForBrandDeletionName).partitions(topicForBrandDeletionPartitions).build();
    }

    @Bean
    public NewTopic topicForProductUpdate() {
        return TopicBuilder.name(topicForProductUpdateName).partitions(topicForProductUpdatePartitions).build();
    }
}
