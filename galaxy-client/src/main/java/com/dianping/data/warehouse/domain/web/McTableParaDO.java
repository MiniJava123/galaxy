package com.dianping.data.warehouse.domain.web;

import com.dianping.data.warehouse.domain.McTableInfo;

/**
 * Created by hongdi.tang on 14-3-19.
 */
public class McTableParaDO {
    private McTableInfo tableInfo;
    private String conditionCol;

    public McTableInfo getTableInfo() {
        return tableInfo;
    }

    public void setTableInfo(McTableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }

    public String getConditionCol() {
        return conditionCol;
    }

    public void setConditionCol(String conditionCol) {
        this.conditionCol = conditionCol;
    }
}
