package com.synpulsebankapi;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.time.Month;

@SpringBootApplication
public class SynpulsebankapiApplication {
 
	public static void main(String[] args) {
		SpringApplication.run(SynpulsebankapiApplication.class, args);
	}

	// @Bean
	// CommandLineRunner commandLineRunner(KafkaTemplate<String, Transaction> kafkaTemplate) {

	// 	LocalDateTime lTime = LocalDateTime.of(2016, Month.JUNE, 29, 12, 00, 00);
	// 	return args -> {
	// 		kafkaTemplate.send(
	// 			"Transactions", 
	// 			"89d3o179-abcd-465b-o9ee-e2d5f6ofEld46", 
	// 			new Transaction(
	// 				"89d3o179-abcd-465b-o9ee-e2d5f6ofEld46",
	// 				"CHF 75",
	// 				"CH93-0000-0000-0000-0000-0",
	// 				lTime,
	// 				"Online payment CHF"
	// 		));
	// 	};
	// }
}
