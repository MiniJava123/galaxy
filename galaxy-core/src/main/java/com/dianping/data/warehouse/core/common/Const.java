package com.dianping.data.warehouse.core.common;

/**
 * Created by hongdi.tang on 14-2-17.
 */
public class Const {

    public static final String DEFAULT_BI_DATABASE = "bi";

    public static final String CONNECT_PROPS = "connectProps";

    public static final String DATASOURCE_TYPE_MYSQL = "mysql",
            DATASOURCE_TYPE_GP = "greenplum",
            DATASOURCE_TYPE_HIVE = "hive",
            DATASOURCE_TYPE_HIVEMETA = "hivemeta",
            DATASOURCE_TYPE_SQLSERVER = "sqlserver",
            DATASOURCE_TYPE_SALESFORCE = "salesforce",
            DATASOURCE_TYPE_POSTGRESQL = "postgresql",
            DATASOURCE_TYPE_GPREPORT = "gp_report",
            DATASOURCE_TYPE_GPANALYSIS = "gp_analysis",
            DATASOURCE_TYPE_GPOLAP = "gp_olap";

    public static final String DEFUALT_SCHEMA_DPODS = "dpods",
            DEFUALT_SCHEMA_DPDIM = "dpdim";
    public static final String DEFUALT_HIVE_PARTITION_NAME = "hp_statdate",
            DEFUALT_DPDIM_PARTITION_NAME = "hp_cal_dt",
            DEFUALT_ZIPPER_PARTITION_NAME = "hp_valid_end_dt";

    public static final String TABLE_TYPE_ZIPPER = "zipper",
            TABLE_TYPE_SNAPSHOT = "snapshot",
            TABLE_TYPE_COMPLETE = "full",
            TABLE_TYPE_INCREMENT = "append";

    public static final String WORMHOLE_READER_TYPE = "reader",
            WORMHOLE_WRITER_TYPE = "writer";

    public static final String TABLE_VALIDATE = "Y",
            TABLE_INVALIDATE = "N";

    public static final String OFFSET_TYPE_MONTH = "M",
            OFFSET_TYPE_DAY = "D",
            OFFSET_TYPE_MTD = "MTD";

    //拉链表
    public static enum EXTRACT_COL_ZIPPER {
        dw_status, valid_start_dt, valid_end_dt, dw_ins_date
    }

    //快照表
    public static enum EXTRACT_COL_SNAPSHOT {
        dw_add_ts, hp_statdate
    }

    //全量表
    public static enum EXTRACT_COL_COMPLETE {
        dw_add_ts
    }

    //增量表
    public static enum EXTRACT_COL_INCREMENT {
        dw_add_ts
    }

    public static enum DDL_TYPE {
        greenplum_increment, greenplum_complete, hive_full, hive_snapshot, hive_zipper, hive_partition, hive_nonpartition
    }

    public static enum HIVE_ENV_TYPE {
        hive_online, hive_predeploy
    }

//    public static final String  PARTITION_VAR = "${partitionName}",
//                                HDFS_DIR_VAR="hdfs://10.2.6.102/user/hive/warehouse/bi.db/",
//                                PATH = "jdbc:hive://10.1.1.161:10000/",
//                                DATADIR = "jdbc:hive://10.1.1.161:10000/";


    public static final String TASK_OBJ = "sh /data/deploy/dwarch/conf/ETL/bin/run_starshuttle_random.sh",
            PARA1 = "-id ${task_id}",
            PARA2 = "-time ${unix_timestamp}",
            PARA3 = "-offset ",
            LOG_HOME = "${wormhole_log_home}/wormhole",
            DEFAULT_CRON_EXPRESSION = "0 5 0 * * ?",
            DEFAULT_CYCLE = "D",
            DEFAULT_SUCCESS_CODE = "0",
            DEFAULT_OFFSET = "D0",
            OFFSET_TYPE = "offset";

    public static final Integer TASK_GROUP_DPODS = 1,
            TASK_GROUP_DPMID = 2,
            TASK_GROUP_DM = 3,
            TASK_GROUP_RPT = 4,
            TASK_GROUP_MAIL = 5,
            TASK_GROUP_DW = 6,
            TASK_GROUP_DQ = 7,
            TASK_GROUP_ATOM = 8,
            DEFAULT_PRIO_LVL = 3,
            DEFAULT_IF_RECALL = 1,
            DEFAULT_IF_WAIT = 0,
            DEFAULT_IF_PRE = 0,
            DEFAULT_IF_VAL = 1,
            WORMHOLE_TYPE = 1,
            WORMHOLE_ZIPPER_TYPE = 3,
            CALCULATE_TYPE = 2,
            DEFAULT_TIMEOUT = 90,
            DEFAULT_RECALL_INTERVAL = 10,
            DEFAULT_RECALL_LIMIT = 3,
            TASK_VALIDATE = 1,
            TASK_INVALIDATE = 2,
            IF_PRE_ON = 1;

    public static final String PROJECTNAME = "galaxy",
            HIVE_PATH = "hive_path",
            HIVE_DATADIR = "hive_dataDir",
            HDFS_DIR_VAR = "hdfs_dir_var",
            STARSHUTTLE_CFG = "starshuttle_cfg",
            DBMAPPING_CFG = "dbmapping_cfg",
            DB_TYPE_MAPPING = "dbTypeMapping",
            SOURCE_DB_TYPES = "sourceDBType",
            HIVE_CONNECTION_CFG = "hive_connection_cfg";

    public static final String MAIL_SUBJECT = "Galaxy User Feedback";
    public static final String MAIL_SMTP_HOST_LABEL = "mail.smtp.host";
    public static final String MAIL_SMTP_HOST = "10.100.100.101";
    public static final String MAIL_SMTP_AUTH_LABEL = "mail.smtp.auth";
    public static final String MAIL_SMTP_AUTH = "false";
    public static final String MAIL_51PING = "dpbi@51ping.com";

    //意见反馈发送到的邮箱
    public static final String[] ADMIN_MAILS = {"gang.lu@dianping.com", "hongdi.tang@dianping.com", "shanshan.jin@dianping.com", "ning.sun@dianping.com"};

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String ACL_ADDRESS_PRODUCT = "http://data.dp/pluto/json",
            ACL_ADMIN_KEY_PRODUCT = "875";

    public static final String LION_ACL_RUL = "galaxy.acl_rul",
            LION_ACL_AK_ID = "galaxy.acl_ak_id",
            LION_SSO_LOGOUT_URL = "cas-server-webapp.logoutUrl",
            LION_GALAXY_URL = "galaxy.home_url";

    public static final int DAYS_OF_WEEK = 7;

    public static final int EMPOWER_RETRY_NUMBER = 3;
}
