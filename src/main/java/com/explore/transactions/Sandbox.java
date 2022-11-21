package com.explore.transactions;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import com.explore.transactions.dto.ActionDto;
import com.explore.transactions.dto.ActionDto.ActionRowMapper;
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

  private final Connection connection;
  private final Statement statement;
  private final DataSource dataSource;

  public Sandbox(DataSource dataSource) throws SQLException {
    this.dataSource = dataSource;
    this.connection = dataSource.getConnection();
    statement = connection.createStatement();
  }

  public void runSandbox() throws SQLException {
    statement.execute("TRUNCATE TABLE actions");

    // TODO - close connection statement

    insert(new ActionDto("move", "move forward"));
    insert(new ActionDto("bake", "make cake"));
    insert(new ActionDto("exercise", "go for a bike ride"));

    List<ActionDto> actions = getAllActions();

    actions.forEach(System.out::println);
  }

  private List<ActionDto> getAllActions() throws SQLException {
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

  private void insert(ActionDto action) throws SQLException {
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