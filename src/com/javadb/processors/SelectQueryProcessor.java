package com.javadb.processors;

import com.javadb.catalog.DatabaseColumns;
import com.javadb.catalog.DatabaseTables;
import com.javadb.parsers.SelectQueryParser;
import com.javadb.queries.SelectQuery;
import com.javadb.tables.Table;
import com.javadb.trees.LeafCell;
import com.javadb.types.DataType;
import com.javadb.types.TableColumn;
import com.javadb.types.Value;

import java.util.List;

public class SelectQueryProcessor {
    SelectQuery selectQuery;

    public SelectQueryProcessor(String selectStatement) {
        this.selectQuery = SelectQueryParser.parse(selectStatement);
        process();
    }

    private void process() {
        if (selectQuery != null) {
            try {
                DatabaseTables tablesCatalog = new DatabaseTables();
                boolean isPresent = tablesCatalog.isTablePresent(selectQuery.getTableName());
                if (!isPresent) {
                    System.out.println("Table " + selectQuery.getTableName() + " not present");
                    return;
                }
                tablesCatalog.close();
                Table table = new Table(selectQuery.getTableName());
                DatabaseColumns columnCatalog = new DatabaseColumns();
                TableColumn[] columns = columnCatalog.getColumnTypes(selectQuery.getTableName());
                columnCatalog.close();
                List<LeafCell> cells;
                if (selectQuery.isHasWhereClause()) {
                    short ordinalVal = findColumnNumber(columns, selectQuery.getClauseCol());
                    if (ordinalVal < 0) {
                        System.out.println("Column " + selectQuery.getClauseCol() + " not present in table " + selectQuery.getTableName());
                        return;
                    }
                    Value val = new Value(selectQuery.getClauseVal(), columns[ordinalVal].getDataType());
                    cells = table.selectByValue(ordinalVal, val);
                } else {
                    cells = table.selectAll();
                }
                table.close();
                print(cells, columns);
                System.out.println("found " + cells.size() + " row(s)");
            } catch(Exception e) {
                e.printStackTrace();
                System.out.println("Something went wrong while processing select query");
            }
        }
    }

    private short findColumnNumber(TableColumn[] columns, String columnName) {
        for (TableColumn column : columns) {
            if (column.getColumnName().equals(columnName)) {
                return column.getOrdinalVal();
            }
        }
        return -1;
    }

    private void print(List<LeafCell> cells, TableColumn[] columns) {
        System.out.print("| ");
        int headerLength = 2;
        for (TableColumn column: columns) {
            headerLength += printColumn(column.getColumnName(), column.getDataType());
        }
        System.out.print("\n");
        while (headerLength > 0) {
            System.out.print("_");
            headerLength--;
        }
        System.out.print("\n");
        for (LeafCell cell: cells) {
            System.out.print("| ");
            for (Value val: cell.getValues()) {
                printColumn(val.toString(), val.getType());
            }
            System.out.print("\n");
        }
    }

    private int printColumn(String data, DataType type) {
        System.out.print(data);
        int offset = type.getPrintOffset() - data.length();
        StringBuilder tail = new StringBuilder();
        while (offset > 0) {
            tail.append(" ");
            offset--;
        }
        tail.append(" | ");
        System.out.print(tail);
        return (type.getPrintOffset() + 3);
    }
}
