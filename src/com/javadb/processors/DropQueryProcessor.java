package com.javadb.processors;

import com.javadb.catalog.DatabaseTables;
import com.javadb.parsers.DropQueryParser;
import com.javadb.queries.DropQuery;
import com.javadb.tables.FileHandler;
import com.javadb.tables.Table;
import com.javadb.types.DataType;
import com.javadb.types.Value;

public class DropQueryProcessor {
    DropQuery dropQuery;

    public DropQueryProcessor(String dropStatement) {
        this.dropQuery = DropQueryParser.parse(dropStatement);
        process();
    }

    private void process() {
        if (dropQuery == null) return;
        if (dropQuery.getTableName().equals("database_tables") || dropQuery.getTableName().equals("database_columns")) {
            System.out.println("Cannot drop catalog tables");
            return;
        }
        try {
            DatabaseTables tablesCatalog = new DatabaseTables();
            boolean isPresent = tablesCatalog.isTablePresent(dropQuery.getTableName());
            tablesCatalog.close();
            if (!isPresent) {
                System.out.println("Table " + dropQuery.getTableName() + " not found");
                return;
            }
            Table table = new Table("database_tables");
            Value val = new Value(dropQuery.getTableName(), DataType.TEXT);
            table.deleteByValue(1, val);
            table.close();
            table = new Table("database_columns");
            table.deleteByValue(1, val);
            table.close();
            boolean deleted = FileHandler.delete(dropQuery.getTableName(), "tbl");
            if (deleted) {
                System.out.println("table " + dropQuery.getTableName() + " deleted successfully");
            } else {
                System.out.println("Could not delete the data file");
            }
        } catch(Exception e) {
            System.out.println("Something went wrong while processing drop table query");
        }
    }
}
