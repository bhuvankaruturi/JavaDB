package com.javadb.queries;

public class CreateQuery {
    String tableName;
    String[] columnNames;
    String[] columnTypes;
    String[] constraints;

    /**
     * Constructor for create table statement wrapper
     * @param tableName String, table name
     * @param columnNames String[], column names
     * @param columnTypes String[], column types
     * @param constraints String[], constraints on columns
     */
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
