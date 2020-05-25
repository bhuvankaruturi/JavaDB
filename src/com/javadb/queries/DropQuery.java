package com.javadb.queries;

public class DropQuery {
    String tableName;

    public DropQuery(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }
}
