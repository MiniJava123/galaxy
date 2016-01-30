package com.dianping.data.warehouse.web.controller;

import com.dianping.data.warehouse.core.model.UserDO;
import com.dianping.data.warehouse.core.utils.GalaxyDateUtils;
import com.dianping.data.warehouse.domain.model.StarShuttleDO;
import com.dianping.data.warehouse.domain.web.BuildTabParaDO;
import com.dianping.data.warehouse.domain.web.StarShuttleParasDO;
import com.dianping.data.warehouse.halley.domain.TaskRelaDO;
import com.dianping.data.warehouse.web.Result;
import com.dianping.data.warehouse.web.UrlHandler.TableUrlHandler;
import com.dianping.data.warehouse.web.model.BuildTableInfoDO;
import com.dianping.data.warehouse.web.model.TaskTransferAndScheduleSaveDO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Created by mt on 2014/5/6.
 */
@Controller
@RequestMapping(value = "/table")
public class TableController extends AbstractController {

    @Resource(name = "tableUrlHandler")
    private TableUrlHandler tableUrlHandler;

    Logger logger = LoggerFactory.getLogger(TableController.class);

    /**
     * 获取源介质类型
     */
    @RequestMapping(value = "/queryDBTypes", method = RequestMethod.GET)
    public
    @ResponseBody
    Result queryDBTypes(
            @ModelAttribute("user") UserDO user
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") query DB types");
        try {
            return tableUrlHandler.handleQueryDBTypes();
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") query DB types error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 根据介质类型获取数据库类型
     */
    @RequestMapping(value = "queryDBs", method = RequestMethod.GET)
    public
    @ResponseBody
    Result queryDBs(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "DBType", defaultValue = "") String DBType
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") query DBs: DBType(" + DBType + ")");
        try {
            return tableUrlHandler.handleQueryDBs(DBType);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") query DBs error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 根据条件查询表
     */
    @RequestMapping(value = "/queryTables", method = RequestMethod.GET)
    public
    @ResponseBody
    Result queryTables(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "sourceDBType", defaultValue = "") String sourceDBType,
            @RequestParam(value = "sourceDBName", defaultValue = "") String sourceDBName,
            @RequestParam(value = "sourceTableName", defaultValue = "") String sourceTableName
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") query tables: sourceDBType(" + sourceDBType +
                "), sourceDBName(" + sourceDBName + "), sourceTableName(" + sourceTableName + ")");
        try {
            return tableUrlHandler.handleQueryTables(sourceDBType, sourceDBName, sourceTableName);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") query tables error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 获取黑名单中的表
     */
    @RequestMapping(value = "/getTableBlackList", method = RequestMethod.GET)
    public
    @ResponseBody
    Result getTableBlackList(
            @ModelAttribute("user") UserDO user
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") get table blacklist");
        try {
            return tableUrlHandler.handleGetTableBlackList();
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") get table blacklist error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 根据源介质类型查询目标介质类型
     */
    @RequestMapping(value = "/queryTargetDBTypes", method = RequestMethod.GET)
    public
    @ResponseBody
    Result getTargetDBTypes(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "sourceDBType") String sourceDBType,
            @RequestParam(value = "sourceDBName") String sourceDBName,
            @RequestParam(value = "sourceTableName") String sourceTableName,
            @RequestParam(value = "type") String type
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") query target DBTypes: sourceDBType(" + sourceDBType + ")");
        try {
            return tableUrlHandler.handleQueryTargetDBTypes(sourceDBType, sourceDBName, sourceTableName, type);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") query target DBTypes error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 获取表的列信息
     */
    @RequestMapping(value = "/getTableColumns", method = RequestMethod.GET)
    public
    @ResponseBody
    Result getTableColumns(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "sourceDBType", defaultValue = "") String sourceDBType,
            @RequestParam(value = "sourceDBName", defaultValue = "") String sourceDBName,
            @RequestParam(value = "sourceTableName", defaultValue = "") String sourceTableName,
            @RequestParam(value = "sourceSchemaName", defaultValue = "") String sourceSchemaName
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") get table columns: sourceDBType(" + sourceDBType +
                "), sourceDBName(" + sourceDBName + "), sourceTableName(" + sourceTableName + "), sourceSchemaName(" +
                sourceSchemaName + ")");
        try {
            return tableUrlHandler.handleGetTableColumns(sourceDBType, sourceDBName, sourceTableName, sourceSchemaName);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") get table columns error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 根据条件获取任务信息，如果是新增任务，则获取默认的信息
     */
    @RequestMapping(value = "/getTaskInfo", method = RequestMethod.GET)
    public
    @ResponseBody
    Result getTaskInfo(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "method", defaultValue = "") String method,
            @RequestParam(value = "sourceDBType", defaultValue = "") String sourceDBType,
            @RequestParam(value = "sourceDBName", defaultValue = "") String sourceDBName,
            @RequestParam(value = "sourceTableName", defaultValue = "") String sourceTableName,
            @RequestParam(value = "sourceSchemaName", defaultValue = "") String sourceSchemaName,
            @RequestParam(value = "targetDBType", defaultValue = "") String targetDBType,
            @RequestParam(value = "targetTableName", defaultValue = "") String targetTableName,
            @RequestParam(value = "targetPartitionName", defaultValue = "") String targetPartitionName,
            @RequestParam(value = "targetTableType", defaultValue = "") String targetTableType,
            @RequestParam(value = "targetDBName", defaultValue = "") String targetDBName,
            @RequestParam(value = "dateType", defaultValue = "") String dateType,
            @RequestParam(value = "dateNumber", defaultValue = "") String dateNumber,
            @RequestParam(value = "dateOffset", defaultValue = "") String dateOffset,
            @RequestParam(value = "owner", defaultValue = "") String owner,
            @RequestParam(value = "taskId", defaultValue = "") String taskId,
            @RequestParam(value = "targetSchemaName", defaultValue = "") String targetSchemaName
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") get task info");
        try {
            //如果从前端传过来的owner为空，则从cookie里获取owner信息赋值
            if (StringUtils.isBlank(owner)) {
                owner = user.getEmployPinyin();
            }
            return tableUrlHandler.handleGetTaskTransferAndScheduleInfo(sourceDBType, sourceDBName, sourceTableName,
                    sourceSchemaName, targetDBType, targetSchemaName, targetTableName, targetPartitionName,
                    targetTableType, targetDBName, dateType, dateNumber, dateOffset, owner, method, taskId);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") get task info error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 根据taskId获取表基本信息,表名,数据库名,schema名,刷新类型等
     */
    @RequestMapping(value = "/getTableBasicInfo", method = RequestMethod.GET)
    public
    @ResponseBody
    Result getTableBasicInfo(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "taskId", defaultValue = "") String taskId
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") get table basic info:taskId(" + taskId + ")");
        try {
            return tableUrlHandler.handleGetTableInfoByTaskID(taskId);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") get table basic info error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 根据条件获得table的详细信息
     */
    @RequestMapping(value = "/getTableInfo", method = RequestMethod.GET)
    public
    @ResponseBody
    Result getTableInfo(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "DBType", defaultValue = "") String DBType,
            @RequestParam(value = "DBName", defaultValue = "") String DBName,
            @RequestParam(value = "tableName", defaultValue = "") String tableName
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") get table info:DBType(" + DBType + "), DBName(" + DBName +
                "), tableName(" + tableName + ")");
        try {
            return tableUrlHandler.handleGetTableInfo(DBType, DBName, tableName, user.getLoginId());
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") get table info error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 获取表的schema名
     */
    @RequestMapping(value = "/getSchemaName", method = RequestMethod.GET)
    public
    @ResponseBody
    Result getTargetSchemaName(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "sourceDBType", defaultValue = "") String sourceDBType,
            @RequestParam(value = "sourceSchemaName", defaultValue = "") String sourceSchemaName,
            @RequestParam(value = "sourceTableName", defaultValue = "") String sourceTableName,
            @RequestParam(value = "targetDBType", defaultValue = "") String targetDBType,
            @RequestParam(value = "type", defaultValue = "") String type
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") get schema name:sourceDBType(" + sourceDBType +
                "), sourceSchemaName(" + sourceSchemaName + "), sourceTableName(" + sourceTableName +
                "), targetDBType(" + targetDBType + ")");
        try {
            return tableUrlHandler.handleGetSchemaName(sourceDBType, sourceSchemaName, sourceTableName, targetDBType, type);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") get schema name error", e);
            return getExceptionResult("系统错误");
        }
    }

    /*
     * 修改任务的高级配置信息
     */
    @RequestMapping(value = "/updateTransferAdvance", method = RequestMethod.POST)
    public
    @ResponseBody
    Result updateTransferAdvance(
            @ModelAttribute("user") UserDO user,
            @RequestBody TaskTransferAndScheduleSaveDO saveData
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") update advance config:taskId(" +
                saveData.getStarShuttleDO().getTaskDO().getTaskId() + ")");
        try {
            StarShuttleDO starShuttleDO = saveData.getStarShuttleDO();
            String nowTimeStamp = GalaxyDateUtils.getCurrentTimeStampStr();
            starShuttleDO.getTaskDO().setUpdateTime(nowTimeStamp);
            starShuttleDO.getTaskDO().setUpdateUser(user.getEmployPinyin());
            for (TaskRelaDO taskRelaDO : starShuttleDO.getTaskDO().getRelaDOList()) {
                taskRelaDO.setTaskId(starShuttleDO.getTaskDO().getTaskId());
            }
            return tableUrlHandler.handleUpdateAdvanceConfig(saveData);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") update advance config error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 根据条件获取建表语句
     */
    @RequestMapping(value = "queryCreateTableSql", method = RequestMethod.POST)
    public
    @ResponseBody
    Result queryCreateTableSql(
            @ModelAttribute("user") UserDO user,
            @RequestParam(value = "method", defaultValue = "") String method,
            @RequestBody BuildTabParaDO buildTabParaDO
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") query create table sql: tableName(" +
                buildTabParaDO.getTable().getTableName() + ")");
        try {
            return tableUrlHandler.handleQueryCreateTableSql(buildTabParaDO);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") query create table sql error", e);
            return getExceptionResult("系统错误");
        }
    }

    /**
     * 根据条件进行建表操作
     */
    @RequestMapping(value = "buildTable", method = RequestMethod.POST)
    public
    @ResponseBody
    Result buildTable(
            @ModelAttribute("user") UserDO user,
            @RequestBody BuildTableInfoDO buildTableInfoDO
    ) {
        if (user == null)
            return getExceptionResult("请尝试重新登录");
        logger.info("User: (" + user.getEmployPinyin() + ") build table: tableName(" +
                buildTableInfoDO.getBuildTabParaDO().getTable().getTableName() + ")");
        try {
            return tableUrlHandler.handleCreateTable(buildTableInfoDO);
        } catch (Exception e) {
            logger.error("User: (" + user.getEmployPinyin() + ") build table error", e);
            return getExceptionResult("系统错误");
        }
    }

}
