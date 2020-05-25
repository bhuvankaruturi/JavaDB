package com.javadb.trees;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.RandomAccessFile;

public class InteriorCell extends Cell {
    int leftChild;

    // constructor
    InteriorCell(int key) {
        this.key = key;
    }

    // constructor
    InteriorCell(long offset, RandomAccessFile tableFile) throws IOException {
        tableFile.seek(offset);
        leftChild = tableFile.readInt();
        key = tableFile.readInt();
    }

    // constructor
    InteriorCell(int leftChild, int key) {
        this.leftChild = leftChild;
        this.key = key;
    }

    // size of a interior cell is 8
    // 4 (left page number) + 4 (key)
    public int size() {
        return 8;
    }

    // saves this cell's content to the tableFile at given offset
    public void save(long offset, @NotNull RandomAccessFile tableFile) throws IOException {
        tableFile.seek(offset);
        tableFile.writeInt(leftChild);
        tableFile.writeInt(key);
    }
}
