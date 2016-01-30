package com.dianping.data.warehouse.core.handler.impl;

import com.dianping.data.warehouse.core.common.*;
import com.dianping.data.warehouse.core.handler.AbstractHandler;
import com.dianping.data.warehouse.core.handler.Handler;
import com.dianping.data.warehouse.core.utils.GalaxyDateUtils;
import com.dianping.data.warehouse.domain.McColumnInfo;
import com.dianping.data.warehouse.domain.web.StarShuttleParasDO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hongdi.tang on 14-2-18.
 */
public class GreenPlumHandler extends AbstractHandler implements Handler {

    private static Logger logger = LoggerFactory.getLogger(GreenPlumHandler.class);

    @Override
    public String generateDropDDL(StarShuttleParasDO parasDO) {
        return "alter table bi."+parasDO.getTargetTableName() + " rename to "+parasDO.getTargetTableName()+"_drop"+Math.abs(new Random().nextInt()%100);
    }

    @Override
    public String generateCreateDDL(List<McColumnInfo> columnInfoList, StarShuttleParasDO paras) {
        try {
            String content = null;
            if (paras.getTargetTableType().equals(Const.TABLE_TYPE_COMPLETE)) {
                content = GlobalResources.DDLMAP.get(Const.DDL_TYPE.greenplum_complete.toString().concat(".ddl"));
            } else if (paras.getTargetTableType().equals(Const.TABLE_TYPE_INCREMENT)) {
                content = GlobalResources.DDLMAP.get(Const.DDL_TYPE.greenplum_increment.toString().concat(".ddl"));
            } else {
                throw new IllegalArgumentException("illegal table type");
            }
            StringBuilder builder = new StringBuilder();

            for (McColumnInfo col : columnInfoList) {
                //String targetColType = GlobalResources.MAPPING_PROPS.get(paras.getSourceDBType().concat("_gp.").concat(col.getColumn_type()));
                builder.append("\"").append(col.getColumn_name().trim().toLowerCase()).append("\"").append("\t")
                        .append(col.getColumn_type()).append(",").append("\n");
            }
            String columnInfo = StringUtils.substring(builder.toString(), 0, builder.toString().length() - 2);
            String tableName = StringUtils.join(new String[]{"bi",paras.getTargetTableName()},".");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

            Date date = new Date();
            Date nextDay = org.apache.commons.lang.time.DateUtils.addDays(date,1);

            String addPartitionSql = null;
//            if(paras.getTargetTableType().equals(Const.TABLE_TYPE_INCREMENT)){
//                Date d1 = (Date)nextDay.clone();
//                Date d2 = org.apache.commons.lang.time.DateUtils.addDays(d1,1);
//
//                StringBuilder tmp = new StringBuilder();
//                tmp.append("alter table ").append(tableName).append(" ADD PARTITION " ).append(" p").append(sdf.format(d1))
//                        .append(" START (' ").append(sdf.format(d1)).append("'::date)")
//                        .append(" END ('").append(sdf.format(d2)).append("'::date)")
//                        .append(" WITH (APPENDONLY=true, COMPRESSLEVEL=6, ORIENTATION=column, COMPRESSTYPE=zlib, OIDS=FALSE)");
//                addPartitionSql = tmp.toString();
//            }

            String ddl = StringUtils.replaceEach(content,
                    new String[]{"${tableName}", "${columnInfo}", "${YYYYMMDD}","${YYYYMMDD_N1}", "${partitionColumn}"},
                    new String[]{tableName, columnInfo,
                            sdf.format(date),
                            sdf.format(nextDay)
                            , paras.getTargetPartitionName()});
            return addPartitionSql == null ? ddl : ddl + addPartitionSql;
        } catch (Exception e) {
            logger.error("generate greenplum ddl failure", e);
            throw new RuntimeException("generate greenplum ddl failure", e);
        }
    }

    @Override
    public String generateAlterDDL(List<McColumnInfo> oldColumnList, List<McColumnInfo> newColumnList, StarShuttleParasDO starShuttleParasDO) {
        StringBuilder builder = new StringBuilder();
        String tableName = starShuttleParasDO.getTargetTableName();
        Map<String,McColumnInfo> changeColumnMap = this.getChangedColumnMap(oldColumnList,newColumnList);
        if(changeColumnMap.size()>0){
            builder.append("alter table ").append(tableName).append(" RENAME TO ").append(tableName).append("_drop").append(Math.abs(new Random().nextInt()%100)).append(";\n");
            builder.append(generateCreateDDL(newColumnList,starShuttleParasDO));
            return builder.toString();
        }else{
            return null;
        }
    }

