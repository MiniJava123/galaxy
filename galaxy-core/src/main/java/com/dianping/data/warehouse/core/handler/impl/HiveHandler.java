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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hongdi.tang on 14-2-18.
 */
public class HiveHandler extends AbstractHandler implements Handler {

    private static Logger logger = LoggerFactory.getLogger(HiveHandler.class);

    @Override
    public String generateDropDDL(StarShuttleParasDO parasDO) {
        StringBuilder builder = new StringBuilder();
        builder.append("use bi;");
        String targetTable = parasDO.getTargetTableName();
        builder.append("alter table ").append(targetTable).append(" rename to ")
                .append(targetTable).append("_drop").append(Math.abs(new Random().nextInt()%100)).append(";\n");
        if(parasDO.getTargetTableType().equals(Const.TABLE_TYPE_ZIPPER)){
            targetTable = targetTable.replace("dpods_","load_");
            builder.append("alter table ").append(targetTable).append(" rename to ")
                    .append(targetTable).append("_drop").append(Math.abs(new Random().nextInt()%100)).append(";\n");
        }

        return builder.toString();
    }

    @Override
    public String generateCreateDDL(List<McColumnInfo> columnInfoList, StarShuttleParasDO paras) {
        try{
            String content = null;
            String targetTable = paras.getTargetTableName();
            String partitionName = null;

            // complete
            if(paras.getTargetTableType().equals(Const.TABLE_TYPE_COMPLETE)){
                content = GlobalResources.DDLMAP.get(Const.DDL_TYPE.hive_full.toString().concat(".ddl"));

            }// zipper
            else if(paras.getTargetTableType().equals(Const.TABLE_TYPE_ZIPPER)){
                String content_dpods = null;
                String content_load = null;
                String targetTable_dpods = "dpods_".concat(paras.getSourceTableName());
                String targetTable_load = "load_".concat(paras.getSourceTableName());

                content_dpods = GlobalResources.DDLMAP.get(Const.DDL_TYPE.hive_zipper.toString().concat(".ddl"));
                content_load = GlobalResources.DDLMAP.get(Const.DDL_TYPE.hive_full.toString().concat(".ddl"));

                //gen dpods table ddl
                StringBuilder dpods_builder = new StringBuilder();
                for (McColumnInfo col : columnInfoList){
                    dpods_builder.append("`").append(col.getColumn_name().trim()).append("`").append(" ")
                            .append(col.getColumn_type()).append(",").append("\n");
                }
                String dpods_columnInfo = StringUtils.substring(dpods_builder.toString(), 0, dpods_builder.toString().length() - 2);
                content_dpods = StringUtils.replaceEach(content_dpods,
                        new String[]{"${tableName}", "${columnInfo}", "${YYYYMMDD}", "${partitionColumn}"},
                        new String[]{targetTable_dpods, dpods_columnInfo, new SimpleDateFormat("yyyyMMdd").format(new Date())
                                , paras.getTargetPartitionName()});

                //gen load table ddl
                List<McColumnInfo> load_columnInfo_list = new ArrayList<McColumnInfo>();
                List<McColumnInfo> toRemoveList = new ArrayList<McColumnInfo>();

                load_columnInfo_list.addAll(columnInfoList);
                Const.EXTRACT_COL_ZIPPER[] array = Const.EXTRACT_COL_ZIPPER.values();
                for (int i=0;i<array.length;i++) {
                    for (McColumnInfo mcColumnInfo:load_columnInfo_list) {
                        if (mcColumnInfo.getColumn_name().equalsIgnoreCase(array[i].toString())) {
                            toRemoveList.add(mcColumnInfo);
                        }
                    }
                }
                load_columnInfo_list.removeAll(toRemoveList);

                StringBuilder load_builder = new StringBuilder();
                for (McColumnInfo col : load_columnInfo_list){
                    load_builder.append("`").append(col.getColumn_name().trim().toLowerCase()).append("`").append(" ").append(col.getColumn_type()).append(",").append("\n");
                }
                String load_columnInfo = StringUtils.substring(load_builder.toString(), 0, load_builder.toString().length() - 2);
                content_load = StringUtils.replaceEach(content_load,
                        new String[]{"${tableName}", "${columnInfo}", "${YYYYMMDD}", "${partitionColumn}"},
                        new String[]{targetTable_load, load_columnInfo, new SimpleDateFormat("yyyyMMdd").format(new Date())
                                , paras.getTargetPartitionName()});

                return content_load+";"+content_dpods;
            }//increment snapshot
            else if(paras.getTargetTableType().equals(Const.TABLE_TYPE_INCREMENT)||
                    paras.getTargetTableType().equals(Const.TABLE_TYPE_SNAPSHOT)){
                content = GlobalResources.DDLMAP.get(Const.DDL_TYPE.hive_snapshot.toString().concat(".ddl"));
                if(paras.getTargetTableName().startsWith(Const.DEFUALT_SCHEMA_DPDIM)){
                    partitionName = Const.DEFUALT_DPDIM_PARTITION_NAME;
                }else{
                    partitionName = Const.DEFUALT_HIVE_PARTITION_NAME;
                }
            } else {
                throw new IllegalArgumentException("illegal hive table type");
            }
            StringBuilder builder = new StringBuilder();
            for (McColumnInfo col : columnInfoList){
                builder.append("`").append(col.getColumn_name().trim().toLowerCase()).append("`").
                        append(" ").append(col.getColumn_type()).append(",").append("\n");
            }
            String columnInfo = StringUtils.substring(builder.toString(), 0, builder.toString().length() - 2);

            return StringUtils.replaceEach(content,
                    new String[]{"${tableName}", "${columnInfo}", "${YYYYMMDD}", "${partitionColumn}"},
                    new String[]{targetTable, columnInfo, new SimpleDateFormat("yyyyMMdd").format(new Date())
                            , partitionName});
        }catch(Exception e){
            logger.error("generate hive ddl failure",e);
            throw new RuntimeException("generate hive ddl failure",e);
        }
    }

