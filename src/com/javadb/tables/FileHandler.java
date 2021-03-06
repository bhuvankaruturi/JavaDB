package com.javadb.tables;

import java.io.File;
import java.io.RandomAccessFile;

public class FileHandler {

    static String path = "data/";

    static RandomAccessFile open(String fileName) {
        try {
            return new RandomAccessFile(path + fileName + "." + Table.extension, "rw");
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean delete(String fileName, String extension) {
        try {
            return  new File(path + fileName + "." + extension).delete();
        }
        catch(SecurityException se) {
            se.printStackTrace();
            return false;
        }
    }
}