    @Override
    public Map<String, Object> genReaderParas(StarShuttleParasDO parasDO, List<McColumnInfo> sourceColumns) {
        Map<String,Object> loadMap = new HashMap<String, Object>();
        String columnStr = this.genColumnStr(sourceColumns);
        String sql = this.genReaderSql(parasDO,columnStr.concat(",now()"));

        loadMap.put(ReaderParameters.GreenplumReaderParas.connectProps.toString(),parasDO.getSourceDBName());
        loadMap.put(ReaderParameters.GreenplumReaderParas.encoding.toString(),"utf-8");
        loadMap.put(ReaderParameters.GreenplumReaderParas.params.toString(),null);
        loadMap.put(ReaderParameters.GreenplumReaderParas.preCheck.toString(),null);
        loadMap.put(ReaderParameters.GreenplumReaderParas.sql.toString(), sql);
        loadMap.put(ReaderParameters.GreenplumReaderParas.tableName.toString(),null);
        loadMap.put(ReaderParameters.GreenplumReaderParas.where.toString(),null);
        loadMap.put(ReaderParameters.GreenplumReaderParas.columns.toString(),null);
        loadMap.put(ReaderParameters.GreenplumReaderParas.concurrency.toString(),"1");
        loadMap.put(ReaderParameters.GreenplumReaderParas.needSplit.toString(),"false");
        loadMap.put(ReaderParameters.GreenplumReaderParas.partitionName.toString(),null);
        loadMap.put(ReaderParameters.GreenplumReaderParas.getPartitionValue.toString(),null);
        loadMap.put(ReaderParameters.GreenplumReaderParas.countSql.toString(),null);

        return loadMap;
    }

    @Override
    public List<McColumnInfo> genTargetTableColumnInfo(StarShuttleParasDO para) {
        //paras.getTargetTableType().equals(Const.E)
        List<McColumnInfo> result = new ArrayList<McColumnInfo>();

        List<McColumnInfo> list = this.genSourceTableColumnInfo(para);
        if(list == null || list.size()==0){
            throw new NullPointerException("source table info is null");
        }

        Integer i =1;
        for (McColumnInfo col : list){
            col.setColumn_rn(i++);
            String targetColType = GlobalResources.MAPPING_PROPS.get(para.getSourceDBType().concat("_gp.").concat(col.getColumn_type()));
            col.setColumn_type(targetColType);
            result.add(col);
        }

//        if(para.getTargetTableType().equals(Const.TABLE_TYPE_COMPLETE)){
//            McColumnInfo col1 = new McColumnInfo();
//            col1.setColumn_name(Const.EXTRACT_COL_COMPLETE.dw_add_ts.toString());
//            col1.setColumn_type("timestamp");
//            col1.setColumn_rn(i++);
//            result.add(col1);
//        }else if(para.getTargetTableType().equals(Const.TABLE_TYPE_COMPLETE)){
//            McColumnInfo col1 = new McColumnInfo();
//            col1.setColumn_name(Const.EXTRACT_COL_INCREMENT.dw_add_ts.toString());
//            col1.setColumn_type("timestamp");
//            col1.setColumn_rn(i++);
//
//            //McColumnInfo col2 = new McColumnInfo();
////            col2.setColumn_name(Const.EXTRACT_COL_INCREMENT.hp_statdate.toString());
////            col2.setColumn_type("varchar(30)");
////            col2.setColumn_rn(i++);
//            result.add(col1);
//            //result.add(col2);
//        }
        return result;
    }


