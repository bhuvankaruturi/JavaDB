package com.javadb.trees;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collections;

public class LeafPage extends Page {
    // leaf page is identified with 13
    byte pageType = 13;

    // constructor
    LeafPage(int pageNumber, long pageSize, RandomAccessFile tableFile) throws IOException {
        super(pageNumber, pageSize, tableFile);
    }

    // constructor
    LeafPage(int pageNumber, long pageSize, RandomAccessFile tableFile, int parentNode) throws IOException {
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
            TableCell tableCell = new TableCell(new LeafCell(offset + getStart(), tableFile), offset);
            tableCells.add(tableCell);
        }
    }

    /**
     * LeafPage's implementation of insert
     * @param cell to be insert
     * @return new sibling in case of overflow, null otherwise
     * @throws IOException while accessing tableFile
     */
    Page insert(Cell cell) throws IOException {
        if (cell.size() + 2 > getFreeSpace()) {
            int from = (cellCount + 1) / 2;
            int to = cellCount;
            if (isRoot()) {
                return handleRootOverflow(from, to, cell);
            } else {
                Page newPage = new LeafPage(-1, pageSize, tableFile, parentNode);
                copyCells(from, to, newPage);
                newPage.addCell(cell);
                deleteCells(from, to);
                newPage.setNextNode(nextNode);
                setNextNode(newPage.pageNumber);
                return newPage;
            }
        }
        addCell(cell);
        return null;
    }

    @Override
    LeafPage getFirstLeafPage() {
        return this;
    }

    /**
     * handles overflow while insertion if current node is the root
     * @param from is the start index of the cells to be copied to rPage
     * @param to is the end index of the cells to be copied to rPage
     * @param cell new cell to be added to rPage, which is causing the overflow
     * @return new root page
     * @throws IOException while accessing tableFile
     */
    Page handleRootOverflow(int from, int to, Cell cell) throws IOException {
        Page lPage = new LeafPage(-1, pageSize, tableFile, 0);
        Page rPage = new LeafPage(-1, pageSize, tableFile, 0);
        rPage.setNextNode(nextNode);
        lPage.setNextNode(rPage.pageNumber);
        return handleRootOverflow(lPage, rPage, from, to, cell);
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
            Cell cell = new LeafCell(tableCells.get(i).offset + getStart(), tableFile);
            page.addCell(cell);
        }
    }

    /**
     * deletes cells at specified index
     * @param index of the cell to be deleted
     * @param mode 0 to delete cell completely, 1 to not delete the key
     * @throws IOException while accessing tableFile
     */
    public void deleteCell(int index, int mode) throws IOException {
        int offset = tableCells.get(index).offset;
        LeafCell cell = new LeafCell(getStart() + offset, tableFile);
        if(!cell.deleted) cell.delete(offset + getStart(), tableFile, mode);
        updateHeaderOnCellDeletion(index);
    }

    /**
     * deletes cells specified of indices from and to
     * @param from start point of cell deletion
     * @param to end point of cell deletion
     * @throws IOException while accessing tableFile
     */
    void deleteCells(int from, int to) throws IOException {
        for (int i = to-1; i >= from; i--) {
            deleteCell(i, 0);
        }
    }

    /**
     * @return int - lowest key among the keys in this page
     * @throws IOException while accessing tableFile
     */
    int getLowestKey() throws IOException {
        if (cellCount <= 0)
            return -1;
        return new LeafCell(tableCells.get(0).offset + getStart(), tableFile).key;
    }

    /**
     * @return next leaf page
     * @throws IOException while accessing tableFile
     */
    public LeafPage getNextPage() throws IOException {
        if (nextNode != -1)
            return new LeafPage(nextNode, pageSize, tableFile);
        return null;
    }

    /**
     * @return int - max key/rowId in the tableFile
     * @throws IOException while accessing tableFile
     */
    int getMaxRowId() throws IOException {
        LeafPage nextPage = getNextPage();
        if (nextPage != null)
            return getNextPage().getMaxRowId();
        else {
            if (cellCount == 0) return 0;
            else return new LeafCell(getStart() + tableCells.get(cellCount-1).offset, tableFile).key;
        }
    }

    @Override
    public LeafCell getWithKey(int key) throws IOException {
        TableCell tableCell = new TableCell(new LeafCell(key), (short) -1);
        int index = Collections.binarySearch(tableCells, tableCell);
        if (index < 0) return null;
        else return (LeafCell) tableCells.get(index).cell;
    }
}
