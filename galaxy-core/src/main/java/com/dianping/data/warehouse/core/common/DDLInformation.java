package com.dianping.data.warehouse.core.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author <a href="mailto:tsensue@gmail.com">dishu.chen</a>
 * 14-3-6.
 */
public class DDLInformation {
    private int type;
    private int taskId;
    private String dbName;
    private String title;
    private String desc;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Map<String, String> toMap() {
        Map<String, String> result = new HashMap<String, String>();
        result.put("type", String.valueOf(this.type));
        result.put("dbname", this.dbName);
        result.put("desc", this.desc);
        result.put("title", this.title);
        result.put("taskid", String.valueOf(this.taskId));

        return result;
    }
}
