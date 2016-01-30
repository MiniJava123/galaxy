package com.dianping.data.warehouse.domain.model;

import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * Created by Sunny on 14-9-18.
 */
public class CascadeDO {

    private String startDate;
    private String endDate;
    private List<String> taskIds;
    private List<String> instanceIds;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<String> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<String> taskIds) {
        this.taskIds = taskIds;
    }

    public List<String> getInstanceIds() {
        return instanceIds;
    }

    public void setInstanceIds(List<String> instanceIds) {
        this.instanceIds = instanceIds;
    }
}
