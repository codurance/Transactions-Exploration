package com.explore.transactions;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class Sandbox {

  private final JdbcTemplate jdbcTemplate;

  public Sandbox(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public void runSandbox() {

    System.out.println("In sandbox");

    List<SimpleItem> items = jdbcTemplate.query(
        "select * from items LIMIT 20",
        (rs, rowNum) -> new SimpleItem(
            rs.getInt("itemID"),
            rs.getString("itemName"),
            rs.getString("itemDesc")
        ));

    items.forEach(System.out::println);
  }

}

class SimpleItem {

  public final int id;
  public final String name;
  public final String description;

  public SimpleItem(int id, String name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
  }

  @Override
  public String toString() {
    return reflectionToString(this, MULTI_LINE_STYLE);
  }
}