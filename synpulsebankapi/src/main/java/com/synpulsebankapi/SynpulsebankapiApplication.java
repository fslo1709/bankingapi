package com.synpulsebankapi;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
public class SynpulsebankapiApplication {
 
	public static void main(String[] args) {
		SpringApplication.run(SynpulsebankapiApplication.class, args);
	}

	// @Bean
	// CommandLineRunner commandLineRunner(KafkaTemplate<String, Transaction> kafkaTemplate) {
	// 	return args -> {
	// 		kafkaTemplate.send("Transactions", new Transaction(
	// 			"89d3o179-abcd-465b-o9ee-e2d5f6ofEld46", 
	// 			"GBP 100-", 
	// 			"CH93-0000-0000-0000-0000-0", 
	// 			"01-10-2020", 
	// 			"Online payment CHF"));
	// 	};
	// }
}
