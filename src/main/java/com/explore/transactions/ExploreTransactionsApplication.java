package com.explore.transactions;

import com.explore.transactions.sandbox.Sandbox;
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
    sandbox.runSandbox();
  }
}
