package com.javadb.processors;

import com.javadb.catalog.Bootstrap;
import com.javadb.catalog.DatabaseColumns;
import com.javadb.catalog.DatabaseTables;
import com.javadb.parsers.CreateQueryParser;
import com.javadb.queries.CreateQuery;
import com.javadb.tables.Table;
import com.javadb.trees.LeafCell;
import com.javadb.types.DataType;
import com.javadb.types.TableColumn;
import com.javadb.types.Value;

import java.util.ArrayList;
import java.util.List;

public class CreateQueryProcessor {
    CreateQuery createQuery;

    public CreateQueryProcessor(String createStatement) {
        this.createQuery = CreateQueryParser.parse(createStatement);
        process();
    }

    private void process() {
        if (createQuery != null) {
            try {
                DatabaseTables catalogTable = new DatabaseTables();
                boolean isPresent = catalogTable.isTablePresent(createQuery.getTableName());
                catalogTable.close();
                if (!Bootstrap.initialized || !isPresent) {
                    Table table = new Table(createQuery.getTableName());
                    table.close();
                    table = new Table("database_columns");
                    int key = table.getMaxRowId() + 1;
                    List<LeafCell> cells = new ArrayList<>();
                    for (int i = 0; i < createQuery.getColumnNames().length; i++) {
                        Value[] values = getValues(key, i);
                        if (values == null) return;
                        int payloadSize = InsertQueryProcessor.getPayloadSize(values);
                        cells.add(new LeafCell(key, (short)payloadSize, (byte)values.length, values, false));
                        key++;
                    }
                    for (LeafCell cell: cells) table.insert(cell);
                    table.close();
                    table = new Table("database_tables");
                    key = table.getMaxRowId() + 1;
                    Value[] values = new Value[2];
                    values[0] = new Value("" + key, DataType.INT);
                    values[1] = new Value(createQuery.getTableName(), DataType.TEXT);
                    int payloadSize = InsertQueryProcessor.getPayloadSize(values);
                    table.insert(new LeafCell(key, (short)payloadSize, (byte)values.length, values, false));
                    table.close();
                    if (Bootstrap.initialized)
                        System.out.println("Table " + createQuery.getTableName() + " created");
                } else {
                    System.out.println("Table " + createQuery.getTableName() + " is already present");
                }
            } catch (Exception e) {
                System.out.println("Something went wrong while processing create query");
            }
        }
    }

    private Value[] getValues(int key, int i) {
        String tableName = createQuery.getTableName();
        String columnName = createQuery.getColumnNames()[i];
        String columnType = createQuery.getColumnTypes()[i];
        if (!isValid(columnType)) {
            System.out.println("Invalid datatype " + columnType + " provided");
            return null;
        }
        String constraint = createQuery.getConstraints()[i];
        String isNullable = "YES";
        if (constraint.toLowerCase().equals("not null"))
            isNullable = "NO";
        String[] sVals = {"" + key, tableName, columnName, columnType, "" + i, isNullable};
        TableColumn[] columns = new TableColumn[sVals.length];
        for (int j = 0; j < sVals.length; j++) {
            columns[j] = new TableColumn();
            columns[j].setDataType(DatabaseColumns.types[j]);
        }
        return InsertQueryProcessor.getValues(sVals, columns);
    }

    private boolean isValid(String columnType) {
        for (DataType t : DataType.values()) {
            if (t.getName().equals(columnType)) return true;
        }
        return false;
    }
}
