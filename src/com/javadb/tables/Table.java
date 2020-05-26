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

    /**
     * Check whether the table has the given value in the column specified by ordinalVal
     * @param ordinalVal int, column ordinal value
     * @param val Value, value to search for
     * @return boolean, if the value is found
     * @throws IOException if the tableFile is not accessible
     */
    public boolean hasValue(int ordinalVal, Value val) throws IOException {
        LeafPage page = tree.getFirstLeafPage();
        while(page != null) {
            for (TableCell tableCell: page.tableCells) {
                LeafCell lCell = (LeafCell)tableCell.getCell();
                if (lCell.getValues()[ordinalVal].equals(val)) return true;
            }
            page = page.getNextPage();
        }
        return false;
    }

    /**
     * Delete cells from table, which contain val in the column specified by ordinalVal
     * @param ordinalVal int, column ordinal value
     * @param val Value, value to search for
     * @return int, number of rows deleted
     * @throws IOException if the tableFile is not accessible
     */
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
     * @return int, max row id in the table
     * @throws IOException
     */
    public int getMaxRowId() throws IOException {
        return tree.getMaxRowId();
    }

    /**
     * closes the tableFile on which this table instance is based upon
     */
    public void close() throws IOException {
        this.tableFile.close();
    }
}
