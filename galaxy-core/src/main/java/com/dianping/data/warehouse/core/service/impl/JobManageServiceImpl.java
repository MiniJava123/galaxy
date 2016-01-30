package com.dianping.data.warehouse.core.service.impl;

import com.dianping.data.warehouse.domain.McTableInfo;
import com.dianping.data.warehouse.domain.McTaskInfo;
import com.dianping.data.warehouse.domain.model.CascadeDO;
import com.dianping.data.warehouse.halley.domain.*;
import com.dianping.data.warehouse.halley.service.TaskService;
import com.dianping.data.warehouse.masterdata.service.MercuryService;
import com.dianping.data.warehouse.service.JobManageService;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Sunny on 14-8-26.
 */
public class JobManageServiceImpl implements JobManageService {

    private TaskService taskService;

    private MercuryService mercuryService;

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public void setMercuryService(MercuryService mercuryService) {
        this.mercuryService = mercuryService;
    }

    /**
     * 根据查询条件查询tasks
     */
    public List<TaskDO> queryTasks(TaskQueryDO taskQueryDO) {
        return taskService.queryTasks(taskQueryDO);
    }

    /**
     * 根据taskId查询task
     */
    @Override
    public TaskDO getTaskByTaskId(int taskId) {
        TaskDO taskDO = taskService.getTaskByTaskId(taskId);
        if (taskDO == null)
            return null;
        taskDO.mergeParas();
        return taskDO;
    }

    /**
     * 预跑任务
     */
    public boolean preRunTask(String startTime, String endTime, String task_committer, Integer jobId) {
        return taskService.preRunTask(jobId, startTime, endTime, task_committer);
    }

    /**
     * 失效任务
     */
    @Override
    public boolean invalidTask(int taskId, String updateTime, String updateUser) {
        return taskService.invalidTaskByTaskId(taskId, updateTime, updateUser);
    }

    /**
     * 生效任务
     */
    @Override
    public boolean validTask(int taskId, String updateTime, String updateUser) {
        return taskService.validTaskByTaskId(taskId, updateTime, updateUser);
    }

    /**
     * 根据taskId获得其所有后续任务
     * 返回后续任务和自身所组成的list
     */
    @Override
    public List<TaskDO> getPostTasksByTaskId(String taskId) {
        return taskService.getPostTasksByTaskId(Integer.parseInt(taskId));
    }

    /**
     * 检测taskName是否合法
     */
    @Override
    public boolean checkTaskName(String taskName) {
        return !taskService.taskNameExists(taskName);
    }


    /**
     * 检测环依赖
     */
    @Override
    public boolean hasCycleDependence(TaskDO taskDO) {
        return taskService.hasCycleDependence(taskDO);
    }

    /**
     * 更新task并通知主数据
     */
    @Override
    public boolean updateTask(TaskDO taskDO) {
        try {
            McTaskInfo mcTaskInfo = getMcTaskInfo(taskDO);
            return taskService.updateTask(taskDO) && mercuryService.updateTaskInfo(mcTaskInfo);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 新增task
     */
    @Override
    public synchronized boolean insertTask(TaskDO taskDO) {
        int taskId = taskService.generateTaskID(taskDO);
        taskDO.setTaskId(taskId);
        taskDO.setTaskIdForTaskRelaDO();
        return taskService.insertTask(taskDO);
    }

    /**
     * 根据传输任务的id获得任务的源表信息
     */
    @Override
    public McTableInfo getSourceTable(int taskId) {
        return mercuryService.getSourceTable(taskId);
    }

    /**
     * 根据任务id获得任务的目标表信息
     */
    @Override
    public McTableInfo getTargetTable(int taskId) {
        return mercuryService.getTaskTable(taskId);
    }

    /**
     * 删除计算任务
     */
    @Override
    public boolean deleteCalculateTask(int taskId, String updateTime, String updateUser) {
        return taskService.deleteTaskByTaskId(taskId, updateTime, updateUser);
    }

    /**
     * 级联预跑获得需要预跑的实例
     */
    @Override
    public List<TaskRelaDO> getTasksForCascadePreRun(int taskId) {
        return taskService.getTasksForCascadePreRun(taskId);
    }

    /**
     * 级联预跑
     */
    @Override
    public boolean cascadePreRun(CascadeDO cascadeDO, String committer) {
        return taskService.cascadePreRun(cascadeDO.getStartDate(), cascadeDO.getEndDate(), cascadeDO.getTaskIds(),
                committer);
    }

    /**
     * 由taskDO封装mctaskDO
     */
    private McTaskInfo getMcTaskInfo(TaskDO taskDO) {
        McTaskInfo mcTaskInfo = new McTaskInfo();
        mcTaskInfo.setTaskid(taskDO.getTaskId());
        mcTaskInfo.setOwner(taskDO.getOwner());
        mcTaskInfo.setRefresh_cycle(taskDO.getCycle());
        mcTaskInfo.setStatus(taskDO.getIfVal());
        mcTaskInfo.setRefresh_type(null);
        mcTaskInfo.setRefresh_offset(null);
        mcTaskInfo.setRefresh_datum_date(Integer.parseInt(taskDO.getOffset().substring(1)));
        List<TaskRelaDO> relaDOs = taskDO.getRelaDOList();
        List<Integer> sourceIds = new ArrayList<Integer>();
        for (int i = 0; i < relaDOs.size(); i++) {
            sourceIds.add(relaDOs.get(i).getTaskPreId());
        }
        mcTaskInfo.setSourceTasklist(sourceIds);
        return mcTaskInfo;
    }

}
