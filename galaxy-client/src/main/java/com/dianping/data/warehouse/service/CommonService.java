package com.dianping.data.warehouse.service;

import com.dianping.data.warehouse.halley.domain.MonitorDO;
import com.dianping.data.warehouse.halley.domain.TaskSQLParserDO;

import java.util.ArrayList;

/**
 * Created by Sunny on 14-8-12.
 */
public interface CommonService {

    /**
     * 获得当前值班用户
     */
    public MonitorDO getCurrentMonitor();

    /**
     * 获得当前值班信息，强制读取数据库
     */
    public boolean updateMonitorByForce();

    /**
     * 发送意见反馈
     */
    public boolean sendReply(String content, String userEmail);

    /**
     * 用户是否是管理员
     */
    public boolean isAdmin(int loginId, int resourceId);

    /**
     * 用户是否是taskid对应的任务的owner
     */
    public boolean isOwner(String pinyinName, int taskId);

    /**
     * 检查表名是否符合数据库的命名规范
     */
    public boolean checkDBRule(String databaseName, String tableName);

    /**
     * 根据dol文件进行SQL解析
     */
    public TaskSQLParserDO gitSQLParse(String projectName, String fileName);

    /**
     * 获取登出url
     */
    public String getLogoutUrl();

}
