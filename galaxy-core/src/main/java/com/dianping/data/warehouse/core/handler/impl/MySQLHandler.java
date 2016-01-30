package com.dianping.data.warehouse.core.handler.impl;

import com.dianping.data.warehouse.core.common.*;
import com.dianping.data.warehouse.core.handler.AbstractHandler;
import com.dianping.data.warehouse.core.handler.Handler;
import com.dianping.data.warehouse.core.utils.GalaxyDateUtils;
import com.dianping.data.warehouse.domain.McColumnInfo;
import com.dianping.data.warehouse.domain.web.StarShuttleParasDO;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hongdi.tang on 14-2-18.
 */
public class MySQLHandler extends AbstractHandler implements Handler {


    @Override
    public String generateDropDDL(StarShuttleParasDO parasDO) {
        return "drop table".concat(parasDO.getTargetTableName());
    }

    @Override
    public String generateCreateDDL(List<McColumnInfo> columnInfoList, StarShuttleParasDO starShuttleParasDO) {
        return null;
    }

    @Override
    public String generateAlterDDL(List<McColumnInfo> oldColumnList, List<McColumnInfo> newColumnList, StarShuttleParasDO starShuttleParasDO) {
        return null;
    }

    @Override
    public List<McColumnInfo> genTargetTableColumnInfo(StarShuttleParasDO para) {
        List<McColumnInfo> result = new ArrayList<McColumnInfo>();

        List<McColumnInfo> list = this.genSourceTableColumnInfo(para);
        if (list == null || list.size() == 0) {
            throw new NullPointerException("source table info is null");
        }

        Integer i = 1;
        for (McColumnInfo col : list) {
            col.setColumn_rn(i++);
            String targetColType = GlobalResources.MAPPING_PROPS.get(para.getSourceDBType().concat("_mysql.").concat(col.getColumn_type()));
            col.setColumn_type(targetColType);
            result.add(col);
        }
        return result;
    }


    @Override
    public Map<String, Object> genReaderParas(StarShuttleParasDO parasDO, List<McColumnInfo> sourceColumns) {
        Map<String, Object> loadMap = new HashMap<String, Object>();
        String columnStr = this.genColumnStr(sourceColumns);
        String sql = this.genReaderSql(parasDO, columnStr.concat(",now()"));

        loadMap.put(ReaderParameters.MysqlReaderParas.plugin.toString(), "mysqlreader");
        loadMap.put(ReaderParameters.MysqlReaderParas.connectProps.toString(), "mysql_" + parasDO.getSourceDBName());
        loadMap.put(ReaderParameters.MysqlReaderParas.encoding.toString(), "utf-8");
        loadMap.put(ReaderParameters.MysqlReaderParas.params.toString(), null);
        loadMap.put(ReaderParameters.MysqlReaderParas.preCheck.toString(), null);
        loadMap.put(ReaderParameters.MysqlReaderParas.sql.toString(), sql);
        loadMap.put(ReaderParameters.MysqlReaderParas.needSplit.toString(), "false");
        loadMap.put(ReaderParameters.MysqlReaderParas.concurrency.toString(), "10");
        loadMap.put(ReaderParameters.MysqlReaderParas.blockSize.toString(), "10000");
//        loadMap.put(ReaderParameters.MysqlReaderParas.tableName.toString(),null);
//        loadMap.put(ReaderParameters.MysqlReaderParas.autoIncKey.toString(),null);
//        loadMap.put(ReaderParameters.MysqlReaderParas.columns.toString(),null);
//        loadMap.put(ReaderParameters.MysqlReaderParas.where.toString(),null);
//        loadMap.put(ReaderParameters.MysqlReaderParas.countSql.toString(),null);
        return loadMap;
    }

