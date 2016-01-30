package com.dianping.data.warehouse.web.UrlHandler;

import com.dianping.data.warehouse.domain.model.CascadeDO;
import com.dianping.data.warehouse.halley.domain.InstanceChildrenDO;
import com.dianping.data.warehouse.halley.domain.InstanceDisplayDO;
import com.dianping.data.warehouse.halley.domain.InstanceQueryDO;
import com.dianping.data.warehouse.service.JobMonitorService;
import com.dianping.data.warehouse.web.Result;
import com.dianping.data.warehouse.web.model.PreRunResultDO;
import com.dianping.data.warehouse.web.model.TaskInstanceDO;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sunny on 14-8-22.
 */
@Service("jobMonitorUrlHandler")
public class JobMonitorUrlHandler {

    @Resource(name = "jobMonitorService")
    private JobMonitorService jobMonitorService;

    /**
     * 查询任务，结果为拓扑图形式
     * 在使用前请确保instanceQueryDO格式正确
     */
    public Result<JSONObject> handleQueryInstancesByTopology(InstanceQueryDO instanceQueryDO) {
        Result<JSONObject> result = new Result<JSONObject>();
        List<InstanceDisplayDO> instanceDisplayDOs;
        if (instanceQueryDO.getStartTime().equals(instanceQueryDO.getEndTime()))
            instanceDisplayDOs = queryInstancesByDate(instanceQueryDO);
        else
            instanceDisplayDOs = queryInstancesByInterval(instanceQueryDO);
        List<JSONObject> instancesJson = parseJson(instanceDisplayDOs, 1);
        if (instancesJson == null) {
            result.setSuccess(false);
            result.setMessages("失败: 查询失败");
            return result;
        } else {
            result.addAllResults(instancesJson);
            result.setSuccess(true);
            result.setMessages("成功: 获得" + instanceDisplayDOs.size() + "个任务实例");
            return result;
        }
    }

    /**
     * 查询任务，结果为列表形式
     * 在使用前请确保instanceQueryDO格式正确
     */
    public Result<InstanceDisplayDO> handleQueryInstancesByList(InstanceQueryDO instanceQueryDO) {
        Result<InstanceDisplayDO> result = new Result<InstanceDisplayDO>();
        List<InstanceDisplayDO> instanceDisplayDOs;
        if (instanceQueryDO.getStartTime().equals(instanceQueryDO.getEndTime()))
            instanceDisplayDOs = queryInstancesByDate(instanceQueryDO);
        else
            instanceDisplayDOs = queryInstancesByInterval(instanceQueryDO);
        if (instanceDisplayDOs == null) {
            result.setSuccess(false);
            result.setMessages("失败: 查询失败");
            return result;
        } else {
            result.addAllResults(instanceDisplayDOs);
            result.setSuccess(true);
            result.setMessages("成功: 获得" + instanceDisplayDOs.size() + "个任务实例");
            return result;
        }
    }

    /**
     * 按日期进行任务查询
     * 在使用前请确保instanceQueryDO格式正确，且startTime==endTime
     */
    private List<InstanceDisplayDO> queryInstancesByDate(InstanceQueryDO instanceQueryDO) {
        return jobMonitorService.queryInstancesByDate(instanceQueryDO);
    }

    /**
     * 按时间段进行任务查询
     * 在使用前请确保instanceQueryDO格式正确，且startTime!=endTime
     */
    private List<InstanceDisplayDO> queryInstancesByInterval(InstanceQueryDO instanceQueryDO) {
        return jobMonitorService.queryInstancesByInterval(instanceQueryDO);
    }

    /**
     * 根据instanceId返回其自身以及所有后继节点
     */
    public Result<JSONObject> handleGetSelfAndPostInstances(String instanceId) {
        Result<JSONObject> result = new Result<JSONObject>();
        List<InstanceDisplayDO> instanceDisplayDOs = jobMonitorService.getAllPostInstancesByInstanceId(instanceId);
        List<JSONObject> instances = parseJson(instanceDisplayDOs, 1);
        if (instanceDisplayDOs == null) {
            result.setSuccess(false);
            result.setMessages("失败: 查询失败");
            return result;
        } else {
            result.addAllResults(instances);
            result.setSuccess(true);
            result.setMessages("成功: 获得" + instanceDisplayDOs.size() + "个任务实例");
            return result;
        }
    }

