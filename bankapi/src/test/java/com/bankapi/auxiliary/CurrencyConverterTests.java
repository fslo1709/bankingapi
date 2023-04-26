package com.bankapi.auxiliary;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CurrencyConverter.class)
public class CurrencyConverterTests {
    CurrencyConverter currencyConverter;

    @BeforeEach
    public void setup() {
        currencyConverter = new CurrencyConverter();
    }

    @Test
    public void assignsTheList() {
        List<Transaction> expectedTransactionList = new ArrayList<>();
        expectedTransactionList.add(new Transaction("A", "USD 1", "IBAN", LocalDateTime.of(2023, 1, 1, 0, 0, 0), "Description"));
        
        currencyConverter.assignList(expectedTransactionList);
        assertEquals(expectedTransactionList.get(0).getId(), currencyConverter.getTransactionList().get(0).getId(), "It assigns the id correctly");
        assertEquals(expectedTransactionList.get(0).getAmount(), currencyConverter.getTransactionList().get(0).getAmount(), "It assigns the amount correctly");
        assertEquals(expectedTransactionList.get(0).getIban(), currencyConverter.getTransactionList().get(0).getIban(), "It assigns the iban correctly");
        assertEquals(expectedTransactionList.get(0).getDate(), currencyConverter.getTransactionList().get(0).getDate(), "It assigns the date correctly");
        assertEquals(expectedTransactionList.get(0).getDescription(), currencyConverter.getTransactionList().get(0).getDescription(), "It assigns the description correctly");
    }
}
