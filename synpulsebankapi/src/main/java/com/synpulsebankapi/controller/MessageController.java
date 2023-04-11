package com.synpulsebankapi.controller;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.synpulsebankapi.Transaction;

@RestController
@RequestMapping("api/v1/synpulse")
public class MessageController {
    
    @Autowired
    private KafkaTemplate<String, Transaction> transactionKafkaTemplate;

    public MessageController(KafkaTemplate<String, Transaction> kafkaTemplate) {
        this.transactionKafkaTemplate = kafkaTemplate;
    }

    /** */
    @GetMapping("/get")
    public void readTransactionInGivenMonth(
        @RequestParam("date") @DateTimeFormat(iso = ISO.DATE) LocalDateTime date
    ) {
        Month month =  date.getMonth();
        int year = date.getYear();

        transactionKafkaTemplate.receive("Transactions", 0, 0);
    }

    /**
     * Post mapping to update the Transactions topic
     * 
     * @param id String to identify the transaction
     * @param amount String representing the amount transacted and the currency
     * @param iban String representing the account IBAN
     * @param date Date_time object in ISO format
     * @param description Description of the transaction
     */
    @PostMapping("/post")
    public void publish(
        @RequestParam("id") String id,
        @RequestParam("amount") String amount,
        @RequestParam("iban") String iban, 
        @RequestParam("date") @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime date, 
        @RequestParam("description") String description
    ) {

        // Object to store the message, reads from parameters
        Transaction transactionObject = new Transaction();
        transactionObject.setId(id);
        transactionObject.setAmount(amount);
        transactionObject.setIban(iban);
        transactionObject.setDate(date);
        transactionObject.setDescription(description);

        // Used to send the messages to kafka asynchronously
        CompletableFuture<SendResult<String, Transaction>> future = 
            transactionKafkaTemplate.send("Transactions", transactionObject);

        // Callback that logs when the message is sent successfully
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                System.out.println(
                    "Message sent with id: " + transactionObject.getId()
                );
            }
            else {
                System.out.println(
                    "Error sending message: " + transactionObject.getId() + 
                    "\nError: " + ex.getMessage()
                );
            }
        });
    }
}
