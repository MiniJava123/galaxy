package com.dianping.data.warehouse.service;

import com.dianping.data.warehouse.domain.McTableInfo;
import com.dianping.data.warehouse.domain.McTableQuery;
import com.dianping.data.warehouse.domain.TableExistStatus;
import com.dianping.data.warehouse.domain.model.TableDO;
import com.dianping.data.warehouse.domain.web.McTableParaDO;
import com.dianping.data.warehouse.halley.domain.TaskDO;

import java.util.List;

/**
 * Created by shanshan.jin on 14-2-26.
 */
public interface TableService {
    public List<String> getDbList(String storageType);

    public List<TableDO> getSourceTableList(McTableQuery Query);

//    public boolean updateStatus(int taskId, int status, String updateTime, String updateUser);
//
//    public boolean updateStatus(List<TaskDO> taskDOList, TableExistStatus status);

    public McTableParaDO getTableInfoByTaskId(Integer taskId);

    String getTargetSchemaName(String sourceDBType, String sourceSchemaName, String sourceTableName);

    /**
     * 获取源介质类型
     */
    public List<String> getSourceDBTypes();

    public List<Integer> getTaskIdsByTaskId(int taskId);

    public boolean updateTableStatus(int taskId, int status);

    /**
     * 获取table的黑名单
     */
    public String getTableBlackList();

    /**
     * 根据taskId获得table
     */
    public McTableInfo getTableByTaskId(Integer taskId);
}
