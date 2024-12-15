package ru.itis.marketplace.userservice.config;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import ru.itis.marketplace.userservice.kafka.message.ProductUpdateKafkaMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class KafkaBeans {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    private Map<String, Object> configProps() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "catalog-consumer-group");
        return configProps;
    }

    @Bean
    public Deserializer<List<Long>> kafkaDeletionDeserializer() {
        ObjectMapper om = new ObjectMapper();
        JavaType type = om.getTypeFactory().constructParametricType(List.class, Long.class);
        return new JsonDeserializer<>(type, om, false);
    }

    @Bean
    public ConsumerFactory<String, List<Long>> consumerFactoryForDeletion() {
        return new DefaultKafkaConsumerFactory<>(configProps(), new StringDeserializer(), kafkaDeletionDeserializer());
    }

    @Bean
    public Deserializer<ProductUpdateKafkaMessage> kafkaUpdateDeserializer() {
        ObjectMapper om = new ObjectMapper();
        JavaType type = om.getTypeFactory().constructType(ProductUpdateKafkaMessage.class);
        return new JsonDeserializer<>(type, om, false);
    }

    @Bean
    public ConsumerFactory<String, ProductUpdateKafkaMessage> consumerFactoryForUpdate() {
        return new DefaultKafkaConsumerFactory<>(configProps(), new StringDeserializer(), kafkaUpdateDeserializer());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, List<Long>> deletionKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, List<Long>> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryForDeletion());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductUpdateKafkaMessage> updateKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, ProductUpdateKafkaMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryForUpdate());
        return factory;
    }
}
