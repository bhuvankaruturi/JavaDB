package com.javadb.parsers;

import com.javadb.queries.InsertQuery;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InsertQueryParser {
    private final static Pattern insertRegex = Pattern.compile("^insert\\sinto\\s(?<name>\\w+?)\\svalues\\s\\((?<vals>.*?)\\)$", Pattern.CASE_INSENSITIVE);

    public static InsertQuery parse(String insertQuery, long pageSize) {
        Matcher match = insertRegex.matcher(insertQuery);
        if (match.matches()) {
            String tableName = match.group("name").trim().toLowerCase();
            String[] columnValues = match.group("vals").trim().replaceAll("\"", "").split(",\\s*");
            return new InsertQuery(tableName, columnValues);
        } else {
            System.out.println("Invalid INSERT query");
            return null;
        }
    }
}
