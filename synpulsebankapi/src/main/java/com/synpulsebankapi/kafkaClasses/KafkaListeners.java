package com.synpulsebankapi.kafkaClasses;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.synpulsebankapi.auxiliary.Transaction;

/**
 * KafkaListeners is where we can specify one or more listeners to log into the
 * topics in Kafka
 */
@Component
public class KafkaListeners {

    /**
     * Listener to the topic 'Transactions'
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