    @Override
    public Map<String, Object> genWriterParas(StarShuttleParasDO parasDO, List<McColumnInfo> columns) {
        Map<String, Object> loadMap = new HashMap<String, Object>();
        String columnStr = this.genColumnStr(columns);
        //String targetTable = parasDO.getTargetSchemaName().concat(".").concat(parasDO.getTargetTableName());
        String targetTable = parasDO.getTargetTableName();

        String preSql = null;
        String postSql = null;
        String rollbackSql = null;
        String tableName = null;

        if (parasDO.getTargetTableType().equals(Const.TABLE_TYPE_COMPLETE)) {
            preSql = "drop table if exists ".concat(targetTable).concat("_tmp;create table ").concat(targetTable).concat("_tmp like ").concat(targetTable);
            postSql = "drop table if exists ".concat(targetTable).concat("_bak;alter table ").concat(targetTable).concat(" rename to ")
                    .concat(targetTable).concat("_bak;alter table ").concat(targetTable).concat("_tmp").concat(" rename to ").concat(targetTable)
                    .concat(";drop table if exists ").concat(targetTable).concat("_tmp;drop table if exists ").concat(targetTable).concat("_bak");
            tableName = targetTable.concat("_tmp");
        } else {
            tableName = targetTable;
            String appendField = parasDO.getTargetPartitionName();
            if (parasDO.getDateType().equals("MTD")) {
                String beginStr = "${CAL_YYYYMM_01}";
                String endStr = DateConst.BATCH_CAL_VARS.CAL_YYYYMMDD.getVariableStr();
                StringBuilder tmp = new StringBuilder();
                tmp.append("delete from ").append(targetTable).append(" where ").append(appendField).append(" >= '").
                        append(beginStr).append("' and ").append(appendField).append(" < '").append(endStr).append("';");
                preSql = tmp.toString();
                rollbackSql = preSql;
            } else {
                String beginStr = parasDO.getDateType() == null ? null : GalaxyDateUtils.genCalVariable(parasDO.getDateType(), parasDO.getDateOffset());
                String endStr = DateConst.BATCH_CAL_VARS.CAL_YYYYMMDD.getVariableStr();
                StringBuilder tmp = new StringBuilder();
                tmp.append("delete from ").append(targetTable).append(" where ").append(appendField).append(" >= '").
                        append(beginStr).append("' and ").append(appendField).append(" < '").append(endStr).append("';");
                preSql = tmp.toString();
                rollbackSql = preSql;
            }
        }


        loadMap.put(WriterParameters.MysqlWriterParas.plugin.toString(), "mysqlwriter");
        loadMap.put(WriterParameters.MysqlWriterParas.connectProps.toString(), "DWOutput");
        loadMap.put(WriterParameters.MysqlWriterParas.priority.toString(), "0");
        loadMap.put(WriterParameters.MysqlWriterParas.encoding.toString(), "utf-8");
        loadMap.put(WriterParameters.MysqlWriterParas.params.toString(), null);
        loadMap.put(WriterParameters.MysqlWriterParas.loadFile.toString(), false);
        loadMap.put(WriterParameters.MysqlWriterParas.tableName.toString(), tableName);
        loadMap.put(WriterParameters.MysqlWriterParas.columns.toString(), columnStr);
        loadMap.put(WriterParameters.MysqlWriterParas.pre.toString(), preSql);
        loadMap.put(WriterParameters.MysqlWriterParas.post.toString(), postSql);
        loadMap.put(WriterParameters.MysqlWriterParas.replace.toString(), null);
        loadMap.put(WriterParameters.MysqlWriterParas.rollback.toString(), rollbackSql);
        loadMap.put(WriterParameters.MysqlWriterParas.failedlinesthreshold.toString(), "0");
        loadMap.put(WriterParameters.MysqlWriterParas.countsql.toString(), null);
        return loadMap;
    }

    @Override
    public String genReaderSql(StarShuttleParasDO parasDO, String columnStr) {
        StringBuilder builder = new StringBuilder();

        builder.append("select ").append(columnStr).append(" from ").append(parasDO.getSourceTableName());
        if (parasDO.getTargetTableType().equals(Const.TABLE_TYPE_INCREMENT)) {
            String beginStr = null;
            if (parasDO.getDateType().equals(Const.OFFSET_TYPE_MTD)) {
//                beginStr = "concat(substring(date_add('"+DateConst.BATCH_CAL_VARS.CAL_YYYYMMDD_P1D.getVariableStr()
//                        +"',interval -1 day),1,7),'-01')";
                beginStr = "'" + DateConst.BATCH_CAL_VARS.CAL_YYYYMM_01.getVariableStr() + "'";
            } else {
                beginStr = "'" + GalaxyDateUtils.genCalVariable(parasDO.getDateType(), parasDO.getDateOffset()) + "'";
            }
            String endStr = "'" + DateConst.BATCH_CAL_VARS.CAL_YYYYMMDD.getVariableStr() + "'";

            builder.append(" where ").append(parasDO.getTargetPartitionName()).append(">=")
                    .append(beginStr).append(" and ").append(parasDO.getTargetPartitionName()).append("<")
                    .append(endStr);

        }
        return builder.toString();
    }

    @Override
    public Map<String, Object> updateMapParas(StarShuttleParasDO parasDO, List<McColumnInfo> columns, Map<String, Object> mapParas, String type) {
        String columnStr = this.genColumnStr(columns);
        String sql = this.genReaderSql(parasDO, columnStr.concat(",now()"));
        if (type.equals(Const.WORMHOLE_READER_TYPE)) {
            mapParas.put(ReaderParameters.MysqlReaderParas.sql.toString(), sql);
        } else if (type.equals(Const.WORMHOLE_WRITER_TYPE)) {
            mapParas.put(WriterParameters.MysqlWriterParas.columns.toString(), columnStr);
        }
        return mapParas;
    }

    @Override
    public String genColumnStr(List<McColumnInfo> columns) {
        StringBuilder builder = new StringBuilder();
        for (McColumnInfo column : columns) {
            builder.append("`").append(column.getColumn_name().toLowerCase())
                    .append("`").append(",");
        }
        return StringUtils.substring(builder.toString(), 0, builder.toString().length() - 1);
    }

}
