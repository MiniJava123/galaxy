package com.dianping.data.warehouse.web.controller;

import com.dianping.data.warehouse.core.model.UserDO;
import com.dianping.data.warehouse.domain.model.CascadeDO;
import com.dianping.data.warehouse.halley.domain.InstanceQueryDO;
import com.dianping.data.warehouse.web.Result;
import com.dianping.data.warehouse.web.UrlHandler.JobMonitorUrlHandler;
import com.dianping.data.warehouse.web.util.CommonUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * Created by Sunny on 14-8-26.
 */
@Controller
@RequestMapping(value = "/jobMonitor")
public class JobMonitorController extends AbstractController {

    @Resource(name = "jobMonitorUrlHandler")
    JobMonitorUrlHandler jobMonitorUrlHandler;

    Logger logger = LoggerFactory.getLogger(JobMonitorController.class);

    /**
     * 任务查询
     * 在使用前请确保由传入参数组成的InstanceQueryDO格式正确
     */
    @RequestMapping(value = "/queryInstances", method = RequestMethod.GET)
    public
    @ResponseBody
    Result queryInstances(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "startDate", defaultValue = "") String startDate,
            @RequestParam(value = "endDate", defaultValue = "") String endDate,
            @RequestParam(value = "executionCycle", defaultValue = "") String executionCycle,
            @RequestParam(value = "developer", defaultValue = "") String developer,
            @RequestParam(value = "executionStatus", defaultValue = "") String executionStatus,
            @RequestParam(value = "executionPriority", defaultValue = "") String executionPriority,
            @RequestParam(value = "dependencyIsShow", defaultValue = "") String dependencyIsShow,
            @RequestParam(value = "taskNameOrId", defaultValue = "") String taskNameOrId,
            @RequestParam(value = "displayType", defaultValue = "") String displayType
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") query instances: startTime(" + startDate + "), endTime(" +
                endDate + "), developer(" + developer + "), cycle(" + executionCycle + "), prioLvl(" + executionPriority
                + "), status(" + executionStatus + "), taskNameOrId(" + taskNameOrId + "), showDependency(" +
                dependencyIsShow + "), displayType(" + displayType + ")");
        try {
            InstanceQueryDO instanceQueryDO = new InstanceQueryDO(startDate, endDate, executionCycle, developer,
                    executionStatus, executionPriority, dependencyIsShow, taskNameOrId);
            if (displayType.equals("topology"))
                return jobMonitorUrlHandler.handleQueryInstancesByTopology(instanceQueryDO);
            else
                return jobMonitorUrlHandler.handleQueryInstancesByList(instanceQueryDO);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") query instances error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 根据instanceId返回其自身以及所有后继节点
     */
    @RequestMapping(value = "/getSelfAndPostInstances", method = RequestMethod.GET)
    public
    @ResponseBody
    Result getSelfAndPostInstances(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "instanceId", defaultValue = "") String instanceId
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") show instance: instanceId(" + instanceId + ")");
        try {
            return jobMonitorUrlHandler.handleGetSelfAndPostInstances(instanceId);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") show instance error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 任务详细信息
     */
    @RequestMapping(value = "/getInstanceByInstanceId", method = RequestMethod.GET)
    public
    @ResponseBody
    Result getInstanceByInstanceId(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "instanceId", defaultValue = "") String instanceId
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") get instance: instanceId(" + instanceId + ")");
        try {
            return jobMonitorUrlHandler.handleGetInstanceByInstanceId(instanceId);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") get instance error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 置为重跑
     */
    @RequestMapping(value = "/recallInstance", method = RequestMethod.POST)
    public
    @ResponseBody
    Result recallInstance(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "instanceId", defaultValue = "") String instanceId
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") recall instance: instanceId(" + instanceId + ")");
        try {
            return jobMonitorUrlHandler.handleRecallInstance(instanceId);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") recall instance error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 批量置为重跑
     */
    @RequestMapping(value = "/recallInstances", method = RequestMethod.POST)
    public
    @ResponseBody
    Result recallInstances(
            @ModelAttribute("user") UserDO user,
            @NotEmpty(message = "reRun jobInstanceId map is empty...")
            @RequestBody List<String> jobInstanceIds
    ) {
        logger.info("User: (" + user.getEmployPinyin() + ") recall instances:" + jobInstanceIds);
        try {
            if (user == null)
                return getExceptionResult("请尝试重新登录");
            return jobMonitorUrlHandler.handleRecallInstances(jobInstanceIds);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") recall instances error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 置为挂起
     */
    @RequestMapping(value = "/suspendInstance", method = RequestMethod.POST)
    public
    @ResponseBody
    Result suspendInstance(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "instanceId", defaultValue = "") String instanceId
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") suspend instance: instanceId(" + instanceId + ")");
        try {
            return jobMonitorUrlHandler.handleSuspendInstance(instanceId);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") suspend instance error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 批量挂起任务
     */
    @RequestMapping(value = "/suspendInstances", method = RequestMethod.POST)
    public
    @ResponseBody
    Result suspendInstances(
            @ModelAttribute("user") UserDO user,
            @NotEmpty(message = "success jobInstanceId list is empty...")
            @RequestBody List<String> jobInstanceIds
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") suspend instances:" + jobInstanceIds);
        try {
            return jobMonitorUrlHandler.handleSuspendInstances(jobInstanceIds);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") suspend instances error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 置为成功
     */
    @RequestMapping(value = "/successInstance", method = RequestMethod.POST)
    public
    @ResponseBody
    Result successInstance(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "instanceId", defaultValue = "") String instanceId
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") success instance: instanceId(" + instanceId + ")");
        try {
            return jobMonitorUrlHandler.handleSuccessInstance(instanceId);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") success instance error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 批量置为成功
     */
    @RequestMapping(value = "/successInstances", method = RequestMethod.POST)
    public
    @ResponseBody
    Result successInstances(
            @ModelAttribute("user") UserDO user,
            @NotEmpty(message = "success jobInstanceId list is empty...")
            @RequestBody List<String> jobInstanceIds
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") success instances:" + jobInstanceIds);
        try {
            return jobMonitorUrlHandler.handleSuccessInstances(jobInstanceIds);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") success instances error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 直接依赖，任务的直接父节点和直接子节点
     */
    @RequestMapping(value = "/queryDirectRelation", method = RequestMethod.GET)
    public
    @ResponseBody
    Result queryDirectRelation(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "instanceId", defaultValue = "") String instanceId
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") query direct relation: instanceId(" + instanceId + ")");
        try {
            return jobMonitorUrlHandler.handleQueryDirectRelation(instanceId);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") query direct relation error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 所有依赖，任务的所有后继节点和任务的所有前驱节点
     */
    @RequestMapping(value = "/queryAllRelation", method = RequestMethod.GET)
    public
    @ResponseBody
    Result queryAllRelation(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "instanceId", defaultValue = "") String instanceId
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") query all relation: instanceId(" + instanceId + ")");
        try {
            return jobMonitorUrlHandler.handleQueryAllRelation(instanceId);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") query all relation error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 查看最长路径，依次查找前序节点中最晚结束的节点
     */
    @RequestMapping(value = "/getLongestPath", method = RequestMethod.GET)
    public
    @ResponseBody
    Result getLongestPath(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "instanceId", defaultValue = "") String instanceId
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") get longest path: instanceId(" + instanceId + ")");
        try {
            return jobMonitorUrlHandler.handleGetLongestPath(instanceId);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") get longest path error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 停止预跑
     */
    @RequestMapping(value = "/batchStopTask", method = RequestMethod.POST)
    public
    @ResponseBody
    Result batchStopTask(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "taskId", defaultValue = "") String taskId
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") batch stop task: taskId(" + taskId + ")");
        try {
            return jobMonitorUrlHandler.handleBatchStopTask(taskId);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") batch stop task error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 快速通道
     */
    @RequestMapping(value = "/raisePriorityInstance", method = RequestMethod.POST)
    public
    @ResponseBody
    Result raisePriority(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "instanceId", defaultValue = "") String instanceId
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") raise priority instance: instanceId(" + instanceId + ")");
        try {
            return jobMonitorUrlHandler.handleRaisePriorityInstance(instanceId);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") raise priority instance error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 级联重跑获得需要重跑的实例
     */
    @RequestMapping(value = "/recallCascadeGetInstances", method = RequestMethod.GET)
    public
    @ResponseBody
    Result recallInstanceCascadeGetInstances(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "instanceId", defaultValue = "") String instanceId,
            @RequestParam(value = "date", defaultValue = "") String date
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") recall cascade get instances: instanceId(" + instanceId +
                "), data(" + date + ")");
        try {
            return jobMonitorUrlHandler.handleRecallCascadeGetInstances(instanceId, date);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") recall cascade get instances error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 级联重跑
     */
    @RequestMapping(value = "/recallCascade", method = RequestMethod.POST)
    public
    @ResponseBody
    Result recallInstanceCascadeChangeChildrenStatus(
            @ModelAttribute("user") UserDO user,
            @Valid @RequestBody CascadeDO cascadeDO
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") recall cascaded job at date [" + cascadeDO.getStartDate() +
                " , " + cascadeDO.getEndDate() + "] " + cascadeDO.getInstanceIds());
        try {
            return jobMonitorUrlHandler.handleRecallCascade(cascadeDO);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") recall cascaded job error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 查看日志
     */
    @RequestMapping(value = "/viewLog", method = RequestMethod.GET)
    public
    @ResponseBody
    Result viewLog(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "logPath", defaultValue = "") String logPath
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") view log:(" + logPath + ")");
        try {

            return jobMonitorUrlHandler.handleGetInstanceLog(logPath);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") view log error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 查询预跑instances
     */
    @RequestMapping(value = "/queryPreRunInstances", method = RequestMethod.GET)
    public
    @ResponseBody
    Result queryPreRunInstances(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "startDate", defaultValue = "") String startDate,
            @RequestParam(value = "taskCommitter", defaultValue = "") String taskCommitter,
            @RequestParam(value = "taskNameOrId", defaultValue = "") String taskNameOrId
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") query prerun instances:startDate(" + startDate +
                "), committer(" + taskCommitter + "), taskNameOrId" + taskNameOrId);
        try {
            if (!CommonUtils.isTaskName(taskNameOrId)) {
                taskCommitter = null;
            }
            return jobMonitorUrlHandler.handleQueryPreRunInstances(startDate, taskCommitter, taskNameOrId);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") query prerun instances error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 任务实例状态分析
     */
    @RequestMapping(value = "/instanceStatusAnalyze", method = RequestMethod.GET)
    public
    @ResponseBody
    Result instanceStatusAnalyze(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "instanceId", defaultValue = "") String instanceId
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") instance state analyze: instanceId(" + instanceId + ")");
        try {
            return jobMonitorUrlHandler.handleInstanceStatusAnalyze(instanceId);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") instance state analyze error", e);
            return getExceptionResult("系统错误");
        }
    }
}
