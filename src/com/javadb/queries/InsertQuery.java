package com.javadb.queries;

public class InsertQuery {
    String tableName;
    String[] columnValues;

    public InsertQuery(String tableName, String[] columnValues) {
        this.tableName = tableName;
        this.columnValues = columnValues;
    }

    public String getTableName() {
        return tableName;
    }

    public String[] getColumnValues() {
        return columnValues;
    }
}
