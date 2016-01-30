package com.dianping.data.warehouse.core.service.impl;

import com.dianping.data.warehouse.core.common.Const;
import com.dianping.data.warehouse.core.common.GlobalResources;
import com.dianping.data.warehouse.core.handler.Handler;
import com.dianping.data.warehouse.core.handler.MediaHandler;
import com.dianping.data.warehouse.domain.McColumnInfo;
import com.dianping.data.warehouse.domain.McTableInfo;
import com.dianping.data.warehouse.domain.McTableQuery;
import com.dianping.data.warehouse.domain.web.BuildTabParaDO;
import com.dianping.data.warehouse.domain.web.HiveColumnDO;
import com.dianping.data.warehouse.domain.web.HiveTableDO;
import com.dianping.data.warehouse.domain.web.StarShuttleParasDO;
import com.dianping.data.warehouse.masterdata.service.McMetaService;
import com.dianping.data.warehouse.masterdata.service.MercuryService;
import com.dianping.data.warehouse.service.AuthorityService;
import com.dianping.data.warehouse.service.AutoBuildTabService;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Table;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by shanshan.jin on 14-6-11.
 */
@Service
public class AutoBuildTabServiceImpl implements AutoBuildTabService {
    private Logger logger = org.slf4j.LoggerFactory.getLogger(AutoBuildTabServiceImpl.class);

    @Resource(name = "handlerMap")
    private Map<String, Handler> handlerMap;

    @Resource(name = "authorityServiceImpl")
    private AuthorityService authorityService;

    @Resource(name = "mediaHandlerImpl")
    private MediaHandler mediaHandler;

    public void setMcMetaService(McMetaService mcMetaService) {
        this.mcMetaService = mcMetaService;
    }

    public void setMercuryService(MercuryService mercuryService) {
        this.mercuryService = mercuryService;
    }

    private McMetaService mcMetaService;

    private MercuryService mercuryService;

    @Override
    public BuildTabParaDO getTableInfo(McTableQuery query, int loginID) {
        try {
            Table table = mcMetaService.getTableInfo(query);

            McTableInfo mcTableInfo = new McTableInfo();
            mcTableInfo.setStorage_type(Const.DATASOURCE_TYPE_HIVE);

            mcTableInfo.setDb_name(table.getDbName());
            mcTableInfo.setTable_name(table.getTableName());
            List<McTableInfo> tableList = mercuryService.getTableInfoList(mcTableInfo);
            McTableInfo tableInfo = new McTableInfo();
            if (tableList.size() > 0) {
                tableInfo = tableList.get(0);
            }

            List<McColumnInfo> columnInfoList = mercuryService.getTgtColumnList(mcTableInfo);
            for (FieldSchema col : table.getSd().getCols()) {
                for (McColumnInfo column : columnInfoList) {
                    if (col.getName().equals(column.getColumn_name())) {
                        col.setComment(column.getColumn_desc());
                        break;
                    }
                }
            }

            for (FieldSchema col : table.getPartitionKeys()) {
                for (McColumnInfo column : columnInfoList) {
                    if (col.getName().equals(column.getColumn_name())) {
                        col.setComment(column.getColumn_desc());
                        break;
                    }
                }
            }

            HiveTableDO hiveTableDO = new HiveTableDO();
            hiveTableDO.setTableType(table.getTableType());
            hiveTableDO.setTableBucketscol(table.getSd().getSortCols().toString());
            hiveTableDO.setTableBucketsNum(table.getSd().getNumBuckets());
            hiveTableDO.setTableOutputformat(table.getSd().getOutputFormat());
            hiveTableDO.setTableInputformat(table.getSd().getInputFormat());
            hiveTableDO.setTableLocation(table.getSd().getLocation());
            hiveTableDO.setTableSerde(table.getSd().getSerdeInfo().getSerializationLib());
            hiveTableDO.setTableName(table.getTableName());
            hiveTableDO.setDbName(table.getDbName());
            hiveTableDO.setParameters(table.getSd().getSerdeInfo().getParameters());

            List<HiveColumnDO> columnDOs = new ArrayList<HiveColumnDO>();
            List<HiveColumnDO> parColumnDOs = new ArrayList<HiveColumnDO>();
            Integer i = 1;
            for (FieldSchema col : table.getSd().getCols()) {
                HiveColumnDO column = new HiveColumnDO();
                column.setColumnKey(i);
                column.setColumnName(col.getName());
                column.setColumnComment(col.getComment());
                column.setColumnType(col.getType());
                columnDOs.add(column);
            }
            i = 1;
            for (FieldSchema col : table.getPartitionKeys()) {
                HiveColumnDO column = new HiveColumnDO();
                column.setColumnKey(i);
                column.setColumnName(col.getName());
                column.setColumnComment(col.getComment());
                column.setColumnType(col.getType());
                parColumnDOs.add(column);
            }


            String[] onlineGroup = authorityService.getACLGroup(true, loginID);
            String[] offlineGroup = authorityService.getACLGroup(false, loginID);
            BuildTabParaDO paraDO = new BuildTabParaDO();
            paraDO.setTable(hiveTableDO);
            paraDO.setTableComment(tableInfo.getTable_desc());
            paraDO.setRefreshType(tableInfo.getRefresh_type());
            paraDO.setColumnDOList(columnDOs);
            paraDO.setPartitionColumnList(parColumnDOs);
            paraDO.setOffLineGroupList(offlineGroup);
            paraDO.setOnLineGroupList(onlineGroup);
            return paraDO;
        } catch (Exception e) {
            logger.error("get hive tableInfo error:", e);
            return null;
        }
    }

