package com.dianping.data.warehouse.web.model;

import com.dianping.data.warehouse.halley.domain.TaskDO;

/**
 * Created by Sunny on 14-9-22.
 */
public class CalculateTaskAddDO {
    private TaskDO taskDO;
    private BuildTableInfoDO buildTableInfoDO;

    public BuildTableInfoDO getBuildTableInfoDO() {
        return buildTableInfoDO;
    }

    public void setBuildTableInfoDO(BuildTableInfoDO buildTableInfoDO) {
        this.buildTableInfoDO = buildTableInfoDO;
    }

    public TaskDO getTaskDO() {
        return taskDO;
    }

    public void setTaskDO(TaskDO taskDO) {
        this.taskDO = taskDO;
    }
}
