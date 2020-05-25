package com.bhuvan.trees;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.RandomAccessFile;

public class InteriorCell extends Cell {
    int leftChild;
    InteriorCell(long offset, RandomAccessFile tableFile) throws IOException {
        tableFile.seek(offset);
        leftChild = tableFile.readInt();
        key = tableFile.readInt();
    }

    InteriorCell(int leftChild, int key) {
        this.leftChild = leftChild;
        this.key = key;
    }

    public int size() {
        return 8;
    }

    public void save(long offset, @NotNull RandomAccessFile tableFile) throws IOException {
        tableFile.seek(offset);
        tableFile.writeInt(leftChild);
        tableFile.writeInt(key);
    }
}
