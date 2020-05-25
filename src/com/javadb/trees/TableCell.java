package com.javadb.trees;

import org.jetbrains.annotations.NotNull;

/**
 * Wrapper class for cell and its offset in the page
 */
public class TableCell implements Comparable<TableCell> {
    Cell cell;
    short offset;

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public short getOffset() {
        return offset;
    }

    public void setOffset(short offset) {
        this.offset = offset;
    }

    public TableCell(Cell cell, short offset) {
        this.cell = cell;
        this.offset = offset;
    }

    @Override
    public int compareTo(@NotNull TableCell t) {
        return cell.key - t.cell.key;
    }
}