    /**
     * 任务详细信息
     */
    public Result<InstanceDisplayDO> handleGetInstanceByInstanceId(String instanceId) {
        Result<InstanceDisplayDO> result = new Result<InstanceDisplayDO>();
        InstanceDisplayDO instanceDisplayDO = jobMonitorService.getInstanceByInstanceId(instanceId);
        if (instanceDisplayDO == null) {
            result.setSuccess(false);
            result.setMessages("失败: 获取任务实例(" + instanceId + ")信息失败");
            return result;
        }
        result.addResults(instanceDisplayDO);
        result.setSuccess(true);
        result.setMessages("成功: 已取得任务实例(" + instanceId + ")信息");
        return result;
    }

    /**
     * 置为重跑
     */
    public Result<Object> handleRecallInstance(String instanceId) {
        Result<Object> result = new Result<Object>();
        int res = jobMonitorService.recallInstance(instanceId);
        if (res > 0) {
            result.setSuccess(true);
            result.setMessages("成功: 任务实例(" + instanceId + ")已置为重跑");
        } else {
            result.setSuccess(false);
            result.setMessages("失败: 任务实例(" + instanceId + ")置为重跑失败");
        }
        return result;
    }

    /**
     * 批量置为重跑
     */
    public Result handleRecallInstances(List<String> jobInstanceIds) {
        Result<String> result = new Result<String>();
        int total = 0;
        for (int i = 0; i < jobInstanceIds.size(); i++) {
            int recallResult = jobMonitorService.recallInstance(jobInstanceIds.get(i));
            if (recallResult > 0)
                total++;
        }
        if (total < jobInstanceIds.size()) {
            result.setMessages("批量重跑任务部分失败，请重新查询后再进行操作");
            result.setSuccess(false);
        } else {
            result.setMessages("成功重跑" + total + "个任务");
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 置为挂起
     */
    public Result<Object> handleSuspendInstance(String instanceId) {
        Result<Object> result = new Result<Object>();
        int res = jobMonitorService.suspendInstance(instanceId);
        if (res > 0) {
            result.setSuccess(true);
            result.setMessages("成功: 任务实例(" + instanceId + ")已置为挂起");
        } else {
            result.setSuccess(false);
            result.setMessages("失败: 任务实例(" + instanceId + ")置为挂起失败");
        }
        return result;
    }

    /**
     * 批量挂起
     */
    public Result handleSuspendInstances(List<String> jobInstanceIds) {
        Result<String> result = new Result<String>();
        int total = 0;
        for (int i = 0; i < jobInstanceIds.size(); i++) {
            int recallResult = jobMonitorService.suspendInstance(jobInstanceIds.get(i));
            if (recallResult > 0)
                total++;
        }
        if (total < jobInstanceIds.size()) {
            result.setMessages("批量挂起任务部分失败，请重新查询后再进行操作");
            result.setSuccess(false);
        } else {
            result.setMessages("成功挂起" + total + "个任务");
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 置为成功
     */
    public Result<Object> handleSuccessInstance(String instanceId) {
        Result<Object> result = new Result<Object>();
        int res = jobMonitorService.successInstance(instanceId);
        if (res > 0) {
            result.setSuccess(true);
            result.setMessages("成功: 任务实例(" + instanceId + ")已置为成功");
        } else {
            result.setSuccess(false);
            result.setMessages("失败: 任务实例(" + instanceId + ")置为成功失败");
        }
        return result;
    }

    /**
     * 批量置为成功
     */
    public Result handleSuccessInstances(List<String> jobInstanceIds) {
        Result<String> result = new Result<String>();
        int total = 0;
        for (int i = 0; i < jobInstanceIds.size(); i++) {
            int recallResult = jobMonitorService.successInstance(jobInstanceIds.get(i));
            if (recallResult > 0)
                total++;
        }
        if (total < jobInstanceIds.size()) {
            result.setMessages("将任务置为成功操作部分失败，请重新查询后再进行操作");
            result.setSuccess(false);
        } else {
            result.setMessages("成功将" + total + "个任务置为成功");
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 直接依赖，任务的直接父节点和直接子节点
     */
    public Result<JSONObject> handleQueryDirectRelation(String instanceId) {
        Result<JSONObject> result = new Result<JSONObject>();
        List<InstanceDisplayDO> instanceDisplayDOs = jobMonitorService.queryDirectRelation(instanceId);
        List<JSONObject> instances = parseJson(instanceDisplayDOs, 1);
        if (instanceDisplayDOs == null) {
            result.setSuccess(false);
            result.setMessages("失败: 获取任务实例(" + instanceId + ")直接依赖失败");
            return result;
        } else {
            result.addAllResults(instances);
            result.setSuccess(true);
            result.setMessages("成功: 已获取任务实例(" + instanceId + ")直接依赖，总计获得" + instanceDisplayDOs.size() + "个实例");
            return result;
        }
    }

    /**
     * 所有依赖，任务的所有后继节点和任务的所有前驱节点
     */
    public Result<JSONObject> handleQueryAllRelation(String instanceId) {
        Result<JSONObject> result = new Result<JSONObject>();
        List<InstanceDisplayDO> instanceDisplayDOs = jobMonitorService.queryAllRelation(instanceId);
        List<JSONObject> instances = parseJson(instanceDisplayDOs, 1);
        if (instanceDisplayDOs == null) {
            result.setSuccess(false);
            result.setMessages("失败: 获取任务实例(" + instanceId + ")所有依赖失败");
            return result;
        } else {
            result.addAllResults(instances);
            result.setSuccess(true);
            result.setMessages("成功: 已获取任务实例(" + instanceId + ")所有依赖，总计获得" + instanceDisplayDOs.size() + "个实例");
            return result;
        }
    }

    /**
     * 查看最长路径，依次查找前序节点中最晚结束的节点
     */
    public Result<JSONObject> handleGetLongestPath(String instanceId) {
        Result<JSONObject> result = new Result<JSONObject>();
        List<InstanceDisplayDO> instanceDisplayDOs = jobMonitorService.getLongestPath(instanceId);
        List<JSONObject> instances = parseJson(instanceDisplayDOs, 1);
        if (instanceDisplayDOs == null) {
            result.setSuccess(false);
            result.setMessages("失败: 获得任务实例(" + instanceId + ")最长路径失败");
            return result;
        } else {
            result.addAllResults(instances);
            result.setSuccess(true);
            result.setMessages("成功: 已获取任务实例(" + instanceId + ")最长路径，总计获得" + instanceDisplayDOs.size() + "个实例");
            return result;
        }
    }

    /**
     * 停止预跑
     */
    public Result<Object> handleBatchStopTask(String taskId) {
        Result<Object> result = new Result<Object>();
        int res = jobMonitorService.batchStopTask(taskId);
        if (res > 0) {
            result.setSuccess(true);
            result.setMessages("成功: 任务(" + taskId + ")已停止预跑");
        } else {
            result.setSuccess(false);
            result.setMessages("失败: 任务(" + taskId + ")停止预跑失败");
        }
        return result;
    }

    /**
     * 快速通道
     */
    public Result<Object> handleRaisePriorityInstance(String instanceId) {
        Result<Object> result = new Result<Object>();
        int res = jobMonitorService.raisePriorityInstance(instanceId);
        if (res > 0) {
            result.setSuccess(true);
            result.setMessages("成功: 任务实例(" + instanceId + ")已设置为快速通道");
        } else {
            result.setSuccess(false);
            result.setMessages("失败: 任务实例(" + instanceId + ")设置快速通道失败");
        }
        return result;
    }

    /**
     * 获取级联重跑所有需要重跑的instance
     */
    public Result handleRecallCascadeGetInstances(String instanceId, String date) {
        Result<List<InstanceChildrenDO>> result = new Result<List<InstanceChildrenDO>>();
        List<InstanceChildrenDO> allChildren = jobMonitorService.recallCascadeGetInstances(instanceId, date);
        if (null == allChildren || allChildren.isEmpty()) {
            result.setMessages("获取任务" + instanceId + "的后继任务失败");
            result.setSuccess(false);
        } else {
            result.setMessages("成功获取" + instanceId + "的后继任务");
            result.setSuccess(true);
            result.setResult(allChildren);
        }
        return result;
    }

    /**
     * 级联重跑
     */
    public Result handleRecallCascade(CascadeDO cascadeDO) {
        Result<Integer> result = new Result<Integer>();
        Integer recourdNum = jobMonitorService.recallCascade(cascadeDO);
        if (null == recourdNum || 0 >= recourdNum) {
            result.setMessages("级联重跑任务失败");
            result.setSuccess(false);
        } else {
            result.setMessages("成功级联重跑" + recourdNum + "任务");
            result.setSuccess(true);
            result.setResult(recourdNum);
        }
        return result;
    }

    /**
     * 获取日志
     */
    public Result handleGetInstanceLog(String logPath) {
        Result<String> result = new Result<String>();
        String logContent = jobMonitorService.getInstanceLog(logPath);
        if (null == logContent || logContent.isEmpty()) {
            result.setMessages("获取日志文件失败");
            result.setSuccess(false);
        } else {
            result.setMessages("成功获取日志文件");
            result.setSuccess(true);
            result.setResult(logContent);
        }
        return result;
    }

    /**
     * 查询预跑instance
     */
    public Result handleQueryPreRunInstances(String startTime, String task_commiter, String taskIDorName) {
        Result<List<PreRunResultDO>> result = new Result<List<PreRunResultDO>>();
        List<HashMap<String, Object>> preRunResult = jobMonitorService.queryPreRunInstances(startTime, task_commiter, taskIDorName);
        if (preRunResult == null) {
            result.setMessages("查询预跑任务失败");
            result.setSuccess(false);
        } else {
            result.setMessages("成功获取" + preRunResult.size() + "个预跑任务实例");
            result.setSuccess(true);
            result.setResult(parsePreRunResultInfo(preRunResult));
        }
        return result;
    }

    /**
     * 任务实例状态分析
     */
    public Result<String> handleInstanceStatusAnalyze(String instanceId) {
        Result<String> result = new Result<String>();
        String analysis = jobMonitorService.instanceStatusAnalyze(instanceId);
        result.setSuccess(true);
        result.setResult(analysis);
        return result;
    }

    /**
     * 将预跑任务的查询结果转换为PreRunResultDO的list
     */
    private List<PreRunResultDO> parsePreRunResultInfo(List<HashMap<String, Object>> preRunResult) {
        Map<Integer, PreRunResultDO> preRunResultMapInfo = new HashMap<Integer, PreRunResultDO>();
        List<PreRunResultDO> preRunResultListInfo = new ArrayList<PreRunResultDO>();

        for (HashMap<String, Object> instance : preRunResult) {
            Integer task_id = (Integer) instance.get("task_id");
            Integer status = (Integer) instance.get("status");
            if (!preRunResultMapInfo.containsKey(task_id)) {
                PreRunResultDO preRunResultDO = new PreRunResultDO();
                preRunResultDO.setTask_id(task_id);
                preRunResultDO.setTask_name((String) instance.get("task_name"));
                preRunResultDO.setCycle((String) instance.get("cycle"));
                preRunResultDO.setTask_success_num(0);
                preRunResultDO.setTask_unSuccess_num(0);
                preRunResultDO.setInstanceList(new ArrayList<TaskInstanceDO>());
                preRunResultMapInfo.put(task_id, preRunResultDO);
            }
            if (status == 1) {
                preRunResultMapInfo.get(task_id).incr_success_num();
            } else {
                preRunResultMapInfo.get(task_id).incr_unSuccess_num();
            }
            TaskInstanceDO taskInstanceDO = new TaskInstanceDO();
            taskInstanceDO.setInstanceID((String) instance.get("task_status_id"));
            if (instance.get("start_time") != null) {
                taskInstanceDO.setStart_time(instance.get("start_time").toString());
            }
            if (instance.get("end_time") != null) {
                taskInstanceDO.setEnd_time(instance.get("end_time").toString());
            }
            if (instance.get("time_id") != null) {
                taskInstanceDO.setTime_id(instance.get("time_id").toString());

            }
            taskInstanceDO.setSchedule_cycle(instance.get("cycle").toString());
            taskInstanceDO.setStatus((Integer) instance.get("status"));
            taskInstanceDO.setTask_committer((String) instance.get("task_committer"));
            taskInstanceDO.setLog_path((String) instance.get("log_path"));
            preRunResultMapInfo.get(task_id).getInstanceList().add(taskInstanceDO);
        }
        for (Integer key : preRunResultMapInfo.keySet()) {
            preRunResultListInfo.add(preRunResultMapInfo.get(key));
        }
        return preRunResultListInfo;
    }

    /**
     * 将InstanceDisplayDO的list转化为jsonobject格式的list
     * 5000个实例的执行时间大概在100ms之内，偶尔会出现较大的执行时间，例如500ms左右，原因未知
     */
    private List<JSONObject> parseJson(List<InstanceDisplayDO> instanceDisplayDOs, int index) {
        if (instanceDisplayDOs == null)
            return null;
        List<JSONObject> instances = new ArrayList<JSONObject>();
        for (InstanceDisplayDO instanceDisplayDO : instanceDisplayDOs)
            instances.add(JSONObject.fromObject(instanceDisplayDO.toString(index)));
        return instances;
    }
}
