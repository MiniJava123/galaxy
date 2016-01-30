package com.dianping.data.warehouse.core.handler.impl;

import com.dianping.data.warehouse.core.handler.AbstractHandler;
import com.dianping.data.warehouse.core.handler.Handler;
import com.dianping.data.warehouse.domain.McColumnInfo;
import com.dianping.data.warehouse.domain.web.StarShuttleParasDO;

import java.util.List;
import java.util.Map;

/**
 * Created by hongdi.tang on 14-2-18.
 */
public class MongoHandler extends AbstractHandler implements Handler {


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
    public Map<String, Object> genReaderParas(StarShuttleParasDO parasDO, List<McColumnInfo> targetColumns) {
        return null;
    }

    @Override
    public Map<String, Object> genWriterParas(StarShuttleParasDO parasDO, List<McColumnInfo> columns) {
        return null;
    }

    @Override
    public String genReaderSql(StarShuttleParasDO parasDO, String columnStr) {
        return null;
    }

    @Override
    public Map<String, Object> updateMapParas(StarShuttleParasDO parasDO, List<McColumnInfo> columns, Map<String, Object> mapParas, String type) {
        return null;
    }

    @Override
    public String genColumnStr(List<McColumnInfo> columns) {
        return null;
    }

}
