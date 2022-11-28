package com.explore.transactions.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DataSourceConfig {

  // Add to .env file
  @Value("${env.MYSQL_JDBC_URL}")
  String mySqlJdbcUrl;
  @Value("${env.MYSQL_JDBC_USER}")
  String mySqlJdbcUsername;
  @Value("${env.MYSQL_JDBC_PASS}")
  String mySqlJdbcPassword;

  @Value("${env.MSSQL_JDBC_URL}")
  String msSqlJdbcUrl;
  @Value("${env.MSSQL_JDBC_USER}")
  String msSqlJdbcUsername;
  @Value("${env.MSSQL_JDBC_PASS}")
  String msSqlJdbcPassword;



  @Bean
  @Profile("MySql")
  public DataSource mySqlDataSource() {
    return DataSourceBuilder.create()
        .driverClassName("com.mysql.cj.jdbc.Driver")
        .url(mySqlJdbcUrl)
        .username(mySqlJdbcUsername)
        .password(mySqlJdbcPassword)
        .build();
  }

  @Bean
  @Profile("MsSql")
  public DataSource msSqlDataSource() {
    return DataSourceBuilder.create()
        .driverClassName("com.mysql.cj.jdbc.Driver")
        .url(msSqlJdbcUrl)
        .username(msSqlJdbcUsername)
        .password(msSqlJdbcPassword)
        .build();
  }


}
