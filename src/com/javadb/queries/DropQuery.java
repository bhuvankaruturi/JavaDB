package com.javadb.queries;

public class DropQuery {
    String tableName;

    /**
     * Constructor for the drop table statement wrapper
     * @param tableName String, table name
     */
    public DropQuery(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }
}
