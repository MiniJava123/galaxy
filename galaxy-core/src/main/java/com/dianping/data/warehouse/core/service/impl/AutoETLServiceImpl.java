package com.dianping.data.warehouse.core.service.impl;

import com.dianping.data.warehouse.core.common.Const;
import com.dianping.data.warehouse.core.common.GlobalResources;
import com.dianping.data.warehouse.core.common.WriterParameters;
import com.dianping.data.warehouse.core.handler.Handler;
import com.dianping.data.warehouse.core.handler.MediaHandler;
import com.dianping.data.warehouse.core.utils.DomainUtils;
import com.dianping.data.warehouse.core.utils.JacksonHelper;
import com.dianping.data.warehouse.core.utils.TaskValidator;
import com.dianping.data.warehouse.domain.*;
import com.dianping.data.warehouse.domain.model.StarShuttleDO;
import com.dianping.data.warehouse.domain.model.WormholeDO;
import com.dianping.data.warehouse.domain.web.StarShuttleParasDO;
import com.dianping.data.warehouse.halley.domain.TaskDO;
import com.dianping.data.warehouse.halley.domain.TaskRelaDO;
import com.dianping.data.warehouse.halley.service.TaskService;
import com.dianping.data.warehouse.masterdata.service.AclInfoService;
import com.dianping.data.warehouse.masterdata.service.McMetaService;
import com.dianping.data.warehouse.masterdata.service.MercuryService;
import com.dianping.data.warehouse.service.AuthorityService;
import com.dianping.data.warehouse.service.AutoETLService;
import com.dianping.data.warehouse.service.LoadCfgService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by shanshan.jin on 14-2-17.
 */
@Service("autoETLServiceImpl")
public class AutoETLServiceImpl implements AutoETLService {
    private Logger logger = LoggerFactory.getLogger(AutoETLService.class);
    @Resource(name = "handlerMap")
    private Map<String, Handler> handlerMap;

    @Resource(name = "mediaHandlerImpl")
    private MediaHandler mediaHandler;

    @Resource(name = "loadCfgServiceImpl")
    private LoadCfgService loadService;

    private TaskService taskService;

    private MercuryService mercuryService;

    private McMetaService mcMetaService;

    @Resource
    private AclInfoService aclInfoService;

    @Resource(name = "authorityServiceImpl")
    private AuthorityService authorityService;

    public TaskService getTaskService() {
        return taskService;
    }

    public McMetaService getMcMetaService() {
        return mcMetaService;
    }

