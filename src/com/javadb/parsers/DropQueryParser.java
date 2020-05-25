package com.javadb.parsers;

import com.javadb.queries.DropQuery;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DropQueryParser {
    private final static Pattern dropTableRegex = Pattern.compile("^drop\\stable\\s(?<name>\\w+?)$", Pattern.CASE_INSENSITIVE);

    public static DropQuery parse(String dropTableQuery, long pageSize) {
        Matcher match = dropTableRegex.matcher(dropTableQuery);
        if (match.matches()) {
            String tableName = match.group("name").trim().toLowerCase();
            return new DropQuery(tableName);
        } else {
            System.out.println("Invalid drop table query");
            return null;
        }
    }
}
