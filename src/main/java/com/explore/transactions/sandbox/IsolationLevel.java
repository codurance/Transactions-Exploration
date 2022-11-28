package com.explore.transactions.sandbox;

public enum IsolationLevel {

  READ_UNCOMMITTED("READ UNCOMMITTED"),
  READ_COMMITTED("READ COMMITTED"),
  REPEATABLE_READ("REPEATABLE READ"),
  SERIALIZABLE("SERIALIZABLE");
  public final String mySqlStringValue;

  IsolationLevel(String mySqlStringValue) {
    this.mySqlStringValue = mySqlStringValue;
  }
}
