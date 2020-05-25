package com.javadb.trees;

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
     * read the cells from tableFile and adds them to cells list
     * @throws IOException while accessing tableFile
     */
    void setCells() throws IOException {
        tableFile.seek(getStart() + headerLength);
        for (int i = 0; i < cellCount; i++) {
            short offset = tableFile.readShort();
            TableCell tableCell = new TableCell(new InteriorCell(offset + getStart(), tableFile), offset);
            tableCells.add(tableCell);
        }
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
            iCell = new InteriorCell(tableCells.get(0).offset + getStart(), tableFile);
            if (cell.compareTo(iCell) <= 0) {
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
     * @return the left most leaf node
     * @throws IOException while accessing the node
     */
    @Override
    LeafPage getFirstLeafPage() throws IOException {
        InteriorCell iCell = new InteriorCell(tableCells.get(0).offset + getStart(), tableFile);
        return getChildPage(iCell.leftChild).getFirstLeafPage();
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
                return handleRootOverflow(from, to, cell, newChildPageNumber);
            } else {
                Page newPage = new InteriorPage(-1, pageSize, tableFile, parentNode);
                copyCells(from, to, newPage);
                newPage.addCell(cell);
                setNextNode(new InteriorCell(tableCells.get(from-1).offset + getStart(), tableFile).leftChild);
                newPage.setNextNode(newChildPageNumber);
                deleteCells(from - 1, to - 1);
                return newPage;
            }
        }
        short offset = (short) (cellContentBegin - cell.size());
        cell.save(offset + getStart(), tableFile);
        updateHeaderOnCellAddition(new TableCell(cell, offset));
        setNextNode(newChildPageNumber);
        return null;
    }

    /**
     * overloads Page's handleRootOverflow method
     * @param from is the start index of the cells to be copied to rPage
     * @param to is the end index of the cells to be copied to rPage
     * @param newCell new cell to be added to rPage, which is causing the overflow
     * @return new root node
     * @throws IOException while accessing tableFile
     */
    Page handleRootOverflow(int from, int to, Cell newCell, int newChildPageNumber) throws IOException {
        Page lPage = new InteriorPage(-1, pageSize, tableFile, 0);
        Page rPage = new InteriorPage(-1, pageSize, tableFile, 0);
        lPage.setNextNode(new InteriorCell(tableCells.get(from - 1).offset + getStart(), tableFile).leftChild);
        rPage.setNextNode(newChildPageNumber);
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
            Cell cell = new InteriorCell(tableCells.get(i).offset + getStart(), tableFile);
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
            short offset = tableCells.get(i).offset;
            int size = new InteriorCell(offset, tableFile).size();
            tableFile.seek(getStart() + offset);
            for (int j = 0; j < size; j++) {
                tableFile.writeByte(0x00);
            }
            tableCells.remove(i);
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
        InteriorCell iCell =  new InteriorCell(tableCells.get(0).offset + getStart(), tableFile);
        return getChildPage(iCell.leftChild).getLowestKey();
    }

    /**
     * @return int - max key/rowId in the tableFile
     * @throws IOException while accessing tableFile
     */
    int getMaxRowId() throws IOException {
        return getChildPage(nextNode).getMaxRowId();
    }

    @Override
    public LeafCell getWithKey(int key) throws IOException {
        for (TableCell tableCell: tableCells) {
            if (tableCell.cell.key > key) {
                InteriorCell iCell = (InteriorCell) tableCell.cell;
                return getChildPage(iCell.leftChild).getWithKey(key);
            }
        }
        return getChildPage(nextNode).getWithKey(key);
    }
}
