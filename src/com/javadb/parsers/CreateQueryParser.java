package com.javadb.parsers;

import com.javadb.queries.CreateQuery;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateQueryParser {
    private final static Pattern createRegex = Pattern.compile("^create\\stable\\s(?<name>[^\\d]\\w+?)\\s\\((?<cols>.*?)\\)$", Pattern.CASE_INSENSITIVE);

    public static CreateQuery parse(String createQuery, long pageSize) {
        Matcher match = createRegex.matcher(createQuery);
        if (match.matches()) {
            String tableName = match.group("name").trim().toLowerCase();
            String[] columns = match.group("cols").trim().split(",\\s*");
            int numCols = columns.length;
            String[] columnNames = new String[numCols];
            String[] columnTypes = new String[numCols];
            String[] constraints = new String[numCols];
            for (int i = 0; i < columns.length; i++) {
                String[] columnDetails = columns[i].split("\\s");
                columnNames[i] = columnDetails[0].toLowerCase();
                columnTypes[i] = columnDetails[1].toLowerCase();
                constraints[i] = columnDetails[2].toLowerCase();
            }
            return new CreateQuery(tableName, columnNames, columnTypes, constraints);
        } else {
            System.out.println("Invalid CREATE query");
            return null;
        }
    }
}
