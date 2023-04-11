package com.synpulsebankapi.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Creates a new topic if not existent that we need for the application,
 * named Transactions. This topic is the one that will be used througout the
 * whole program.
 */
@Configuration
public class KafkaTopicConfig {
    
    @Bean
    public NewTopic synpulseTopic() {
        return TopicBuilder.name("Transactions")
            .build();
    }
}
