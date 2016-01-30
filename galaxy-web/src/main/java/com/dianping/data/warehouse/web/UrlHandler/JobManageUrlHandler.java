package com.dianping.data.warehouse.web.UrlHandler;

import com.dianping.data.warehouse.core.utils.GalaxyDateUtils;
import com.dianping.data.warehouse.core.utils.TaskValidator;
import com.dianping.data.warehouse.domain.McTableInfo;
import com.dianping.data.warehouse.domain.model.CascadeDO;
import com.dianping.data.warehouse.domain.model.StarShuttleDO;
import com.dianping.data.warehouse.domain.web.StarShuttleParasDO;
import com.dianping.data.warehouse.halley.domain.*;
import com.dianping.data.warehouse.service.AutoBuildTabService;
import com.dianping.data.warehouse.service.AutoETLService;
import com.dianping.data.warehouse.service.JobManageService;
import com.dianping.data.warehouse.service.TableService;
import com.dianping.data.warehouse.web.Result;
import com.dianping.data.warehouse.web.common.Const;
import com.dianping.data.warehouse.web.model.BuildTableInfoDO;
import com.dianping.data.warehouse.web.model.CalculateTaskAddDO;
import com.dianping.data.warehouse.web.model.TaskTransferAndScheduleSaveDO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sunny on 14-8-26.
 */
@Service("jobManageUrlHandler")
public class JobManageUrlHandler {

    @Resource(name = "jobManageService")
    private JobManageService jobManageService;

    @Resource(name = "tableService")
    private TableService tableService;

    @Resource(name = "autoETLServiceImpl")
    private AutoETLService autoETLService;

    @Resource(name = "autoBuildTabService")
    private AutoBuildTabService autoBuildTabService;

    /**
     * 根据查询条件查询tasks
     */
    public Result handleQueryTasks(TaskQueryDO taskQueryDO) {
        Result<TaskDO> result = new Result<TaskDO>();
        List<TaskDO> taskList = jobManageService.queryTasks(taskQueryDO);
        if (taskList == null) {
            result.setMessages("查询任务失败");
            result.setSuccess(false);
        } else {
            result.setMessages("成功获取" + taskList.size() + "个任务");
            result.setSuccess(true);
            result.addAllResults(taskList);
        }
        return result;
    }

    /**
     * 根据taskId获得task
     */
    public Result<TaskDO> handleGetTaskByTaskId(int taskId) {
        Result<TaskDO> result = new Result<TaskDO>();
        TaskDO taskDO = jobManageService.getTaskByTaskId(taskId);
        if (taskDO == null) {
            result.setSuccess(false);
            result.setMessages("失败: 查询任务(" + taskId + ")失败");
        } else {
            result.setResult(taskDO);
            result.setSuccess(true);
            result.setMessages("成功: 查询任务(" + taskId + ")成功");
        }
        return result;
    }

    /**
     * 根据taskId获得其所有影响任务
     * 如果是计算任务，即本身和后继任务，如果是传输任务即本身、后继任务以及同源任务（58、59)
     */
    public Result handleGetInfluencedTasksByTaskId(String taskId) {
        Result<TaskDO> result = new Result<TaskDO>();
        TaskDO taskDO = jobManageService.getTaskByTaskId(Integer.parseInt(taskId));
        List<TaskDO> taskDOs = jobManageService.getPostTasksByTaskId(taskId);
        if (taskDO.getType() == 1) {
            List<TaskDO> sameTaskDOs = getSameTransferTasks(taskDO);
            taskDOs.addAll(sameTaskDOs);
        }
        if (taskDOs.size() == 0)
            result.setSuccess(false);
        else
            result.setSuccess(true);
        result.addAllResults(taskDOs);
        return result;
    }

    /**
     * 根据taskId获得其所有同源任务
     */
    public Result handleGetSameSourceTasksByTaskId(String taskId) {
        Result<TaskDO> result = new Result<TaskDO>();
        TaskDO taskDO = jobManageService.getTaskByTaskId(Integer.parseInt(taskId));
        List<TaskDO> taskDOs = new ArrayList<TaskDO>();
        taskDOs.add(taskDO);
        if (taskDO.getType() == 1) {
            List<TaskDO> sameTaskDOs = getSameTransferTasks(taskDO);
            taskDOs.addAll(sameTaskDOs);
        }
        if (taskDOs.size() == 0)
            result.setSuccess(false);
        else
            result.setSuccess(true);
        result.addAllResults(taskDOs);
        return result;
    }

