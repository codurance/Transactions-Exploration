package com.explore.transactions;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExploreTransactionsApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ExploreTransactionsApplication.class, args);
	}

	@Override
	public void run(String... args) {
		System.out.println("Hello\uD83D\uDE03 ");
	}
}
