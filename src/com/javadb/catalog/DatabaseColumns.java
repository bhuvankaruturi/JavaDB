package com.javadb.catalog;

import com.javadb.tables.Table;
import com.javadb.trees.LeafCell;
import com.javadb.types.DataType;
import com.javadb.types.TableColumn;
import com.javadb.types.Value;

import java.io.IOException;
import java.util.List;

import static com.javadb.types.DataType.*;

public class DatabaseColumns {
    public static final String tableName = "database_columns";
    public static final String createQuery = "CREATE TABLE database_columns (rowid INT PRIMARY KEY, table_name TEXT, column_name TEXT, data_type TEXT, ord_pos TINYINT, is_nullable TEXT)";
    public static final DataType[] types = {INT, TEXT, TEXT, TEXT, TINYINT, TEXT};
    private final Table table;

    /**
     * Constructor
     * @throws IOException while accessing the tableFile
     */
    public DatabaseColumns() throws IOException {
        this.table = new Table(tableName);
    }

    /**
     * method to find get the column details of the table
     * @param tName String, name of the table to search for
     * @return TableColumn[], columns details of the table
     */
    public TableColumn[] getColumnTypes(String tName) {
        Value val = new Value(tName, DataType.TEXT);
        try {
            List<LeafCell> cells = table.selectByValue(1, val);
            TableColumn[] columns = new TableColumn[cells.size()];
            for (LeafCell cell: cells) {
                short ordinalVal = Short.parseShort(cell.getValues()[4].toString());
                columns[ordinalVal] = new TableColumn();
                columns[ordinalVal].setOrdinalVal(ordinalVal);
                columns[ordinalVal].setColumnName(cell.getValues()[2].toString());
                String type = cell.getValues()[3].toString();
                for (DataType t: DataType.values()) {
                    if (t.getName().equals(type)) {
                        columns[ordinalVal].setDataType(t);
                    }
                }
                boolean isNullable = cell.getValues()[5].toString().toLowerCase().equals("yes");
                columns[ordinalVal].setNullable(isNullable);
            }
            return columns;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void close() throws IOException {
        table.close();
    }
}
