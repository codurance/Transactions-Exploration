package com.explore.transactions.sandbox;

public enum IsolationLevel {

  READ_UNCOMMITTED("READ UNCOMMITTED"),
  READ_COMMITTED("READ COMMITTED"),
  REPEATABLE_READ("REPEATABLE READ"),
  SERIALIZABLE("SERIALIZABLE");
  public final String mySqlStringValue;
  public final String msSqlStringValue;

  IsolationLevel(String mySqlStringValue) {
    this.mySqlStringValue = mySqlStringValue;
    this.msSqlStringValue = mySqlStringValue; // The MS SQL string value is the same as for MySQL
  }
}
