package com.explore.transactions.database.item;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SimpleItemRowMapper implements RowMapper<SimpleItemDto> {

  @Override
  public SimpleItemDto mapRow(ResultSet rs, int rowNum) throws SQLException {
    return new SimpleItemDto(
        rs.getInt("itemID"),
        rs.getString("vmid"),
        rs.getString("itemName"),
        rs.getString("itemDesc")
    );
  }
}
