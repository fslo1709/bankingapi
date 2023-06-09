package com.bankapi.controller;

import java.util.List;

import com.bankapi.auxiliary.Transaction;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Object used to send the response of the microservice
 */
@Getter
@Setter
@Data
public class ResponseObject {
    float creditTotal;
    float debitTotal;
    List<Transaction> transactions;
}
