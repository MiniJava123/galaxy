package com.dianping.data.warehouse.core.service.impl;

import com.dianping.data.warehouse.domain.model.CascadeDO;
import com.dianping.data.warehouse.halley.domain.InstanceChildrenDO;
import com.dianping.data.warehouse.halley.domain.InstanceDisplayDO;
import com.dianping.data.warehouse.halley.domain.InstanceQueryDO;
import com.dianping.data.warehouse.halley.service.InstanceService;
import com.dianping.data.warehouse.service.JobMonitorService;

import java.util.HashMap;
import java.util.List;


/**
 * Created by Sunny on 14-8-22.
 */
public class JobMonitorServiceImpl implements JobMonitorService {

    private InstanceService instanceService;

    public void setInstanceService(InstanceService instanceService) {
        this.instanceService = instanceService;
    }

    /**
     * 按照日期进行任务查询，查询所有符合条件的任务以及其子任务直至叶子节点
     * 调用前请确保对InstanceQueryDO的格式进了检测，且startTime==endTime
     * 如果查询失败返回空list
     */
    @Override
    public List<InstanceDisplayDO> queryInstancesByDate(InstanceQueryDO instanceQueryDO) {
        return instanceService.queryInstancesByDate(instanceQueryDO);
    }

    /**
     * 按照时间段进行任务查询，查询所有符合条件的任务
     * 调用前请确保对InstanceQueryDO的格式进了检测，且startTime!=endTime
     * 如果查询失败返回空list
     */
    @Override
    public List<InstanceDisplayDO> queryInstancesByInterval(InstanceQueryDO instanceQueryDO) {
        return instanceService.queryInstancesByInterval(instanceQueryDO);
    }

    /**
     * 任务详细信息
     * 如果查询失败返回null
     */
    @Override
    public InstanceDisplayDO getInstanceByInstanceId(String taskStatusId) {
        return instanceService.getInstanceByInstanceId(taskStatusId);
    }

    /**
     * 任务重跑，将任务状态置为init
     * 返回状态受影响的instance数
     */
    @Override
    public int recallInstance(String taskStatusId) {
        return instanceService.recallInstance(taskStatusId);
    }

    /**
     * 置为挂起，将任务状态置为suspend
     * 返回状态受影响的instance数
     */
    @Override
    public int suspendInstance(String taskStatusId) {
        return instanceService.suspendInstance(taskStatusId);
    }

    /**
     * 置为成功，将任务状态置为success
     * 返回状态受影响的instance数
     */
    @Override
    public int successInstance(String taskStatusId) {
        return instanceService.successInstance(taskStatusId);
    }

    /**
     * 直接依赖，任务的直接父节点和直接子节点
     * 如果查询失败返回空list
     */
    @Override
    public List<InstanceDisplayDO> queryDirectRelation(String taskStatusId) {
        return instanceService.queryDirectRelation(taskStatusId);
    }

    /**
     * 所有依赖，任务的所有儿子节点直至叶子节点，即以该任务为根节点的所有节点
     * 如果查询失败返回空list
     */
    @Override
    public List<InstanceDisplayDO> queryAllRelation(String taskStatusId) {
        return instanceService.queryAllRelation(taskStatusId);
    }

    /**
     * 查看最长路径，任务最晚结束的前序节点直至根节点
     * 如果查询失败返回空list
     */
    @Override
    public List<InstanceDisplayDO> getLongestPath(String taskStatusId) {
        return instanceService.getLongestPath(taskStatusId);
    }

    /**
     * 停止预跑，将任务状态置为挂起
     * 返回状态受影响的instance数
     */
    @Override
    public int batchStopTask(String taskId) {
        return instanceService.batchStopTask(taskId);
    }

    /**
     * 快速通道，将任务的running_prio置为400
     * 返回状态受影响的instance数
     */
    @Override
    public int raisePriorityInstance(String taskStatusId) {
        return instanceService.raisePriorityInstance(taskStatusId);
    }

    /**
     * 根据instanceId返回其自身以及所有直接和间接依赖其的节点
     */
    @Override
    public List<InstanceDisplayDO> getAllPostInstancesByInstanceId(String taskStatusId) {
        return instanceService.getAllPostInstancesByInstanceId(taskStatusId);
    }

    /**
     * 级联重跑获取重跑的节点
     */
    @Override
    public List<InstanceChildrenDO> recallCascadeGetInstances(String instanceId, String date) {
        return instanceService.getAllChildrenOfQueryInstanceId(instanceId, date);
    }

    /**
     * 级联重跑
     */
    @Override
    public Integer recallCascade(CascadeDO cascadeDO) {
        return instanceService.recallCascade(cascadeDO.getStartDate(), cascadeDO.getEndDate(), cascadeDO.getTaskIds());
    }

    /**
     * 获取日志
     */
    @Override
    public String getInstanceLog(String logPath) {
        return instanceService.getInstanceLog(logPath);
    }

    /**
     * 查询预跑任务
     */
    @Override
    public List<HashMap<String, Object>> queryPreRunInstances(String startTime, String task_commiter, String taskIDorName) {
        return instanceService.queryPreRunInstances(startTime, task_commiter, taskIDorName);
    }

    /**
     * 任务实例状态分析
     */
    @Override
    public String instanceStatusAnalyze(String taskStatusId) {
        return instanceService.instanceStatusAnalyze(taskStatusId);
    }
}