    @Override
    public String getDdl(BuildTabParaDO paraDO) {
        HiveTableDO table = paraDO.getTable();
        McTableQuery query = new McTableQuery();
        query.setDbName(Const.HIVE_ENV_TYPE.hive_online.toString() + "_" + table.getDbName());
        query.setTableName(table.getTableName());
        query.setStorageType(Const.DATASOURCE_TYPE_HIVE);
        Table onlineTable = mcMetaService.getTableInfo(query);
        boolean hasComment = true;
        for (HiveColumnDO col : paraDO.getColumnDOList()) {
            if (StringUtils.isBlank(col.getColumnComment())) {
                hasComment = false;
                break;
            }
        }
        if (hasComment) {
            if (onlineTable != null) {
                paraDO.setIfONlineExist(true);
                return getAlterTabDdl(paraDO, onlineTable);
            } else {
                paraDO.setIfONlineExist(false);
                return getCreateTabDdl(paraDO);
            }
        } else {
            throw new RuntimeException("some comments are empty!!");
        }
    }

    /**
     * 同步表结构
     */
    @Override
    public boolean buildTable(BuildTabParaDO paraDO, String ddl) {
        McTableInfo targetTable = getMcTableInfo(paraDO);
        if (!executeDDL(targetTable, ddl))
            return false;
        if (!saveMercuryInfo(paraDO, targetTable)) {
            logger.error("Attention! executeD ddl success but save mercury info fail: " + ddl);
            return false;
        }
        if (!pushACLInfo(paraDO, targetTable)) {
            logger.error("Attention! executeD ddl and save mercury info success, but push acl info fails: " + ddl);
            return false;
        }
        return true;
    }

