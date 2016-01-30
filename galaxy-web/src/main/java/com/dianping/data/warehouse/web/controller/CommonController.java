package com.dianping.data.warehouse.web.controller;

import com.dianping.data.warehouse.core.model.UserDO;
import com.dianping.data.warehouse.web.Result;
import com.dianping.data.warehouse.web.UrlHandler.CommonUrlHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Created by Sunny on 14-8-12.
 */
@Controller
@RequestMapping(value = "/common")
public class CommonController extends AbstractController {

    Logger logger = LoggerFactory.getLogger(CommonController.class);

    @Resource(name = "commonUrlHandler")
    private CommonUrlHandler commonUrlHandler;

    /**
     * 获得值班人员
     */
    @RequestMapping(value = "/getCurrentMonitor", method = RequestMethod.GET)
    public
    @ResponseBody
    Result getCurrentMonitor(
            @ModelAttribute("user") UserDO user
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") get current monitor");
        try {
            return commonUrlHandler.handleGetCurrentMonitor();
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") get current monitor error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 获得当前值班信息，强制读取数据库
     */
    @RequestMapping(value = "/updateMonitorByForce", method = RequestMethod.POST)
    public
    @ResponseBody
    Result updateMonitorByForce(
            @ModelAttribute("user") UserDO user
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") update monitor by force");
        try {
            return commonUrlHandler.handleUpdateMonitorByForce();
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") update monitor by force error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 发送意见反馈
     */
    @RequestMapping(value = "/sendReply", method = RequestMethod.POST)
    public
    @ResponseBody
    Result sendReply(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "reply", defaultValue = "") String reply,
            @RequestParam(value = "email", defaultValue = "") String email

    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") send reply");
        try {
            return commonUrlHandler.handleSendReply(reply.trim(), email.trim());
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ")send reply error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 用户是否是管理员,resourceId表示galaxy管理员
     */
    @RequestMapping(value = "/isAdmin", method = RequestMethod.GET)
    public
    @ResponseBody
    Result authorityIsAdmin(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "resourceId", defaultValue = "") String resourceId
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") is admin: resourceId(" + resourceId + ")");
        try {
            return commonUrlHandler.handleAuthorityIsAdmin(user, resourceId);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") is admin error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 用户是否是taskid对应的任务的owner
     */
    @RequestMapping(value = "/isTaskOwner", method = RequestMethod.GET)
    public
    @ResponseBody
    Result authorityIsOwner(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "taskId", defaultValue = "") String taskId
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") isTaskOwner: taskId(" + taskId + ")");
        try {
            return commonUrlHandler.handleAuthorityIsOwner(user.getEmployPinyin(), taskId);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") isTaskOwner error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 根据DOL进行SQL解析
     */
    @RequestMapping(value = "/importGit", method = RequestMethod.GET)
    public
    @ResponseBody
    Result importGit(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "projectName", defaultValue = "") String projectName,
            @RequestParam(value = "fileName", defaultValue = "") String fileName
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") import git: projectName(" + projectName + "),fileName(" +
                fileName + ")");
        try {
            return commonUrlHandler.handleImportGit(projectName, fileName);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") import git error", e);
            return getExceptionResult(e);
        }
    }

    /**
     * 获取当前登录用户信息
     */
    @RequestMapping(value = "/getCurrentUser", method = RequestMethod.GET)
    public
    @ResponseBody
    Result getCurrentUser(
            @ModelAttribute("user") UserDO user
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") get current user");
        try {
            return commonUrlHandler.handleGetCurrentUser(user);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") get current user error", e);
            return getExceptionResult(e);
        }
    }

    /**
     * 获取登出url
     */
    @RequestMapping(value = "/getLogoutUrl", method = RequestMethod.GET)
    public
    @ResponseBody
    Result getLogoutUrl(
            @ModelAttribute("user") UserDO user
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") get logout url");
        try {
            return commonUrlHandler.handleGetLogoutUrl();
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ")get logout url error", e);
            return getExceptionResult(e);
        }
    }

}
