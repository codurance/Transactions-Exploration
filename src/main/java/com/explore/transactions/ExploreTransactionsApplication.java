package com.explore.transactions;

import java.sql.SQLException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExploreTransactionsApplication implements CommandLineRunner {

  private final Sandbox sandbox;

  public ExploreTransactionsApplication(Sandbox sandbox) {
    this.sandbox = sandbox;
  }

  public static void main(String[] args) {
    SpringApplication.run(ExploreTransactionsApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    System.out.println("Hello\uD83D\uDE03 ");
    sandbox.runSandbox();
  }
}
