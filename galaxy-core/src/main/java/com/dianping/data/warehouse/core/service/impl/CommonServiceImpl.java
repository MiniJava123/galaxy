package com.dianping.data.warehouse.core.service.impl;

import com.dianping.data.warehouse.core.common.Const;
import com.dianping.data.warehouse.core.dao.BaseDAO;
import com.dianping.data.warehouse.core.dao.impl.SqlMap;
import com.dianping.data.warehouse.core.utils.GalaxyDateUtils;
import com.dianping.data.warehouse.core.utils.LionUtils;
import com.dianping.data.warehouse.halley.domain.MonitorDO;
import com.dianping.data.warehouse.halley.domain.TaskDO;
import com.dianping.data.warehouse.halley.domain.TaskSQLParserDO;
import com.dianping.data.warehouse.halley.service.MonitorService;
import com.dianping.data.warehouse.halley.service.TaskPublish;
import com.dianping.data.warehouse.halley.service.TaskService;
import com.dianping.data.warehouse.service.CommonService;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sunny on 14-8-12.
 */

@Service("commonServiceImpl")
public class CommonServiceImpl implements CommonService {

    private Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);
    //所有的值班人员，缓存用
    List<MonitorDO> monitorDOs = null;
    //当前值班人员，缓存用
    private MonitorDO currentMonitor = null;
    //上次调用getCurrentMonitor的日志
    private Date lastCallDate = null;

    private MonitorService monitorService;

    private TaskService taskService;

    private TaskPublish taskPublish;

    @Resource(name = "baseDAOImpl")
    private BaseDAO dao;

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public void setTaskPublish(TaskPublish taskPublish) {
        this.taskPublish = taskPublish;
    }

    public void setMonitorService(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    /**
     * 获得当前值班人员
     */
    @Override
    public MonitorDO getCurrentMonitor() {
        //初始化
        if (monitorDOs == null) {
            monitorDOs = monitorService.getMonitors();
            if (monitorDOs == null)
                return null;
            currentMonitor = monitorDOs.get(0);
            lastCallDate = new Date();
            if (currentMonitor.isFinished()) {
                updateMonitor();
            }
        }
        //过了一天
        if (isNextDay()) {
            lastCallDate = new Date();
            //当前值班人员是否值班结束
            if (currentMonitor.isFinished()) {
                updateMonitor();
            }
        }
        return currentMonitor;
    }

    /**
     * 获得当前值班信息，强制读取数据库
     */
    @Override
    public boolean updateMonitorByForce() {
        //初始化
        monitorDOs = monitorService.getMonitors();
        if (monitorDOs == null)
            return false;
        currentMonitor = monitorDOs.get(0);
        lastCallDate = new Date();
        if (currentMonitor.isFinished()) {
            updateMonitor();
        }
        return true;
    }

    /**
     * 由于当前值班人员已经值班结束，更新为下一个值班人员
     */
    private void updateMonitor() {
        String lastBeginDate = currentMonitor.getBeginDate();
        currentMonitor = getNextMonitorUser(monitorDOs, currentMonitor);
        addMonitorRecord(lastBeginDate);
    }

    /**
     * 设置下一个值班人员
     */
    private Integer addMonitorRecord(String lastBeginDate) {
        Calendar calendar = Calendar.getInstance();
        try {
            Date beginDate = GalaxyDateUtils.strToDate(lastBeginDate, Const.DATE_FORMAT);
            calendar.setTime(beginDate);
            calendar.add(Calendar.DAY_OF_YEAR, Const.DAYS_OF_WEEK);
            Date dt = calendar.getTime();
            currentMonitor.setBeginDate(GalaxyDateUtils.dateToStr(dt, Const.DATE_FORMAT));
            return monitorService.addMonitorRecord(currentMonitor);
        } catch (Exception e) {
            logger.error("add monitor record fails", e);
            return 0;
        }
    }

    /**
     * 通过orderId排序的获取下一个值班用户
     */
    private MonitorDO getNextMonitorUser(List<MonitorDO> users, MonitorDO currentUser) {
        int currentOrderId = currentUser.getOrderId();
        int index = 0;
        //以orderId排序
        Collections.sort(users, new Comparator<MonitorDO>() {
            public int compare(MonitorDO user0, MonitorDO user1) {
                return user0.getOrderId() - user1.getOrderId();
            }
        });
        for (; index < users.size(); index++) {
            if (users.get(index).getOrderId() == currentOrderId)
                break;
        }
        index = (index + 1) % users.size();
        return users.get(index);
    }

    /**
     * 发送意见反馈至管理员和用户自己
     * userEmail为用户自己的邮箱
     */
    @Override
    public boolean sendReply(String content, String userEmail) {
        List<String> mailAddresses = new ArrayList<String>();
        String[] adminMails = Const.ADMIN_MAILS;
        for (int i = 0; i < adminMails.length; i++)
            mailAddresses.add(adminMails[i]);
        mailAddresses.add(userEmail);
        return sendEmail(content, mailAddresses);
    }

    /**
     * 向邮箱mailAddresses发送内容content
     */
    private boolean sendEmail(String content, List<String> mailAddresses) {
        Properties props = new Properties();
        props.put(Const.MAIL_SMTP_HOST_LABEL, Const.MAIL_SMTP_HOST);
        props.put(Const.MAIL_SMTP_AUTH_LABEL, Const.MAIL_SMTP_AUTH);
        Session session = Session.getInstance(props);
        MimeMessage msg = new MimeMessage(session);
        try {
            msg.setFrom(new InternetAddress(Const.MAIL_51PING));
            for (int i = 0; i < mailAddresses.size(); i++)
                msg.addRecipients(Message.RecipientType.TO, mailAddresses.get(i));
            msg.setSubject(Const.MAIL_SUBJECT);
            msg.setSentDate(new Date());
            msg.setContent(content, "text/plain;charset=utf8");
            Transport.send(msg);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 用户是否是管理员
     */
    @Override
    public boolean isAdmin(int loginId, int resourceId) {
        String aclAddress = LionUtils.getValue(Const.LION_ACL_RUL);
        String aclAuthorityKey = LionUtils.getValue(Const.LION_ACL_AK_ID + resourceId);
        if (aclAddress == null)
            aclAddress = Const.ACL_ADDRESS_PRODUCT;
        if (aclAuthorityKey == null)
            aclAuthorityKey = Const.ACL_ADMIN_KEY_PRODUCT;
        String url = getACLRequestAddress(aclAddress, loginId, aclAuthorityKey);
        String context = null;
        try {
            context = Jsoup.connect(url).ignoreContentType(true).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        if (context == null)
            return false;
        JSONObject jsonObj = JSONObject.fromObject(context);
        return jsonObj.getInt("msg") == 1;
    }

    /**
     * 用户是否是taskid对应的任务的owner
     */
    @Override
    public boolean isOwner(String pinyinName, int taskId) {
        if (StringUtils.isBlank(pinyinName) || taskService == null)
            return false;
        TaskDO task = taskService.getTaskByTaskId(taskId);
        if (task == null)
            return false;
        String taskOwner = task.getOwner();
        if (StringUtils.isBlank(taskOwner))
            return false;
        return StringUtils.equals(pinyinName, taskOwner);
    }

    /**
     * 检查表名是否符合数据库的命名规范
     */
    @Override
    public boolean checkDBRule(String databaseName, String tableName) {
        List<String> list = this.dao.getByQuery(databaseName, SqlMap.STATEMENT_GETRULE_BY_DB);
        if (list == null || list.size() == 0) {
            return true;
        } else {
            String rule = list.get(0);
            Pattern p = Pattern.compile(rule);
            Matcher m = p.matcher(tableName.trim().toLowerCase());
            return m.find();
        }
    }

    /**
     * 根据DOL文件进行SQL解析
     * 请在使用前保证参数不为null
     */
    @Override
    public TaskSQLParserDO gitSQLParse(String projectName, String fileName) {
        return taskPublish.publish(projectName, fileName);
    }

    /**
     * 获取登出url
     */
    @Override
    public String getLogoutUrl() {
        String ssoLogoutUrl = LionUtils.getValue(Const.LION_SSO_LOGOUT_URL);
        String homeUrl = LionUtils.getValue(Const.LION_GALAXY_URL);
        if (ssoLogoutUrl != null && homeUrl != null)
            return ssoLogoutUrl + "?service=" + homeUrl;
        return "";
    }

    /**
     * 距离上次计算monitor是否过了一天
     */
    private boolean isNextDay() {
        return !GalaxyDateUtils.dateToStr(new Date(), Const.DATE_FORMAT).
                equals(GalaxyDateUtils.dateToStr(lastCallDate, Const.DATE_FORMAT));
    }

    private String getACLRequestAddress(String aclAddress, int loginId, String aclAuthorityKey) {
        return aclAddress + "/hasPower?info={login_id:" + loginId + ",authcode:" + aclAuthorityKey + "}";
    }

}
