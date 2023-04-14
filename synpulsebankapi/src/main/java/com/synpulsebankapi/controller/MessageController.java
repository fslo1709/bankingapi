package com.synpulsebankapi.controller;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.synpulsebankapi.auxiliary.CurrencyConverter;
import com.synpulsebankapi.auxiliary.JwtTokens;
import com.synpulsebankapi.auxiliary.Token;
import com.synpulsebankapi.auxiliary.Transaction;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

@RestController
@RequestMapping("api/v1/synpulse")
public class MessageController {
    
    @Autowired
    private KafkaTemplate<String, Transaction> transactionKafkaTemplate;

    public MessageController(KafkaTemplate<String, Transaction> kafkaTemplate) {
        this.transactionKafkaTemplate = kafkaTemplate;
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
    */
    @GetMapping("")
    public void readTransactionInGivenMonth(
        @RequestParam("date") @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime date
    ) {
        Month month =  date.getMonth();
        int year = date.getYear();
        CurrencyConverter converter = new CurrencyConverter(null);
		List<String> balance = converter.convert();

        /*
        try {
            
        }
        finally {
            consumer.close();
        } */
    }

    /**
     * Simple entry point to get a valid token for verifying this microservice works
     * 
     * @return String with a valid token as a get response
     */
    @GetMapping("/getToken")
    public String getToken() {
        return JwtTokens.getToken();
    }

    // This code was here just to check that the tokens are working correctly
    //
    // @PostMapping("/checkToken")     
    // public String checkToken(
    //     @RequestBody Token token
    // ) {
    //     Jws<Claims> jws = JwtTokens.parseJwt(token.getToken());
    //     System.out.println(jws);
    //     return jws.getSignature();
    // }

    /**
     * Post mapping to update the Transactions topic
     * Going to delete later, since populating the data stream is outside
     * the scope of the microservice
     * 
     * @param id String to identify the transaction
     * @param amount String representing the amount transacted and the currency
     * @param iban String representing the account IBAN
     * @param date Date_time object in ISO format
     * @param description Description of the transaction
     */
    // @PostMapping("")
    // public void publish(
    //     @RequestParam("id") String id,
    //     @RequestParam("amount") String amount,
    //     @RequestParam("iban") String iban, 
    //     @RequestParam("date") @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime date, 
    //     @RequestParam("description") String description
    // ) {

    //     // Object to store the message, reads from parameters
    //     Transaction transactionObject = new Transaction();
    //     transactionObject.setId(id);
    //     transactionObject.setAmount(amount);
    //     transactionObject.setIban(iban);
    //     transactionObject.setDate(date);
    //     transactionObject.setDescription(description);

    //     // Used to send the messages to kafka asynchronously
    //     CompletableFuture<SendResult<String, Transaction>> future = 
    //         transactionKafkaTemplate.send(
    //             "Transactions", 
    //             transactionObject.getId(),
    //             transactionObject);

    //     // Callback that logs when the message is sent successfully
    //     future.whenComplete((result, ex) -> {
    //         if (ex == null) {
    //             System.out.println(
    //                 "Message sent with id: " + transactionObject.getId()
    //             );
    //         }
    //         else {
    //             System.out.println(
    //                 "Error sending message: " + transactionObject.getId() + 
    //                 "\nError: " + ex.getMessage()
    //             );
    //         }
    //     });
    // }
    
}
