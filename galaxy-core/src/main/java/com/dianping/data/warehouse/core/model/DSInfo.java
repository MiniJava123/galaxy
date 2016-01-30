package com.dianping.data.warehouse.core.model;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class DSInfo {
    private static Logger logger = LoggerFactory.getLogger(DSInfo.class);
    private String type;
    private String dbname;

    private String username;
    private String password;
    private String schemaName;
    private String url;
    private String driver;

    public String setProperty(String name, String value) {
        try {
            Method method = DSInfo.class.getDeclaredMethod("set".concat(StringUtils.capitalize(name)),name.getClass());
            return (String) method.invoke(this, value);
        } catch (Exception e) {
            logger.error("property "+name +" or value " +value + " is illegal");
            throw new RuntimeException("no such set method", e);
        }
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
