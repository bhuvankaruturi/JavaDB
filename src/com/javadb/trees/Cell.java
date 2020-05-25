package com.javadb.trees;

import java.io.IOException;
import java.io.RandomAccessFile;

public abstract class Cell {
    int key = 0;

    int compare(Cell cell) {
        return this.key - cell.key;
    }

    // abstract methods
    abstract void save(long offset, RandomAccessFile tableFile) throws IOException;
    abstract int size();
}
