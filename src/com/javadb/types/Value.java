package com.javadb.types;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class Value {
    DataType type;
    byte[] data;
    int size;

    public Value(DataType type) {
        this.type = type;
    }

    public Value(String data, DataType type) {
        this.type = type;
        this.data = this.type.stringToByteArr(data);
        this.size = this.type.getSize(data);
    }

    public Value(byte[] data, DataType type) {
        this.data = data;
        this.type = type;
        this.size = data.length;
    }

    public DataType getType() {
        return type;
    }

    public byte[] getData() {
        return data;
    }

    public int getSize() {
        return size;
    }

    public boolean equals(@NotNull Value v) {
        return Arrays.equals(data, v.data);
    }

    @Override
    public String toString() {
        return this.type.byteArrToString(data);
    }
}