    @Override
    public String generateAlterDDL(List<McColumnInfo> oldColumnList, List<McColumnInfo> newColumnList, StarShuttleParasDO starShuttleParasDO) {
        String tableName = starShuttleParasDO.getTargetTableName();
        String loadTableName ="load_" + starShuttleParasDO.getSourceTableName();
        String targetTableType = starShuttleParasDO.getTargetTableType();
        String ddl ="";
        List<McColumnInfo> removeList = new ArrayList<McColumnInfo>();
        List<McColumnInfo> parColumnList = new ArrayList<McColumnInfo>();

        Comparator comparator = new Comparator(){
            public int compare(Object o1, Object o2) {
                return ((McColumnInfo) o1).getColumn_rn().compareTo(((McColumnInfo) o2).getColumn_rn())  ;
            }
        };
        if (targetTableType.equals(Const.TABLE_TYPE_ZIPPER)){
              Collections.sort(oldColumnList,comparator);
            for(McColumnInfo column : oldColumnList){
                Const.EXTRACT_COL_ZIPPER[] tmp = Const.EXTRACT_COL_ZIPPER.values();
                for(Const.EXTRACT_COL_ZIPPER col : tmp){
                    if(column.getColumn_name().equals(col.toString())){
                        removeList.add(column);
                        break;
                    }
                }
                column.setColumn_rn(column.getColumn_rn()-removeList.size());
                if(column.getIs_partition_column().equals(1)){
                    parColumnList.add(column);
                }
            }
            oldColumnList.removeAll(removeList);
            oldColumnList.removeAll(parColumnList);
            removeList.clear();
            for(McColumnInfo column : newColumnList){
                Const.EXTRACT_COL_ZIPPER[] tmp = Const.EXTRACT_COL_ZIPPER.values();
                for(Const.EXTRACT_COL_ZIPPER col : tmp){
                    if(column.getColumn_name().equals(col.toString())){
                        removeList.add(column);
                    }
                }
            }
            newColumnList.removeAll(removeList);
            removeList.clear();
            Map<String,McColumnInfo> changeColumnMap = this.getChangedColumnMap(oldColumnList,newColumnList);
            String loadDdl = getAlterTableDDL(changeColumnMap, loadTableName);
            ddl = getAlterTableDDL(changeColumnMap,tableName);
            ddl = ddl + loadDdl;
        }else{
            Map<String,McColumnInfo> changeColumnMap = this.getChangedColumnMap(oldColumnList,newColumnList);
            ddl = getAlterTableDDL(changeColumnMap, tableName);
        }
        return ddl.length() == 0? ddl : "use bi;\n" + ddl;
    }