    @Override
    public Map<String, Object> genWriterParas(StarShuttleParasDO parasDO,List<McColumnInfo> columns) {
        Map<String,Object> loadMap = new HashMap<String, Object>();
        String columnStr = this.genColumnStr(columns);

        String targetTable = Const.DEFAULT_BI_DATABASE.concat(".").concat(parasDO.getTargetTableName());

        loadMap.put(WriterParameters.GreenplumWriterParas.plugin.toString(),"greenplumwriter");

        if(parasDO.getTargetDBType().equals(Const.DATASOURCE_TYPE_GPANALYSIS)){
            loadMap.put(WriterParameters.GreenplumWriterParas.connectProps.toString(),"gpanalysis_"+Const.DEFAULT_BI_DATABASE);
        }else{
            loadMap.put(WriterParameters.GreenplumWriterParas.connectProps.toString(),parasDO.getTargetDBName()+"_"+Const.DEFAULT_BI_DATABASE);
        }

        String preSql = null;
        String rollbackSql = null;
        if(parasDO.getTargetTableType().equals(Const.TABLE_TYPE_COMPLETE)){
            preSql = "truncate table " + targetTable;
            rollbackSql = "truncate table " + targetTable;
        }else{
            if(parasDO.getDateType().equals("MTD")){
                StringBuilder tmp = new StringBuilder();
                tmp.append("alter table ").append(targetTable).append(" drop partition if exists p").append("${CAL_YYYYMM8_01}").append(";")
                        .append("alter table ").append(targetTable).append(" ADD PARTITION " ).append(" p").append("${CAL_YYYYMM8_01}")
                        .append(" START (' ").append("${CAL_YYYYMM8_01}").append("'::date)")
                        .append(" END ('").append("${CAL_CUR_YYYYMM8_01}").append("'::date)")
                        .append(" WITH (APPENDONLY=true, COMPRESSLEVEL=6, ORIENTATION=column, COMPRESSTYPE=zlib, OIDS=FALSE)");
                preSql = tmp.toString();
                StringBuilder tmp1 = new StringBuilder();
                tmp1.append("alter table ").append(targetTable).append(" drop partition if exists p").append("${CAL_YYYYMM8_01}");
                rollbackSql = tmp1.toString();
            }else{
                String beginStr = parasDO.getDateType()==null? null: GalaxyDateUtils.genCalVariable(parasDO.getDateType(), parasDO.getDateOffset());
                String endStr = DateConst.BATCH_CAL_VARS.CAL_YYYYMMDD.getVariableStr();
                StringBuilder tmp = new StringBuilder();
                tmp.append("alter table ").append(targetTable).append(" drop partition if exists p").append(beginStr.replace("YYYYMMDD","YYYYMMDD8")).append(";")
                        .append("alter table ").append(targetTable).append(" ADD PARTITION " ).append(" p").append(beginStr.replace("YYYYMMDD","YYYYMMDD8"))
                        .append(" START (' ").append(beginStr.replace("YYYYMMDD","YYYYMMDD8")).append("'::date)")
                        .append(" END ('").append(endStr.replace("YYYYMMDD", "YYYYMMDD8")).append("'::date)")
                        .append(" WITH (APPENDONLY=true, COMPRESSLEVEL=6, ORIENTATION=column, COMPRESSTYPE=zlib, OIDS=FALSE)");
                preSql = tmp.toString();

                StringBuilder tmp1 = new StringBuilder();
                tmp1.append("alter table ").append(targetTable).append(" drop partition if exists p")
                        .append(beginStr.replace("YYYYMMDD", "YYYYMMDD8"));
                rollbackSql = tmp1.toString();
            }
        }

        loadMap.put(WriterParameters.GreenplumWriterParas.priority.toString(), "0");
        loadMap.put(WriterParameters.GreenplumWriterParas.encoding.toString(),"utf-8");
        loadMap.put(WriterParameters.GreenplumWriterParas.tableName.toString(),targetTable);
        loadMap.put(WriterParameters.GreenplumWriterParas.columns.toString(),columnStr); 
        loadMap.put(WriterParameters.GreenplumWriterParas.pre.toString(),preSql);
        loadMap.put(WriterParameters.GreenplumWriterParas.post.toString(),null);
        loadMap.put(WriterParameters.GreenplumWriterParas.rollback.toString(),rollbackSql);
        loadMap.put(WriterParameters.GreenplumWriterParas.failedlinesthreshold.toString(),"0");

        return loadMap;
    }

    @Override
    public String genReaderSql(StarShuttleParasDO parasDO, String columnStr) {
        StringBuilder builder = new StringBuilder();
        String beginStr = parasDO.getDateType()==null? null: GalaxyDateUtils.genCalVariable(parasDO.getDateType(), parasDO.getDateOffset());
        String endStr = DateConst.BATCH_CAL_VARS.CAL_YYYYMMDD.getVariableStr();

        builder.append("select ").append(columnStr).append(" from ").append(parasDO.getTargetSchemaName())
                .append(".").append(parasDO.getTargetTableName());
        if(StringUtils.isNotBlank(parasDO.getSourceSplitColumn())){
            builder.append(" where ").append(parasDO.getSourceSplitColumn()).append(">=")
                    .append(beginStr).append(parasDO.getSourceSplitColumn())
                    .append("<").append(endStr);
        }
        return builder.toString();
    }

    @Override
    public Map<String, Object> updateMapParas(StarShuttleParasDO parasDO, List<McColumnInfo> columns, Map<String, Object> mapParas, String type) {
        String columnStr = this.genColumnStr(columns);
        String sql = this.genReaderSql(parasDO,columnStr.concat(",now()"));
        if(type.equals(Const.WORMHOLE_READER_TYPE)){
            mapParas.put(ReaderParameters.GreenplumReaderParas.sql.toString(), sql);
        }else if(type.equals(Const.WORMHOLE_WRITER_TYPE)){
           mapParas.put(WriterParameters.GreenplumWriterParas.columns.toString(),columnStr);
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
        return StringUtils.substring(builder.toString(),0,builder.toString().length()-1);
    }

}
