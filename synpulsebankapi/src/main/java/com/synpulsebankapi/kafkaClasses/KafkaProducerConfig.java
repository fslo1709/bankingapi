package com.synpulsebankapi.kafkaClasses;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.synpulsebankapi.auxiliary.Transaction;

/**
 * Configuration Class for Kafka Producers
 * 
 * Includes a configurator for the producers, 
 */

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Calls Kafka's default producer factory to create a new producer using
     * the specified properties Map
     * 
     * @return Producer Factory object
     */
    @Bean
    public ProducerFactory<String, Transaction> transactionProducerFactory() {
        Map<String, Object> properties = new HashMap<>();

        // Stores the server in use
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Using strings for the keys and values, the producer serializes the values
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(properties);
    }

    /**
     * Bean utilized to send messages using KafkaTemplate
     * 
     * @return KafkaTemplate message
     */
    @Bean
    public KafkaTemplate<String, Transaction> transactionKafkaTemplate() {
        return new KafkaTemplate<>(transactionProducerFactory());
    }
}
