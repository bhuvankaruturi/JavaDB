package com.javadb.catalog;

import com.javadb.processors.CreateQueryProcessor;

import java.io.File;

public class Bootstrap {
    public static boolean initialized = false;
    public static void init() {
        boolean hasTables = initializeDataDir();
        if (hasTables) { 
            initialized = true; 
            return;
        }
        cleanDataDir();
        new CreateQueryProcessor(DatabaseTables.createQuery);
        new CreateQueryProcessor(DatabaseColumns.createQuery);
        initialized= true;
    }

    public static boolean initializeDataDir() {
        String directory = System.getProperty("user.dir");
        directory += "/data";
        try {
            File dir = new File(directory);
            if (!dir.exists()) {
                if (!dir.mkdir()) throw new SecurityException("Cannot create directory");
            }
            File file = new File(directory + "/" + "database_tables.tbl");
            if (file.isFile()) {
                file = new File(directory + "/" + "database_columns.tbl");
                if (file.isFile()) {
                    return true;
                } else return false;
            } else return false;
        }
        catch(SecurityException se) {
            se.printStackTrace();
            return false;
        }
    }

    static void cleanDataDir() {
        String directory = System.getProperty("user.dir");
        directory += "/data";
        try {
            File dir = new File(directory);
            File[] files = dir.listFiles();
            if (files != null) {
                /* delete all existing files in the data folder */
                for (File file : files) {
                    if (!file.delete()) throw new SecurityException("Cannot delete");
                }
            }
        } catch(SecurityException se) {
            se.printStackTrace();
        }
    }
}
