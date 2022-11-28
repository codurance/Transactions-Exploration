package com.explore.transactions.database.action;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.jdbc.core.RowMapper;

public class ActionDto {

  public Integer id;
  public String name;
  public String description;

  public ActionDto(Integer id, String name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
  }

  public ActionDto(String name, String description) {
    this(null, name, description);
  }

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this,
        ToStringStyle.SHORT_PREFIX_STYLE);
  }

  public static class ActionRowMapper implements RowMapper<ActionDto> {
    @Override
    public ActionDto mapRow(ResultSet rs, int rowNum) throws SQLException {
      return new ActionDto(
          rs.getInt("Id"),
          rs.getString("Name"),
          rs.getString("Description")
      );
    }
  }
}
