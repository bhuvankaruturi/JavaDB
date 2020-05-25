package com.bhuvan.trees;

import java.io.IOException;
import java.io.RandomAccessFile;

public class InteriorPage extends Page {
    // Interior page is identified with 5
    byte pageType = 5;

    // constructor
    InteriorPage(int pageNumber, long pageSize, RandomAccessFile tableFile) throws IOException {
        super(pageNumber, pageSize, tableFile);
    }

    // constructor
    InteriorPage(int pageNumber, long pageSize, RandomAccessFile tableFile, int parentNode) throws IOException {
        super(pageNumber, pageSize, tableFile, parentNode);
        createHeader();
    }

    /**
     * overrides Page.createHeader
     * @throws IOException while accessing tableFile
     */
    @Override
    void createHeader() throws IOException {
        tableFile.seek(getStart());
        tableFile.writeByte(pageType);
        super.createHeader();
    }

    /**
     * InteriorPage's implementation of insert
     * @param cell to be insert
     * @return new sibling in case of overflow, null otherwise
     * @throws IOException while accessing tableFile
     */
    Page insert(Cell cell) throws IOException {
        int i;
        InteriorCell iCell = null;
        for (i = 0; i < cellCount; i++) {
            iCell = new InteriorCell(cellOffsets.get(0) + getStart(), tableFile);
            if (cell.compare(iCell) <= 0) {
                break;
            }
            iCell = null;
        }
        Page child;
        if (iCell != null)
            child = getChildPage(iCell.leftChild);
        else
            child = getChildPage(nextNode);
        Page newChild = child.insert(cell);
        if (newChild != null) {
            int lowestChildKey = newChild.getLowestKey();
            return addChild(new InteriorCell(nextNode, lowestChildKey), newChild.pageNumber);
        }
        return null;
    }

    /**
     * gets the child page at the given pageNumber
     * @param childPageNumber page number of the child page to be returned
     * @return child page
     * @throws IOException while accessing tableFile
     */
    Page getChildPage(int childPageNumber) throws IOException {
        tableFile.seek(childPageNumber * pageSize);
        byte type = tableFile.readByte();
        if (type  == 5)
            return new InteriorPage(childPageNumber, pageSize, tableFile);
        else
            return new LeafPage(childPageNumber, pageSize, tableFile);
    }

    /**
     * adds a page at newChildPageNumber as its child
     * @param cell new interior cell pointing to new child page
     * @param newChildPageNumber page number of the new child
     * @return new sibling in case of an overflow, null otherwise
     * @throws IOException while accessing tableFile
     */
    Page addChild(InteriorCell cell, int newChildPageNumber) throws IOException {
        if (cell.size() + 2 > getFreeSpace()) {
            int from = (cellCount + 1) / 2 + 1;
            int to = cellCount;
            if (isRoot()) {
                Page lPage = new InteriorPage(-1, pageSize, tableFile, 0);
                Page rPage = new InteriorPage(-1, pageSize, tableFile, 0);
                lPage.setNextNode(new InteriorCell(cellOffsets.get(from - 1) + getStart(), tableFile).leftChild);
                rPage.setNextNode(newChildPageNumber);
                return handleRootOverflow(lPage, rPage, from, to, cell);
            } else {
                Page newPage = new InteriorPage(-1, pageSize, tableFile, parentNode);
                copyCells(from, to, newPage);
                newPage.addCell(cell);
                setNextNode(new InteriorCell(cellOffsets.get(from-1) + getStart(), tableFile).leftChild);
                newPage.setNextNode(newChildPageNumber);
                deleteCells(from - 1, to - 1);
                return newPage;
            }
        }
        int offset = cellContentBegin - cell.size();
        cell.save(offset + getStart(), tableFile);
        updateHeaderOnCellAddition(offset);
        setNextNode(newChildPageNumber);
        return null;
    }

    /**
     * override's Page's handleRootOverflow method
     * @param lPage is the left child
     * @param rPage is the right child
     * @param from is the start index of the cells to be copied to rPage
     * @param to is the end index of the cells to be copied to rPage
     * @param newCell new cell to be added to rPage, which is causing the overflow
     * @return new root node
     * @throws IOException while accessing tableFile
     */
    Page handleRootOverflow(Page lPage, Page rPage, int from, int to, Cell newCell) throws IOException {
        copyCells(0, from - 1, lPage);
        copyCells(from, to, rPage);
        rPage.addCell(newCell);
        InteriorPage root = new InteriorPage(0, pageSize, tableFile, -1);
        root.addChild(new InteriorCell(lPage.pageNumber, rPage.getLowestKey()), rPage.pageNumber);
        return root;
    }

    /**
     * @return int - free space in bytes available in the page
     */
    int getFreeSpace() {
        return this.cellContentBegin - (headerLength + cellCount * 2);
    }

    /**
     * copies cell starting from 'from' to 'to' into 'page'
     * @param from start index of the cells to be copied
     * @param to end index of the cells to be copied
     * @param page is the Page instance where the cells will be copied
     * @throws IOException while accessing tableFile
     */
    void copyCells(int from, int to, Page page) throws IOException {
        for (int i = from; i < to; i++) {
            Cell cell = new InteriorCell(cellOffsets.get(i) + getStart(), tableFile);
            page.addCell(cell);
        }
    }

    /**
     * deletes cells specified of indices from and to
     * @param from start point of cell deletion
     * @param to end point of cell deletion
     * @throws IOException while accessing tableFile
     */
    void deleteCells(int from, int to) throws IOException {
        for (int i = to-1; i >= from; i--) {
            int offset = cellOffsets.get(i);
            int size = new InteriorCell(offset, tableFile).size();
            tableFile.seek(getStart() + offset);
            for (int j = 0; j < size; j++) {
                tableFile.writeByte(0x00);
            }
            cellOffsets.remove(i);
            cellCount--;
        }
    }

    /**
     * @return int - lowest key among the keys in the children of this page
     * @throws IOException while accessing tableFile
     */
    int getLowestKey() throws IOException {
        if (cellCount <= 0)
            return -1;
        InteriorCell iCell =  new InteriorCell(cellOffsets.get(0) + getStart(), tableFile);
        return getChildPage(iCell.leftChild).getLowestKey();
    }

    /**
     * @return int - max key/rowId in the tableFile
     * @throws IOException while accessing tableFile
     */
    int getMaxRowId() throws IOException {
        return getChildPage(nextNode).getMaxRowId();
    }
}
