package com.javadb.queries;

public class SelectQuery {
    String tableName;
    boolean hasWhereClause;
    String clauseCol;
    String clauseVal;

    /**
     * Constructor for the select statement wrapper
     * @param tableName String, table name
     * @param hasWhereClause boolean, true if there is a where clause
     * @param clauseCol String, column name in the where clause
     * @param clauseVal String, column value in the where clause
     */
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
