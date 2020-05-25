package com.javadb.trees;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class LeafCell extends Cell {
    short payloadSize;
    byte numCols;
    byte[] serialCodes;
    byte[] columnData;
    boolean deleted;

    // constructor
    public LeafCell(int key) {
        this.key = key;
    }

    // constructor
    public LeafCell(int key, short payloadSize, byte numCols, byte[] serialCodes, byte[] columnData, boolean deleted) {
        this.payloadSize = payloadSize;
        this.numCols = numCols;
        this.serialCodes = serialCodes;
        this.columnData = columnData;
        this.deleted = deleted;
    }

    // constructor
    LeafCell(long offset, RandomAccessFile tableFile) throws IOException {
        super();
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

    /**
     * @return the content in the bytebuffer as a string
     */
    public String toString() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(payloadSize);
        for (int i = 0; i < payloadSize; i++) {
            byteBuffer.put(columnData[i]);
        }
        return new String(byteBuffer.array());
    }

    /**
     * @return size of the cell
     */
    public int size() {
        return 2 + 4 + 1 + numCols + payloadSize;
    }

    /**
     * saves the contents of this cell the tableFile at given offset
     * @param tableFile RandomAccessFile object to write to
     * @throws IOException while accessing the tableFile
     */
    public void save(long offset, RandomAccessFile tableFile) throws IOException {
        tableFile.seek(offset);
        tableFile.writeShort(payloadSize);
        tableFile.writeInt(key);
        tableFile.writeByte(numCols);
        tableFile.write(serialCodes);
        tableFile.write(columnData);
    }

    /**
     * deletes the cell contents from the tableFile
     * @param tableFile RandomAccessFile instance to read and write
     * @param mode mode == 0 for complete deletion, mode == 1 for not deleting the key
     * @throws IOException while accessing the tableFile
     */
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

