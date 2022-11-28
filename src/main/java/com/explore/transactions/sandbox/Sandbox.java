package com.explore.transactions.sandbox;

import static com.explore.transactions.sandbox.IsolationLevel.REPEATABLE_READ;

import com.explore.transactions.database.action.ActionDto;
import com.explore.transactions.database.item.SimpleItemDto;
import com.explore.transactions.database.item.SimpleItemRowMapper;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;


public abstract class Sandbox {

  private Connection connectionA;
  private Connection connectionB;
  private Statement statementA;
  private Statement statementB;
  public final DataSource dataSource;

  protected abstract void createTables()
      throws SQLException;

  protected abstract void selectFirstElementFromItemsTable(Connection connection)
      throws SQLException;

  protected abstract void setTransactionIsolationLevel(Connection connection, IsolationLevel level)
      throws SQLException;

  protected abstract String getTransactionIsolationLevel(Connection connection)
      throws SQLException;

  public Sandbox(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public void runSandbox() throws SQLException {
    createTables();

    connectionA = dataSource.getConnection();
    connectionB = dataSource.getConnection();

    IsolationLevel isolationLevel = REPEATABLE_READ;
    setTransactionIsolationLevel(connectionA, isolationLevel);
    setTransactionIsolationLevel(connectionB, isolationLevel);

    statementA = connectionA.createStatement();
    statementB = connectionB.createStatement();

    // TODO - close connection & statement
    statementA.execute("TRUNCATE TABLE actions");

    this.connectionA.setAutoCommit(false);
    this.connectionB.setAutoCommit(false);

    initNewTransactionsOnBothConnections();
    showAllActionsFromBothConnections(
        "BEFORE: insert & commit <-- Showcases phantom read\n(select empty set, but not empty on second select)");
    insert(statementB, new ActionDto("will be committed 1", "INSERTED by B"));
    insert(statementB, new ActionDto("will be committed 2", "INSERTED by B"));
    insert(statementB, new ActionDto("will be committed and updated", "INSERTED by B"));
    this.connectionB.commit();
    showAllActionsFromBothConnections(
        "AFTER : insert & commit <-- Showcases phantom read\n(select empty set, but not empty on second select)");

    initNewTransactionsOnBothConnections();
    showAllActionsFromBothConnections("BEFORE: insert & rollback");
    insert(statementB, new ActionDto("will be rolled back 1", "INSERTED by B"));
    insert(statementB, new ActionDto("will be rolled back 2", "INSERTED by B"));
    this.connectionB.rollback();
    showAllActionsFromBothConnections("AFTER : insert & rollback");

    initNewTransactionsOnBothConnections();
    showAllActionsFromBothConnections(
        "BEFORE: update & commit in Connection B <- Showcases Non-Repeatable Read");
    String rowToUpdateName = "will be committed and updated";
    statementB.execute(
        "UPDATE actions SET Description='UPDATED by B' WHERE Name='" + rowToUpdateName + "'");
    this.connectionB.commit();
    showAllActionsFromBothConnections(
        "AFTER : update & commit in Connection B <- Showcases Non-Repeatable Read");

    initNewTransactionsOnBothConnections();
    showAllActionsFromBothConnections(
        "BEFORE: insert & 'nothing' (no commit or rollback) <- Showcases Dirty Read");
    insert(statementB, new ActionDto("wont be committed or rolled back 1", "INSERTED by B"));
    insert(statementB, new ActionDto("wont be committed or rolled back 2", "INSERTED by B"));
    showAllActionsFromBothConnections(
        "AFTER : insert & 'nothing' (no commit or rollback) <- Showcases Dirty Read");
  }

  private void initNewTransactionsOnBothConnections() throws SQLException {
    connectionA.commit();
    selectFirstElementFromItemsTable(connectionA);
    connectionB.commit();
    selectFirstElementFromItemsTable(connectionB);
    System.out.println("");
    System.out.println("");
  }

  private void showAllActionsFromBothConnections(String tag) throws SQLException {
    System.out.println(tag);
    System.out.println(
        "-----------------------------------------------------------------------------------");
    System.out.println("Connection A - " + getTransactionIsolationLevel(connectionA));
    printAllActionsSeenFrom(connectionA);
    System.out.println("");
    System.out.println("Connection B - " + getTransactionIsolationLevel(connectionB));
    printAllActionsSeenFrom(connectionB);
    System.out.println("");
  }


  private void printAllActionsSeenFrom(Connection connection) throws SQLException {
    queryActionTable(connection, "SELECT * FROM actions").forEach(System.out::println);
  }

  private List<ActionDto> queryActionTable(Connection connection, String sql) throws SQLException {
    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery(sql);
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
        String.format("INSERT INTO actions (Name, Description) VALUES ('%s', '%s')", action.name,
            action.description));
  }

  private void firstExperiment() {
    System.out.println("In sandbox");
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

    List<SimpleItemDto> items = jdbcTemplate.query(
        "select * from items LIMIT 20",
        new SimpleItemRowMapper()
    );

    items.forEach(System.out::println);

    String vmid = UUID.randomUUID().toString();
    int something = jdbcTemplate.update(
        new SimpleItemDto(1, vmid, "Hello", "Hi").insertQueryWithoutId()
    );
    System.out.println("something = " + something);

    List<SimpleItemDto> itemsAfterInsert = jdbcTemplate.query(
        "select * from items where itemName = 'Hello'",
        new SimpleItemRowMapper()
    );
    itemsAfterInsert.forEach(System.out::println);
  }

}




