package com.dianping.data.warehouse.core.model;

/**
 * Created by hongdi.tang on 14-3-18.
 */
public class DBRule {
    private String dbName;
    private String regex;

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }
}
