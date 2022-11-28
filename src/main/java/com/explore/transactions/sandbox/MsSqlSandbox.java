package com.explore.transactions.sandbox;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
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
    //language=TSQL
    String CREATE_ACTION_TABLE_INSTRUCTION =
        "create table actions\n"
            + "(\n"
            + "    Id          int IDENTITY (1,1) PRIMARY KEY,\n"
            +
            "    Name        varchar(255) default '' not null,\n"
            + "    Description varchar(255)            null,\n"
            + "    constraint Name "
            + "unique (Name),\n"
            +
            ");\n"
            + "\n";
    //language=TSQL
    String CREATE_ITEMS_TABLE_INSTRUCTION =
        "create table items\n"
            + "(\n"
            + "    Id          int IDENTITY (1,1) PRIMARY KEY\n"
            + ");\n"
            + "\n";

    try (Statement statement = dataSource.getConnection().createStatement()) {
      statement.executeUpdate("drop table IF EXISTS actions;");
      statement.executeUpdate("drop table IF EXISTS items;");
      statement.executeUpdate(CREATE_ACTION_TABLE_INSTRUCTION);
      statement.executeUpdate(CREATE_ITEMS_TABLE_INSTRUCTION);

    }
  }

  @Override
  protected void selectFirstElementFromItemsTable(Connection connection) throws SQLException {
    //language=TSQL
    connection.createStatement().execute("select TOP 1 * from items;");
  }

  @Override
  protected void setTransactionIsolationLevel(Connection connection, IsolationLevel level)
      throws SQLException {
    connection
        .createStatement()
        .execute("SET TRANSACTION ISOLATION LEVEL " + level.msSqlStringValue);
  }

  @Override
  protected String getTransactionIsolationLevel(Connection connection) throws SQLException {
    //language=TSQL
    String MS_SQL_GET_ISOLATION_LEVEL_QUERY =
        "SELECT CASE transaction_isolation_level \n"
            + "    WHEN 0 THEN 'Unspecified' \n"
            + "    WHEN 1 THEN 'ReadUncommitted' \n"
            + "    WHEN 2 THEN 'ReadCommitted' \n"
            + "    WHEN 3 THEN 'Repeatable' \n"
            + "    WHEN 4 THEN 'Serializable' \n"
            + "    WHEN 5 THEN 'Snapshot' END AS TRANSACTION_ISOLATION_LEVEL \n"
            + "FROM sys.dm_exec_sessions \n"
            + "where session_id = @@SPID";
    ResultSet rs = connection.createStatement().executeQuery(MS_SQL_GET_ISOLATION_LEVEL_QUERY);
    rs.next();
    return rs.getString(1);
  }

}
