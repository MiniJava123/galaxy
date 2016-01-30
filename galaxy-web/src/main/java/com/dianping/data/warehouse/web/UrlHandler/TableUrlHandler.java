package com.dianping.data.warehouse.web.UrlHandler;

import com.dianping.data.warehouse.domain.McColumnInfo;
import com.dianping.data.warehouse.domain.McTableInfo;
import com.dianping.data.warehouse.domain.McTableQuery;
import com.dianping.data.warehouse.domain.TableExistStatus;
import com.dianping.data.warehouse.domain.model.StarShuttleDO;
import com.dianping.data.warehouse.domain.model.TableDO;
import com.dianping.data.warehouse.domain.web.BuildTabParaDO;
import com.dianping.data.warehouse.domain.web.McTableParaDO;
import com.dianping.data.warehouse.domain.web.StarShuttleParasDO;
import com.dianping.data.warehouse.service.*;
import com.dianping.data.warehouse.web.Result;
import com.dianping.data.warehouse.web.common.Const;
import com.dianping.data.warehouse.web.model.BuildTableInfoDO;
import com.dianping.data.warehouse.web.model.TaskTransferAndScheduleSaveDO;
import javolution.io.Struct;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mt on 2014/5/6.
 */

@Service("tableUrlHandler")
public class TableUrlHandler {

    @Resource(name = "tableService")
    private TableService tableService;

    @Resource(name = "autoETLServiceImpl")
    private AutoETLService autoETLService;

    @Resource(name = "commonService")
    private CommonService commonService;

    @Resource(name = "autoBuildTabService")
    private AutoBuildTabService autoBuildTabService;

    /**
     * 查询源介质类型
     */
    public Result<List<String>> handleQueryDBTypes() {
        Result<List<String>> result = new Result<List<String>>();
        List<String> sourceDBTypes = tableService.getSourceDBTypes();
        if (sourceDBTypes == null) {
            result.setSuccess(false);
            result.setMessages("查询源数据存储介质失败");
        } else {
            result.setSuccess(true);
            result.setResult(sourceDBTypes);
        }
        return result;
    }

    /**
     * 根据源介质类型查询源数据库
     */
    public Result<List<String>> handleQueryDBs(String sourceDBType) {
        Result<List<String>> result = new Result<List<String>>();
        if (StringUtils.isBlank(sourceDBType)) {
            result.setSuccess(false);
            result.setMessages("查询条件 源数据介质为空");
            return result;
        }
        List<String> dbList = tableService.getDbList(sourceDBType);
        if (dbList == null) {
            result.setSuccess(false);
            result.setMessages("获取存储介质 " + sourceDBType + "下的数据库失败");
            return result;
        } else {
            result.setResult(dbList);
            result.setSuccess(true);
            return result;
        }
    }

