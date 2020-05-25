package com.javadb.queries;

public class CreateQuery {
    String tableName;
    String[] columnNames;
    String[] columnTypes;
    String[] constraints;

    public CreateQuery(String tableName, String[] columnNames, String[] columnTypes, String[] constraints) {
        this.tableName = tableName;
        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
        this.constraints = constraints;
    }

    public String getTableName() {
        return tableName;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public String[] getColumnTypes() {
        return columnTypes;
    }

    public String[] getConstraints() {
        return constraints;
    }
}
