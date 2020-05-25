package com.bhuvan.trees;

import java.io.IOException;
import java.io.RandomAccessFile;

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
                Page lPage = new LeafPage(-1, pageSize, tableFile, 0);
                Page rPage = new LeafPage(-1, pageSize, tableFile, 0);
                rPage.setNextNode(nextNode);
                lPage.setNextNode(rPage.pageNumber);
                return handleRootOverflow(lPage, rPage, from, to, cell);
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
            Cell cell = new LeafCell(cellOffsets.get(i) + getStart(), tableFile);
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
            LeafCell cell = new LeafCell(getStart() + offset, tableFile);
            if(!cell.deleted) cell.delete(offset + getStart(), tableFile, 0);
            updateHeaderOnCellDeletion(i);
        }
    }

    /**
     * @return int - lowest key among the keys in this page
     * @throws IOException while accessing tableFile
     */
    int getLowestKey() throws IOException {
        if (cellCount <= 0)
            return -1;
        return new LeafCell(cellOffsets.get(0) + getStart(), tableFile).key;
    }

    /**
     * @return int - max key/rowId in the tableFile
     * @throws IOException while accessing tableFile
     */
    int getMaxRowId() throws IOException {
        if (nextNode != -1)
            return new LeafPage(nextNode, pageSize, tableFile).getMaxRowId();
        else {
            if (cellCount == 0) return 0;
            else return new LeafCell(getStart() + cellOffsets.get(cellCount-1), tableFile).key;
        }
    }
}
