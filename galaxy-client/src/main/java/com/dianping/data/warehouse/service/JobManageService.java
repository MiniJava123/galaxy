package com.dianping.data.warehouse.service;

import com.dianping.data.warehouse.domain.McTableInfo;
import com.dianping.data.warehouse.domain.model.CascadeDO;
import com.dianping.data.warehouse.halley.domain.*;

import java.util.List;
import java.util.Map;

/**
 * Created by Sunny on 14-8-26.
 */
public interface JobManageService {

    /**
     * 根据查询条件查询tasks
     */
    public List<TaskDO> queryTasks(TaskQueryDO taskQueryDO);

    /**
     * 根据taskId获得task
     */
    public TaskDO getTaskByTaskId(int taskId);

    /**
     * 预跑任务
     */
    public boolean preRunTask(String startTime, String endTime, String task_committer, Integer jobId);

    /**
     * 失效任务
     */
    public boolean invalidTask(int taskId, String updateTime, String updateUser);

    /**
     * 生效任务
     */
    public boolean validTask(int taskId, String updateTime, String updateUser);

    /**
     * 根据taskId获得其所有后续任务
     * 返回后续任务和自身所组成的list
     */
    public List<TaskDO> getPostTasksByTaskId(String taskId);

    /**
     * 检测taskName是否合法
     */
    public boolean checkTaskName(String taskName);

    /**
     * 检测环依赖
     */
    public boolean hasCycleDependence(TaskDO taskDO);

    /**
     * 更新task并通知主数据
     */
    public boolean updateTask(TaskDO taskDO);

    /**
     * 新增task
     */
    public boolean insertTask(TaskDO taskDO);

//    /**
//     * 将taskId对应的任务的tableName列更新为tableName
//     */
//    public void updateTaskTableName(String tableName, int taskId);

    /**
     * 根据传输任务的id获得该任务的源表信息
     */
    public McTableInfo getSourceTable(int taskId);

    /**
     * 根据任务id获得任务的目标表信息
     */
    public McTableInfo getTargetTable(int taskId);

    /**
     * 删除计算任务
     */
    public boolean deleteCalculateTask(int taskId, String updateTime, String updateUser);

    /**
     * 级联预跑获得需要预跑的实例
     */
    public List<TaskRelaDO> getTasksForCascadePreRun(int taskId);

    /**
     * 级联预跑
     */
    public boolean cascadePreRun(CascadeDO cascadeDO, String committer);

}
