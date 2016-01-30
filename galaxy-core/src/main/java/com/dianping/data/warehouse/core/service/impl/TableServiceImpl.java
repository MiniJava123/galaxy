package com.dianping.data.warehouse.core.service.impl;

import com.dianping.data.warehouse.core.common.Const;
import com.dianping.data.warehouse.core.common.GlobalResources;
import com.dianping.data.warehouse.domain.McTableInfo;
import com.dianping.data.warehouse.domain.McTableQuery;
import com.dianping.data.warehouse.domain.TableExistStatus;
import com.dianping.data.warehouse.domain.model.TableDO;
import com.dianping.data.warehouse.domain.model.WormholeDO;
import com.dianping.data.warehouse.domain.web.McTableParaDO;
import com.dianping.data.warehouse.halley.domain.TaskDO;
import com.dianping.data.warehouse.halley.service.TaskService;
import com.dianping.data.warehouse.masterdata.service.McMetaService;
import com.dianping.data.warehouse.masterdata.service.MercuryService;
import com.dianping.data.warehouse.service.LoadCfgService;
import com.dianping.data.warehouse.service.TableService;
import com.dianping.lion.EnvZooKeeperConfig;
import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.LionException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shanshan.jin on 14-2-28.
 */

@Service
public class TableServiceImpl implements TableService {
    private Logger logger = LoggerFactory.getLogger(TableServiceImpl.class);

    private MercuryService mercuryService;

    private McMetaService mcMetaService;

    private TaskService taskService;

    @Resource(name = "loadCfgServiceImpl")
    private LoadCfgService loadCfgService;

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public void setMercuryService(MercuryService mercuryService) {
        this.mercuryService = mercuryService;
    }

    public void setMcMetaService(McMetaService mcMetaService) {
        this.mcMetaService = mcMetaService;
    }

    @Override
    public List<String> getDbList(String storageType) {
        try {
            return mcMetaService.getDbList(storageType);
        } catch (Exception e) {
            logger.error("read mercury dblist service error:", e);
            return null;
        }
    }

    @Override
    public List<TableDO> getSourceTableList(McTableQuery query) {
        List<McTableInfo> tableList = null;
        try {
            tableList = mcMetaService.getTableList(query);
        } catch (Exception e) {
            logger.error("get mercury tableList service error:" + e);
        }
        List<TableDO> tableDOList = new ArrayList<TableDO>();
        if (null == tableList) {
            return null;
        } else {
            List<TableExistStatus> statusesList = mercuryService.gettableExistStatus(tableList);
            Map<String, TableExistStatus> map = new HashMap<String, TableExistStatus>();
            if (null != statusesList) {
                for (TableExistStatus status : statusesList) {
                    map.put(status.getCluster_name() + "." + status.getDb_name() + "." + status.getTable_name(), status);
                }
            }
//            try {
//                taskDOList = taskService.getAllTasks();
//                if (null == taskDOList) {
//                    logger.info("get tasks from halley is null.");
//                }
//            } catch (Exception e) {
//                logger.error("get halley all task info service error:"+e);
//            }

            for (McTableInfo table : tableList) {
                TableDO tableDO = new TableDO();
                tableDO.setTableInfo(table);
                TableExistStatus status = map.get(table.getStorage_type() + "." + table.getDb_name() + "." + table.getTable_name());
                if (null == status) {
                    status = new TableExistStatus();
                    Map<String, Boolean> existMap = new HashMap<String, Boolean>();
                    existMap.put("hive", false);
                    existMap.put("gp_report", false);
                    existMap.put("gp_analysis", false);
                    existMap.put("gp_olap", false);
                    existMap.put("mysql", false);
                    status.setExistMap(existMap);
                    Map<String, Integer> taskIdMap = new HashMap<String, Integer>();
                    taskIdMap.put("hive", 0);
                    taskIdMap.put("gp_report", 0);
                    taskIdMap.put("gp_analysis", 0);
                    taskIdMap.put("gp_olap", 0);
                    taskIdMap.put("mysql", 0);
                    status.setTaskIdMap(taskIdMap);
                    Map<String, Boolean> taskStorageLocationMap = new HashMap<String, Boolean>();
                    taskStorageLocationMap.put("hive", false);
                    taskStorageLocationMap.put("gp_report", false);
                    taskStorageLocationMap.put("gp_analysis", false);
                    taskStorageLocationMap.put("gp_olap", false);
                    taskStorageLocationMap.put("mysql", false);
                    status.setTaskStorageLocationMap(taskStorageLocationMap);
                }
//                else {
//                    updateStatus(taskDOList,status); // 更新每个task的wormhole,schedule信息的location
//                }
                tableDO.setStatus(status);
                tableDOList.add(tableDO);
            }
        }
        return tableDOList;
    }

