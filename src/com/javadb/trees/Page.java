package com.javadb.trees;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public abstract class Page {
    final static int headerLength = 16;
    protected final RandomAccessFile tableFile;
    int pageNumber;
    long pageSize;
    public byte pageType;
    short cellCount;
    short cellContentBegin;
    public int nextNode;
    int parentNode;
    public ArrayList<TableCell> tableCells = new ArrayList<>();

    /**
     * Creates a page instance from contents of page at pageNumber in tableFile
     * @param pageNumber number of the page in the tableFile
     * @param pageSize size of a page in the tableFile
     * @param tableFile RandomAccessFile object to read from
     * @throws IOException while accessing tableFile
     */
    Page(int pageNumber, long pageSize, RandomAccessFile tableFile) throws IOException {
        this.pageSize = pageSize;
        this.tableFile = tableFile;
        this.pageNumber = pageNumber;
        setHeader();
        setCells();
    }

    /**
     * Create a empty page at the given pageNumber
     * @param pageNumber number of the page in the tableFile
     * @param pageSize size of a page in the tableFile
     * @param tableFile RandomAccessFile object to read from
     * @param parentNode pageNumber of the parent node(page) to this page
     * @throws IOException while accessing tableFile
     */
    Page(int pageNumber, long pageSize, RandomAccessFile tableFile, int parentNode) throws IOException {
        this.pageSize = pageSize;
        this.tableFile = tableFile;
        if (pageNumber < 0) {
            tableFile.setLength(tableFile.length() + pageSize);
            this.pageNumber = (int) (tableFile.length() / pageSize) - 1;
        } else {
            this.pageNumber = pageNumber;
            clearPage();
        }
        this.parentNode = parentNode;
        this.cellContentBegin = (short) pageSize;
    }

    /**
     * creates the default header for the page
     * @throws IOException while accessing tableFile
     */
    void createHeader() throws IOException {
        tableFile.seek(getStart() + 6);
        // set next node to undefined i.e 0xFFFFFFFF
        tableFile.writeInt(-1);
        // if page is the root set parent node to 0xFFFFFFFF
        // else set to this.parentNode
        if (pageNumber == 0) tableFile.writeInt(-1);
        else tableFile.writeInt(parentNode);
    }

    /**
     * read header information from the tableFile
     * @throws IOException while accessing tableFile
     */
    void setHeader() throws IOException {
        tableFile.seek(getStart());
        pageType = tableFile.readByte();
        // unused byte in the header
        tableFile.readByte();
        cellCount =  tableFile.readShort();
        cellContentBegin = tableFile.readShort();
        cellContentBegin = cellContentBegin == 0 ? (short) pageSize : cellContentBegin;
        nextNode = tableFile.readInt();
        parentNode = tableFile.readInt();
    }

    /**
     * @return long - location of start of the page (i.e pageSize * pageNumber)
     */
    long getStart() {
        return pageSize * pageNumber;
    }

    /**
     * @return boolean - true if the page is root node
     */
    boolean isRoot() {
        return parentNode == -1 && pageNumber == 0;
    }

    /**
     * Add given to the page and save to tableFile
     * @param cell instance to be saved
     * @throws IOException while accessing tableFile
     */
    void addCell(Cell cell) throws IOException {
        short offset = (short) (cellContentBegin - cell.size());
        cell.save(getStart() + offset, tableFile);
        updateHeaderOnCellAddition(new TableCell(cell, offset));
    }

    /**
     * Update the header of page when a cell is inserted
     * @param tableCell is the new cell that was added
     * @throws IOException while accessing tableFile
     */
    protected void updateHeaderOnCellAddition(TableCell tableCell) throws IOException {
        tableFile.seek(getStart() + 2);
        cellCount++;
        tableFile.writeShort(cellCount);
        tableFile.writeShort(tableCell.offset);
        cellContentBegin = tableCell.offset;
        tableFile.seek(getStart() + headerLength + (cellCount-1) * 2);
        tableFile.writeShort(tableCell.offset);
        tableCells.add(tableCell);
    }

    /**
     * Updates the header when cell is deleted
     * @param cellIndex is the index of the deleted cell in the cellOffsets list
     * @throws IOException while accessing tableFile
     */
    void updateHeaderOnCellDeletion(int cellIndex) throws IOException {
        tableFile.seek(getStart() + 2);
        cellCount--;
        tableFile.writeShort(cellCount);

        if (cellCount > 0) cellContentBegin = tableCells.get(cellIndex-1).offset;
        else cellContentBegin = (short) pageSize;
        tableFile.writeShort(cellContentBegin == pageSize ? 0 : cellContentBegin);
        tableFile.seek(getStart() + headerLength + cellCount * 2);
        tableFile.writeShort(0x00);
        tableCells.remove(cellIndex);
    }

    /**
     * method to handle an overflow in the root page
     * @param lPage is the left child
     * @param rPage is the right child
     * @param from is the start index of the cells to be copied to rPage
     * @param to is the end index of the cells to be copied to rPage
     * @param newCell new cell to be added to rPage, which is causing the overflow
     * @return Page - new root
     * @throws IOException while accessing tableFile
     */
    Page handleRootOverflow(Page lPage, Page rPage, int from, int to, Cell newCell) throws IOException {
        copyCells(0, from, lPage);
        copyCells(from, to, rPage);
        rPage.addCell(newCell);
        InteriorPage root = new InteriorPage(0, pageSize, tableFile, -1);
        root.addChild(new InteriorCell(lPage.pageNumber, rPage.getLowestKey()), rPage.pageNumber);
        return root;
    }

    /**
     * clears the contents of the page
     * @throws IOException while accessing tableFile
     */
    void clearPage() throws IOException {
        tableFile.seek(getStart());
        for (int i = 0; i < pageSize; i++) {
            tableFile.writeByte(0x00);
        }
    }

    /**
     * sets the nextNode of the page
     * or right most child in case of interior page
     * @param pageNumber is page number of the next node
     * @throws IOException while accessing tableFile
     */
    void setNextNode(int pageNumber) throws IOException {
        tableFile.seek(getStart() + 6);
        tableFile.writeInt(pageNumber);
        nextNode = pageNumber;
    }

    /* abstract methods */
    abstract void setCells() throws IOException;
    abstract Page insert(Cell cell) throws IOException;
    abstract LeafPage getFirstLeafPage() throws IOException;
    abstract int getLowestKey() throws IOException;
    abstract void copyCells(int from, int to, Page page) throws IOException;
    abstract int getFreeSpace();
    abstract int getMaxRowId() throws IOException;
    public abstract LeafCell getWithKey(int key) throws IOException;
}
