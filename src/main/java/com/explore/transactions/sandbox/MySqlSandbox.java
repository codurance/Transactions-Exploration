package com.explore.transactions.sandbox;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("MySql")
public class MySqlSandbox extends Sandbox {

  public MySqlSandbox(DataSource dataSource) {
    super(dataSource);
  }

  @Override
  protected void createTables() throws SQLException {
    // Do nothing => We're using the original EMR DB for mySql, no need to create tables
  }

  @Override
  protected void selectFirstElementFromItemsTable(Connection connection) throws SQLException {
    connection.createStatement().execute("select * from items LIMIT 1;");
  }

  @Override
  protected void setTransactionIsolationLevel(Connection connection, IsolationLevel level)
      throws SQLException {
    connection
        .createStatement()
        .execute("SET SESSION TRANSACTION ISOLATION LEVEL " + level.mySqlStringValue);
  }

  @Override
  protected String getTransactionIsolationLevel(Connection connection) throws SQLException {
    ResultSet rs = connection.createStatement().executeQuery("SELECT @@TX_ISOLATION");
    rs.next();
    return rs.getString(1);
  }
}
