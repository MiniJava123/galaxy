package com.dianping.data.warehouse.web.model;

import java.util.List;

/**
 * Created by mt on 2014/5/27.
 */
public class PreRunResultDO {
    private Integer task_id;
    private String task_name;
    private String task_commiter;
    private String cycle;
    private String task_start_time;
    private Integer task_success_num;
    private Integer task_unSuccess_num;
    private List<TaskInstanceDO> instanceList;

    public Integer getTask_id() {
        return task_id;
    }

    public void setTask_id(Integer task_id) {
        this.task_id = task_id;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getTask_commiter() {
        return task_commiter;
    }

    public void setTask_commiter(String task_commiter) {
        this.task_commiter = task_commiter;
    }

    public String getTask_start_time() {
        return task_start_time;
    }

    public void setTask_start_time(String task_start_time) {
        this.task_start_time = task_start_time;
    }

    public Integer getTask_success_num() {
        return task_success_num;
    }

    public void setTask_success_num(Integer task_success_num) {
        this.task_success_num = task_success_num;
    }

    public Integer getTask_unSuccess_num() {
        return task_unSuccess_num;
    }

    public void incr_success_num() {
        this.task_success_num++;
    }

    ;

    public void incr_unSuccess_num() {
        this.task_unSuccess_num++;
    }

    ;

    public void setTask_unSuccess_num(Integer task_unSuccess_num) {
        this.task_unSuccess_num = task_unSuccess_num;
    }

    public List<TaskInstanceDO> getInstanceList() {
        return instanceList;
    }

    public void setInstanceList(List<TaskInstanceDO> instanceList) {
        this.instanceList = instanceList;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }
}