    /**
     * 检测taskName是否合法
     */
    public Result<Object> handleCheckTaskName(String taskName) {
        Result<Object> result = new Result<Object>();
        boolean taskNameCheck = jobManageService.checkTaskName(taskName);
        if (taskNameCheck) {
            result.setSuccess(true);
            result.setMessages("成功: 任务名(" + taskName + ")可以使用");
        } else {
            result.setSuccess(false);
            result.setMessages("警告: 任务名(" + taskName + ")已存在");
        }
        return result;
    }

    /**
     * 根据任务传输任务的ID获取任务的源表信息
     */
    public Result<McTableInfo> handleGetSourceTable(int taskId) {
        Result<McTableInfo> result = new Result<McTableInfo>();
        McTableInfo mcTableInfo = jobManageService.getSourceTable(taskId);
        if (mcTableInfo != null) {
            result.setSuccess(true);
            result.addResults(mcTableInfo);
        } else {
            result.setSuccess(false);
            result.setMessages("失败: 获得源表信息失败");
        }
        return result;
    }

    /**
     * 根据任务ID获取任务的目标表信息
     */
    public Result<McTableInfo> handleGetTargetTable(int taskId) {
        Result<McTableInfo> result = new Result<McTableInfo>();
        McTableInfo mcTableInfo = jobManageService.getTargetTable(taskId);
        if (mcTableInfo != null) {
            result.setSuccess(true);
            result.setResult(mcTableInfo);
        } else {
            result.setSuccess(false);
            result.setMessages("失败: 获得目标表信息失败");
        }
        return result;
    }

