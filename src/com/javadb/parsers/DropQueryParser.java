package com.javadb.parsers;

import com.javadb.queries.DropQuery;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DropQueryParser {
    // regex for the drop table query
    private final static Pattern dropTableRegex = Pattern.compile("^drop\\stable\\s(?<name>\\w+?)$", Pattern.CASE_INSENSITIVE);

    /**
     * parses a string and returns a DropQuery object
     * @param dropQuery string to be parsed
     * @return DropQuery object which wraps the parsed query
     */
    public static DropQuery parse(String dropQuery) {
        Matcher match = dropTableRegex.matcher(dropQuery);
        if (match.matches()) {
            String tableName = match.group("name").trim().toLowerCase();
            return new DropQuery(tableName);
        } else {
            System.out.println("Invalid drop table query");
            return null;
        }
    }
}
