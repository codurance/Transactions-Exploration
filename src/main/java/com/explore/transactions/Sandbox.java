package com.explore.transactions;

import static com.explore.transactions.IsolationLevel.READ_COMMITTED;
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
    setTransactionIsolationLevel(READ_COMMITTED);

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

    insert(new ActionDto("will be committed 1", "move forward"));
    insert(new ActionDto("will be committed 2", "make cake"));
    insert(new ActionDto("will be committed and updated", "ORIGINAL"));
    this.connectionA.commit();
    showAllActionsFromBothConnections("Just after insert & commit");

    insert(new ActionDto("will be rolled back 1", "move forward"));
    insert(new ActionDto("will be rolled back 2", "make cake"));
    this.connectionA.rollback();
    showAllActionsFromBothConnections("Just after insert & rollback");

    statementB.execute("UPDATE actions SET Description='MANIPULATED' WHERE Name='will be committed and updated'");
    this.connectionB.commit();
    showAllActionsFromBothConnections("Just after update & commit");

    insert(new ActionDto("wont be committed or rolled back 1", "go for a bike ride"));
    insert(new ActionDto("wont be committed or rolled back 2", "go for a bike ride"));
    showAllActionsFromBothConnections("Just after insert & 'nothing' (no commit or rollback)");
  }

  private void showAllActionsFromBothConnections(String tag) throws SQLException {
    System.out.println("");
    System.out.println(tag);
    System.out.println("connectionA = " + connectionA);
    printAllActionsSeenFrom(connectionA);
    System.out.println("connectionB = " + connectionB);
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
    ResultSet rs = connection.createStatement().executeQuery("SELECT @@TX_ISOLATION");
    rs.next();
    String transactionIsolationLevel = rs.getString(1);
    System.out.println("transactionIsolationLevel = " + transactionIsolationLevel);
  }

  private void printAllActionsSeenFrom(Connection connection) throws SQLException {
    getAllActions(connection.createStatement()).forEach(System.out::println);
  }

  private List<ActionDto> getAllActions(Statement statement1) throws SQLException {
    ResultSet resultSet = statement1.executeQuery("SELECT * FROM actions");
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

  private void insert(ActionDto action) throws SQLException {
    statementA.execute(
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