    @Override
    public List<McColumnInfo> genTargetTableColumnInfo(StarShuttleParasDO para) {
        //paras.getTargetTableType().equals(Const.E)
        List<McColumnInfo> result = new ArrayList<McColumnInfo>();
        List<McColumnInfo> list = this.genSourceTableColumnInfo(para);
        if(list == null || list.size()==0){
            throw new NullPointerException("source table column info is null");
        }
        Pattern patternType = Pattern.compile("\\(\\d{1,}\\)");
        Integer j =1;
        for (McColumnInfo col : list){
            Matcher m = patternType.matcher(col.getColumn_type());
            if(m.find()){
                col.setColumn_type(m.replaceAll(""));
            }
            col.setColumn_rn(j++);
            String targetColType = GlobalResources.MAPPING_PROPS.get(para.getSourceDBType().concat("_hive.").concat(col.getColumn_type()));
            col.setColumn_type(targetColType == null ? "string" : targetColType);
            result.add(col);
        }

        if(para.getTargetTableType().equals(Const.TABLE_TYPE_COMPLETE)){
            Const.EXTRACT_COL_COMPLETE[] array = Const.EXTRACT_COL_COMPLETE.values();
            for(int i=0;i<array.length;i++){
                McColumnInfo col = new McColumnInfo();
                col.setColumn_name(array[i].toString());
                col.setColumn_type("string");
                col.setColumn_rn(j++);
                result.add(col);
            }
        }else if(para.getTargetTableType().equals(Const.TABLE_TYPE_SNAPSHOT) ||
                para.getTargetTableType().equals(Const.TABLE_TYPE_INCREMENT)){
            Const.EXTRACT_COL_INCREMENT[] array = Const.EXTRACT_COL_INCREMENT.values();
            for(int i=0;i<array.length;i++){
                McColumnInfo col = new McColumnInfo();
                col.setColumn_name(array[i].toString());
                col.setColumn_type("string");
                col.setColumn_rn(j++);
                result.add(col);
            }
        } else if(para.getTargetTableType().equals(Const.TABLE_TYPE_ZIPPER)){
            Const.EXTRACT_COL_ZIPPER[] array = Const.EXTRACT_COL_ZIPPER.values();
            McColumnInfo addTs = new McColumnInfo();
            addTs.setColumn_name("dw_add_ts");
            addTs.setColumn_type("string");
            addTs.setColumn_rn(j++);
            result.add(addTs);

            for(int i=0;i<array.length;i++){
                McColumnInfo col = new McColumnInfo();
                col.setColumn_name(array[i].toString());
                col.setColumn_type("string");
                col.setColumn_rn(j++);
                result.add(col);
            }
        }
        return result;
    }


    @Override
    public Map<String, Object> genReaderParas(StarShuttleParasDO parasDO, List<McColumnInfo> sourceColumns) {
        Map<String,Object> loadMap = new HashMap<String, Object>();
        String columnStr = this.genColumnStr(sourceColumns);
        String sql = this.genReaderSql(parasDO, columnStr);

        loadMap.put(ReaderParameters.HiveReaderParas.plugin.toString(),"hivereader");
        loadMap.put(ReaderParameters.HiveReaderParas.path.toString(),GlobalResources.HIVE_PROPS.get("hive_path")+parasDO.getSourceDBName());
        loadMap.put(ReaderParameters.HiveReaderParas.sql.toString(),sql);
        loadMap.put(ReaderParameters.HiveReaderParas.mode.toString(),"READ_FROM_HDFS");
        loadMap.put(ReaderParameters.HiveReaderParas.dataDir.toString(),GlobalResources.HIVE_PROPS.get("hive_dataDir"));
        loadMap.put(ReaderParameters.HiveReaderParas.reduceNumber.toString(),"-1");
        loadMap.put(ReaderParameters.HiveReaderParas.concurrency.toString(),"10");

        return loadMap;

    }

