package com.explore.transactions.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

  // Add to .env file
  @Value("${env.JDBC_URL}")
  String jdbcUrl;
  @Value("${env.JDBC_USER}")
  String jdbcUsername;
  @Value("${env.JDBC_PASS}")
  String jdbcPassword;



  @Bean
  public DataSource dataSource() {
    return DataSourceBuilder.create()
        .driverClassName("com.mysql.cj.jdbc.Driver")
        .url(jdbcUrl)
        .username(jdbcUsername)
        .password(jdbcPassword)
        .build();
  }


}
