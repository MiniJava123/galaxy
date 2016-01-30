package com.dianping.data.warehouse.service;

import com.dianping.data.warehouse.domain.model.CascadeDO;
import com.dianping.data.warehouse.halley.domain.InstanceChildrenDO;
import com.dianping.data.warehouse.halley.domain.InstanceDisplayDO;
import com.dianping.data.warehouse.halley.domain.InstanceQueryDO;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Sunny on 14-8-22.
 */
public interface JobMonitorService {

    /**
     * 按照日期进行任务查询，查询所有符合条件的任务以及其子任务直至叶子节点
     * 调用前请确保对InstanceQueryDO的格式进了检测，且startTime==endTime
     * 如果查询失败返回空list
     */
    public List<InstanceDisplayDO> queryInstancesByDate(InstanceQueryDO instanceQueryDO);

    /**
     * 按照时间段进行任务查询，查询所有符合条件的任务
     * 调用前请确保对InstanceQueryDO的格式进了检测，且startTime!=endTime
     * 如果查询失败返回空list
     */
    public List<InstanceDisplayDO> queryInstancesByInterval(InstanceQueryDO instanceQueryDO);

    /**
     * 任务详细信息
     * 如果查询失败返回null
     */
    public InstanceDisplayDO getInstanceByInstanceId(String taskStatusId);

    /**
     * 任务重跑，将任务状态置为init
     * 返回状态受影响的instance数
     */
    public int recallInstance(String taskStatusId);

    /**
     * 置为挂起，将任务状态置为suspend
     * 返回状态受影响的instance数
     */
    public int suspendInstance(String taskStatusId);

    /**
     * 置为成功，将任务状态置为success
     * 返回状态受影响的instance数
     */
    public int successInstance(String taskStatusId);

    /**
     * 直接依赖，任务的直接父节点和直接子节点
     * 如果查询失败返回空list
     */
    public List<InstanceDisplayDO> queryDirectRelation(String taskStatusId);

    /**
     * 所有依赖，任务的所有儿子节点直至叶子节点，即以该任务为根节点的所有节点
     * 如果查询失败返回空list
     */
    public List<InstanceDisplayDO> queryAllRelation(String taskStatusId);

    /**
     * 查看最长路径，任务最晚结束的前序节点直至根节点
     * 如果查询失败返回空list
     */
    public List<InstanceDisplayDO> getLongestPath(String taskStatusId);

    /**
     * 停止预跑，将任务状态置为挂起
     * 返回状态受影响的instance数
     */
    public int batchStopTask(String taskId);

    /**
     * 快速通道，将任务的running_prio置为400
     * 返回状态受影响的instance数
     */
    public int raisePriorityInstance(String taskStatusId);

    /**
     * 根据instanceId返回其自身以及所有直接和间接依赖其的节点
     */
    public List<InstanceDisplayDO> getAllPostInstancesByInstanceId(String instanceId);

    /**
     * 获得级联重跑需要重跑的instance
     */
    public List<InstanceChildrenDO> recallCascadeGetInstances(String jobInstanceId, String date);

    /**
     * 级联重跑
     */
    public Integer recallCascade(CascadeDO cascadeDO);

    /**
     * 获取日志
     */
    public String getInstanceLog(String logPath);

    /**
     * 查询预跑任务
     */
    public List<HashMap<String, Object>> queryPreRunInstances(String startTime, String task_commiter, String taskIDorName);

    /**
     * 任务实例状态分析
     */
    public String instanceStatusAnalyze(String taskStatusId);

}
