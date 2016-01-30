package com.dianping.data.warehouse.domain.web;

import com.dianping.data.warehouse.domain.McTableInfo;

import java.util.List;

/**
 * Created by shanshan.jin on 14-6-13.
 */
public class BuildTabParaDO {
    private HiveTableDO table;
    private String[] onLineGroupList;
    private String[] offLineGroupList;
    private String refreshType;
    private String refreshCycle;
    private String owner;
    private String tableComment;
    private boolean ifONlineExist;
    private Integer taskId;
    private List<McTableInfo> srcTableList;

    public List<McTableInfo> getSrcTableList() {
        return srcTableList;
    }

    public void setSrcTableList(List<McTableInfo> srcTableList) {
        this.srcTableList = srcTableList;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public List<HiveColumnDO> getColumnDOList() {
        return columnDOList;
    }

    public void setColumnDOList(List<HiveColumnDO> columnDOList) {
        this.columnDOList = columnDOList;
    }

    public List<HiveColumnDO> getPartitionColumnList() {
        return partitionColumnList;
    }

    public void setPartitionColumnList(List<HiveColumnDO> partitionColumnList) {
        this.partitionColumnList = partitionColumnList;
    }

    private List<HiveColumnDO> columnDOList;
    private List<HiveColumnDO> partitionColumnList;

    public boolean isIfONlineExist() {
        return ifONlineExist;
    }

    public void setIfONlineExist(boolean ifONlineExist) {
        this.ifONlineExist = ifONlineExist;
    }

    public HiveTableDO getTable() {
        return table;
    }

    public void setTable(HiveTableDO table) {
        this.table = table;
    }

    public String[] getOnLineGroupList() {
        return onLineGroupList;
    }

    public void setOnLineGroupList(String[] onLineGroupList) {
        this.onLineGroupList = onLineGroupList;
    }

    public String[] getOffLineGroupList() {
        return offLineGroupList;
    }

    public void setOffLineGroupList(String[] offLineGroupList) {
        this.offLineGroupList = offLineGroupList;
    }

    public String getRefreshType() {
        return refreshType;
    }

    public void setRefreshType(String refreshType) {
        this.refreshType = refreshType;
    }

    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }


    public String getRefreshCycle() {
        return refreshCycle;
    }

    public void setRefreshCycle(String refreshCycle) {
        this.refreshCycle = refreshCycle;
    }
}
