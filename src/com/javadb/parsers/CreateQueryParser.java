package com.javadb.parsers;

import com.javadb.queries.CreateQuery;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateQueryParser {
    // regex pattern for create query
    private final static Pattern createRegex = Pattern.compile("^create\\stable\\s(?<name>[^\\d]\\w+?)\\s\\((?<cols>.*?)\\)$", Pattern.CASE_INSENSITIVE);

    /**
     * parses a string and returns a CreateQuery object
     * @param createQuery string to be parsed
     * @return CreateQuery object which wraps the parsed query
     */
    public static CreateQuery parse(String createQuery) {
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
                if (columnDetails.length < 2) {
                    System.out.println("Invalid column definitions in CREATE query");
                    return null;
                }
                columnNames[i] = columnDetails[0].toLowerCase();
                columnTypes[i] = columnDetails[1].toLowerCase();
                // if there is a constraint join all the remaining strings in columnDetails array
                if (columnDetails.length >= 3) {
                    StringBuilder constraint = new StringBuilder();
                    for (int j = 2; j < columnDetails.length; j++)
                        constraint.append(columnDetails[j]).append(" ");
                    constraints[i] = constraint.toString().trim().toLowerCase();
                }
                else
                    constraints[i] = "";
            }
            return new CreateQuery(tableName, columnNames, columnTypes, constraints);
        } else {
            System.out.println("Invalid CREATE query");
            return null;
        }
    }
}
