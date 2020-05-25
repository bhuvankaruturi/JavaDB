package com.bhuvan.trees;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class LeafCell extends Cell {
    short payloadSize;
    byte numCols;
    byte[] serialCodes;
    byte[] columnData;
    boolean deleted;

    LeafCell(long offset, RandomAccessFile tableFile) throws IOException {
        tableFile.seek(offset);
        payloadSize = tableFile.readShort();
        if (payloadSize == 0) deleted = true;
        key = tableFile.readInt();
        numCols = tableFile.readByte();
        serialCodes = new byte[numCols];
        tableFile.read(serialCodes);
        columnData = new byte[payloadSize];
        tableFile.read(columnData);
    }

    public String toString() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(payloadSize);
        for (int i = 0; i < payloadSize; i++) {
            byteBuffer.put(columnData[i]);
        }
        return new String(byteBuffer.array());
    }

    public int size() {
        return 2 + 4 + 1 + numCols + payloadSize;
    }

    public void save(long offset, RandomAccessFile tableFile) throws IOException {
        tableFile.seek(offset);
        tableFile.writeShort(payloadSize);
        tableFile.writeInt(key);
        tableFile.writeByte(numCols);
        tableFile.write(serialCodes);
        tableFile.write(columnData);
    }

    void delete(long offset, RandomAccessFile tableFile, int mode) throws IOException {
        tableFile.seek(offset);
        tableFile.writeShort(0);
        if (mode == 0) tableFile.writeInt(0);
        else tableFile.writeInt(key);
        tableFile.writeByte(0);
        tableFile.write(new byte[numCols]);
        tableFile.write(new byte[payloadSize]);
        payloadSize = 0;
        numCols = 0;
        deleted = true;
    }
}

