package com.dianping.data.warehouse.domain.model;

import com.dianping.data.warehouse.domain.McTableInfo;
import com.dianping.data.warehouse.domain.TableExistStatus;

import java.io.Serializable;

/**
 * Created by shanshan.jin on 14-2-26.
 */
public class TableDO implements Serializable{

    private static final long serialVersionUID = -3675645054647943612L;

    public McTableInfo getTableInfo() {
        return tableInfo;
    }

    public void setTableInfo(McTableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }

    public TableExistStatus getStatus() {
        return status;
    }

    public void setStatus(TableExistStatus status) {
        this.status = status;
    }

    public McTableInfo tableInfo;
    public TableExistStatus status;
}
