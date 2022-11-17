package com.explore.transactions.config;

import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

  @Bean
  public DataSource dataSource() {
    return DataSourceBuilder.create()
        .driverClassName("com.mysql.cj.jdbc.Driver")
        .url(
            ***REMOVED***)
        .username(***REMOVED***)
        .password(***REMOVED***)
        .build();
  }


}
