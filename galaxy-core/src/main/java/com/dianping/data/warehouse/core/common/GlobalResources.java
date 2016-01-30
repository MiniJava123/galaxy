package com.dianping.data.warehouse.core.common;

import com.dianping.data.warehouse.core.model.DSInfo;
import com.dianping.lion.EnvZooKeeperConfig;
import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.LionException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * Created by hongdi.tang on 14-2-18.
 */
public class GlobalResources {

    private static Logger logger = LoggerFactory.getLogger(GlobalResources.class);

    private static final String MAPPING_CFG = "config/mapping.properties";
    private static final String TEMPLATE_DIR = "template";
    public static final String TOKEN = "xiiqwpbtanvgcnafmnfw";

    public static final String RESOURCE_LOCATION = GlobalResources.class.getClassLoader().getResource("config").getPath();
    public static Map<String, String> MAPPING_PROPS = new HashMap<String, String>();
    public static Map<String, String> HIVE_PROPS = new HashMap<String, String>();
    public static Map<String, String> DDLMAP = new HashMap<String, String>();
    private static Map<String, DSInfo> DATASOURCE_MAP = new HashMap<String, DSInfo>();
    public static Map<String, List<String>> DBMAPPING_MAP = new HashMap<String, List<String>>();
    public static Map<String, List<String>> DBTypeMAPPING_MAP = new HashMap<String, List<String>>();
    public static Map<String, List<String>> SourceDBTypeMAPPING_MAP = new HashMap<String, List<String>>();
    public static Configuration configuration = new Configuration();
    private static ConfigCache configCache;
    private static String starshuttle_cfg_str;
    public static String ACL_URL;
    public static String ACL_AK_ID;


    static {
        Properties properties1 = new Properties();

        try {
            configCache = ConfigCache.getInstance(EnvZooKeeperConfig.getZKAddress());

            properties1.load(GlobalResources.class.getClassLoader().getResourceAsStream(MAPPING_CFG));
            for (Map.Entry<Object, Object> entry : properties1.entrySet()) {
                MAPPING_PROPS.put((String) entry.getKey(), (String) entry.getValue());
            }

            HIVE_PROPS.put(Const.HIVE_PATH,
                    configCache.getProperty(Const.PROJECTNAME + "." + Const.HIVE_PATH));
            HIVE_PROPS.put(Const.HIVE_DATADIR,
                    configCache.getProperty(Const.PROJECTNAME + "." + Const.HIVE_DATADIR));
            HIVE_PROPS.put(Const.HDFS_DIR_VAR,
                    configCache.getProperty(Const.PROJECTNAME + "." + Const.HDFS_DIR_VAR));


            Collection<File> files = FileUtils.listFiles(new File(RESOURCE_LOCATION.concat(File.separator).concat(TEMPLATE_DIR)), null, true);
            for (File file : files) {
                String content = FileUtils.readFileToString(file, "utf-8");
                DDLMAP.put(file.getName(), content);
            }

            starshuttle_cfg_str = configCache.getProperty(Const.PROJECTNAME + "." + Const.STARSHUTTLE_CFG);
            List<String> starshuttle_cfg_list = Arrays.asList(starshuttle_cfg_str.split(";"));
            for (String lion_key : starshuttle_cfg_list) {
                String tmp = StringUtils.substringAfterLast(lion_key, "galaxy.");
                String key = StringUtils.substringBeforeLast(tmp, ".");
                String property = StringUtils.substringAfterLast(tmp, ".");
                if (!DATASOURCE_MAP.containsKey(key)) {
                    DSInfo db = new DSInfo();
                    db.setProperty(property, configCache.getProperty(lion_key));
                    DATASOURCE_MAP.put(key, db);
                } else {
                    DSInfo db = DATASOURCE_MAP.get(key);
                    db.setProperty(property, configCache.getProperty(lion_key));
                }
            }

            String dbmapping_cfg_str = configCache.getProperty(Const.PROJECTNAME + "." + Const.DBMAPPING_CFG);
            List<String> dbmapping_cfg_list = Arrays.asList(dbmapping_cfg_str.split(";"));
            for (String lion_key : dbmapping_cfg_list) {
                String key = StringUtils.substringAfterLast(lion_key, "galaxy.");
                List<String> list = Arrays.asList(configCache.getProperty(lion_key).split(";"));
                DBMAPPING_MAP.put(key, list);
            }

            String dbTypeMapping_cfg_str = configCache.getProperty(Const.PROJECTNAME + "." + Const.DB_TYPE_MAPPING);
            List<String> dbTypeMapping_cfg_list = Arrays.asList(dbTypeMapping_cfg_str.split(";"));
            for (String lion_key : dbTypeMapping_cfg_list) {
                String key = StringUtils.substringBetween(lion_key, "galaxy.", "_type");
                List<String> list = Arrays.asList(configCache.getProperty(lion_key).split(";"));
                DBTypeMAPPING_MAP.put(key, list);
            }

            String sourceDBTypes_cfg_str = configCache.getProperty(Const.PROJECTNAME + "." + Const.SOURCE_DB_TYPES);
            String key = Const.SOURCE_DB_TYPES;
            List<String> list = Arrays.asList(sourceDBTypes_cfg_str.split(";"));
            SourceDBTypeMAPPING_MAP.put(key, list);

            //set hive conf props
            String hive_cfg_str = configCache.getProperty(Const.PROJECTNAME + "." + Const.HIVE_CONNECTION_CFG);
            List<String> hive_cfg_list = Arrays.asList(hive_cfg_str.split(";"));
            for (String lion_key : hive_cfg_list) {
                String hive_key = StringUtils.substringAfterLast(lion_key, "galaxy.");
                String hive_value = configCache.getProperty(lion_key);
//                logger.info(hive_key + ":= " + hive_value);
                configuration.set(hive_key, hive_value);
            }

        } catch (Exception e) {
            logger.error("load mapping file or lion cfg failure", e);
        }
    }

    public static DSInfo getDsInfoFromLion(String dsInfoKey) {
        DSInfo db = DATASOURCE_MAP.get(dsInfoKey);
        if (db == null) {
            try {
                db = new DSInfo();
                starshuttle_cfg_str = configCache.getProperty(Const.PROJECTNAME + "." + Const.STARSHUTTLE_CFG);
                List<String> starshuttle_cfg_list = Arrays.asList(starshuttle_cfg_str.split(";"));
                for (String lion_key : starshuttle_cfg_list) {
                    if (lion_key.contains(dsInfoKey)) {
                        String tmp = StringUtils.substringAfterLast(lion_key, Const.PROJECTNAME.concat("."));
                        String property = StringUtils.substringAfterLast(tmp, ".");
                        db.setProperty(property, configCache.getProperty(lion_key));
                    }
                }
                System.out.println(dsInfoKey);
                DATASOURCE_MAP.put(dsInfoKey, db);

            } catch (LionException e) {
                logger.error("get dsinfo key :" + dsInfoKey + " error", e);
                return null;
            }
        }
        return db;
    }

}
