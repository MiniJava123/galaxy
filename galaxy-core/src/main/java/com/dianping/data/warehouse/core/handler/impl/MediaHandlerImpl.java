package com.dianping.data.warehouse.core.handler.impl;

import com.dianping.data.warehouse.core.common.Const;
import com.dianping.data.warehouse.core.common.GlobalResources;
import com.dianping.data.warehouse.core.handler.MediaHandler;
import com.dianping.data.warehouse.core.model.DSInfo;
import com.dianping.data.warehouse.core.utils.HiveSecurityVerify;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by hongdi.tang on 14-3-3.
 */
@Service
public class MediaHandlerImpl implements MediaHandler{
    private static Logger logger = LoggerFactory.getLogger(MediaHandlerImpl.class);

    private Connection initConnection(DSInfo dsInfo){
        Connection ret = null;
        try {
            Class.forName(dsInfo.getDriver());
            logger.info("get connection begin");
            ret = DriverManager.getConnection(dsInfo.getUrl(), dsInfo.getUsername(), dsInfo.getPassword());
            logger.info("get connection end");
        } catch (Exception e1) {
            logger.error(dsInfo.getUrl()+" "+
                    dsInfo.getUsername()+" "+
                    dsInfo.getPassword(),e1);
            throw new RuntimeException(
                    "JDBC Driver Manager cannot get connection, url ["
                            + dsInfo.getUrl() + "], username [" + dsInfo.getUsername()
                            + "], password [ ... ] " , e1);
        }
        return ret;
    }

    private String generateKey(String dbType,String dbName){
        StringBuilder builder = new StringBuilder(dbType.toLowerCase()).append(".");
        String instanceName = null;
        if (dbType.equalsIgnoreCase(Const.DATASOURCE_TYPE_GPREPORT)||
                dbType.equalsIgnoreCase(Const.DATASOURCE_TYPE_GPOLAP)){
            instanceName = dbName;
        } else {
            instanceName = "NULL";
        }
        String dsName = null;
        if (dbType.equalsIgnoreCase(Const.DATASOURCE_TYPE_MYSQL)){
            dsName = dbName;
        } else {
            dsName = Const.DEFAULT_BI_DATABASE;
        }
        return builder.append(instanceName).append(".").append(dsName).toString();
    }

    /********
     * @param dbType
     * @param dbName
     * @return
     */
    public Connection getConnection(String dbType,String dbName,String schemaName) {
        logger.info("dbname:="+dbName +" "+"dbType:="+dbType);
        String key = this.generateKey(dbType,dbName);
        logger.info("key:="+key);
        //DSInfo dsInfo = GlobalResources.DATASOURCE_MAP.get(key);
        DSInfo dsInfo = GlobalResources.getDsInfoFromLion(key);
        if(Const.DATASOURCE_TYPE_HIVE.equals(dbType)){
            HiveSecurityVerify.getInstance().verify();
            logger.info("verify success");
            return this.initConnection(dsInfo);
        }else{
            return this.initConnection(dsInfo);
        }
    }

    public boolean executeDDL(String dbType,String dbName, String schemaname,String ddl){
        if (StringUtils.isBlank(ddl) || dbType.equals(Const.DATASOURCE_TYPE_MYSQL)) {
            logger.info(",source Table has not changed,ddl is null,or target dbType is mysql");
            return  true;
        }
        logger.info("execute sql start");
        Connection conn = this.getConnection(dbType,dbName,schemaname);
        Statement stmt = null;
        String[] sqls = ddl.split(";");
        boolean flag = true;
        try {
            stmt = conn.createStatement();
            for (String sql : sqls) {
                if (!StringUtils.isBlank(sql) && !"\n".equals(sql.trim())) {
                    logger.info(sql);
                    stmt.execute(sql);
                }
            }
            flag = true;
        } catch (SQLException e) {
            flag = false;
            logger.error(e.getMessage(), e);
//            for (String sql : sqls) {
//                logger.error(sql);
//            }
            throw new RuntimeException("create table fail, sqls [ "
                    + ddl + " ] " + e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    flag = false;
                    logger.error(e.getMessage(), e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    flag = false;
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return flag;
    }

}
