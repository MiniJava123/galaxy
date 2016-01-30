package com.dianping.data.warehouse.core.handler.impl;

import com.dianping.data.warehouse.core.common.Const;
import com.dianping.data.warehouse.core.common.DateConst;
import com.dianping.data.warehouse.core.common.ReaderParameters;
import com.dianping.data.warehouse.core.handler.AbstractHandler;
import com.dianping.data.warehouse.core.handler.Handler;
import com.dianping.data.warehouse.core.utils.GalaxyDateUtils;
import com.dianping.data.warehouse.domain.McColumnInfo;
import com.dianping.data.warehouse.domain.web.StarShuttleParasDO;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hongdi.tang on 14-2-18.
 */
public class SQLServerHandler extends AbstractHandler implements Handler {


    @Override
    public String generateDropDDL(StarShuttleParasDO starShuttleParasDO) {
        return null;
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
        return null;
    }


    @Override
    public Map<String, Object> genReaderParas(StarShuttleParasDO parasDO, List<McColumnInfo> sourceColumns) {
        Map<String,Object> loadMap = new HashMap<String, Object>();
        String columnStr = this.genColumnStr(sourceColumns);
        String sql = this.genReaderSql(parasDO,columnStr.concat(",getdate()"));

        loadMap.put(ReaderParameters.SqlServerReaderParas.plugin.toString(),"sqlserverreader");
        loadMap.put(ReaderParameters.SqlServerReaderParas.connectProps.toString(),parasDO.getSourceDBName());
        loadMap.put(ReaderParameters.SqlServerReaderParas.encoding.toString(),"utf-8");
//        loadMap.put(ReaderParameters.SqlServerReaderParas.params.toString(),null);
//        loadMap.put(ReaderParameters.SqlServerReaderParas.preCheck.toString(),null);
        loadMap.put(ReaderParameters.SqlServerReaderParas.sql.toString(), sql);
//        loadMap.put(ReaderParameters.SqlServerReaderParas.tableName.toString(),null);
//        loadMap.put(ReaderParameters.SqlServerReaderParas.where.toString(),null);
//        loadMap.put(ReaderParameters.SqlServerReaderParas.columns.toString(),null);
        loadMap.put(ReaderParameters.SqlServerReaderParas.concurrency.toString(),"1");
        loadMap.put(ReaderParameters.SqlServerReaderParas.needSplit.toString(),"false");
//        loadMap.put(ReaderParameters.SqlServerReaderParas.countSql.toString(),null);
//        loadMap.put(ReaderParameters.SqlServerReaderParas.autoIncKey.toString(),null);
        return loadMap;
    }

    @Override
    public Map<String, Object> genWriterParas(StarShuttleParasDO parasDO, List<McColumnInfo> columns) {
        return null;
    }

    @Override
    public String genReaderSql(StarShuttleParasDO parasDO, String columnStr) {
        StringBuilder builder = new StringBuilder();
        //String beginStr = GalaxyDateUtils.genCalVariable(parasDO.getDateType(), Integer.valueOf(parasDO.getDateNumber()));
        builder.append("select ").append(columnStr).append(" from ").append(parasDO.getSourceSchemaName())
                .append(".").append(parasDO.getSourceTableName());
        if(parasDO.getTargetTableType().equals(Const.TABLE_TYPE_INCREMENT)){
            String beginStr = null;
            if(parasDO.getDateType().equals(Const.OFFSET_TYPE_MTD)){
//                beginStr = "substring(convert(varchar(11),dateadd(dd,-1,'"+DateConst.BATCH_CAL_VARS.CAL_YYYYMMDD_P1D.getVariableStr()
//                        +"'),23),1,7) + '-01'";
                beginStr = "'"+DateConst.BATCH_CAL_VARS.CAL_YYYYMM_01.getVariableStr()+"'";
            }else{
                beginStr = "'"+GalaxyDateUtils.genCalVariable(parasDO.getDateType(), parasDO.getDateOffset())+"'";
            }
            String endStr = "'"+DateConst.BATCH_CAL_VARS.CAL_YYYYMMDD.getVariableStr()+"'";
            builder.append(" where ").append(parasDO.getTargetPartitionName()).append(">=")
                    .append(beginStr).append(" and ").append(parasDO.getTargetPartitionName()).append("<")
                    .append(endStr);
        }
        return builder.toString();
    }

    @Override
    public Map<String, Object> updateMapParas(StarShuttleParasDO parasDO, List<McColumnInfo> columns, Map<String, Object> mapParas, String type) {
        String columnStr = this.genColumnStr(columns);
        String sql = this.genReaderSql(parasDO,columnStr.concat(",getdate()"));
        if(type.equals(Const.WORMHOLE_READER_TYPE)){
            mapParas.put(ReaderParameters.SqlServerReaderParas.sql.toString(), sql);
        }
        return  mapParas;
    }

    @Override
    public String genColumnStr(List<McColumnInfo> columns) {
        StringBuilder builder = new StringBuilder();
        for(McColumnInfo column : columns){
            builder.append("\"").append(column.getColumn_name().toLowerCase())
                    .append("\"").append(",");
        }
        return StringUtils.substring(builder.toString(), 0, builder.toString().length() - 1);
    }

}
