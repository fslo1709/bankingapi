package com.synpulsebankapi.config;

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
     * Configuration class for kafka factories
     * 
     * @return Map between the producer configuration fields and their values
     */
    public Map<String, Object> producerConfig() {
        HashMap<String, Object> properties = new HashMap<>();
        
        // Stores the server in use
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Using strings for the keys and values, the producer serializes the values
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return properties;
    }

    /**
     * Calls Kafka's default producer factory to create a new producer using
     * the config class producerConfig
     * 
     * Instantiated as a Bean so it can be injected into the KafkaTemplate function
     * 
     * @return Producer Factory object
     */
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    /**
     * Bean utilized to send messages using KafkaTemplate
     * 
     * @param producerFactory is an injection of a producer used to message the clients
     * @return KafkaTemplate message
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(
        ProducerFactory<String, String> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }
}
