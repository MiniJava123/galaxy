package com.dianping.data.warehouse.core.handler;

import com.dianping.data.warehouse.core.common.Const;
import com.dianping.data.warehouse.domain.McColumnInfo;
import com.dianping.data.warehouse.domain.McTableInfo;
import com.dianping.data.warehouse.domain.McTableQuery;
import com.dianping.data.warehouse.domain.web.StarShuttleParasDO;
import com.dianping.data.warehouse.halley.domain.TaskDO;
import com.dianping.data.warehouse.halley.domain.TaskRelaDO;
import com.dianping.data.warehouse.halley.service.TaskService;
import com.dianping.data.warehouse.masterdata.service.McMetaService;
import com.dianping.data.warehouse.masterdata.service.MercuryService;
import com.dianping.data.warehouse.service.LoadCfgService;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hongdi.tang on 14-2-18.
 */

public abstract class AbstractHandler implements Handler {

    private TaskService taskService;

    private McMetaService mcMetaService;

    public MercuryService getMercuryService() {
        return mercuryService;
    }

    public void setMercuryService(MercuryService mercuryService) {
        this.mercuryService = mercuryService;
    }

    private MercuryService mercuryService;

    @Resource(name = "loadCfgServiceImpl")
    private LoadCfgService loadService;

    public McMetaService getMcMetaService() {
        return mcMetaService;
    }

