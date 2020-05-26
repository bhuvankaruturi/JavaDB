package com.javadb.queries;

public class InsertQuery {
    String tableName;
    String[] columnValues;

    /**
     * Constructor for the insert statement wrapper
     * @param tableName String, table name
     * @param columnValues String[], column values
     */
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
