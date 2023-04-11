package com.synpulsebankapi;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * KafkaListeners is where we can specify one or more listeners to log into the
 * topics in Kafka
 */
@Component
public class KafkaListeners {

    /**
     * Listener to the topic 'Transactions'
     * 
     * 
     * @param data Data received in Kafka
     */
    @KafkaListener(
        topics="Transactions", 
        containerFactory = "transactionKafkaListenerContainerFactory",
        groupId = "latestTransaction"
    )
    void listener(Transaction transaction) {
        System.out.println("Listener received: " + transaction.getId());
    }
    
}
