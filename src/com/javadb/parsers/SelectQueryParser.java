package com.javadb.parsers;

import com.javadb.queries.SelectQuery;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectQueryParser {
    // regex for the select with where clause query
    private final static Pattern selectWithWhere =
            Pattern.compile("(^select\\s\\*\\sfrom\\s(?<name>\\w+?))(\\swhere\\s(?<whr>.*?))?$", Pattern.CASE_INSENSITIVE);
    // regex for the where clause within a select query
    private final static Pattern wherePattern = Pattern.compile("(?<col>\\w+?)\\s(?<op>[=<>])\\s(?<val>(\".*?\")|(\\d+))");

    /**
     * parses a string and return a SelectQuery object
     * @param selectQuery string to be parsed
     * @return SelectQuery object which wraps the parsed query
     */
    public static SelectQuery parse(String selectQuery) {
        Matcher match = selectWithWhere.matcher(selectQuery);
        if (match.matches()) {
            String tableName = match.group("name").trim().toLowerCase();
            String whereClause = match.group("whr");
            boolean hasWhereClause = false;
            if (whereClause != null) {
                whereClause = whereClause.trim();
                hasWhereClause = true;
            }
            String clauseCol = null;
            String clauseVal = null;
            if (hasWhereClause) {
                match = wherePattern.matcher(whereClause);
                if (match.matches()) {
                    clauseCol = match.group("col").trim();
                    clauseVal = match.group("val").replaceAll("\"", "").trim();
                } else {
                    System.out.println("Invalid where clause in SELECT query");
                    return null;
                }
            }
            return new SelectQuery(tableName, hasWhereClause, clauseCol, clauseVal);
        }
        else {
            System.out.println("Invalid SELECT query");
            return null;
        }
    }
}
