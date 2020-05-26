package com.javadb.catalog;

import com.javadb.processors.CreateQueryProcessor;

import java.io.File;

public class Bootstrap {
    public static boolean initialized = false;
    public static void init() {
        initializeDataDir();
        new CreateQueryProcessor(DatabaseTables.createQuery);
        new CreateQueryProcessor(DatabaseColumns.createQuery);
        initialized= true;
    }

    public static void initializeDataDir() {
        String directory = System.getProperty("user.dir");
        directory += "/data";
        try {
            File dir = new File(directory);
            if (!dir.exists()) {
                if (!dir.mkdir()) throw new SecurityException("Cannot create directory");
            }
            File[] files = dir.listFiles();
            if (files != null) {
                /* delete all existing files in the data folder */
                for (File file : files) {
                    if(!file.delete()) throw new SecurityException("Cannot delete");
                }
            }
        }
        catch(SecurityException se) {
            se.printStackTrace();
        }
    }
}
