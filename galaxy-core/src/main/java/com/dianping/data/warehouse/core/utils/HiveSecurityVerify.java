package com.dianping.data.warehouse.core.utils;

import com.dianping.data.warehouse.core.common.Const;
import com.dianping.data.warehouse.core.common.GlobalResources;
import com.dianping.lion.EnvZooKeeperConfig;
import com.dianping.lion.client.ConfigCache;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hongdi.tang on 14-3-14.
 */
@Component("hiveSecurityVerify")
public class HiveSecurityVerify {
    private static Configuration conf;

    private static Logger logger = Logger.getLogger(HiveSecurityVerify.class);
    private static HiveSecurityVerify securityLogin = null;

    private HiveSecurityVerify() {

    }

    public static HiveSecurityVerify getInstance(){
        if(HiveSecurityVerify.securityLogin == null){
            return securityLogin = new HiveSecurityVerify();
        }else {
            return securityLogin;
        }
    }

    public synchronized void verify(){
        conf = new Configuration();
        try{
            ConfigCache configCache = ConfigCache.getInstance(EnvZooKeeperConfig.getZKAddress());
            String hive_cfg_str = configCache.getProperty(Const.PROJECTNAME+"."+Const.HIVE_CONNECTION_CFG);
            List<String> hive_cfg_list = Arrays.asList(hive_cfg_str.split(";"));
            for (String lion_key:hive_cfg_list) {
                String hive_key = StringUtils.substringAfterLast(lion_key, "galaxy.");
                String hive_value = configCache.getProperty(lion_key);
                logger.info(hive_key + ":= " + hive_value);
                conf.set(hive_key,hive_value);
            }
        }catch(Exception e){
            logger.error("get pigeon conf error",e);
            conf = GlobalResources.configuration;
        }
    // Kerberos Authentication
        UserGroupInformation.setConfiguration(conf);
        try {
            SecurityUtil.login(conf, "test.hadoop.keytab.file","test.hadoop.principal");
        }
        catch (IOException e) {
            logger.error("security verify error",e);
        }
    }

}