    private String getCreateTabDdl(BuildTabParaDO paraDO) {
        HiveTableDO table = paraDO.getTable();
        String tableType = table.getTableType().equals("EXTERNAL_TABLE") ? "external" : "";
        String tableName = table.getTableName();
        String contentPartition = GlobalResources.DDLMAP.get(Const.DDL_TYPE.hive_partition.toString().concat(".ddl"));
        String contentNonPartition = GlobalResources.DDLMAP.get(Const.DDL_TYPE.hive_nonpartition.toString().concat(".ddl"));
        String ddl = "";
        List<HiveColumnDO> columns = paraDO.getColumnDOList();
        List<HiveColumnDO> partitionColumns = paraDO.getPartitionColumnList();
        String partitionColInfo = "";
        String serdeInfo = table.getTableSerde();
        Map<String, String> serdePropMap = table.getParameters();
        String inputFormat = table.getTableInputformat();
        String location = tableType.equals("external") ? "LOCATION \"" + table.getTableLocation() + "\"" : "";
        String outputFormat = table.getTableOutputformat();
        StringBuilder builder = new StringBuilder();

        for (HiveColumnDO col : columns) {
            builder.append("`").append(col.getColumnName().trim()).append("`").append("    ").append(col.getColumnType()).append(",").append("\n");
        }
        String columnInfo = StringUtils.substring(builder.toString(), 0, builder.toString().length() - 2);
        if (partitionColumns.size() > 0) {
            builder = new StringBuilder();
            for (HiveColumnDO col : partitionColumns) {
                builder.append("`").append(col.getColumnName().trim()).append("`").append("    ").append(col.getColumnType()).append(",").append("\n");
            }
            partitionColInfo = StringUtils.substring(builder.toString(), 0, builder.toString().length() - 2);
        }
        String serdePropInfo = "";
        if (serdePropMap.size() > 0) {
            builder = new StringBuilder();
            for (Map.Entry entry : serdePropMap.entrySet()) {
                builder.append("'").append(entry.getKey().toString()).append("'='").append(entry.getValue().toString()).append("'").append(",");
            }
            serdePropInfo = StringUtils.substring(builder.toString(), 0, builder.toString().length() - 1);

        } else {
            serdePropInfo = "'serialization.null.format' = \"\"";
        }
        if (partitionColumns.size() > 0) {
            ddl = StringUtils.replaceEach(contentPartition, new String[]{"${tableType}", "${tableName}", "${columnInfo}", "${partitionColInfo}", "${serdePropInfo}", "${inputFormat}", "${outputFormat}", "${serdeInfo}", "${locationDdl}"}
                    , new String[]{tableType, tableName, columnInfo, partitionColInfo, serdePropInfo, inputFormat, outputFormat, serdeInfo, location});
        } else {
            ddl = StringUtils.replaceEach(contentNonPartition, new String[]{"${tableType}", "${tableName}", "${columnInfo}", "${serdePropInfo}", "${inputFormat}", "${outputFormat}", "${serdeInfo}", "${locationDdl}"}
                    , new String[]{tableType, tableName, columnInfo, serdePropInfo, inputFormat, outputFormat, serdeInfo, location});
        }
        return ddl;
    }

    private String getAlterTabDdl(BuildTabParaDO paraDO, Table onlineTable) {
        Handler handler = this.handlerMap.get(Const.DATASOURCE_TYPE_HIVE.toString());

        List<McColumnInfo> preColumns = getPreColumns(paraDO.getColumnDOList());
        List<McColumnInfo> onlineColumns = getOnlineColumns(onlineTable.getSd().getCols());

        StarShuttleParasDO starShuttleParasDO = new StarShuttleParasDO();
        starShuttleParasDO.setTargetTableName(paraDO.getTable().getTableName());
        starShuttleParasDO.setTargetTableType(Const.TABLE_TYPE_SNAPSHOT.toString());
        starShuttleParasDO.setSourceTableName(onlineTable.getTableName());
        return handler.generateAlterDDL(onlineColumns, preColumns, starShuttleParasDO);
    }

