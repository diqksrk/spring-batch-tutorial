package com.example.hellobatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Spring Boot 3 + Spring Batch 5 부터 @EnableBatchProcessing 불필요.
// @SpringBootApplication 하나면 끝.
@SpringBootApplication
public class HelloBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloBatchApplication.class, args);
	}
}
