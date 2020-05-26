package com.javadb.processors;

import com.javadb.catalog.DatabaseColumns;
import com.javadb.catalog.DatabaseTables;
import com.javadb.parsers.InsertQueryParser;
import com.javadb.queries.InsertQuery;
import com.javadb.tables.Table;
import com.javadb.trees.LeafCell;
import com.javadb.types.DataType;
import com.javadb.types.TableColumn;
import com.javadb.types.Value;

public class InsertQueryProcessor {
    InsertQuery insertQuery;

    public InsertQueryProcessor(String insertStatement) {
        this.insertQuery = InsertQueryParser.parse(insertStatement);
        process();
    }

    private void process() {
        if (insertQuery != null) {
            try {
                int key = Integer.parseInt(insertQuery.getColumnValues()[0].trim());
                if (key <= 0) throw new NumberFormatException();
                DatabaseTables tablesCatalog = new DatabaseTables();
                boolean isPresent = tablesCatalog.isTablePresent(insertQuery.getTableName());
                tablesCatalog.close();
                if (isPresent) {
                    DatabaseColumns columnCatalog = new DatabaseColumns();
                    TableColumn[] columns = columnCatalog.getColumnTypes(insertQuery.getTableName());
                    columnCatalog.close();
                    Value[] values = getValues(insertQuery.getColumnValues(), columns);
                    if (values != null) {
                        int payloadSize = getPayloadSize(values);
                        LeafCell newCell = new LeafCell(key, (short)payloadSize, (byte)columns.length, values, false);
                        Table table = new Table(insertQuery.getTableName());
                        table.insert(newCell);
                        table.close();
                        System.out.println("Inserted 1 row into " + insertQuery.getTableName());
                    } else {
                        System.out.println("Values provided do not conform to the table column data types");
                    }
                } else {
                    System.out.println("Table " + insertQuery.getTableName() + " not present in the database");
                }
            }
            catch(NumberFormatException e) {
                System.out.println("Invalid primary key provided.");
            }
            catch(Exception e) {
                e.printStackTrace();
                System.out.println("Something went wrong while processing the insert query");
            }
        }
    }

    protected static Value[] getValues(String[] sVals, TableColumn[] columns) {
        Value[] values = new Value[columns.length];
        for (int i = 0; i < columns.length; i++) {
            if (sVals.length-1 < i && !columns[i].isNullable()) return null;
            else if (sVals.length-1 < i) {
                values[i] = new Value(new byte[0], DataType.NULL);
                continue;
            }
            byte[] arr = columns[i].getDataType().stringToByteArr(sVals[i]);
            if (arr == null) return null;
            else values[i] = new Value(arr, columns[i].getDataType());
        }
        return values;
    }

    protected static int getPayloadSize(Value[] values) {
        int size = 0;
        for (Value value: values) {
            size += value.getSize();
        }
        return size;
    }
}
