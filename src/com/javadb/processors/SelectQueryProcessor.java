package com.javadb.processors;

import com.javadb.catalog.DatabaseColumns;
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

    SelectQueryProcessor(String selectStatement) {
        this.selectQuery = SelectQueryParser.parse(selectStatement);
        process();
    }

    private void process() {
        if (selectQuery != null) {
            try {
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
            } catch(Exception e) {
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
        for (TableColumn column: columns) {
            printColumn(column.getColumnName(), column.getDataType());
        }
        System.out.print("\n");
        for (LeafCell cell: cells) {
            for (Value val: cell.getValues()) {
                printColumn(val.toString(), val.getType());
            }
            System.out.print("\n");
        }
    }

    private void printColumn(String data, DataType type) {
        System.out.print("| ");
        System.out.print(data);
        int offset = type.getPrintOffset() - data.length();
        StringBuilder tail = new StringBuilder();
        while (offset > 0) {
            tail.append(" ");
            offset--;
        }
        tail.append(" |");
        System.out.print(tail);
    }
}
