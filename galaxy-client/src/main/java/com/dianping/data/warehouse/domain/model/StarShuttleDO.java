package com.dianping.data.warehouse.domain.model;


import com.dianping.data.warehouse.domain.McColumnInfo;
import com.dianping.data.warehouse.halley.domain.TaskDO;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hongdi.tang on 14-2-20.
 */
public class StarShuttleDO implements Serializable{
    private List<WormholeDO> wormholeDOs;
    private TaskDO taskDO;
    private List<McColumnInfo> columns;
    private String ddl;
    private boolean flag;
    private String errorInfo;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public String getDdl() {
        return ddl;
    }

    public void setDdl(String ddl) {
        this.ddl = ddl;
    }

    public List<WormholeDO> getWormholeDOs() {
        return wormholeDOs;
    }

    public void setWormholeDOs(List<WormholeDO> wormholeDOs) {
        this.wormholeDOs = wormholeDOs;
    }

    public TaskDO getTaskDO() {
        return taskDO;
    }

    public void setTaskDO(TaskDO taskDO) {
        this.taskDO = taskDO;
    }

    public List<McColumnInfo> getColumns() {
        return columns;
    }

    public void setColumns(List<McColumnInfo> columns) {
        this.columns = columns;
    }

}
