package com.dianping.data.warehouse.domain.web;

/**
 * Created by shanshan.jin on 14-6-18.
 */
public class HiveColumnDO {
    private Integer columnKey;
    private String columnName;
    private String columnType;
    private String columnComment;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public Integer getColumnKey() {
        return columnKey;
    }

    public void setColumnKey(Integer columnKey) {
        this.columnKey = columnKey;
    }

    public String getColumnComment() {
        return columnComment;
    }

    public void setColumnComment(String columnComment) {
        this.columnComment = columnComment;
    }
}
