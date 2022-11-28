package com.explore.transactions.sandbox;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("MsSql")
public class MsSqlSandbox extends Sandbox {

  public MsSqlSandbox(DataSource dataSource) {
    super(dataSource);
  }

  @Override
  protected void createTables() throws SQLException {
    dataSource.getConnection();//todo do things
    throw new RuntimeException("Not Yet Implemented");
  }

  @Override
  protected void selectFirstElementFromItemsTable(Connection connection) throws SQLException {
    throw new RuntimeException("Not Yet Implemented");
  }

  @Override
  protected void setTransactionIsolationLevel(Connection connection, IsolationLevel level)
      throws SQLException {
    throw new RuntimeException("Not Yet Implemented");
  }

  @Override
  protected String getTransactionIsolationLevel(Connection connection) throws SQLException {
    throw new RuntimeException("Not Yet Implemented");
  }

}