    /**
     * 级联预跑获得需要预跑的实例
     */
    public Result<TaskRelaDO> handleGetTasksForCascadePreRun(int taskId) {
        Result<TaskRelaDO> result = new Result<TaskRelaDO>();
        List<TaskRelaDO> taskRelaDOs = jobManageService.getTasksForCascadePreRun(taskId);
        if (taskRelaDOs != null) {
            result.setSuccess(true);
            result.addAllResults(taskRelaDOs);
        } else {
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 级联预跑给定任务
     */
    public Result handleCascadePreRun(CascadeDO cascadeDO, String committer) {
        Result<Integer> result = new Result<Integer>();
        boolean res = jobManageService.cascadePreRun(cascadeDO, committer);
        if (res) {
            result.setMessages("成功预跑选中任务");
            result.setSuccess(true);
        } else {
            result.setMessages("级联预跑任务失败");
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 预跑传输任务
     */
    public Result handlePreRunTransferTask(String startTime, String endTime, String committer, Integer taskId) {
        Result<List<Object>> result = new Result<List<Object>>();
        List<Integer> taskIds = tableService.getTaskIdsByTaskId(taskId);
        boolean res = true;
        for (Integer taskID : taskIds) {
            res = res && jobManageService.preRunTask(startTime, endTime, committer, taskID);
        }
        if (res) {
            result.setSuccess(true);
            result.setMessages("成功预跑" + taskIds.size() + "个任务：" + taskIds);
        } else {
            result.setSuccess(false);
            result.setMessages("预跑任务失败");
        }
        return result;
    }

    /**
     * 失效传输任务
     */
    public Result handleInvalidTransferTask(Integer taskId, String updateTime, String updateUser) {
        Result<Integer> result = new Result<Integer>();
        tableService.updateTableStatus(taskId, 0);
        List<Integer> taskIds = tableService.getTaskIdsByTaskId(taskId);
        boolean res = true;
        for (Integer taskID : taskIds) {
            res = res && jobManageService.invalidTask(taskID, updateTime, updateUser);
        }
        if (res) {
            result.setSuccess(true);
            result.setMessages("成功失效" + taskIds.size() + "个任务：" + taskIds);
            result.addAllResults(taskIds);
        } else {
            result.setSuccess(false);
            result.setMessages("失效操作失败");
        }
        return result;
    }

    /**
     * 生效传输任务
     */
    public Result handleValidTransferTask(Integer taskId, String updateTime, String updateUser) {
        Result<Integer> result = new Result<Integer>();
        tableService.updateTableStatus(taskId, 1);
        List<Integer> taskIds = tableService.getTaskIdsByTaskId(taskId);
        boolean res = true;
        for (Integer taskID : taskIds) {
            res = res && jobManageService.validTask(taskID, updateTime, updateUser);
        }
        if (res) {
            result.setSuccess(true);
            result.setMessages("成功生效" + taskIds.size() + "个任务：" + taskIds);
            result.addAllResults(taskIds);
        } else {
            result.setSuccess(false);
            result.setMessages("生效操作失败");
        }
        return result;
    }

    /**
     * 删除传输任务
     */
    public Result<Integer> handleDeleteTransferTask(String taskID, String updateTime, String updateUser) {
        Result<Integer> result = new Result<Integer>();
        List<Integer> taskIds = tableService.getTaskIdsByTaskId(new Integer(taskID));
        boolean flag = autoETLService.deleteAutoEtlTask(new Integer(taskID), updateTime, updateUser);
        if (flag) {
            result.addAllResults(taskIds);
            result.setSuccess(true);
            result.setMessages("成功删除任务: " + taskIds);
            return result;
        } else {
            result.setSuccess(false);
            result.setMessages("删除任务失败");
            return result;
        }
    }

    /**
     * 新增传输任务
     */
    public Result<Object> handleSaveTaskTransferAndScheduleInfo(TaskTransferAndScheduleSaveDO saveData, String pinyinName) {
        Result<Object> result = new Result<Object>();
        boolean isSaveSuccess;
        StarShuttleDO starShuttleDO = saveData.getStarShuttleDO();
        StarShuttleParasDO starShuttleParasDO = saveData.getStarShuttleParasDO();
        if (starShuttleDO == null) {
            starShuttleDO = autoETLService.getAllInfo(starShuttleParasDO);
            starShuttleDO.getTaskDO().setOwner(pinyinName);
            starShuttleDO.getTaskDO().setUpdateUser(pinyinName);
            starShuttleDO.getTaskDO().setAddUser(pinyinName);
        }
        String nowTimeStamp = GalaxyDateUtils.getCurrentTimeStampStr();
        starShuttleDO.getTaskDO().setUpdateTime(nowTimeStamp);
        starShuttleDO.getTaskDO().setUpdateUser(pinyinName);
        isSaveSuccess = autoETLService.generateAutoETLTask(starShuttleParasDO, starShuttleDO,
                GalaxyDateUtils.getCurrentTimeStampStr(), pinyinName);
        result.setSuccess(isSaveSuccess);
        result.setMessages(isSaveSuccess ? "成功配置任务" : "新增任务失败");
        return result;
    }

    /**
     * 传输任务调度配置页面更新
     */
    public Result<TaskDO> handelUpdateTransferTask(TaskTransferAndScheduleSaveDO saveData) {
        TaskDO taskDO = saveData.getStarShuttleDO().getTaskDO();
        Result<TaskDO> result = validateTask(taskDO);
        if (!result.isSuccess())
            return result;
        boolean res = true;
        List<Integer> taskIds = tableService.getTaskIdsByTaskId(taskDO.getTaskId());
        //先check环依赖
        for (int i = 0; i < taskIds.size(); i++) {
            taskDO.setTaskId(taskIds.get(i));
            taskDO.setTaskIdForTaskRelaDO();
            if (jobManageService.hasCycleDependence(taskDO)) {
                result.setSuccess(false);
                result.setMessages("失败: 任务(" + taskDO.getTaskName() + ")存在依赖环，请检查依赖配置!");
                return result;
            }
        }
        //再依次增加
        for (int i = 0; i < taskIds.size(); i++) {
            TaskDO originTask = jobManageService.getTaskByTaskId(taskIds.get(i));
            taskDO.setTaskId(originTask.getTaskId());
            taskDO.setTaskName(originTask.getTaskName());
            taskDO.setTaskIdForTaskRelaDO();
            res = res && jobManageService.updateTask(taskDO);
        }
        autoETLService.updateMcTableInfo(saveData.getStarShuttleDO(), saveData.getStarShuttleParasDO());
        if (res) {
            result.setSuccess(true);
            result.addResults(taskDO);
            result.setMessages("成功: 任务(" + taskIds + ")已成功保存");
        } else {
            result.setSuccess(false);
            result.setMessages("失败: 任务(" + taskIds + ")保存失败");
        }
        return result;
    }

    /**
     * 预跑计算任务
     */
    public Result handlePreRunCalculateTask(String startTime, String endTime, String committer, Integer taskId) {
        Result<List<Object>> result = new Result<List<Object>>();
        boolean res = jobManageService.preRunTask(startTime, endTime, committer, taskId);
        if (res) {
            result.setSuccess(true);
            result.setMessages("成功预跑任务：" + taskId);
        } else {
            result.setSuccess(false);
            result.setMessages("预跑任务失败");
        }
        return result;
    }

    /**
     * 失效计算任务
     */
    public Result handleInvalidCalculateTask(Integer taskId, String updateTime, String updateUser) {
        Result<Integer> result = new Result<Integer>();
        tableService.updateTableStatus(taskId, 0);
        boolean res = jobManageService.invalidTask(taskId, updateTime, updateUser);
        if (res) {
            result.setSuccess(true);
            result.setMessages("成功失效任务: " + taskId);
            result.addResults(taskId);
        } else {
            result.setSuccess(false);
            result.setMessages("失效操作失败");
        }
        return result;
    }

    /**
     * 生效计算任务
     */
    public Result handleValidCalculateTask(Integer taskId, String updateTime, String updateUser) {
        Result<Integer> result = new Result<Integer>();
        tableService.updateTableStatus(taskId, 1);
        boolean res = jobManageService.validTask(taskId, updateTime, updateUser);
        if (res) {
            result.setSuccess(true);
            result.setMessages("成功生效任务: " + taskId);
            result.addResults(taskId);
        } else {
            result.setSuccess(false);
            result.setMessages("生效操作失败");
        }
        return result;
    }

    /**
     * 删除计算任务
     */
    public Result<Integer> handleDeleteCalculateTask(String taskID, String updateTime, String updateUser) {
        Result<Integer> result = new Result<Integer>();
        boolean flag = jobManageService.deleteCalculateTask(Integer.parseInt(taskID), updateTime, updateUser);
        if (flag) {
            result.setSuccess(true);
            result.addResults(Integer.parseInt(taskID));
            result.setMessages("成功删除任务: " + taskID + "");
            return result;
        } else {
            result.setSuccess(false);
            result.setMessages("删除任务失败");
            return result;
        }
    }

    /**
     * 新增计算任务
     */
    public Result<Object> handleAddCalculateTask(CalculateTaskAddDO calculateTaskAddDO, String updateTime, String updateUser) {
        Result result = addCalculateTask(calculateTaskAddDO);
        if (!result.isSuccess())
            return result;
        if (calculateTaskAddDO.getBuildTableInfoDO() != null)
            result = syncTable(calculateTaskAddDO, updateTime, updateUser);
        if (result.isSuccess()) {
            result.setMessages("成功新增任务：" + calculateTaskAddDO.getTaskDO().getTaskId() + "(" +
                    calculateTaskAddDO.getTaskDO().getTaskName() + ")");
        }
        return result;
    }

    /**
     * 计算任务调度配置更新
     */
    public Result<TaskDO> handleUpdateCalculateTask(TaskDO taskDO) {
        Result<TaskDO> result = validateTask(taskDO);
        if (!result.isSuccess())
            return result;
        if (jobManageService.hasCycleDependence(taskDO)) {
            result.setSuccess(false);
            result.setMessages("失败: 任务(" + taskDO.getTaskName() + ")存在依赖环，请检查依赖配置!");
            return result;
        }
        boolean res = jobManageService.updateTask(taskDO);
        if (res) {
            result.setSuccess(true);
            result.addResults(taskDO);
            result.setMessages("成功: 任务(" + taskDO.getTaskName() + ")已成功保存");
        } else {
            result.setSuccess(false);
            result.setMessages("失败: 任务(" + taskDO.getTaskName() + ")保存失败");
        }
        return result;
    }

    /**
     * 新增传输任务，向数据库增加记录
     */
    private Result addCalculateTask(CalculateTaskAddDO calculateTaskAddDO) {
        TaskDO taskDO = calculateTaskAddDO.getTaskDO();
        Result<TaskDO> result = validateTaskName(taskDO.getTaskName());
        if (!result.isSuccess())
            return result;
        result = validateTask(taskDO);
        if (!result.isSuccess())
            return result;
        boolean res = jobManageService.insertTask(taskDO);
        if (!res) {
            result.setSuccess(false);
            result.setMessages("失败: 任务(" + taskDO.getTaskName() + ")保存失败");
        } else {
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 同步表结构
     */
    private Result<Object> syncTable(CalculateTaskAddDO calculateTaskAddDO, String updateTime, String updateUser) {
        Result<Object> result = new Result<Object>();
        BuildTableInfoDO buildTableInfoDO = calculateTaskAddDO.getBuildTableInfoDO();
        buildTableInfoDO.getBuildTabParaDO().setRefreshCycle(calculateTaskAddDO.getTaskDO().getCycle());
        Integer taskID = calculateTaskAddDO.getTaskDO().getTaskId();
        if (null != taskID) {
            buildTableInfoDO.getBuildTabParaDO().setTaskId(taskID);
        }
        List<McTableInfo> mcTableInfos = new ArrayList<McTableInfo>();
        List<TaskRelaDO> taskRelaDOs = calculateTaskAddDO.getTaskDO().getRelaDOList();
        for (TaskRelaDO taskRelaDO : taskRelaDOs) {
            McTableInfo mcTableInfo = tableService.getTableByTaskId(taskRelaDO.getTaskPreId());
            if (mcTableInfo != null)
                mcTableInfos.add(mcTableInfo);
        }
        buildTableInfoDO.getBuildTabParaDO().setSrcTableList(mcTableInfos);
        Boolean isBuildSuccess = autoBuildTabService.buildTable(buildTableInfoDO.getBuildTabParaDO(), buildTableInfoDO.getSql());
        if (!isBuildSuccess) {
            jobManageService.deleteCalculateTask(taskID, updateTime, updateUser);
            result.setMessages("建表失败");
            result.setSuccess(false);
        } else {
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 验证taskDO的参数是否符合规则，不符合的话返回错误result
     */
    private Result<TaskDO> validateTask(TaskDO taskDO) {
        Result<TaskDO> result = new Result<TaskDO>();
        String[] isValid = TaskValidator.validateTask(taskDO);
        //valid检测不通过res = false
        if (isValid[0].equals("1")) {
            result.setSuccess(true);
        } else {
            result.setSuccess(false);
            result.setMessages("失败: 任务参数验证失败");
        }
        return result;
    }

    /**
     * 验证TaskName是否重复
     */
    private Result<TaskDO> validateTaskName(String taskName) {
        Result<TaskDO> result = new Result<TaskDO>();
        result.setSuccess(true);
        boolean taskNameCheck = jobManageService.checkTaskName(taskName);
        if (!taskNameCheck) {
            result.setSuccess(false);
            result.setMessages("失败: 任务名(" + taskName + ")已存在");
        }
        return result;
    }

    /**
     * 返回传输任务的同源任务，例如58、59
     */
    private List<TaskDO> getSameTransferTasks(TaskDO taskDO) {
        Integer taskId = taskDO.getTaskId();
        List<Integer> taskIds = tableService.getTaskIdsByTaskId(taskId);
        List<TaskDO> taskDOs = new ArrayList<TaskDO>();
        if (taskIds.size() > 1) {
            taskIds.remove(taskId);
            for (int i = 0; i < taskIds.size(); i++) {
                TaskDO task = jobManageService.getTaskByTaskId(taskIds.get(i));
                if (task != null)
                    taskDOs.add(task);
            }
        }
        return taskDOs;
    }
}
