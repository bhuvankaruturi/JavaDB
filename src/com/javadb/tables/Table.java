package com.javadb.tables;

import com.javadb.tables.records.Record;
import com.javadb.trees.BPlusTree;
import com.javadb.trees.LeafCell;
import com.javadb.trees.LeafPage;
import com.javadb.trees.TableCell;

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
    public Table(String tableName) throws IOException {
        this.tableName = tableName;
        this.tableFile = FileHandler.open(tableName, extension);
        assert tableFile != null;
        this.tree = new BPlusTree(pageSize, tableFile);
    }

    /** insert the given record into tableFile
     * @param record to be inserted
     * @throws IOException while accessing tableFile
     */
    public void insert(Record record) throws IOException {
        LeafCell cell = record.recordToCell();
        tree.insert(cell);
    }

    /** retrieve all records from this table
     * @return list of records
     * @throws IOException while accessing tableFile
     */
    public List<Record> selectAll() throws IOException {
        LeafPage page = tree.getFirstLeafPage();
        List<Record> records = new ArrayList<>();
        while(page != null) {
            for (TableCell tableCell: page.tableCells) {
                records.add(Record.cellToRecord((LeafCell)tableCell.getCell()));
            }
            page = page.getNextPage();
        }
        return records;
    }
}
