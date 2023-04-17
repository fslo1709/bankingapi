package com.bankapi.controller;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.utils.Utils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bankapi.auxiliary.CurrencyConverter;
import com.bankapi.auxiliary.Transaction;
import com.bankapi.kafkaClasses.KafkaConsumerConfig;
import com.bankapi.kafkaClasses.Polling;
import com.bankapi.security.JwtTokens;

import lombok.Getter;
import lombok.Setter;

/**
 * Message Controller of the RESTful API
 */
@RestController
@RequestMapping("api/v1/synpulse")
@Getter
@Setter
public class MessageController {
    // Objects used by the controller are declared here for unit testing
    private KafkaConsumerConfig config;
    private Polling poller;
    private CurrencyConverter converter;
    private JwtTokens jwtTokens;

    private int numPartitions;

    // No args constructor
    public MessageController() {
        config = new KafkaConsumerConfig();
        poller = new Polling();
        converter = new CurrencyConverter();
        jwtTokens = new JwtTokens();
        numPartitions = 100000;
    }

    // Argument constructor
    public MessageController(
        KafkaConsumerConfig config,
        Polling poller,
        CurrencyConverter converter,
        JwtTokens jwtTokens
    ) {
        this.config = config;
        this.poller = poller;
        this.converter = converter;
        this.jwtTokens = jwtTokens;
        this.numPartitions = 100000;
    }
    /** 
     * Processes a get request to our API Endpoint
     * For any date specified in the parameters, it selects only the transactions
     * in the same month and year, then gets the total amount of the debited
     * and credited transactions in USD. Since topics are organized by month 
     * and year, we read and then filter by user id
     * 
     * To access the AbstractAPI for currencies, it calls the CurrencyConverter
     * Object, which handles the conversion
     * 
     * @return Response Entity that holds the response object and the status of the operation
    */
    @GetMapping("")
    public ResponseEntity<ResponseObject> readTransactionInGivenMonth(
        @RequestParam("date") @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime date,
        @RequestHeader("Bearer") String bearer
    ) {
        String username = "";
        try {
            username = jwtTokens.parseJwt(bearer);
        }
        catch (Exception e) {
            return new ResponseEntity<ResponseObject>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Extracts the required month and year from the request parameters
        Month month =  date.getMonth();
        int year = date.getYear();

        // Creates a new response object to store the response to the user
        ResponseObject responseObject = new ResponseObject();

        // Based on the algorithm kafka uses to assign partitions based on the key (username)
        int partition = Utils.toPositive(Utils.murmur2(username.getBytes())) % numPartitions;
        KafkaConsumer<String, Transaction> consumer = config.transactionByIdConsumer(partition);
            
        /**
         * Uses the transaction list consumed and converts the currencies using
         * the currency converter object. That object also returns the total 
         * debit and credit, which we can send back in the body form.
         */
        List<Transaction> transactionList = poller.pollTransactions(consumer, username, month, year);

        // Instantiates a converter object to perform the conversion, it stores the transaction list in that object
        converter.assignList(transactionList);

        // Calls the convert function and stores the totals in the response object
        List<Float> balance = converter.convert();
        responseObject.setCreditTotal(balance.get(0));        
        responseObject.setDebitTotal(balance.get(1));

        System.out.println(responseObject);

        // Creates a response entity to send the object back
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<ResponseObject> entity = new ResponseEntity<>(responseObject, headers, HttpStatus.OK);

        return entity;
    }
}
