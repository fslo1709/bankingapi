package com.synpulsebankapi.kafkaClasses;

import java.time.Duration;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Service;

import com.synpulsebankapi.auxiliary.Transaction;

/**
 * Auxiliary class to poll using the specified consumer
 */
@Service
public class Polling {

    /**
     * Uses the specified consumer to poll transactions from the kafka container
     * 
     * @param consumer Consumer used to poll
     * @param username Username that requests the transactions
     * @param month Month of the transactions
     * @param year Year of the transactions
     * @return List of valid transactions
     */
    public List<Transaction> pollTransactions(
        KafkaConsumer<String, Transaction> consumer, 
        String username,
        Month month,
        int year
    ) {
        // Variables for the consumer loop
        List<Transaction> transactionList = new ArrayList<>();
        boolean keepOnReading = true;

        // Consumes until it remains idle for the duration specified in consumer.poll()
        while (keepOnReading) {
            ConsumerRecords<String, Transaction> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, Transaction> record: records) {
                Transaction thisTransaction = record.value();
                
                // Only stores the consumed object if it meets the constraints
                if (username == record.key() 
                        && month == thisTransaction.getDate().getMonth()
                        && year == thisTransaction.getDate().getYear()) {
                    transactionList.add(record.value());
                }
            }
        }
        return transactionList;
    }
}
