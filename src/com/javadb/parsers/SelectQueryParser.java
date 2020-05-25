package com.javadb.parsers;

import com.javadb.queries.SelectQuery;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectQueryParser {
    private final static Pattern selectWithWhere =
            Pattern.compile("(^select\\s\\*\\sfrom\\s(?<name>\\w+?))(\\swhere\\s(?<whr>.*?))?$", Pattern.CASE_INSENSITIVE);
    private final static Pattern wherePattern = Pattern.compile("(?<col>\\w+?)\\s(?<op>[=<>])\\s(?<val>(\".*?\")|(\\d+))");

    public static SelectQuery parse(String selectQuery, long pageSize) {
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