    /**
     * 根据查询条件获取所有的表
     */
    public Result<TableDO> handleQueryTables(String sourceDBType, String sourceDBName, String sourceTableName) {
        McTableQuery mcTableQuery = new McTableQuery();
        Result<TableDO> result = new Result<TableDO>();
        if (StringUtils.isBlank(sourceDBType)) {
            result.setSuccess(false);
            result.setMessages("查询条件源数据存储介质为空");
            return result;
        }
        if (StringUtils.isNotBlank(sourceDBName)) {
            if (StringUtils.isNotBlank(sourceTableName)) {
                mcTableQuery.setStorageType(sourceDBType);
                mcTableQuery.setDbName(sourceDBName);
                mcTableQuery.setTableName(sourceTableName);
            } else {
                mcTableQuery.setStorageType(sourceDBType);
                mcTableQuery.setDbName(sourceDBName);
            }
        } else {
            if (StringUtils.isNotBlank(sourceTableName)) {
                mcTableQuery.setStorageType(sourceDBType);
                mcTableQuery.setTableName(sourceTableName);
            } else {
                result.setSuccess(false);
                result.setMessages("查询条件：数据库名和表名不能都为空");
                return result;
            }
        }
        List<TableDO> list = tableService.getSourceTableList(mcTableQuery);
        if (list == null) {
            result.setSuccess(false);
            result.setMessages("查询失败");
        } else {
            result.addAllResults(list);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 获取table的黑名单
     */
    public Result<Object> handleGetTableBlackList() {
        Result<Object> result = new Result<Object>();
        String blackListStr = tableService.getTableBlackList();
        String[] array = blackListStr.split(",");
        result.setSuccess(true);
        result.setResult(array);
        return result;
    }

    /**
     * 根据源介质类型获取目标介质类型
     */
    public Result<List<String>> handleQueryTargetDBTypes(String sourceDBType, String sourceDBName,
                                                         String sourceTableName, String type) {
        Result<List<String>> result = new Result<List<String>>();
        if (StringUtils.isBlank(sourceDBType)) {
            result.setSuccess(false);
            result.setMessages("查询条件源数据存储介质:" + sourceDBType + "为空");
            return result;
        }
        List<String> targetDBTypes = autoETLService.getTargetDBTypeBySourceDBType(sourceDBType);
        //新增时去除已经存在的dbtypes和不符合dbrule的dbtypes,修改时无限制
        if (type.equals("add")) {
            List<String> exsitDBTypes = getExistDBTypes(sourceDBType, sourceDBName, sourceTableName);
            targetDBTypes.removeAll(exsitDBTypes); //去除已经存在的
            targetDBTypes = removeTargetDBTypesByDBRule(targetDBTypes, sourceTableName);
        }
        if (targetDBTypes == null) {
            result.setSuccess(false);
            result.setMessages("获取目标存储介质失败");
        } else {
            result.setSuccess(true);
            result.setResult(targetDBTypes);
            result.setMessages("成功获取目标存储介质");
        }
        return result;
    }

    /**
     * 获取源表的列信息
     */
    public Result<List<McColumnInfo>> handleGetTableColumns(String sourceDBType, String sourceDBName,
                                                            String sourceTableName, String sourceSchemaName) {
        Result<List<McColumnInfo>> result = new Result<List<McColumnInfo>>();
        StarShuttleParasDO queryConditions = new StarShuttleParasDO();
        if (StringUtils.isBlank(sourceDBType) || StringUtils.isBlank(sourceDBName)
                || StringUtils.isBlank(sourceTableName)) {
            result.setSuccess(false);
            result.setMessages("查询条件:源介质，源数据库，源表名之一有空值");
            return result;
        }
        queryConditions.setSourceDBType(sourceDBType);
        queryConditions.setSourceDBName(sourceDBName);
        queryConditions.setSourceTableName(sourceTableName);
        queryConditions.setSourceSchemaName(sourceSchemaName);
        List<McColumnInfo> mcColumnInfoList = autoETLService.getSourceColumnInfo(queryConditions);
        if (mcColumnInfoList == null) {
            result.setSuccess(false);
            result.setMessages("查询目标表的columns失败");
        } else {
            result.setResult(mcColumnInfoList);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 根据条件获取任务信息，如果是新增任务，则获取默认的信息
     */
    public Result<StarShuttleDO> handleGetTaskTransferAndScheduleInfo(
            String sourceDBType, String sourceDBName, String sourceTableName,
            String sourceSchemaName, String targetDBType, String targetSchemaName,
            String targetTableName, String targetPartitionName, String targetTableType,
            String targetDBName, String dateType, String dateNumber, String dateOffset,
            String owner, String loadMethod, String taskID
    ) {
        StarShuttleParasDO queryConditions = new StarShuttleParasDO();
        Result<StarShuttleDO> result = new Result<StarShuttleDO>();
        if (StringUtils.isBlank(sourceDBType) || StringUtils.isBlank(sourceDBName)
                || StringUtils.isBlank(sourceTableName) || StringUtils.isBlank(targetDBType)
                || StringUtils.isBlank(targetDBName) || StringUtils.isBlank(targetTableType)
                || StringUtils.isBlank(targetTableName)) {
            result.setSuccess(false);
            result.setMessages("查询条件有空值");
            return result;
        }
        if (targetTableType.equals(Const.TABLE_TYPE_INCREMENT)) {
            if (StringUtils.isBlank(targetPartitionName) || StringUtils.isBlank(dateType)) {
                result.setSuccess(false);
                result.setMessages("查询条件:增量字段、偏移类型为空");
                return result;
            }
            if (!dateType.equalsIgnoreCase("MTD") && (StringUtils.isBlank(dateNumber)
                    || StringUtils.isBlank(dateOffset))) {
                result.setSuccess(false);
                result.setMessages("查询条件:基准时间、偏移量为空...");
                return result;
            }
        }
        queryConditions.setSourceDBType(sourceDBType);
        queryConditions.setSourceDBName(sourceDBName);
        queryConditions.setSourceTableName(sourceTableName);
        queryConditions.setSourceSchemaName(sourceSchemaName);
        queryConditions.setTargetDBType(targetDBType);
        queryConditions.setTargetDBName(targetDBName);
        queryConditions.setTargetTableName(targetTableName);
        queryConditions.setTargetTableType(targetTableType);
        queryConditions.setTargetSchemaName(targetSchemaName);
        queryConditions.setTargetPartitionName(targetPartitionName);
        queryConditions.setDateNumber(dateNumber == null ? null : Integer.valueOf(dateNumber));
        queryConditions.setDateOffset(dateOffset);
        queryConditions.setDateType(dateType);
        queryConditions.setOwner(owner);
        StarShuttleDO starShuttleDO;
        if (StringUtils.equalsIgnoreCase(loadMethod, "taskUpdate")) {
            queryConditions.setTaskId(new Integer(taskID));
            starShuttleDO = autoETLService.readAllInfo(queryConditions);
        } else if (StringUtils.equalsIgnoreCase(loadMethod, "taskAdd")) {
            starShuttleDO = autoETLService.getAllInfo(queryConditions);
        } else {
            result.setSuccess(false);
            result.setMessages("Error: loadMethod is not correct,please Check!");
            return result;
        }
        if (starShuttleDO.isFlag()) {
            result.setResult(starShuttleDO);
            result.setSuccess(starShuttleDO.isFlag());
            return result;
        } else {
            result.setSuccess(false);
            result.setMessages("内部错误");
            return result;
        }
    }

    /**
     * 根据taskId获取表基本信息,表名,数据库名,schema名,刷新类型等
     */
    public Result<McTableParaDO> handleGetTableInfoByTaskID(String taskID) {
        Result<McTableParaDO> result = new Result<McTableParaDO>();
        if (StringUtils.isBlank(taskID)) {
            result.setSuccess(false);
            result.setMessages("查询条件taskID为空");
            return result;
        }
        McTableParaDO mcTableInfo = tableService.getTableInfoByTaskId(Integer.valueOf(taskID));
        if (mcTableInfo != null) {
            result.setSuccess(true);
            result.setResult(mcTableInfo);
            result.setMessages("成功获取目标表的信息");
        } else {
            result.setSuccess(false);
            result.setMessages("获取目标表的信息失败");
        }
        return result;
    }

    /**
     * 根据条件获得table的详细信息
     */
    public Result handleGetTableInfo(String DBType, String DBName, String tableName, Integer loginId) {
        Result<BuildTabParaDO> result = new Result<BuildTabParaDO>();
        McTableQuery mcTableQuery = new McTableQuery();
        mcTableQuery.setStorageType(DBType);
        mcTableQuery.setDbName(DBName);
        mcTableQuery.setTableName(tableName);
        BuildTabParaDO buidTabParaDO = autoBuildTabService.getTableInfo(mcTableQuery, loginId);
        if (buidTabParaDO == null) {
            result.setMessages("获取表" + tableName + "的信息失败");
            result.setSuccess(false);
        } else {
            result.setMessages("成功获取该表的信息");
            result.setSuccess(true);
            result.setResult(buidTabParaDO);
        }
        return result;
    }

    /**
     * 更新高级配置信息
     */
    public Result<Object> handleUpdateAdvanceConfig(TaskTransferAndScheduleSaveDO saveData) {
        Result<Object> result = new Result<Object>();
        boolean isSaveSuccess;
        StarShuttleDO starShuttleDO = saveData.getStarShuttleDO();
        StarShuttleParasDO starShuttleParasDO = saveData.getStarShuttleParasDO();
        isSaveSuccess = autoETLService.updateAdvanceCfg(starShuttleDO, starShuttleParasDO);
        result.setSuccess(isSaveSuccess);
        result.setMessages(isSaveSuccess ? "成功修改任务高级配置信息" : "更新任务信息失败");
        return result;
    }

    /**
     * 获得表的schema名
     */
    public Result<String> handleGetSchemaName(String sourceDBType, String sourceSchemaName, String sourceTableName,
                                              String targetDBType, String type) {
        Result<String> result = new Result<String>();
        if (StringUtils.isBlank(sourceDBType)) {
            result.setSuccess(false);
            result.setMessages("查询条件为空");
            return result;
        }
        //新增任务才check规则，修改任务不check
        if (type.equals("add") && !commonService.checkDBRule(targetDBType, sourceTableName)) {
            result.setSuccess(false);
            if (targetDBType.equals(Const.DATASOURCE_TYPE_GPREPORT))
                result.setMessages("目标介质与目标表不匹配，目标表名必须以以下字段开头：dprpt、dpdim");
            else if (targetDBType.equals(Const.DATEBASE_TYPE_GPANALYSIS))
                result.setMessages("目标介质与目标表不匹配，目标表名必须以以下字段开头：dprpt、dpdim、dpdm、dpods、dpmid、agg开头");
            else
                result.setMessages("目标表介质与目标表不匹配，匹配规则请参考使用帮助");
            return result;
        }
        String targetSchemaName = tableService.getTargetSchemaName(sourceDBType, sourceSchemaName, sourceTableName);
        result.setSuccess(true);
        result.setResult(targetSchemaName);
        result.setMessages("成功获取目标schema");
        return result;
    }

    /**
     * 根据条件获得建表语句
     */
    public Result handleQueryCreateTableSql(BuildTabParaDO buidTabParaDO) {
        Result<String> result = new Result<String>();
        String sql = autoBuildTabService.getDdl(buidTabParaDO);
        if (sql == null) {
            result.setMessages("获取建表语句失败");
            result.setSuccess(false);
        } else {
            result.setMessages("成功获取建表语句");
            result.setSuccess(true);
            result.setResult(sql);
        }
        return result;
    }

    /**
     * 根据条件进行建表操作
     */
    public Result handleCreateTable(BuildTableInfoDO buildTableInfoDO) {
        Result<Boolean> result = new Result<Boolean>();
        Integer taskID = buildTableInfoDO.getTaskID();
        List<Integer> srcTaskIds = buildTableInfoDO.getSrcTaskIds();
        if (null != taskID) {
            buildTableInfoDO.getBuildTabParaDO().setTaskId(taskID);
        }
        List<McTableInfo> mcTableInfos = new ArrayList<McTableInfo>();
        if (srcTaskIds != null) {
            for (Integer srcTaskId : srcTaskIds) {
                McTableInfo mcTableInfo = tableService.getTableByTaskId(srcTaskId);
                if (mcTableInfo != null)
                    mcTableInfos.add(mcTableInfo);
            }
            buildTableInfoDO.getBuildTabParaDO().setSrcTableList(mcTableInfos);
        } else {
            buildTableInfoDO.getBuildTabParaDO().setSrcTableList(null);
        }
        Boolean isBuildSuccess = autoBuildTabService.buildTable(buildTableInfoDO.getBuildTabParaDO(), buildTableInfoDO.getSql());
        if (!isBuildSuccess) {
            result.setMessages("同步表结构失败");
            result.setSuccess(false);
        } else {
            result.setMessages("同步表结构成功");
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 获得已经存在的目标表类型
     */
    private List<String> getExistDBTypes(String sourceDBType, String sourceDBName, String sourceTableName) {
        List<String> existDBTypes = new ArrayList<String>();
        McTableQuery mcTableQuery = new McTableQuery();
        mcTableQuery.setStorageType(sourceDBType);
        mcTableQuery.setDbName(sourceDBName);
        mcTableQuery.setTableName(sourceTableName);
        TableDO realTable = null;
        List<TableDO> tableDOs = tableService.getSourceTableList(mcTableQuery);
        if (tableDOs == null || tableDOs.isEmpty())
            return existDBTypes;
        for (TableDO tableDO : tableDOs) {
            if (tableDO.getTableInfo().getTable_name().toLowerCase().equals(sourceTableName.toLowerCase())) {
                realTable = tableDO;
            }
        }
        if(realTable == null)
            return existDBTypes;
        Map<String, Integer> status = realTable.getStatus().getTaskIdMap();
        for (Map.Entry<String, Integer> entry : status.entrySet()) {
            if (entry.getValue() != 0) {
                existDBTypes.add(entry.getKey());
            }
        }
        return existDBTypes;
    }

    /**
     * targetDBTypes去除不符合dbrule的
     */
    private List<String> removeTargetDBTypesByDBRule(List<String> targetDBTypes, String tableName) {
        List<String> targetDBTypesAccordRule = new ArrayList<String>();
        for (String targetDBType : targetDBTypes) {
            if (commonService.checkDBRule(targetDBType, tableName)) {
                targetDBTypesAccordRule.add(targetDBType);
            }
        }
        return targetDBTypesAccordRule;
    }
}
