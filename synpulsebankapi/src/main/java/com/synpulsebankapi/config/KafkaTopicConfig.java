package com.synpulsebankapi.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Class used to configure the topics for our application
 */
@Configuration
public class KafkaTopicConfig {
    
    /**
     * Creates a new topic programatically, named Transactions. 
     * This topic is the one that will be used througout the whole program.
     */
    @Bean
    public NewTopic transactionsTopic() {
        return TopicBuilder.name("Transactions")
            .build();
    }
}
