package com.dianping.data.warehouse.web.model;

import com.dianping.data.warehouse.domain.web.BuildTabParaDO;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * Created by mt on 2014/6/18.
 */
public class BuildTableInfoDO {
    @NotEmpty(message = "crate table sql can not be empty...")
    private String sql;
    private BuildTabParaDO buildTabParaDO;
    private Integer taskID;
    private List<Integer> srcTaskIds;

    public Integer getTaskID() {
        return taskID;
    }

    public void setTaskID(Integer taskID) {
        this.taskID = taskID;
    }


    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }


    public List<Integer> getSrcTaskIds() {
        return srcTaskIds;
    }

    public void setSrcTaskIds(List<Integer> srcTaskIds) {
        this.srcTaskIds = srcTaskIds;
    }

    public BuildTabParaDO getBuildTabParaDO() {
        return buildTabParaDO;
    }

    public void setBuildTabParaDO(BuildTabParaDO buildTabParaDO) {
        this.buildTabParaDO = buildTabParaDO;
    }
}
