package com.synpulsebankapi.kafkaClasses;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.synpulsebankapi.auxiliary.Transaction;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Calls Kafka's default consumer factory to create a new consumer using
     * the specified properties Map
     * 
     * @return Producer Factory object
     */
    @Bean
    public ConsumerFactory<String, Transaction> transactionConsumerFactory() {
        Map<String, Object> properties = new HashMap<>();
        
        // Stores the server in use
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Sets a group id for the transaction listener
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "transaction-id");

        return new DefaultKafkaConsumerFactory<>(properties,
            new StringDeserializer(),
            new JsonDeserializer<>(Transaction.class));
    }

    /**
     * Sets the consumer factory to listen to messages and consume them
     * 
     * @return KafkaListenerContainerFactory with the specified factory above
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Transaction> transactionKafkaListenerContainerFactory () {
        ConcurrentKafkaListenerContainerFactory<String, Transaction> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(transactionConsumerFactory());
        return factory;
    }

    /**
     * Configuration properties for consumers that consume only from a specific
     * partition
     * 
     * @param id The id of the user
     * @return Properties object
     */
    @Bean
    public Properties transactionByIdConsumerProperties() {
        Properties properties = new Properties();
        
        // Stores the server in use
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Assigns deserializers
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return properties;
    }

    /**
     * Creates a new consumer and assigns it to the desired partition
     * 
     * @param id To assign it to the specific partition
     * @return consumer to consume from the partition
     */
    @Bean
    public KafkaConsumer<String, Transaction> transactionByIdConsumer () {
            KafkaConsumer<String, Transaction> consumer = 
                new KafkaConsumer<String, Transaction>(transactionByIdConsumerProperties());
            TopicPartition partitionToReadFrom = new TopicPartition("Transactions", 0);
            consumer.assign(Arrays.asList(partitionToReadFrom));
            return consumer;
    }
}