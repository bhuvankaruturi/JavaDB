package com.javadb.catalog;

import com.javadb.processors.CreateQueryProcessor;

public class Bootstrap {
    public static boolean initialized = false;
    public static void init() {
        new CreateQueryProcessor(DatabaseTables.createQuery);
        new CreateQueryProcessor(DatabaseColumns.createQuery);
        initialized= true;
    }
}