    /**
     * 通过BuildTabParaDO封装McTableInfo
     */
    private McTableInfo getMcTableInfo(BuildTabParaDO paraDO) {
        McTableInfo targetTable = new McTableInfo();
        HiveTableDO table = paraDO.getTable();
        targetTable.setRefresh_cycle(paraDO.getRefreshCycle());
        targetTable.setDb_name(table.getDbName());
        targetTable.setTable_name(table.getTableName());
        targetTable.setSchema_name(table.getTableName().split("_")[0]);
        targetTable.setStorage_type(Const.DATASOURCE_TYPE_HIVE);
        targetTable.setRefresh_type(paraDO.getRefreshType());
        targetTable.setTable_desc(paraDO.getTableComment());
        targetTable.setStatus(Const.TABLE_VALIDATE);
        targetTable.setSchema_name(table.getTableName().split("_")[0]);
        targetTable.setTable_owner(paraDO.getOwner());
        List<McColumnInfo> columns = new ArrayList<McColumnInfo>();
        int rn = 1;
        for (HiveColumnDO col : paraDO.getColumnDOList()) {
            McColumnInfo column = new McColumnInfo();
            column.setColumn_name(col.getColumnName());
            column.setColumn_type(col.getColumnType());
            column.setColumn_desc(col.getColumnComment());
            column.setIs_partition_column(0);
            column.setColumn_rn(rn);
            columns.add(column);
            rn++;
        }
        for (HiveColumnDO col : paraDO.getPartitionColumnList()) {
            McColumnInfo column = new McColumnInfo();
            column.setColumn_name(col.getColumnName());
            column.setColumn_type(col.getColumnType());
            column.setColumn_desc(col.getColumnComment());
            column.setIs_partition_column(1);
            column.setColumn_rn(rn);
            columns.add(column);
            rn++;
        }
        targetTable.setColumns(columns);
        return targetTable;
    }

    private boolean executeDDL(McTableInfo targetTable, String ddl) {
        try {
            return mediaHandler.executeDDL(targetTable.getStorage_type(), targetTable.getDb_name(), targetTable.getSchema_name(), ddl);
        } catch (Exception e) {
            logger.error("ddl excute error :" + e + ddl);
            return false;
        }
    }

    private boolean saveMercuryInfo(BuildTabParaDO paraDO, McTableInfo targetTable) {
        try {
            List<Integer> taskIdList = new ArrayList<Integer>();
            taskIdList.add(paraDO.getTaskId());
            return mercuryService.saveMercuryInfo(paraDO.getSrcTableList(), targetTable, taskIdList, Const.CALCULATE_TYPE);
        } catch (Exception e) {
            logger.error("save mercury info error ", e);
            return false;
        }
    }

    private boolean pushACLInfo(BuildTabParaDO paraDO, McTableInfo targetTable) {
        Integer tableId = mercuryService.getTableInfoList(targetTable).get(0).getTable_id();
        ArrayList group1 = new ArrayList<String>();
        if (paraDO.getOnLineGroupList().length > 0) {
            for (int i = 0; i < paraDO.getOnLineGroupList().length; i++) {
                group1.add(paraDO.getOnLineGroupList()[i]);
            }
        }
        ArrayList group2 = new ArrayList<String>();
        if (paraDO.getOffLineGroupList().length > 0) {
            for (int i = 0; i < paraDO.getOffLineGroupList().length; i++) {
                group2.add(paraDO.getOffLineGroupList()[i]);
            }
        }
        ArrayList<Integer> tables = new ArrayList<Integer>();
        if (tableId > 0)
            tables.add(tableId);
        if ((group1.size() > 0 || group2.size() > 0) && tables.size() > 0) {
            String pushResult = authorityService.pushACLInfo(group1, group2, tables);
            JSONObject object = JSONObject.fromObject(pushResult);
            logger.info("ACL push authorization info:" + object.toString());
            if (object.getInt("code") != 200) {
                logger.error("授权请求发送失败" + object.toString());
                return false;
            }
        }
        return true;
    }

    private List<McColumnInfo> getPreColumns(List<HiveColumnDO> preCols) {
        List<McColumnInfo> preColumns = new ArrayList<McColumnInfo>();
        int rn = 1;
        for (HiveColumnDO col : preCols) {
            McColumnInfo column = new McColumnInfo();
            column.setColumn_name(col.getColumnName());
            column.setColumn_type(col.getColumnType());
            column.setColumn_rn(rn);
            preColumns.add(column);
            rn++;
        }
        return preColumns;
    }

    private List<McColumnInfo> getOnlineColumns(List<FieldSchema> onlineCols) {
        List<McColumnInfo> onlineColumns = new ArrayList<McColumnInfo>();
        int rn = 1;
        for (FieldSchema col : onlineCols) {
            McColumnInfo column = new McColumnInfo();
            column.setColumn_name(col.getName());
            column.setColumn_type(col.getType());
            column.setColumn_rn(rn);
            onlineColumns.add(column);
            rn++;
        }
        return onlineColumns;
    }


}
