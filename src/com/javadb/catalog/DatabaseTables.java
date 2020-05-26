package com.javadb.catalog;

import com.javadb.tables.Table;
import com.javadb.types.DataType;
import com.javadb.types.Value;

import java.io.IOException;

public class DatabaseTables {
    static final String tableName = "database_tables";
    static final String createQuery = "CREATE TABLE database_tables (rowid INT PRIMARY KEY, table_name TEXT)";
    private final Table table;

    /**
     * Constructor
     * @throws IOException while accessing the tableFile
     */
    public DatabaseTables() throws IOException {
        this.table = new Table(tableName);
    }

    /**
     * checks whether a table is present
     * @param tName String, name of the table to search for
     * @return boolean, true if the table is present
     */
    public boolean isTablePresent(String tName) {
        Value val = new Value(tName, DataType.TEXT);
        try {
            return table.hasValue(1, val);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void close() throws IOException {
        table.close();
    }
}
