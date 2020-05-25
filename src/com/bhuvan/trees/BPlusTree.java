package com.bhuvan.trees;

import java.io.IOException;
import java.io.RandomAccessFile;

public class BPlusTree {
    Page root;
    /**
     * Default constructor
     * @param pageSize is the size of the page in the table
     * @param tableFile is the RandomAccessFile object of the .tbl file
     */
    BPlusTree(long pageSize, RandomAccessFile tableFile) throws IOException  {
        int type = 0;
        if (tableFile.length() > 0) {
            tableFile.seek(0);
            type = tableFile.readByte();
        }
        if (type == 0) {
            root = new LeafPage(-1, pageSize, tableFile, -1);
        } else {
            if (type == 5) {
                root = new InteriorPage(0, pageSize, tableFile);
            } else {
                root = new LeafPage(0, pageSize, tableFile);
            }
        }
    }

    /**
     * method to insert a cell into table
     * @param cell is the cell object to be inserted
     * @throws IOException while accessing tableFile
     */
    public void insert(Cell cell) throws IOException {
        Page result = root.insert(cell);
        if (result != null) {
            root = result;
        }
    }
}
