package com.dianping.data.warehouse.web.controller;

import com.dianping.data.warehouse.core.model.UserDO;
import com.dianping.data.warehouse.core.utils.GalaxyDateUtils;
import com.dianping.data.warehouse.domain.model.CascadeDO;
import com.dianping.data.warehouse.halley.domain.TaskDO;
import com.dianping.data.warehouse.halley.domain.TaskQueryDO;
import com.dianping.data.warehouse.web.Result;
import com.dianping.data.warehouse.web.UrlHandler.JobManageUrlHandler;
import com.dianping.data.warehouse.web.model.CalculateTaskAddDO;
import com.dianping.data.warehouse.web.model.TaskTransferAndScheduleSaveDO;
import com.dianping.data.warehouse.web.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * Created by Sunny on 14-8-26.
 */
@Controller
@RequestMapping(value = "/jobManage")
public class JobManageController extends AbstractController {

    @Resource(name = "jobManageUrlHandler")
    JobManageUrlHandler jobManageUrlHandler;

    Logger logger = LoggerFactory.getLogger(JobManageController.class);

    /**
     * 根据查询条件查询tasks
     */
    @RequestMapping(value = "/queryTasks", method = RequestMethod.GET)
    public
    @ResponseBody
    Result queryTasks(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "group", defaultValue = "") String group,
            @RequestParam(value = "cycle", defaultValue = "") String cycle,
            @RequestParam(value = "developer", defaultValue = "") String developer,
            @RequestParam(value = "nameOrId", defaultValue = "") String nameOrId
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") query tasks, group: " + group + ", cycle: " + cycle +
                " ,developer: " + developer + " ,nameOrId: " + nameOrId);
        try {
            TaskQueryDO taskQueryDO = new TaskQueryDO(Integer.parseInt(group), cycle, developer, nameOrId);
            return jobManageUrlHandler.handleQueryTasks(taskQueryDO);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") query tasks error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 根据taskId获得任务
     */
    @RequestMapping(value = "/getTaskByTaskId", method = RequestMethod.GET)
    public
    @ResponseBody
    Result queryTask(
            @RequestParam(value = "taskId", defaultValue = "") String taskId,
            @ModelAttribute("user") UserDO user
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") query task: taskId(" + taskId + ")");
        try {
            return jobManageUrlHandler.handleGetTaskByTaskId(Integer.parseInt(taskId));
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") query task error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 根据taskId获得其所有影响任务
     * 如果是计算任务，即本身和后继任务，如果是传输任务即本身、后继任务以及同源任务（58、59)
     */
    @RequestMapping(value = "/getInfluencedTasksByTaskId", method = RequestMethod.GET)
    public
    @ResponseBody
    Result getInfluencedTasksByTaskId(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "taskId", defaultValue = "") String taskId
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") get influenced tasks by taskId: taskId(" + taskId + ")");
        try {
            return jobManageUrlHandler.handleGetInfluencedTasksByTaskId(taskId);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") get influenced tasks by taskId error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 根据taskId获得其所有同源任务
     */
    @RequestMapping(value = "/getSameSourceTasksByTaskId", method = RequestMethod.GET)
    public
    @ResponseBody
    Result getSameSourceTasksByTaskId(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "taskId", defaultValue = "") String taskId
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") get same source tasks by taskId: taskId(" + taskId + ")");
        try {
            return jobManageUrlHandler.handleGetSameSourceTasksByTaskId(taskId);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") get same source tasks by taskId error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 检测taskName是否合法
     */
    @RequestMapping(value = "/checkTaskName", method = RequestMethod.GET)
    public
    @ResponseBody
    Result checkTaskName(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "taskName", defaultValue = "") String taskName
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") check task name: taskName(" + taskName + ")");
        try {
            return jobManageUrlHandler.handleCheckTaskName(taskName);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") check task name error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 根据传输任务的ID获取任务的源表信息
     */
    @RequestMapping(value = "/getSourceTable", method = RequestMethod.GET)
    public
    @ResponseBody
    Result getSourceTable(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "taskId", defaultValue = "") String taskId
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") get source table: taskId(" + taskId + ")");
        try {
            return jobManageUrlHandler.handleGetSourceTable(Integer.parseInt(taskId));
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") get pedigree error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 根据任务ID获取任务的目标表信息
     */
    @RequestMapping(value = "/getTargetTable", method = RequestMethod.GET)
    public
    @ResponseBody
    Result getTargetTable(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "taskId", defaultValue = "") String taskId
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") get pedigree: taskId(" + taskId + ")");
        try {
            return jobManageUrlHandler.handleGetTargetTable(Integer.parseInt(taskId));
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") get pedigree error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 级联预跑获得需要预跑的实例
     */
    @RequestMapping(value = "/getTasksForCascadePreRun", method = RequestMethod.GET)
    public
    @ResponseBody
    Result getTasksForCascadePreRun(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "taskId", defaultValue = "") String taskId
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") get pedigree: taskId(" + taskId + ")");
        try {
            return jobManageUrlHandler.handleGetTasksForCascadePreRun(Integer.parseInt(taskId));
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") get pedigree error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 级联预跑给定任务
     */
    @RequestMapping(value = "/cascadePreRun", method = RequestMethod.POST)
    public
    @ResponseBody
    Result cascadePreRun(
            @ModelAttribute("user") UserDO user,
            @Valid @RequestBody CascadeDO cascadeDO
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") cascade preRun tasks at date [" +
                cascadeDO.getStartDate() + " , " + cascadeDO.getEndDate() + "]" + cascadeDO.getTaskIds());
        try {
            return jobManageUrlHandler.handleCascadePreRun(cascadeDO, user.getEmployPinyin());
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") recall cascaded job error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 预跑任务
     */
    @RequestMapping(value = "/preRunTask", method = RequestMethod.POST)
    public
    @ResponseBody
    Result preRunTask(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "startTime", defaultValue = "") String startTime,
            @RequestParam(value = "endTime", defaultValue = "") String endTime,
            @RequestParam(value = "taskId", defaultValue = "") String taskId,
            @RequestParam(value = "committer", defaultValue = "") String committer,
            @RequestParam(value = "type", defaultValue = "") String type
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") prerun task: taskId(" + taskId + ")");
        try {
            if (type.equals("transfer"))
                return jobManageUrlHandler.handlePreRunTransferTask(startTime, endTime, committer, Integer.parseInt(taskId));
            else
                return jobManageUrlHandler.handlePreRunCalculateTask(startTime, endTime, committer, Integer.parseInt(taskId));
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") prerun task error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 失效任务
     */
    @RequestMapping(value = "/invalidTask", method = RequestMethod.POST)
    public
    @ResponseBody
    Result invalidTask(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "taskId", defaultValue = "") String taskId,
            @RequestParam(value = "type", defaultValue = "") String type
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") invalid task: taskId(" + taskId + ")");
        try {
            String nowTimeStamp = GalaxyDateUtils.getCurrentTimeStampStr();
            if (type.equals("transfer"))
                return jobManageUrlHandler.handleInvalidTransferTask(Integer.parseInt(taskId), nowTimeStamp,
                        user.getEmployPinyin());
            else
                return jobManageUrlHandler.handleInvalidCalculateTask(Integer.parseInt(taskId), nowTimeStamp,
                        user.getEmployPinyin());
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") invalid task error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 生效任务
     */
    @RequestMapping(value = "/validTask", method = RequestMethod.POST)
    public
    @ResponseBody
    Result validTask(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "taskId", defaultValue = "") String taskId,
            @RequestParam(value = "type", defaultValue = "") String type
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") valid task: taskId(" + taskId + ")");
        try {
            String nowTimeStamp = GalaxyDateUtils.getCurrentTimeStampStr();
            if (type.equals("transfer"))
                return jobManageUrlHandler.handleValidTransferTask(Integer.parseInt(taskId), nowTimeStamp,
                        user.getEmployPinyin());
            else
                return jobManageUrlHandler.handleValidCalculateTask(Integer.parseInt(taskId), nowTimeStamp,
                        user.getEmployPinyin());
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") valid task error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 删除任务
     */
    @RequestMapping(value = "/deleteTask", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Result deleteTask(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "taskId", defaultValue = "") String taskId,
            @RequestParam(value = "type", defaultValue = "") String type
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") delete task: taskId(" + taskId + ")");
        try {
            String nowTimeStamp = GalaxyDateUtils.getCurrentTimeStampStr();
            if (type.equals("transfer"))
                return jobManageUrlHandler.handleDeleteTransferTask(taskId, nowTimeStamp, user.getEmployPinyin());
            else
                return jobManageUrlHandler.handleDeleteCalculateTask(taskId, nowTimeStamp, user.getEmployPinyin());
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") delete task error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 新增传输任务
     */
    @RequestMapping(value = "/addTransferTask", method = RequestMethod.POST)
    public
    @ResponseBody
    Result addTransferTask(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "method", defaultValue = "") String method,
            @Valid @RequestBody TaskTransferAndScheduleSaveDO saveData
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") add transfer task: taskName(" +
                saveData.getStarShuttleDO().getTaskDO().getTaskName() + ")");
        try {
            return jobManageUrlHandler.handleSaveTaskTransferAndScheduleInfo(saveData, user.getEmployPinyin());
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") add transfer task error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 新增计算任务
     */
    @RequestMapping(value = "/addCalculateTask", method = RequestMethod.POST)
    public
    @ResponseBody
    Result addCalculateTask(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "method", defaultValue = "") String method,
            @Valid @RequestBody CalculateTaskAddDO calculateTaskAddDO
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") add calculate task: taskName(" +
                calculateTaskAddDO.getTaskDO().getTaskName() + ")");
        try {
            TaskDO taskDO = calculateTaskAddDO.getTaskDO();
            taskDO.setParametersForAdd(user.getEmployPinyin());
            return jobManageUrlHandler.handleAddCalculateTask(calculateTaskAddDO, GalaxyDateUtils.getCurrentTimeStampStr(), user.getEmployPinyin());
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") add transfer task error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 修改传输任务的调度配置信息
     */
    @RequestMapping(value = "/updateTransferTask", method = RequestMethod.POST)
    public
    @ResponseBody
    Result updateTransferTask(
            @ModelAttribute("user") UserDO user,
            @Valid @RequestBody TaskTransferAndScheduleSaveDO saveData
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") update transfer task: taskId(" +
                saveData.getStarShuttleDO().getTaskDO().getTaskId() + ")");
        try {
            return jobManageUrlHandler.handelUpdateTransferTask(saveData);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") update transfer task error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 修改计算任务的调度配置
     * 在使用前请确保由传入参数组成的TaskDO格式正确
     */
    @RequestMapping(value = "/updateCalculateTask", method = RequestMethod.POST)
    public
    @ResponseBody
    Result updateCalculateTask(
            @ModelAttribute("user") UserDO user,
            @RequestBody TaskDO taskDO
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") save calculate task: taskName(" + taskDO.getTaskName() + ")");
        try {
            taskDO.setParametersForUpdate(user.getEmployPinyin());
            return jobManageUrlHandler.handleUpdateCalculateTask(taskDO);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") save calculate task error", e);
            return getExceptionResult("系统错误");
        }
    }
}
