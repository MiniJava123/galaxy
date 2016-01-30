package com.dianping.data.warehouse.web.model;

/**
 * Created by mt on 2014/5/27.
 */
public class TaskInstanceDO {
    private String instanceID;
    private String start_time;
    private String end_time;
    private String schedule_cycle;
    private Integer status;
    private Integer return_code;
    private String log_path;
    private String task_committer;
    private String time_id;

    public String getTask_committer() {
        return task_committer;
    }

    public void setTask_committer(String task_committer) {
        this.task_committer = task_committer;
    }



    public String getInstanceID() {
        return instanceID;
    }

    public void setInstanceID(String instanceID) {
        this.instanceID = instanceID;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getSchedule_cycle() {
        return schedule_cycle;
    }

    public void setSchedule_cycle(String schedule_cycle) {
        this.schedule_cycle = schedule_cycle;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getReturn_code() {
        return return_code;
    }

    public void setReturn_code(Integer return_code) {
        this.return_code = return_code;
    }

    public String getLog_path() {
        return log_path;
    }

    public void setLog_path(String log_path) {
        this.log_path = log_path;
    }

    public String getTime_id() {
        return time_id;
    }

    public void setTime_id(String time_id) {
        this.time_id = time_id;
    }
}