    @Override
    public Map<String, Object> genWriterParas(StarShuttleParasDO parasDO, List<McColumnInfo> columns) {
        Map<String,Object> loadMap = new HashMap<String, Object>();

        //String targetTable = parasDO.getTargetDBName().concat("_").concat(parasDO.getTargetTableName());
        String targetTable = parasDO.getTargetTableName();
        String partitionColumnName = null;
        if(targetTable.startsWith(Const.DEFUALT_SCHEMA_DPDIM)){
           partitionColumnName = Const.DEFUALT_DPDIM_PARTITION_NAME;
        }else{
           partitionColumnName = Const.DEFUALT_HIVE_PARTITION_NAME;
        }

        String partitionName = "";
        String addPartition = "false";
        String addPartitionCond = null;
        if (parasDO.getTargetTableType().equals(Const.TABLE_TYPE_INCREMENT)){
            if(parasDO.getDateType().equals(Const.OFFSET_TYPE_MTD)){
                partitionName = partitionColumnName + "=" + DateConst.BATCH_CAL_VARS.CAL_YYYYMM_01.getVariableStr() ;
                addPartition = "true";
                addPartitionCond = partitionColumnName+ "='" +DateConst.BATCH_CAL_VARS.CAL_YYYYMM_01.getVariableStr() +"'@bi."+targetTable;
            }else{
                String beginStr = parasDO.getDateType()==null? null: GalaxyDateUtils.genCalVariable(parasDO.getDateType(), parasDO.getDateOffset());
                partitionName = partitionColumnName + "=" + beginStr ;
                addPartition = "true";
                addPartitionCond = partitionColumnName+ "='"+beginStr +"'@bi."+targetTable;
            }

        }else if(parasDO.getTargetTableType().equals(Const.TABLE_TYPE_SNAPSHOT)){
            String beginStr = "${CAL_YYYYMMDD_P1D}";
            partitionName = partitionColumnName + "=" + beginStr ;
            addPartition = "true";
            addPartitionCond = partitionColumnName+ "='"+beginStr +"'@bi."+targetTable;
        }


        loadMap.put(WriterParameters.HdfsWriterParas.plugin.toString(),"hdfswriter");
        String tmpTable = targetTable.toLowerCase();
        if(parasDO.getTargetTableType().equals(Const.TABLE_TYPE_ZIPPER)){
            tmpTable = "load_" + parasDO.getSourceTableName().toLowerCase();
        }
        loadMap.put(WriterParameters.HdfsWriterParas.dir.toString(),GlobalResources.HIVE_PROPS.get("hdfs_dir_var") + "/" + tmpTable + "/" + partitionName);
        loadMap.put(WriterParameters.HdfsWriterParas.prefix_filename.toString(),tmpTable);
        loadMap.put(WriterParameters.HdfsWriterParas.field_split.toString(),"\\005");
        loadMap.put(WriterParameters.HdfsWriterParas.line_split.toString(),"\\n");
        loadMap.put(WriterParameters.HdfsWriterParas.encoding.toString(),"UTF-8");
        loadMap.put(WriterParameters.HdfsWriterParas.nullchar.toString(),null);
        loadMap.put(WriterParameters.HdfsWriterParas.replace_char.toString(),null);
        loadMap.put(WriterParameters.HdfsWriterParas.codec_class.toString(),"com.hadoop.compression.lzo.LzopCodec");
        loadMap.put(WriterParameters.HdfsWriterParas.buffer_size.toString(),"4096");
        loadMap.put(WriterParameters.HdfsWriterParas.file_type.toString(),"TXT_COMP");
        loadMap.put(WriterParameters.HdfsWriterParas.concurrency.toString(),"5");
        loadMap.put(WriterParameters.HdfsWriterParas.hive_table_add_partition_switch.toString(),addPartition);
        loadMap.put(WriterParameters.HdfsWriterParas.hive_table_add_partition_condition.toString(),addPartitionCond );
        loadMap.put(WriterParameters.HdfsWriterParas.dataTransformClass.toString(),null);
        loadMap.put(WriterParameters.HdfsWriterParas.dataTransformParams.toString(),null);
        loadMap.put(WriterParameters.HdfsWriterParas.createLzoIndexFile.toString(),null);

        return loadMap;
    }

    @Override
    public String genReaderSql(StarShuttleParasDO parasDO, String columnStr) {
        StringBuilder builder = new StringBuilder();
        builder.append("select ").append(columnStr).append(" from ").append(parasDO.getSourceDBName())
                .append(".").append(parasDO.getTargetTableName());

        String beginStr = null;
        String endStr = null;

        if(parasDO.getTargetTableType().equals(Const.TABLE_TYPE_INCREMENT)){
            if(StringUtils.equals(parasDO.getDateType(),Const.OFFSET_TYPE_MTD)){
//                beginStr = "concat(substring(date_add('"+DateConst.BATCH_CAL_VARS.CAL_YYYYMMDD_P1D.getVariableStr()
//                        +"',-1),1,7),'-01')";
                beginStr = "'"+DateConst.BATCH_CAL_VARS.CAL_YYYYMM_01.getVariableStr()+"'";
            }else{
                beginStr = "'"+GalaxyDateUtils.genCalVariable(parasDO.getDateType(), parasDO.getDateOffset())+"'";
            }
            endStr = "'"+DateConst.BATCH_CAL_VARS.CAL_YYYYMMDD.getVariableStr()+"'";
            builder.append(" where ").append(parasDO.getTargetPartitionName()).append(">=")
                    .append(beginStr).append(" and ").append(parasDO.getTargetPartitionName()).append("<")
                    .append(endStr);
        }
        return builder.toString();
    }

