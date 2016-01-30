package com.dianping.data.warehouse.core.handler;

import com.dianping.data.warehouse.domain.McColumnInfo;
import com.dianping.data.warehouse.domain.web.StarShuttleParasDO;
import com.dianping.data.warehouse.halley.domain.TaskDO;

import java.util.List;
import java.util.Map;

/**
 * Created by hongdi.tang on 14-2-18.
 */
public interface Handler {

    String generateDropDDL(StarShuttleParasDO starShuttleParasDO);

    //生成DDL
    String generateCreateDDL(List<McColumnInfo> columnInfoList, StarShuttleParasDO starShuttleParasDO);

    String generateAlterDDL(List<McColumnInfo> oldColumnList, List<McColumnInfo> newColumnList, StarShuttleParasDO starShuttleParasDO);
    //建表
    boolean createTable(String dbType,String dbName, String ddl);

    //调用调度接口
    TaskDO getDefaultTask(StarShuttleParasDO starShuttleParasDO);

    //调用主数据接口
    List<McColumnInfo> genTargetTableColumnInfo( StarShuttleParasDO para);

    List<McColumnInfo> genSourceTableColumnInfo(StarShuttleParasDO para);

    Map<String, Object> genReaderParas(StarShuttleParasDO parasDO, List<McColumnInfo> targetColumns);

    Map<String, Object> genWriterParas(StarShuttleParasDO parasDO, List<McColumnInfo> columns);

    String genReaderSql(StarShuttleParasDO parasDO,String columnStr);

    Map<String, Object> updateMapParas(StarShuttleParasDO parasDO, List<McColumnInfo> columns, Map<String, Object> mapParas, String type);

    public String genColumnStr(List<McColumnInfo> columns );

}