package com.dianping.data.warehouse.web.UrlHandler;

import com.dianping.data.warehouse.core.model.UserDO;
import com.dianping.data.warehouse.halley.domain.MonitorDO;
import com.dianping.data.warehouse.halley.domain.TaskSQLParserDO;
import com.dianping.data.warehouse.service.CommonService;
import com.dianping.data.warehouse.web.Result;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Sunny on 14-6-10.
 */
@Service("commonUrlHandler")
public class CommonUrlHandler {

    @Resource(name = "commonService")
    private CommonService commonService;

    /**
     * 获得当前值班人员
     */
    public Result handleGetCurrentMonitor() {
        Result<MonitorDO> result = new Result<MonitorDO>();
        MonitorDO monitorDO = commonService.getCurrentMonitor();
        if (monitorDO != null) {
            result.setSuccess(true);
            result.addResults(monitorDO);
        } else {
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 获得当前值班信息，强制读取数据库
     */
    public Result handleUpdateMonitorByForce() {
        Result<Object> result = new Result<Object>();
        boolean res = commonService.updateMonitorByForce();
        if (res) {
            result.setSuccess(true);
            result.setMessages("刷新值班人员信息成功，请重新刷新页面");
        } else {
            result.setSuccess(true);
            result.setMessages("刷新值班人员信息失败");
        }
        return result;
    }

    /**
     * 发送意见反馈
     */
    public Result handleSendReply(String content, String userEmail) {
        Result result = new Result();
        if (StringUtils.isBlank(content)) {
            result.setSuccess(false);
            result.setMessages("内容为空");
            return result;
        } else if (StringUtils.isBlank(content)) {
            result.setSuccess(false);
            result.setMessages("用户email为空");
            return result;
        }
        boolean sendSuccess = commonService.sendReply(content, userEmail);
        result.setSuccess(sendSuccess);
        if (sendSuccess) {
            result.setMessages("发送成功，感谢您的反馈！");
        } else {
            result.setMessages("发送失败，请重新尝试发送或直接联系开发平台管理员！");
        }
        return result;
    }

    /**
     * 用户是否是管理员，resourceId表示的是galaxy管理员功能
     */
    public Result<Object> handleAuthorityIsAdmin(UserDO user, String resourceId) {
        Result<Object> result = new Result<Object>();
//        if (StringUtils.isBlank(resourceId)) {
//            result.setSuccess(false);
//            result.setMessages("Error: param(authorityKey), someone is null, please Check!");
//            return result;
//        }
        MonitorDO monitor = commonService.getCurrentMonitor();
        if (monitor != null && monitor.getPinyinName().equals(user.getEmployPinyin())) {
            result.setSuccess(true);
            return result;
        }
        if (commonService.isAdmin(user.getLoginId(), Integer.parseInt(resourceId))) {
            result.setSuccess(true);
        } else {
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 用户是否是taskid对应的任务的owner
     */
    public Result<Object> handleAuthorityIsOwner(String pinyinName, String taskId) {
        Result<Object> result = new Result<Object>();
        if (commonService.isOwner(pinyinName, Integer.parseInt(taskId))) {
            result.setSuccess(true);
        } else {
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 根据DOL地址解析结果
     */
    public Result<TaskSQLParserDO> handleImportGit(String projectName, String fileName) {
        Result<TaskSQLParserDO> result = new Result<TaskSQLParserDO>();
        TaskSQLParserDO taskSQLParserDO = commonService.gitSQLParse(projectName, fileName);
        if (taskSQLParserDO.isSuccess()) {
            result.setSuccess(true);
            result.addResults(taskSQLParserDO);
            result.setMessages("成功: 导入成功，请检查调度依赖列表是否与DOL内容相符，不符请手动添加依赖");
        } else {
            result.setSuccess(false);
            result.setMessages("失败: " + taskSQLParserDO.getMessage());
        }
        return result;
    }

    /**
     * 获取当前登录用户信息
     */
    public Result<UserDO> handleGetCurrentUser(UserDO user) {
        Result<UserDO> result = new Result<UserDO>();
        if (user != null) {
            result.setSuccess(true);
            result.setResult(user);
            result.setMessages("成功: 成功获得当前登录用户信息");
        } else {
            result.setSuccess(false);
            result.setMessages("失败: 获得当前登录用户信息失败");
        }
        return result;
    }

    /**
     * 获取登出url
     */
    public Result<String> handleGetLogoutUrl() {
        Result<String> result = new Result<String>();
        String logoutUrl = commonService.getLogoutUrl();
        result.setSuccess(true);
        result.setResult(logoutUrl);
        result.setMessages("成功: 成功获得登出url");
        return result;
    }
}
