package com.synpulsebankapi;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Transaction object used to write and read from Kafka
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Transaction {
    private String id;
    private String amount;
    private String iban;
    private LocalDateTime date;
    private String description;
}