    public void setMcMetaService(McMetaService mcMetaService) {
        this.mcMetaService = mcMetaService;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public MercuryService getMercuryService() {
        return mercuryService;
    }

    public void setMercuryService(MercuryService mercuryService) {
        this.mercuryService = mercuryService;
    }

    public void setAclInfoService(AclInfoService aclInfoService) {
        this.aclInfoService = aclInfoService;
    }


    private Handler getHandler(String type) {
        String targetDBType = null;
        if (type.equalsIgnoreCase(Const.DATASOURCE_TYPE_GPANALYSIS) ||
                type.equalsIgnoreCase(Const.DATASOURCE_TYPE_GPREPORT) ||
                type.equalsIgnoreCase(Const.DATASOURCE_TYPE_GPOLAP)
                ) {
            targetDBType = Const.DATASOURCE_TYPE_GP;
        } else {
            targetDBType = type;
        }
        return this.handlerMap.get(targetDBType);
    }

    @Override
    public StarShuttleDO getAllInfo(StarShuttleParasDO parasDO) {
        parasDO = DomainUtils.processData(parasDO);
        StarShuttleDO dos = new StarShuttleDO();
        try {
            Handler sourceHandler = this.getHandler(parasDO.getSourceDBType());
            Handler targetHandler = this.getHandler(parasDO.getTargetDBType());

            List<McColumnInfo> sourceColumns = sourceHandler.genSourceTableColumnInfo(parasDO);
            List<McColumnInfo> targetColumns = targetHandler.genTargetTableColumnInfo(parasDO);

            //wormhole reader parmeter
            Map<String, Object> readerMap = sourceHandler.genReaderParas(parasDO, sourceColumns);

            WormholeDO readerLoad = new WormholeDO();
            readerLoad.setConnectProps(parasDO.getSourceDBType().concat("_").concat(parasDO.getSourceDBName()));
            readerLoad.setType(Const.WORMHOLE_READER_TYPE);
            readerLoad.setParameterMap(readerMap);

            //wormhole writer parameter
            Map<String, Object> writerMap = targetHandler.genWriterParas(parasDO, targetColumns);
            WormholeDO writerLoad = new WormholeDO();
            writerLoad.setConnectProps(parasDO.getTargetDBType().concat("_").concat(parasDO.getTargetDBName()));
            writerLoad.setType(Const.WORMHOLE_WRITER_TYPE);
            writerLoad.setParameterMap(writerMap);

            List<WormholeDO> list = new ArrayList<WormholeDO>();
            list.add(readerLoad);
            list.add(writerLoad);

            //默认任务信息
            TaskDO task = targetHandler.getDefaultTask(parasDO);
            //ddl语句
            String ddl = targetHandler.generateCreateDDL(targetColumns, parasDO);
            dos.setWormholeDOs(list);
            dos.setColumns(targetColumns);
            dos.setTaskDO(task);
            dos.setDdl(ddl);
            dos.setFlag(true);

            return dos;
        } catch (Exception e) {
            logger.error("get starshuttle info failure", e);
            dos.setFlag(false);
            dos.setErrorInfo(e.getMessage());
            return dos;
        }
    }

    @Override
    public StarShuttleDO readAllInfo(StarShuttleParasDO parasDO) {
        StarShuttleDO dos = new StarShuttleDO();
        try {
            Integer taskId = parasDO.getTaskId();
            TaskDO taskDO = taskService.getTaskByTaskId(taskId);
            Handler sourceHandler = this.getHandler(parasDO.getSourceDBType());
            List<McColumnInfo> sourceColumns = sourceHandler.genSourceTableColumnInfo(parasDO);
            Handler targetHandler = this.getHandler(parasDO.getTargetDBType());
            McTableInfo targetTable = new McTableInfo();
            targetTable.setTable_name(parasDO.getTargetTableName().toLowerCase());
            targetTable.setStorage_type(parasDO.getTargetDBType());
            targetTable.setDb_name(parasDO.getTargetDBName());
            targetTable.setSchema_name(parasDO.getTargetSchemaName());
            McTableQuery query = new McTableQuery();
            if (targetTable.getStorage_type().startsWith("gp")) {
                query.setStorageType("greenplum");
                query.setDbName(targetTable.getStorage_type());
            } else {
                query.setStorageType(targetTable.getStorage_type());
                query.setDbName(targetTable.getDb_name());
            }

            query.setTableName(targetTable.getTable_name());
            List<McColumnInfo> oldTargetColumns = mcMetaService.getColumnList(query);
            List<McColumnInfo> targetColumns = targetHandler.genTargetTableColumnInfo(parasDO);

            List<WormholeDO> list = loadService.getLoadCfgListByID(taskId);
            for (WormholeDO wormholeDO : list) {
                String parameterStr = wormholeDO.getParameterMapStr();
                Map<String, Object> parameterMap = JacksonHelper.jsonToPojo(parameterStr, HashMap.class);
                if (wormholeDO.getType().equals(Const.WORMHOLE_WRITER_TYPE)) {
                    parameterMap = targetHandler.updateMapParas(parasDO, sourceColumns, parameterMap, Const.WORMHOLE_WRITER_TYPE);
                } else if (wormholeDO.getType().equals(Const.WORMHOLE_READER_TYPE)) {
                    parameterMap = sourceHandler.updateMapParas(parasDO, sourceColumns, parameterMap, Const.WORMHOLE_READER_TYPE);
                }
                wormholeDO.setParameterMap(parameterMap);
                wormholeDO.setParameterMapStr(null);
            }

            dos.setWormholeDOs(list);
            dos.setColumns(targetColumns);
            dos.setTaskDO(taskDO);
            List<McColumnInfo> targetColumnsClone = new ArrayList<McColumnInfo>();
            targetColumnsClone.addAll(targetColumns);
            String ddl = targetHandler.generateAlterDDL(oldTargetColumns, targetColumnsClone, parasDO);
            dos.setDdl(ddl);
            dos.setFlag(true);


            return dos;
        } catch (Exception e) {
            logger.error("get starshuttle info failure", e);
            dos.setFlag(false);
            dos.setErrorInfo(e.getMessage());
            return dos;
        }
    }

    private String getWriterConnectProps(String targetDB, StarShuttleParasDO parasDO) {
        if (parasDO.getTargetDBType().equals("mysql")) {
            return "mysql_".concat(targetDB);
        } else {
            return targetDB + "_" + Const.DEFAULT_BI_DATABASE;
        }
    }

    private List<McColumnInfo> getParcolumnList(StarShuttleDO dos, StarShuttleParasDO paras) {
        List<McColumnInfo> parcolumnList = new ArrayList<McColumnInfo>();
        if (paras.getTargetDBType().equals("hive")) {
            if (paras.getTargetTableType().equals(Const.TABLE_TYPE_ZIPPER)) {
                McColumnInfo colmun = new McColumnInfo();
                colmun.setColumn_name(Const.DEFUALT_ZIPPER_PARTITION_NAME);
                colmun.setColumn_type("string");
                colmun.setColumn_rn(dos.getColumns().size() + 1);
                colmun.setIs_partition_column(1);
                parcolumnList.add(colmun);

            } else if (paras.getTargetTableType().equals(Const.TABLE_TYPE_INCREMENT) ||
                    paras.getTargetTableType().equals(Const.TABLE_TYPE_SNAPSHOT)) {
                McColumnInfo colmun = new McColumnInfo();
                if (paras.getTargetTableName().startsWith(Const.DEFUALT_SCHEMA_DPDIM)) {
                    colmun.setColumn_name(Const.DEFUALT_DPDIM_PARTITION_NAME);
                } else {
                    colmun.setColumn_name(Const.DEFUALT_HIVE_PARTITION_NAME);
                }
                colmun.setColumn_type("string");
                colmun.setColumn_rn(dos.getColumns().size() + 1);
                colmun.setIs_partition_column(1);
                parcolumnList.add(colmun);

            }
        }

        return parcolumnList;
    }

    @Override
    public synchronized boolean generateAutoETLTask(StarShuttleParasDO parasDO, StarShuttleDO dos, String updateTime,
                                                    String updateUser) {
        boolean flag;
        parasDO = DomainUtils.processData(parasDO);
        McTableInfo sourceTable = new McTableInfo();
        McTableInfo targetTable = new McTableInfo();
        parasDO.setTargetTableName(parasDO.getTargetTableName().toLowerCase());
        List<McTableInfo> sourceTables = new ArrayList<McTableInfo>();

        List<String> targetDBs = GlobalResources.DBMAPPING_MAP.get(parasDO.getTargetDBType().toLowerCase());
        List<Boolean> flags = new ArrayList<Boolean>();
        List<Integer> taskIds = new ArrayList<Integer>();

        Handler targetHandler = null;

        for (String targetDB : targetDBs) {
//            parasDO.setTargetDBName(targetDB);
            //生成
            targetHandler = this.getHandler(parasDO.getTargetDBType());
            Integer taskID = taskService.generateTaskID(dos.getTaskDO());

            //调用调度接口
            TaskDO task = dos.getTaskDO();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currTime = formatter.format(new Date());
            task.setTaskName(parasDO.getSourceDBType().concat("2").concat(parasDO.getTargetDBType()).concat("##").concat(targetDB).concat(".").concat(parasDO.getTargetTableName()));
            task.setAddTime(currTime);
            task.setUpdateTime(currTime);
            task.setIfVal(Const.TASK_VALIDATE);
            task.setTaskId(taskID);
            for (TaskRelaDO taskRelaDO : task.getRelaDOList()) {
                taskRelaDO.setTaskId(taskID);
            }
            targetTable.setTable_name(parasDO.getTargetTableName().toLowerCase());
            targetTable.setStorage_type(parasDO.getTargetDBType());
            targetTable.setDb_name(parasDO.getTargetDBName());
            targetTable.setTable_owner(dos.getTaskDO().getOwner());
            if (StringUtils.isBlank(parasDO.getTargetSchemaName()) && parasDO.getSourceTableName().startsWith(Const.DEFUALT_SCHEMA_DPDIM.toLowerCase())) {
                targetTable.setSchema_name(Const.DEFUALT_SCHEMA_DPDIM.toLowerCase());
            } else {
                targetTable.setSchema_name(parasDO.getTargetSchemaName());
            }
            targetTable.setRefresh_cycle(parasDO.getDateType());
            targetTable.setRefresh_type(parasDO.getTargetTableType());
            targetTable.setStatus(Const.TABLE_VALIDATE);
            List<McColumnInfo> parColumnList = getParcolumnList(dos, parasDO);
            List<McColumnInfo> columnList = dos.getColumns();
            columnList.addAll(parColumnList);
            targetTable.setColumns(columnList);

            String[] validStr = TaskValidator.validateTask(task);
            if (validStr[0].equals("0")) {
                throw new RuntimeException(validStr[1]);
            }

            List<Integer> idList = mercuryService.getParentTaskId(targetTable);
            if (idList != null && idList.size() > 0) {
                throw new RuntimeException("该表在数据库中已存在");
            }

            //建表
            List<McColumnInfo> targetColumns = targetHandler.genTargetTableColumnInfo(parasDO);
            String ddl = targetHandler.generateCreateDDL(targetColumns, parasDO);

            //调用主数据接口
            sourceTable.setDb_name(parasDO.getSourceDBName());
            sourceTable.setSchema_name(parasDO.getSourceSchemaName());
            sourceTable.setStorage_type(parasDO.getSourceDBType());
            sourceTable.setTable_name(parasDO.getSourceTableName());
            sourceTables.add(sourceTable);
            for (int i = 0; i < dos.getTaskDO().getRelaDOList().size(); i++) {
                int taskId = dos.getTaskDO().getRelaDOList().get(i).getTaskPreId();
                McTableInfo mcTableInfo = mercuryService.getTaskTable(taskId);
                if (mcTableInfo != null)
                    sourceTables.add(mcTableInfo);
            }

            //sourceTable.
            targetTable.setTable_desc(null);
            targetTable.setRefresh_datum_date(parasDO.getDateNumber());
            targetTable.setRefresh_offset(parasDO.getDateOffset());
            targetTable.setAdd_time(currTime);
            targetTable.setUpdate_time(currTime);

            List<WormholeDO> wormholeDOs = dos.getWormholeDOs();
            List<WormholeDO> loadCfgs = new ArrayList<WormholeDO>();
            for (WormholeDO loadCfg : wormholeDOs) {
                loadCfg.setTaskId(taskID);
                logger.info(parasDO.getTargetDBType() + "_" + targetDB);

                loadCfg.setParameterMapStr(JacksonHelper.pojoToJson(loadCfg.getParameterMap()));
                if (loadCfg.getType().equals(Const.WORMHOLE_READER_TYPE)) {
                    loadCfg.setConnectProps(parasDO.getSourceDBType() + "_" + parasDO.getSourceDBName());
                    loadCfg.setConditionCol(parasDO.getTargetPartitionName());
                    if (loadCfg.getParameterMap().containsKey(Const.CONNECT_PROPS)) {
                        loadCfg.getParameterMap().put(Const.CONNECT_PROPS, parasDO.getSourceDBType() + "_" + parasDO.getSourceDBName());
                    }
                } else {
                    if (loadCfg.getParameterMap().containsKey(Const.CONNECT_PROPS)) {
                        //loadCfg.getParameterMap().put(Const.CONNECT_PROPS, targetDB + "_" + Const.DEFAULT_BI_DATABASE);
                        loadCfg.getParameterMap().put(Const.CONNECT_PROPS, this.getWriterConnectProps(targetDB, parasDO));
                    }
                    loadCfg.setConnectProps(parasDO.getTargetDBType() + "_" + targetDB);
                }

                loadCfg.setParameterMapStr(JacksonHelper.pojoToJson(loadCfg.getParameterMap()));
                loadCfgs.add(loadCfg);
            }
            try {
                logger.info("start create table");
                flag = mediaHandler.executeDDL(parasDO.getTargetDBType(), targetDB, parasDO.getSourceSchemaName(), ddl) &&
                        taskService.insertTask(task) &&
                        loadService.insertLoadCfg(loadCfgs);

            } catch (Exception e) {
                logger.error("generate autoetl task error", e);
                flag = false;
            }

            flags.add(flag);
            taskIds.add(taskID);
            if (!flag) {
                logger.error("Generate AutoETL TaskId: " + taskID + " => " + targetDB + " failure...");
            }
        }

        boolean flag2;
        try {
            flag2 = mercuryService.saveMercuryInfo(sourceTables, targetTable, taskIds, Const.WORMHOLE_TYPE);
        } catch (Exception e) {
            flag2 = false;
            logger.error("mercury save error", e);
        }


        for (boolean flag1 : flags) {
            if (!flag2 || !flag1) {
                String dropSql = targetHandler.generateDropDDL(parasDO);
                for (Integer id : taskIds) {
                    try {
                        taskService.deleteTaskByTaskId(id, updateTime, updateUser);
                    } catch (Exception e2) {
                        logger.error(e2.getMessage(), e2);
                    }
                    try {
                        loadService.deleteLoadCfg(id);
                    } catch (Exception e3) {
                        logger.error(e3.getMessage(), e3);
                    }
                }
                mercuryService.deleteMercuryInfo(sourceTables, targetTable, taskIds);
                for (String targetDB : targetDBs) {
                    try {
                        mediaHandler.executeDDL(parasDO.getTargetDBType(), targetDB, parasDO.getTargetSchemaName(), dropSql);
                    } catch (Exception e4) {
                        logger.error("drop table error", e4);
                    }
                }
                return false;
            }
        }
        return true;
    }


    @Override
    public boolean updateAutoETLTask(StarShuttleDO dos, StarShuttleParasDO parasDO) {
        try {
            List<WormholeDO> list = new ArrayList<WormholeDO>();
            for (WormholeDO load : dos.getWormholeDOs()) {
                load.setParameterMapStr(JacksonHelper.pojoToJson(load.getParameterMap()));
                list.add(load);
            }
            McTableInfo targetTable = new McTableInfo();

            targetTable.setTable_name(parasDO.getTargetTableName().toLowerCase());
            targetTable.setStorage_type(parasDO.getTargetDBType());
            targetTable.setDb_name(parasDO.getTargetDBName());
            targetTable.setSchema_name(parasDO.getTargetSchemaName());
            targetTable.setStatus(Const.TABLE_VALIDATE);
            List<McColumnInfo> parColumnList = getParcolumnList(dos, parasDO);
            List<McColumnInfo> columnList = dos.getColumns();
            columnList.addAll(parColumnList);
            targetTable.setColumns(columnList);
            targetTable.setTable_access_level(2);
            targetTable.setTable_access_desc(null);
            targetTable.setTable_owner(parasDO.getOwner());
            targetTable.setRefresh_datum_date(parasDO.getDateNumber());
            targetTable.setRefresh_offset(parasDO.getDateOffset());
            targetTable.setRefresh_cycle(parasDO.getDateType());
            targetTable.setRefresh_type(parasDO.getTargetTableType());
            targetTable.setSchema_name(parasDO.getTargetSchemaName());
            Integer tableId = mercuryService.getTableIDbyTaskID(dos.getTaskDO().getTaskId()).get(0);
            List<Integer> taskIdList = mercuryService.getTaskIDbyTableID(tableId);

            if (taskIdList.size() > 1 && parasDO.getTargetDBType().equals(Const.DATASOURCE_TYPE_GPREPORT)) {
                TaskDO taskDO = dos.getTaskDO();
                for (Integer taskId : taskIdList) {
                    TaskDO oldTaskDO = taskService.getTaskByTaskId(taskId);
                    taskDO.setTaskId(taskId);
                    List<TaskRelaDO> taskRelaList = taskDO.getRelaDOList();
                    List<TaskRelaDO> newTaskRelaList = new ArrayList<TaskRelaDO>();
                    if (taskRelaList.size() > 0) {
                        for (TaskRelaDO taskRelaDO : taskRelaList) {
                            taskRelaDO.setTaskId(taskId);
                            newTaskRelaList.add(taskRelaDO);
                        }
                    }
                    taskDO.setRelaDOList(newTaskRelaList);
                    taskDO.setTaskName(oldTaskDO.getTaskName());
                    String[] validStr = TaskValidator.validateTask(dos.getTaskDO());
                    if (validStr[0].equals("0")) {
                        throw new RuntimeException(validStr[1]);
                    }

                    taskService.updateTask(taskDO);

//                    updateMcTaskInfo(parasDO, taskDO);

                    List<WormholeDO> oldList = loadService.getLoadCfgListByID(taskId);
                    String targetDbName = "";
                    for (WormholeDO wormholeDO : oldList) {
                        if (wormholeDO.getType().equals(Const.WORMHOLE_WRITER_TYPE)) {
                            targetDbName = wormholeDO.getConnectProps().replace(parasDO.getTargetDBType().concat("_"), "");
                        }
                    }
                    for (WormholeDO wormholeDO : list) {
                        wormholeDO.setTaskId(taskId);
                        if (wormholeDO.getType().equals(Const.WORMHOLE_WRITER_TYPE)) {
                            wormholeDO.getParameterMap().put(WriterParameters.GreenplumWriterParas.connectProps.toString(), targetDbName + "_" + Const.DEFAULT_BI_DATABASE);
                            wormholeDO.setParameterMapStr(JacksonHelper.pojoToJson(wormholeDO.getParameterMap()));
                            wormholeDO.setConnectProps(parasDO.getTargetDBType().concat("_").concat(targetDbName));
                        }
                    }
                    loadService.updateLoadCfg(taskId, list);
                    mediaHandler.executeDDL(parasDO.getTargetDBType(), targetDbName, parasDO.getSourceSchemaName(), dos.getDdl());
                }

            } else {
                String[] validStr = TaskValidator.validateTask(dos.getTaskDO());
                if (validStr[0].equals("0")) {
                    throw new RuntimeException(validStr[1]);
                }

                taskService.updateTask(dos.getTaskDO());
//                updateMcTaskInfo(parasDO, dos.getTaskDO());

                loadService.updateLoadCfg(dos.getTaskDO().getTaskId(), list);
                mediaHandler.executeDDL(parasDO.getTargetDBType(), parasDO.getTargetDBName(), parasDO.getSourceSchemaName(), dos.getDdl());
            }
            mercuryService.updateMercuryInfo(targetTable);
            return true;
        } catch (Exception e) {
            logger.error("update AutoETL Task error", e);
            return false;
        }
    }

    @Override
    public List<McColumnInfo> getSourceColumnInfo(StarShuttleParasDO parasDO) {
        Handler sourceHandler = this.handlerMap.get(parasDO.getSourceDBType());
        return sourceHandler.genSourceTableColumnInfo(parasDO);
    }

    @Override
    public boolean updateAdvanceCfg(StarShuttleDO dos, StarShuttleParasDO parasDO) {
        try {
            List<WormholeDO> list = new ArrayList<WormholeDO>();
            for (WormholeDO load : dos.getWormholeDOs()) {
                load.setParameterMapStr(JacksonHelper.pojoToJson(load.getParameterMap()));
                list.add(load);
            }
            McTableInfo targetTable = new McTableInfo();

            targetTable.setTable_name(parasDO.getTargetTableName().toLowerCase());
            targetTable.setStorage_type(parasDO.getTargetDBType());
            targetTable.setDb_name(parasDO.getTargetDBName());
            targetTable.setSchema_name(parasDO.getTargetSchemaName());
            targetTable.setStatus(Const.TABLE_VALIDATE);
            List<McColumnInfo> parColumnList = getParcolumnList(dos, parasDO);
            List<McColumnInfo> columnList = dos.getColumns();
            columnList.addAll(parColumnList);
            targetTable.setColumns(columnList);
            targetTable.setTable_access_level(2);
            targetTable.setTable_access_desc(null);
            targetTable.setTable_owner(parasDO.getOwner());
            targetTable.setRefresh_datum_date(parasDO.getDateNumber());
            targetTable.setRefresh_offset(parasDO.getDateOffset());
            targetTable.setRefresh_cycle(parasDO.getDateType());
            targetTable.setRefresh_type(parasDO.getTargetTableType());
            targetTable.setSchema_name(parasDO.getTargetSchemaName());

            Integer tableId = mercuryService.getTableIDbyTaskID(dos.getTaskDO().getTaskId()).get(0);

            List<AclUserInfoBase> originAclUserInfoBases = aclInfoService.getUserInfoByTableId(tableId);


            List<Integer> taskIdList = mercuryService.getTaskIDbyTableID(tableId);

            if (taskIdList.size() > 1 && parasDO.getTargetDBType().equals(Const.DATASOURCE_TYPE_GPREPORT)) {
                for (Integer taskId : taskIdList) {
                    List<WormholeDO> oldList = loadService.getLoadCfgListByID(taskId);
                    String targetDbName = "";
                    for (WormholeDO wormholeDO : oldList) {
                        if (wormholeDO.getType().equals(Const.WORMHOLE_WRITER_TYPE)) {
                            targetDbName = wormholeDO.getConnectProps().replace(parasDO.getTargetDBType().concat("_"), "");
                        }
                    }
                    for (WormholeDO wormholeDO : list) {
                        wormholeDO.setTaskId(taskId);
                        if (wormholeDO.getType().equals(Const.WORMHOLE_WRITER_TYPE)) {
                            wormholeDO.getParameterMap().put(WriterParameters.GreenplumWriterParas.connectProps.toString(), targetDbName + "_" + Const.DEFAULT_BI_DATABASE);
                            wormholeDO.setParameterMapStr(JacksonHelper.pojoToJson(wormholeDO.getParameterMap()));
                            wormholeDO.setConnectProps(parasDO.getTargetDBType().concat("_").concat(targetDbName));
                        }
                    }
                    loadService.updateLoadCfg(taskId, list);
                    mediaHandler.executeDDL(parasDO.getTargetDBType(), targetDbName, parasDO.getSourceSchemaName(), dos.getDdl());
                }

            } else {
                String[] validStr = TaskValidator.validateTask(dos.getTaskDO());
                if (validStr[0].equals("0")) {
                    throw new RuntimeException(validStr[1]);
                }
                loadService.updateLoadCfg(dos.getTaskDO().getTaskId(), list);
                mediaHandler.executeDDL(parasDO.getTargetDBType(), parasDO.getTargetDBName(), parasDO.getSourceSchemaName(), dos.getDdl());
            }
            mercuryService.updateMercuryInfo(targetTable);

            authorityService.pushACLInfo(originAclUserInfoBases, Arrays.asList(tableId));

            return true;
        } catch (Exception e) {
            logger.error("update AutoETL Task error", e);
            return false;
        }
    }

    @Override
    public boolean migrateTaskData(Integer taskID) {

        Integer tableID = mercuryService.getTableIDbyTaskID(taskID).get(0);
        List<Integer> taskIDs = mercuryService.getTaskIDbyTableID(tableID);

        WormholeDO readWormholeDO = new WormholeDO();
        WormholeDO writeWormholeDO = new WormholeDO();
        List<WormholeDO> wormholeDOList = new ArrayList<WormholeDO>();

        List<TaskDO> originTaskDOList = new ArrayList<TaskDO>();

        boolean flag = true;
        for (Integer taskId : taskIDs) {

            try {
                TaskDO taskDO = taskService.getTaskByTaskId(taskId);
                TaskDO originTaskDO = (TaskDO) taskDO;
                originTaskDOList.add(originTaskDO);

                String param1 = taskDO.getPara1().replace("\"", "");
                String[] paramInfoArray = param1.split("\\s");
                if (paramInfoArray.length != 3) {
                    logger.error("param2 do not has enough info...");
                    return false;
                }

                //更新task schedule info
                taskDO.setTaskObj("sh /data/deploy/dwarch/conf/ETL/bin/starshuttle.sh");
                taskDO.setPara1("-id ${task_id}");
                taskDO.setPara2("-time ${unix_timestamp}");
                taskDO.setPara3("-offset " + taskDO.getOffset());

                flag = taskService.updateTask(taskDO);
                if (!flag)
                    break;

                String wormholeSourceDB = "etl_" + paramInfoArray[1] + "_reader_cfg";
                String wormholeTargetDB = null;
                if (paramInfoArray[2].equalsIgnoreCase("hive"))
                    wormholeTargetDB = "etl_hdfs_writer_cfg";
                else
                    wormholeTargetDB = "etl_" + paramInfoArray[2] + "_writer_cfg";

                //更新task wormhole info
                Map<String, String> getWormholeReaderInfoParamMap = new HashMap<String, String>();
                getWormholeReaderInfoParamMap.put("taskID", taskId.toString());
                getWormholeReaderInfoParamMap.put("queryDB", wormholeSourceDB);
                Map<Object, Object> readResult = loadService.getOldLoadCfgByID(getWormholeReaderInfoParamMap);
                Map<String, String> wormholeReaderInfo = (HashMap<String, String>) readResult.get(taskId);
                for (String key : wormholeReaderInfo.keySet()) {
                    if (null != wormholeReaderInfo.get(key)) {
                        wormholeReaderInfo.put(key, String.valueOf(wormholeReaderInfo.get(key)));
                    }
                }
                readWormholeDO.setTaskId(taskId);
                readWormholeDO.setType("reader");
                if (wormholeSourceDB.equals("etl_hive_reader_cfg")) {
                    readWormholeDO.setConnectProps("hive_bi");
                } else {
                    readWormholeDO.setConnectProps(wormholeReaderInfo.get("connectprops"));
                }
                readWormholeDO.setConditionCol(null);
                readWormholeDO.setParameterMapStr(JacksonHelper.pojoToJson(wormholeReaderInfo));
                wormholeDOList.add(readWormholeDO);

                Map<String, String> getWormholeWriterInfoParamMap = new HashMap<String, String>();
                getWormholeWriterInfoParamMap.put("taskID", taskId.toString());
                getWormholeWriterInfoParamMap.put("queryDB", wormholeTargetDB);
                Map<Object, Object> writeResult = loadService.getOldLoadCfgByID(getWormholeWriterInfoParamMap);
                Map<String, String> wormholeWriterInfo = (HashMap<String, String>) writeResult.get(taskId);
                for (String key : wormholeWriterInfo.keySet()) {
                    if (null != wormholeWriterInfo.get(key)) {
                        wormholeWriterInfo.put(key, String.valueOf(wormholeWriterInfo.get(key)));
                    }
                }

                writeWormholeDO.setTaskId(taskId);
                writeWormholeDO.setType("writer");
                if (wormholeTargetDB.equals("etl_hdfs_writer_cfg")) {
                    writeWormholeDO.setConnectProps("hive_bi");
                } else {
                    writeWormholeDO.setConnectProps(wormholeWriterInfo.get("connectprops"));
                }
                writeWormholeDO.setConditionCol(null);
                writeWormholeDO.setParameterMapStr(JacksonHelper.pojoToJson(wormholeWriterInfo));
                wormholeDOList.add(writeWormholeDO);

                flag = loadService.insertLoadCfg(wormholeDOList);
                if (!flag)
                    break;

                logger.info("success migrate task: " + taskId + " data to galaxy.");
            } catch (Exception e) {
                flag = false;
                logger.error("migrate task:" + taskId + " data failure...", e);
                break;
            }
        }
        if (!flag) {
            for (TaskDO taskDO : originTaskDOList) {
                taskService.updateTask(taskDO);
            }
            logger.info("migrate task error,task info has roll back...");
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteAutoEtlTask(int taskId, String updateTime, String updateUser) {
        McTableInfo table = mercuryService.getTaskTable(taskId);
        if (table == null) {
            taskService.deleteTaskByTaskId(taskId, updateTime, updateUser);
            return true;
        }
        List<Integer> taskIDs = mercuryService.getTaskIDbyTableID(table.getTable_id());
        for (Integer taskID : taskIDs) {
            taskService.deleteTaskByTaskId(taskID, updateTime, updateUser);
        }
        mercuryService.deleteMercuryInfo(null, table, taskIDs);
        List<String> targetDBs = GlobalResources.DBMAPPING_MAP.get(table.getStorage_type().toLowerCase());
        Handler handler = this.getHandler(table.getStorage_type());
        StarShuttleParasDO parasDO = new StarShuttleParasDO();
        parasDO.setTargetDBName(table.getDb_name());
        parasDO.setTargetTableType(table.getRefresh_type());
        parasDO.setTargetTableName(table.getTable_name());
        parasDO.setTargetSchemaName(table.getSchema_name());
        String dropSql = handler.generateDropDDL(parasDO);
        for (String targetDB : targetDBs) {
            mediaHandler.executeDDL(table.getStorage_type(), targetDB, table.getSchema_name(), dropSql);
        }
        return true;
    }

    public List<String> getTargetDBTypeBySourceDBType(String sourceDBType) {
        List<String> typesAsList = GlobalResources.DBTypeMAPPING_MAP.get(sourceDBType);
        //typesAsList是不可元素的list
        List<String> targetDBTypes = new ArrayList<String>();
        for (String type : typesAsList) {
            targetDBTypes.add(type);
        }
        return targetDBTypes;
    }

    @Override
    public boolean deleteTaskTables(int taskId) {
        McTableInfo table = mercuryService.getTaskTable(taskId);
        List<Integer> taskIDs = mercuryService.getTaskIDbyTableID(table.getTable_id());
        mercuryService.deleteMercuryInfo(null, table, taskIDs);
        List<String> targetDBs = GlobalResources.DBMAPPING_MAP.get(table.getStorage_type().toLowerCase());
        Handler handler = this.getHandler(table.getStorage_type());
        StarShuttleParasDO parasDO = new StarShuttleParasDO();
        parasDO.setTargetDBName(table.getDb_name());
        parasDO.setTargetTableType(table.getRefresh_type());
        parasDO.setTargetTableName(table.getTable_name());
        parasDO.setTargetSchemaName(table.getSchema_name());
        String dropSql = handler.generateDropDDL(parasDO);
        for (String targetDB : targetDBs) {
            mediaHandler.executeDDL(table.getStorage_type(), targetDB, table.getSchema_name(), dropSql);
        }
        return true;
    }

    public boolean updateMcTableInfo(StarShuttleDO dos, StarShuttleParasDO parasDO) {
        McTableInfo targetTable = new McTableInfo();
        targetTable.setTable_name(parasDO.getTargetTableName().toLowerCase());
        targetTable.setStorage_type(parasDO.getTargetDBType());
        targetTable.setDb_name(parasDO.getTargetDBName());
        targetTable.setSchema_name(parasDO.getTargetSchemaName());
        targetTable.setRefresh_cycle(com.dianping.data.warehouse.core.common.Const.DEFAULT_CYCLE);
        targetTable.setRefresh_type(parasDO.getTargetTableType());
        targetTable.setStatus(com.dianping.data.warehouse.core.common.Const.TABLE_VALIDATE);
        List<McColumnInfo> parColumnList = getParcolumnList(dos, parasDO);
        List<McColumnInfo> columnList = dos.getColumns();
        columnList.addAll(parColumnList);
        targetTable.setColumns(columnList);
        targetTable.setTable_access_level(2);
        targetTable.setTable_access_desc(null);
        targetTable.setTable_owner(dos.getTaskDO().getOwner());
        targetTable.setRefresh_datum_date(parasDO.getDateNumber());
        targetTable.setRefresh_offset(parasDO.getDateOffset());
        targetTable.setSchema_name(parasDO.getTargetSchemaName());
        return mercuryService.updateMercuryInfo(targetTable);
    }

}
