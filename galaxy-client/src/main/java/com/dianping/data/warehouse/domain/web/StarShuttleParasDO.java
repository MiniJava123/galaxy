package com.dianping.data.warehouse.domain.web;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by hongdi.tang on 14-2-17.
 */
public class StarShuttleParasDO {

    @NotEmpty(message = "sourceDBType can not be null ...")
    private String sourceDBType;

    @NotEmpty(message = "sourceDBName can not be null ...")
    private String sourceDBName;
    private String sourceSchemaName;

    @NotEmpty(message = "sourceTableName can not be null ...")
    private String sourceTableName;
    private String sourceSplitColumn;

    @NotEmpty(message = "targetDBType can not be null ...")
    private String targetDBType;

    @NotEmpty(message = "targetDBName can not be null ...")
    private String targetDBName;

    @NotEmpty(message = "targetTableType can not be null ...")
    private String targetTableType;
    private String targetSchemaName;

    @NotEmpty(message = "targetTableName can not be null ...")
    private String targetTableName;
    private String targetPartitionName;

    private String owner;
    private String dateType;
    private Integer dateNumber;
    private String dateOffset;

    private Integer taskId;

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public Integer getDateNumber() {
        return dateNumber;
    }

    public void setDateNumber(Integer dateNumber) {
        this.dateNumber = dateNumber;
    }

    public String getDateOffset() {
        return dateOffset;
    }

    public void setDateOffset(String dateOffset) {
        this.dateOffset = dateOffset;
    }

    public String getSourceDBType() {
        return sourceDBType;
    }

    public void setSourceDBType(String sourceDBType) {
        this.sourceDBType = sourceDBType;
    }

    public String getSourceDBName() {
        return sourceDBName;
    }

    public void setSourceDBName(String sourceDBName) {
        this.sourceDBName = sourceDBName;
    }

    public String getSourceSchemaName() {
        return sourceSchemaName;
    }

    public void setSourceSchemaName(String sourceSchemaName) {
        this.sourceSchemaName = sourceSchemaName;
    }

    public String getSourceTableName() {
        return sourceTableName;
    }

    public void setSourceTableName(String sourceTableName) {
        this.sourceTableName = sourceTableName;
    }

    public String getTargetDBType() {
        return targetDBType;
    }

    public void setTargetDBType(String targetDBType) {
        this.targetDBType = targetDBType;
    }

    public String getTargetDBName() {
        return targetDBName;
    }

    public void setTargetDBName(String targetDBName) {
        this.targetDBName = targetDBName;
    }

    public String getTargetTableType() {
        return targetTableType;
    }

    public void setTargetTableType(String targetTableType) {
        this.targetTableType = targetTableType;
    }

    public String getTargetSchemaName() {
        return targetSchemaName;
    }

    public void setTargetSchemaName(String targetSchemaName) {
        this.targetSchemaName = targetSchemaName;
    }

    public String getTargetTableName() {
        return targetTableName;
    }

    public void setTargetTableName(String targetTableName) {
        this.targetTableName = targetTableName;
    }

    public String getTargetPartitionName() {
        return targetPartitionName;
    }

    public void setTargetPartitionName(String targetPartitionName) {
        this.targetPartitionName = targetPartitionName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getSourceSplitColumn() {
        return sourceSplitColumn;
    }

    public void setSourceSplitColumn(String sourceSplitColumn) {
        this.sourceSplitColumn = sourceSplitColumn;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }
}
