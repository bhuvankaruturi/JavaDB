package com.javadb.types;

public class TableColumn {
    String columnName;
    DataType dataType;
    short ordinalVal;
    boolean isNullable;

    public TableColumn() {

    }

    public TableColumn(String columnName, DataType dataType, short ordinalVal, boolean isNullable) {
        this.columnName = columnName;
        this.dataType = dataType;
        this.ordinalVal = ordinalVal;
        this.isNullable = isNullable;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public short getOrdinalVal() {
        return ordinalVal;
    }

    public void setOrdinalVal(short ordinalVal) {
        this.ordinalVal = ordinalVal;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public void setNullable(boolean nullable) {
        isNullable = nullable;
    }
}
