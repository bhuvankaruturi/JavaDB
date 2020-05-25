package com.javadb.trees;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.RandomAccessFile;

public abstract class Cell implements Comparable<Cell> {
    // key
    int key = 0;

    // constructor
    Cell() {}

    @Override
    public int compareTo(@NotNull Cell c) {
        return key - c.key;
    }

    // abstract methods
    abstract void save(long offset, RandomAccessFile tableFile) throws IOException;
    abstract int size();
}