    @Override
    public List<Integer> getTaskIdsByTaskId(int taskId) {
        logger.info("getTaskIdsByTaskId begin, taskId: " + taskId);
        List<Integer> taskIds = new ArrayList<Integer>();
        taskIds.add(taskId);
        List<Integer> tableIds = mercuryService.getTableIDbyTaskID(taskId);
        logger.info("getTaskIdsByTaskId begin, tableIds: " + tableIds);
        if (tableIds != null && tableIds.size() >= 1) {
            Integer tableId = tableIds.get(0);
            taskIds = mercuryService.getTaskIDbyTableID(tableId);
        }
        logger.info("getTaskIdsByTaskId begin, taskIds: " + taskIds);
        return taskIds;
    }

    @Override
    public boolean updateTableStatus(int taskId, int status) {
        return mercuryService.updateTableStatus(taskId, status);
    }

    /**
     * 任务taskId获取对应的table的信息
     */
    @Override
    public McTableParaDO getTableInfoByTaskId(Integer taskId) {
        try {
            McTableParaDO result = new McTableParaDO();
            result.setTableInfo(mercuryService.getTaskTable(taskId));
            WormholeDO readerCfg = loadCfgService.getReaderCfgByID(taskId);
            if (null != readerCfg) {
                if (StringUtils.isNotBlank(readerCfg.getConditionCol())) {
                    result.setConditionCol(readerCfg.getConditionCol());
                }
            }
            return result;
        } catch (Exception e) {
            logger.error("get taskinfo failure", e);
            throw new NullPointerException("get taskinfo failure");
        }
    }

    /**
     * 根据源数据库和源表的信息获取目标表的schema名
     */
    @Override
    public String getTargetSchemaName(String sourceDBType, String sourceSchemaName, String sourceTableName) {
        if (sourceDBType.equals(Const.DATASOURCE_TYPE_MYSQL) ||
                sourceDBType.equals(Const.DATASOURCE_TYPE_SQLSERVER) ||
                sourceDBType.equals(Const.DATASOURCE_TYPE_SALESFORCE)) {
            if (sourceTableName.startsWith(Const.DEFUALT_SCHEMA_DPDIM.toLowerCase())) {
                return "";
            } else {
                return Const.DEFUALT_SCHEMA_DPODS.toLowerCase();
            }
        } else {
            return sourceSchemaName;
        }
    }

    /**
     * 获取源数据库的类型
     */
    @Override
    public List<String> getSourceDBTypes() {
        return GlobalResources.SourceDBTypeMAPPING_MAP.get("sourceDBType");
    }

    /**
     * 获取table的黑名单
     */
    @Override
    public String getTableBlackList() {
        try {
            ConfigCache configCache = ConfigCache.getInstance(EnvZooKeeperConfig.getZKAddress());
            String blackListStr = configCache.getProperty("galaxy.update_task.blacklist");
            return blackListStr;
        } catch (LionException e) {
            logger.error("get black list from lion error", e);
            throw new RuntimeException("get black list from lion error", e);
        }
    }

    /**
     * 根据taskId获得table
     */
    @Override
    public McTableInfo getTableByTaskId(Integer taskId) {
        return mercuryService.getTaskTable(taskId);
    }
}