    @Override
    public Map<String, Object> updateMapParas(StarShuttleParasDO parasDO, List<McColumnInfo> columns, Map<String, Object> mapParas, String type) {
        String columnStr = this.genColumnStr(columns);
        //String sql = this.genReaderSql(parasDO,columnStr.concat(",now()"));
        String sql = this.genReaderSql(parasDO,columnStr);
        if(type.equals(Const.WORMHOLE_READER_TYPE)){
            mapParas.put(ReaderParameters.HiveReaderParas.sql.toString(), sql);
        }
        return  mapParas;
    }

    @Override
    public String genColumnStr(List<McColumnInfo> columns) {
        StringBuilder builder = new StringBuilder();
        for(McColumnInfo column : columns){
            builder.append("`").append(column.getColumn_name().toLowerCase())
                    .append("`").append(",");
        }
        return StringUtils.substring(builder.toString(),0,builder.toString().length()-1);
    }

//    private String getAlterTableDDL(Map<String, McColumnInfo> changeColumnMap, String tableName){
//        StringBuilder builder = new StringBuilder();
//        StringBuilder tmpBuilder = new StringBuilder();
//        if(changeColumnMap.size()>0){
//            List<Integer> addRnList = new ArrayList<Integer>();
//            for(Map.Entry entry : changeColumnMap.entrySet() ){
//                McColumnInfo column = (McColumnInfo) entry.getValue();
//                if(!entry.getKey().equals("#add "+column.getColumn_rn())){
//                    tmpBuilder.append("alter table ").append(tableName).append(" change ").append(entry.getKey()).append(" ").append(column.getColumn_name()).append("_tmp ").append(column.getColumn_type()).append(";\n");
//                    builder.append("alter table ").append(tableName).append(" change ").append(column.getColumn_name()).append("_tmp ").append(column.getColumn_name()).append(" ").append(column.getColumn_type()).append(";\n");
//                }else{
//                    addRnList.add(column.getColumn_rn());
//                }
//            }
//            if(addRnList.size() > 0){
//                Collections.sort(addRnList);
//                int rn1 = addRnList.get(0);
//                McColumnInfo column1 = changeColumnMap.get("#add "+rn1);
//                builder.append("alter table ").append(tableName).append(" change ").append(Const.EXTRACT_COL_COMPLETE.dw_add_ts.toString()).append(" ").append(column1.getColumn_name()).append(" ").append(column1.getColumn_type()).append(";\n");
//                addRnList.remove(0);
//                if(addRnList.size()>0){
//                    for(Integer rn : addRnList){
//                        McColumnInfo column = changeColumnMap.get("#add "+rn);
//                        builder.append("alter table ").append(tableName).append(" ADD COLUMNS (").append(column.getColumn_name()).append(" ").append(column.getColumn_type()).append(");\n");
//                    }
//                }
//                builder.append("alter table ").append(tableName).append(" ADD COLUMNS (").append(Const.EXTRACT_COL_COMPLETE.dw_add_ts.toString()).append(" ").append("string").append(");\n");
//            }
//        }
//        String ddl = tmpBuilder.toString() + builder.toString();
//        return ddl;
//    }

    private String getAlterTableDDL(Map<String, McColumnInfo> changeColumnMap, String tableName){
        StringBuilder builder = new StringBuilder();
        StringBuilder tmpBuilder = new StringBuilder();
        if(changeColumnMap.size()>0){
            List<Integer> addRnList = new ArrayList<Integer>();
            for(Map.Entry entry : changeColumnMap.entrySet() ){
                McColumnInfo column = (McColumnInfo) entry.getValue();
                if(!entry.getKey().equals("#add "+column.getColumn_rn())){
                    tmpBuilder.append("alter table ").append(tableName).append(" change ").append(entry.getKey()).append(" ").append(column.getColumn_name()).append("_tmp ").append(column.getColumn_type()).append(";\n");
                    builder.append("alter table ").append(tableName).append(" change ").append(column.getColumn_name()).append("_tmp ").append(column.getColumn_name()).append(" ").append(column.getColumn_type()).append(";\n");
                }else{
                    addRnList.add(column.getColumn_rn());
                }
            }
            if(addRnList.size() > 0){
                Collections.sort(addRnList);
                    for(Integer rn : addRnList){
                        McColumnInfo column = changeColumnMap.get("#add "+rn);
                        builder.append("alter table ").append(tableName).append(" ADD COLUMNS (").append(column.getColumn_name()).append(" ").append(column.getColumn_type()).append(");\n");
                    }
               }
        }
        String ddl = tmpBuilder.toString() + builder.toString();
        return ddl;
    }
}