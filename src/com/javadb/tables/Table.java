package com.javadb.tables;

import com.javadb.trees.BPlusTree;
import com.javadb.trees.LeafCell;
import com.javadb.trees.LeafPage;
import com.javadb.trees.TableCell;
import com.javadb.types.Value;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class Table {

    static final String extension = "tbl";
    static final long pageSize = 512;
    String tableName;
    RandomAccessFile tableFile;
    BPlusTree tree;

    // constructor
    // open a tableFile specified by tableName
    // create the tableFile and initializes root page
    // if the table file is not present
    public Table(String tableName) throws IOException {
        this.tableName = tableName;
        this.tableFile = FileHandler.open(tableName, extension);
        assert tableFile != null;
        this.tree = new BPlusTree(pageSize, tableFile);
    }

    /** insert the given cell into tableFile
     * @param cell to be inserted
     * @throws IOException while accessing tableFile
     */
    public void insert(LeafCell cell) throws IOException {
        tree.insert(cell);
    }

    /** retrieve all records from this table
     * @return list of records
     * @throws IOException while accessing tableFile
     */
    public List<LeafCell> selectAll() throws IOException {
        LeafPage page = tree.getFirstLeafPage();
        List<LeafCell> cells = new ArrayList<>();
        while(page != null) {
            for (TableCell tableCell: page.tableCells) {
                cells.add((LeafCell)tableCell.getCell());
            }
            page = page.getNextPage();
        }
        return cells;
    }

    /**
     * Return all cells which contain given val at given ordinalVal
     * @param ordinalVal column number
     * @param val value to be compared with
     * @return List of Leaf Cells
     * @throws IOException while accessing tableFile
     */
    public List<LeafCell> selectByValue(int ordinalVal, Value val) throws IOException {
        LeafPage page = tree.getFirstLeafPage();
        List<LeafCell> cells = new ArrayList<>();
        while(page != null) {
            for (TableCell tableCell: page.tableCells) {
                LeafCell lCell = (LeafCell)tableCell.getCell();
                if (lCell.getValues()[ordinalVal].equals(val)) cells.add(lCell);
            }
            page = page.getNextPage();
        }
        return cells;
    }

    public int deleteByValue(int ordinalVal, Value val) throws IOException {
        LeafPage page = tree.getFirstLeafPage();
        int count = 0;
        while(page != null) {
            for (int i = 0; i < page.tableCells.size(); i++) {
                LeafCell lCell = (LeafCell)page.tableCells.get(i).getCell();
                if (lCell.getValues()[ordinalVal].equals(val)) {
                    page.deleteCell(i, 1);
                    count++;
                }
            }
            page = page.getNextPage();
        }
        return count;
    }

    /**
     * closes the tableFile on which this table instance is based upon
     */
    public void close() throws IOException {
        this.tableFile.close();
    }
}