    public void setMcMetaService(McMetaService mcMetaService) {
        this.mcMetaService = mcMetaService;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public TaskService getTaskService() {
        return taskService;
    }

    @Override
    public boolean createTable(String dbType, String dbName, String ddl) {
        return false;
    }

    @Override
    public TaskDO getDefaultTask(StarShuttleParasDO parasDO) {
        TaskDO task = new TaskDO();
        String owner = parasDO.getOwner();
        task.setTaskName(parasDO.getSourceDBType().concat("2").concat(parasDO.getTargetDBType()).concat("##").concat(parasDO.getTargetDBName()).concat(".").concat(parasDO.getTargetTableName()));
        task.setTableName(parasDO.getTargetDBName().concat(".").concat(parasDO.getTargetTableName()));
        task.setDatabaseSrc(parasDO.getSourceDBType().concat("_").concat(parasDO.getSourceDBName()));
        task.setTaskObj(Const.TASK_OBJ);
//        task.setPara1(Const.PARA1);
//        task.setPara2(Const.PARA2);

        StringBuilder builder = new StringBuilder();
        if (StringUtils.equals(parasDO.getDateType(), Const.OFFSET_TYPE_MTD)) {
            builder.append(Const.PARA3).append(" ").append("D0");
        } else {
            String para3 = Const.PARA3.concat(parasDO.getDateType() == null ? " D0"
                    : " " + parasDO.getDateType().trim() + parasDO.getDateNumber());

            builder.append(para3);
            if (parasDO.getTargetTableType().equals(Const.TABLE_TYPE_ZIPPER)) {
                builder.append(" -col ").append(parasDO.getTargetPartitionName())
                        .append(" -src ").append(" load_" + parasDO.getSourceTableName().toLowerCase())
                        .append(" -target ").append(parasDO.getTargetTableName());
            }
        }
        task.setPara1(Const.PARA1 + " " + Const.PARA2 + " " + builder.toString());
//        task.setPara3("");
        task.setLogHome(Const.LOG_HOME);
        task.setLogFile(parasDO.getTargetTableName());
        task.setTaskGroupId(getTaskGroupByTableName(parasDO.getSourceTableName()));
        task.setCycle(Const.DEFAULT_CYCLE);
        task.setPrioLvl(Const.DEFAULT_PRIO_LVL);
        task.setIfRecall(Const.DEFAULT_IF_RECALL);
        task.setIfWait(Const.DEFAULT_IF_WAIT);
        task.setIfPre(Const.DEFAULT_IF_PRE);
        task.setIfVal(Const.DEFAULT_IF_VAL);
        task.setAddUser(owner);
        task.setUpdateUser(owner);
        if (parasDO.getTargetTableType().equals(Const.TABLE_TYPE_ZIPPER)) {
            task.setType(Const.WORMHOLE_ZIPPER_TYPE);
        } else {
            task.setType(Const.WORMHOLE_TYPE);
        }
        task.setOffset(Const.DEFAULT_OFFSET);
        //增量表时，任务的偏移量由用户选择的偏移类型和基准时间拼接而成
        if (parasDO.getTargetTableType().equals(Const.TABLE_TYPE_INCREMENT) && !parasDO.getDateType().equals("MTD")) {
            task.setOffset(parasDO.getDateType() + parasDO.getDateNumber());
        }
        task.setOffsetType(Const.OFFSET_TYPE);
        task.setFreq(Const.DEFAULT_CRON_EXPRESSION);
        task.setOwner(owner);
        task.setTimeout(Const.DEFAULT_TIMEOUT);
        task.setRecallInterval(Const.DEFAULT_RECALL_INTERVAL);
        task.setRecallLimit(Const.DEFAULT_RECALL_LIMIT);
        task.setSuccessCode(Const.DEFAULT_SUCCESS_CODE);
        McTableInfo sourceTable = new McTableInfo();
        sourceTable.setDb_name(parasDO.getSourceDBName());
        sourceTable.setSchema_name(parasDO.getSourceSchemaName());
        sourceTable.setStorage_type(parasDO.getSourceDBType());
        sourceTable.setTable_name(parasDO.getSourceTableName());
        List<Integer> parentTaskIdList = mercuryService.getParentTaskId(sourceTable);
        List<TaskRelaDO> taskRelaDOList = new ArrayList<TaskRelaDO>();
        if (parentTaskIdList != null) {
            for (Integer id : parentTaskIdList) {
                TaskDO pTask = taskService.getTaskByTaskId(id);
                TaskRelaDO taskRelaDO = new TaskRelaDO();
                taskRelaDO.setTaskPreId(id);
                taskRelaDO.setTaskName(pTask.getTaskName());
                taskRelaDO.setOwner(pTask.getOwner());
                taskRelaDO.setCycleGap(Const.DEFAULT_OFFSET);
                taskRelaDO.setRemark("");
                taskRelaDOList.add(taskRelaDO);
            }
            task.setIfPre(Const.IF_PRE_ON);
        }
        task.setRelaDOList(taskRelaDOList);
        return task;
    }


    public List<McColumnInfo> genSourceTableColumnInfo(StarShuttleParasDO paras) {
        McTableQuery query = new McTableQuery();
        query.setStorageType(paras.getSourceDBType());
        query.setSchemaName(paras.getSourceSchemaName());
        query.setTableName(paras.getSourceTableName());
        query.setDbName(paras.getSourceDBName());

        return mcMetaService.getColumnList(query);
    }

//    public String genColumnStr(List<McColumnInfo> columns ){
//        StringBuilder builder = new StringBuilder();
//        for(McColumnInfo column : columns){
//            builder.append(column.getColumn_name()).append(",");
//        }
//        return StringUtils.substring(builder.toString(),0,builder.toString().length()-1);
//    }

    public Map<String, McColumnInfo> getChangedColumnMap(List<McColumnInfo> oldColumnInfoList, List<McColumnInfo> newColumnInfoList) {

        Map<String, McColumnInfo> changeColumnMap = new HashMap<String, McColumnInfo>();
        for (McColumnInfo newColumn : newColumnInfoList) {
            boolean isAddColumn = true;
            McColumnInfo oldColumn = new McColumnInfo();
            for (McColumnInfo column : oldColumnInfoList) {
                oldColumn = column;
                if (newColumn.getColumn_rn() == oldColumn.getColumn_rn()) {
                    isAddColumn = false;
                    break;
                }
            }
            if (isAddColumn) {
                changeColumnMap.put("#add " + newColumn.getColumn_rn(), newColumn);
            } else {
                if (!newColumn.getColumn_name().toLowerCase().equals(oldColumn.getColumn_name().toLowerCase())
                        || !newColumn.getColumn_type().toLowerCase().equals(oldColumn.getColumn_type().toLowerCase())) {
                    changeColumnMap.put(oldColumn.getColumn_name(), newColumn);
                }
            }
        }
        return changeColumnMap;
    }

    private int getTaskGroupByTableName(String tableName) {
        String names[] = tableName.split("_");
        String group = names[0];
        int index = group.indexOf('.');
        if (index != -1)
            group = group.substring(index + 1);
        if (group.toLowerCase().equals("dpods")) {
            return Const.TASK_GROUP_DPODS;
        } else if (group.toLowerCase().equals("dpmid")) {
            return Const.TASK_GROUP_DPMID;
        } else if (group.toLowerCase().equals("dm") || group.toLowerCase().equals("dpdm")) {
            return Const.TASK_GROUP_DM;
        } else if (group.toLowerCase().equals("rpt") || group.toLowerCase().equals("dprpt")) {
            return Const.TASK_GROUP_RPT;
        } else if (group.toLowerCase().equals("mail")) {
            return Const.TASK_GROUP_MAIL;
        } else if (group.toLowerCase().equals("dw") || group.toLowerCase().equals("dpdw")) {
            return Const.TASK_GROUP_DW;
        } else if (group.toLowerCase().equals("dq")) {
            return Const.TASK_GROUP_DQ;
        } else if (group.toLowerCase().equals("atom")) {
            return Const.TASK_GROUP_ATOM;
        } else {
            return Const.TASK_GROUP_DPODS;
        }
    }
}
