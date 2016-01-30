package com.dianping.data.warehouse.domain.web;

import java.util.Map;

/**
 * Created by shanshan.jin on 14-6-18.
 */
public class HiveTableDO {
    String dbName;
    String tableName;
    String tableType;
    String tableSerde;
    String tableInputformat;
    String tableOutputformat;
    String tableLocation;
    Integer tableBucketsNum;
    String tableBucketscol;
    String tableSortcol;

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    Map<String, String> parameters;

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    public String getTableSerde() {
        return tableSerde;
    }

    public void setTableSerde(String tableSerde) {
        this.tableSerde = tableSerde;
    }

    public String getTableInputformat() {
        return tableInputformat;
    }

    public void setTableInputformat(String tableInputformat) {
        this.tableInputformat = tableInputformat;
    }

    public String getTableOutputformat() {
        return tableOutputformat;
    }

    public void setTableOutputformat(String tableOutputformat) {
        this.tableOutputformat = tableOutputformat;
    }

    public String getTableLocation() {
        return tableLocation;
    }

    public void setTableLocation(String tableLocation) {
        this.tableLocation = tableLocation;
    }

    public Integer getTableBucketsNum() {
        return tableBucketsNum;
    }

    public void setTableBucketsNum(Integer tableBucketsNum) {
        this.tableBucketsNum = tableBucketsNum;
    }

    public String getTableBucketscol() {
        return tableBucketscol;
    }

    public void setTableBucketscol(String tableBucketscol) {
        this.tableBucketscol = tableBucketscol;
    }

    public String getTableSortcol() {
        return tableSortcol;
    }

    public void setTableSortcol(String tableSortcol) {
        this.tableSortcol = tableSortcol;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
