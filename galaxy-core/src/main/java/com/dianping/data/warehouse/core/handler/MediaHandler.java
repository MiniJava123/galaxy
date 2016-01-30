package com.dianping.data.warehouse.core.handler;

/**
 * Created by hongdi.tang on 14-3-3.
 */
public interface MediaHandler {
    boolean executeDDL(String dbType,String dbName,String schemaName,String ddl);
}
