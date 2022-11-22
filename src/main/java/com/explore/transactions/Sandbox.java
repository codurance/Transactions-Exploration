package com.explore.transactions;

import static com.explore.transactions.IsolationLevel.READ_COMMITTED;
import static com.explore.transactions.IsolationLevel.REPEATABLE_READ;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import com.explore.transactions.dto.ActionDto;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class Sandbox {

  private Connection connectionA;
  private Connection connectionB;
  private Statement statementA;
  private Statement statementB;
  private final DataSource dataSource;

  public Sandbox(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public void runSandbox() throws SQLException {
    setTransactionIsolationLevel(REPEATABLE_READ);

    connectionA = dataSource.getConnection();
    connectionB = dataSource.getConnection();

    printTransactionIsolationLevelSeenFrom(connectionA);
    printTransactionIsolationLevelSeenFrom(connectionB);

    statementA = connectionA.createStatement();
    statementB = connectionB.createStatement();


    // TODO - close connection & statement
    statementA.execute("TRUNCATE TABLE actions");


    this.connectionA.setAutoCommit(false);
    this.connectionB.setAutoCommit(false);

    initNewTransactionsOnBothConnections();
    showAllActionsFromBothConnections("BEFORE: insert & commit");
    insert(statementB, new ActionDto("will be committed 1", "INSERTED by B"));
    insert(statementB, new ActionDto("will be committed 2", "INSERTED by B"));
    insert(statementB, new ActionDto("will be committed and updated", "INSERTED by B"));
    this.connectionB.commit();
    showAllActionsFromBothConnections("AFTER : insert & commit");


    initNewTransactionsOnBothConnections();
    showAllActionsFromBothConnections("BEFORE: insert & rollback");
    insert(statementB, new ActionDto("will be rolled back 1", "INSERTED by B"));
    insert(statementB, new ActionDto("will be rolled back 2", "INSERTED by B"));
    this.connectionB.rollback();
    showAllActionsFromBothConnections("AFTER : insert & rollback");


    initNewTransactionsOnBothConnections();
    showAllActionsFromBothConnections("BEFORE: update & commit <- Showcases Non-Repeatable Read");
    statementB.execute("UPDATE actions SET Description='UPDATED by B' WHERE Name='will be committed and updated'");
    this.connectionB.commit();
    showAllActionsFromBothConnections("AFTER : update & commit <- Showcases Non-Repeatable Read");


    initNewTransactionsOnBothConnections();
    showAllActionsFromBothConnections("BEFORE: NEW insert & commit <- Showcases Phantom Read BUT DOESN'T WORK");
    insert(statementB, new ActionDto("NEW action committed", "INSERTED by B"));
    this.connectionB.commit();
    showAllActionsFromBothConnections("AFTER : NEW insert & commit <- Showcases Phantom Read BUT DOESN'T WORK");


    initNewTransactionsOnBothConnections();
    showAllActionsFromBothConnections("BEFORE: insert & 'nothing' (no commit or rollback) <- Showcases Dirty Read");
    insert(statementB, new ActionDto("wont be committed or rolled back 1", "INSERTED by B"));
    insert(statementB, new ActionDto("wont be committed or rolled back 2", "INSERTED by B"));
    showAllActionsFromBothConnections("AFTER : insert & 'nothing' (no commit or rollback) <- Showcases Dirty Read");
  }

  private void initNewTransactionsOnBothConnections() throws SQLException {
    this.connectionA.commit();
    this.connectionB.commit();
    System.out.println("");
    System.out.println("");
  }

  private void showAllActionsFromBothConnections(String tag) throws SQLException {
    System.out.println(tag);
    System.out.println("-----------------------------------------------------------------------------------");
    System.out.println("Connection A - " + getTransactionIsolationLevel(connectionA));
    printAllActionsSeenFrom(connectionA);
    System.out.println("");
    System.out.println("Connection B - " + getTransactionIsolationLevel(connectionB));
    printAllActionsSeenFrom(connectionB);
    System.out.println("");
  }

  private void setTransactionIsolationLevel(IsolationLevel level) throws SQLException {
    dataSource
        .getConnection()
        .createStatement()
        .execute("SET GLOBAL TRANSACTION ISOLATION LEVEL " + level.mySqlStringValue);
  }

  private void printTransactionIsolationLevelSeenFrom(Connection connection) throws SQLException {
    String transactionIsolationLevel = getTransactionIsolationLevel(connection);
    System.out.println("transactionIsolationLevel = " + transactionIsolationLevel);
  }

  private static String getTransactionIsolationLevel(Connection connection) throws SQLException {
    ResultSet rs = connection.createStatement().executeQuery("SELECT @@TX_ISOLATION");
    rs.next();
    return rs.getString(1);
  }

  private void printAllActionsSeenFrom(Connection connection) throws SQLException {
    getAllActions(connection).forEach(System.out::println);
  }

  private List<ActionDto> getAllActions(Connection connection) throws SQLException {
    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery("SELECT * FROM actions");
    List<ActionDto> actions = new ArrayList<>();

    while (resultSet.next()) {
      actions.add(new ActionDto(
          resultSet.getInt("Id"),
          resultSet.getString("Name"),
          resultSet.getString("Description")
      ));
    }
    return actions;
  }

  private void insert(Statement statement, ActionDto action) throws SQLException {
    statement.execute(
        String.format("INSERT INTO actions (Name, Description) VALUES ('%s', '%s')", action.name, action.description));
  }

  private void firstExperiment() {
    System.out.println("In sandbox");
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

    List<SimpleItem> items = jdbcTemplate.query(
        "select * from items LIMIT 20",
        new SimpleItemRowMapper()
    );

    items.forEach(System.out::println);

    String vmid = UUID.randomUUID().toString();
    int something = jdbcTemplate.update(
        new SimpleItem(1, vmid, "Hello", "Hi").insertQueryWithoutId()
    );
    System.out.println("something = " + something);

    List<SimpleItem> itemsAfterInsert = jdbcTemplate.query(
        "select * from items where itemName = 'Hello'",
        new SimpleItemRowMapper()
    );
    itemsAfterInsert.forEach(System.out::println);
  }


}

class SimpleItemRowMapper implements RowMapper<SimpleItem> {

  @Override
  public SimpleItem mapRow(ResultSet rs, int rowNum) throws SQLException {
    return new SimpleItem(
        rs.getInt("itemID"),
        rs.getString("vmid"),
        rs.getString("itemName"),
        rs.getString("itemDesc")
    );
  }
}

class SimpleItem {

  public final int id;
  public final String vmid;
  public final String name;
  public final String description;

  public SimpleItem(int id, String vmid, String name, String description) {
    this.id = id;
    this.vmid = vmid;
    this.name = name;
    this.description = description;
  }

  public String insertQuery() {
    return String.format(
        "INSERT into items (itemID, vmid, itemName, itemDesc) VALUES (%d, '%s', '%s', '%s')",
        id,
        vmid,
        name,
        description
    );
  }

  public String insertQueryWithoutId() {
    return String.format(
        "INSERT into items (vmid, itemName, itemDesc) VALUES ('%s', '%s', '%s')",
        vmid,
        name,
        description
    );
  }

  @Override
  public String toString() {
    return reflectionToString(this, MULTI_LINE_STYLE);
  }
}