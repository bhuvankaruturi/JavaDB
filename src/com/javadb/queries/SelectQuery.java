package com.javadb.queries;

public class SelectQuery {
    String tableName;
    boolean hasWhereClause;
    String clauseCol;
    String clauseVal;

    public SelectQuery(String tableName, boolean hasWhereClause, String clauseCol, String clauseVal) {
        this.tableName = tableName;
        this.hasWhereClause = hasWhereClause;
        this.clauseCol = clauseCol;
        this.clauseVal = clauseVal;
    }

    public String getTableName() {
        return tableName;
    }

    public boolean isHasWhereClause() {
        return hasWhereClause;
    }

    public String getClauseCol() {
        return clauseCol;
    }

    public String getClauseVal() {
        return clauseVal;
    }
}
