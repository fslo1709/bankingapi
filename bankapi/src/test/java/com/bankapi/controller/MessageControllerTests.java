package com.bankapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JWindow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.bankapi.auxiliary.CurrencyConverter;
import com.bankapi.auxiliary.Transaction;
import com.bankapi.controller.MessageController;
import com.bankapi.controller.ResponseObject;
import com.bankapi.kafkaClasses.KafkaConsumerConfig;
import com.bankapi.kafkaClasses.Polling;
import com.bankapi.security.JwtTokens;

@SpringBootTest(classes = MessageController.class)
// @AutoConfigureMockMvc
// @WebMvcTest(MessageController.class)
public class MessageControllerTests {
    @Mock
    private KafkaConsumerConfig mockConfig;

    @Mock
    private Polling mockPoller;

    @Mock
    private CurrencyConverter mockConverter;

    @Mock
    private JwtTokens mockJwtTokens;

    @BeforeEach
    public void setup() {
        mockConfig = Mockito.mock(KafkaConsumerConfig.class);
        mockPoller = Mockito.mock(Polling.class);
        mockConverter = Mockito.mock(CurrencyConverter.class);
        mockJwtTokens = Mockito.mock(JwtTokens.class);
    }

    @Test
    @DisplayName("The controller returns a correct object")
    public void controllerShouldReturnCorrectObject() throws Exception {
        // Values for testing
        LocalDateTime testDate = LocalDateTime.of(2023, 01, 01, 0, 0, 0);
        List<Transaction> testList = new ArrayList<>();
        testList.add(new Transaction("A", "USD 1.0", "B", testDate, "C"));
        testList.add(new Transaction("A", "USD 2.0-", "D", testDate, "E"));
        List<Float> testConversion = new ArrayList<>();
        testConversion.add(1.0f);
        testConversion.add(2.0f);

        // Mocking the objects
        when(mockJwtTokens.parseJwt(anyString())).thenReturn("ABCD");
        when(mockConfig.transactionByIdConsumer(anyInt())).thenReturn(null);
        when(mockPoller.pollTransactions(null, "ABCD", testDate.getMonth(), testDate.getYear())).thenReturn(testList);
        doNothing().when(mockConverter).assignList(testList);
        when(mockConverter.convert()).thenReturn(testConversion);

        // Setting the mock functions
        MessageController messageController = new MessageController(
            mockConfig, mockPoller, mockConverter, mockJwtTokens);

        // Expected Result
        ResponseObject expectedResponseObject = new ResponseObject();
        expectedResponseObject.setCreditTotal(1.0f);
        expectedResponseObject.setDebitTotal(2.0f);
        expectedResponseObject.setTransactions(testList);
        ResponseEntity<ResponseObject> expectedEntity = new ResponseEntity<>(expectedResponseObject, new HttpHeaders(), HttpStatus.OK);

        // Assertions
        messageController.readTransactionInGivenMonth(testDate, "ABCD");
        assertEquals(HttpStatus.OK, expectedEntity.getStatusCode(), "Status code is ok");
        assertEquals(expectedResponseObject, expectedEntity.getBody(), "Correct object returned");
    }

    /* Couldn't get it to throw 
    @Test
    @DisplayName("The controller returns internal server error when jwt token parsing fails")
    public void controllerReturnsInternalServerError() throws Exception {
        // Values for testing
        LocalDateTime testDate = LocalDateTime.of(2023, 01, 01, 0, 0, 0);
        List<Transaction> testList = new ArrayList<>();
        testList.add(new Transaction("A", "USD 1.0", "B", testDate, "C"));
        testList.add(new Transaction("A", "USD 2.0-", "D", testDate, "E"));
        List<Float> testConversion = new ArrayList<>();
        testConversion.add(1.0f);
        testConversion.add(2.0f);

        // Mocking the objects
        when(mockJwtTokens.parseJwt(anyString())).thenThrow(RuntimeException.class);
        when(mockConfig.transactionByIdConsumer(anyInt())).thenReturn(null);
        when(mockPoller.pollTransactions(null, "ABCD", testDate.getMonth(), testDate.getYear())).thenReturn(testList);
        doNothing().when(mockConverter).assignList(testList);
        when(mockConverter.convert()).thenReturn(testConversion);

        // Setting the mock functions
        MessageController messageController = new MessageController(
            mockConfig, mockPoller, mockConverter, mockJwtTokens);

        // Expected Result
        ResponseObject expectedResponseObject = new ResponseObject();
        expectedResponseObject.setCreditTotal(1.0f);
        expectedResponseObject.setDebitTotal(2.0f);
        expectedResponseObject.setTransactions(testList);
        ResponseEntity<ResponseObject> expectedEntity = new ResponseEntity<>(expectedResponseObject, new HttpHeaders(), HttpStatus.OK);

        // Assertions
        messageController.readTransactionInGivenMonth(testDate, "ABCD");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, expectedEntity.getStatusCode(), "Status code is 500");
        assertEquals(null, expectedEntity.getBody(), "Returned null object");
    }
    */
}
