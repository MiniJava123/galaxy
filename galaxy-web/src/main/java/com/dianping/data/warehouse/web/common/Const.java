package com.dianping.data.warehouse.web.common;

import org.apache.commons.lang.StringUtils;

/**
 * Created by hongdi.tang on 14-2-17.
 */
public class Const {

    public static final String DATASOURCE_TYPE_MYSQL = "mysql",
            DATASOURCE_TYPE_GP = "greenplum",
            DATASOURCE_TYPE_HIVE = "hive",
            DATASOURCE_TYPE_HIVEMETA = "hivemeta",
            DATASOURCE_TYPE_SQLSERVER = "sqlserver",
            DATASOURCE_TYPE_SALESFORCE = "salesforce",
            DATASOURCE_TYPE_POSTGRESQL = "postgresql",
            DATASOURCE_TYPE_GPREPORT = "gpreport",
            DATASOURCE_TYPE_GPANALYSIS = "gpanalysis";

    public static final String TABLE_TYPE_ZIPPER = "zipper",
            TABLE_TYPE_SNAPSHOT = "snapshot",
            TABLE_TYPE_COMPLETE = "full",
            TABLE_TYPE_INCREMENT = "append";

    public static final String WORMHOLE_READER_TYPE = "reader",
            WORMHOLE_WRITER_TYPE = "writer";

//    // ETL_TASK_CFG的字段
//    public static final String FLD_TASK_ID = "taskId",
//            FLD_TASK_NAME = "taskName",
//            FLD_TASK_TABLE_NAME = "tableName",
//            FLD_TASK_REMARK = "remark",
//            FLD_TASK_DATABASE_SRC = "databaseSrc",
//            FLD_TASK_OBJECT = "taskObj",
//            FLD_TASK_PARA1 = "para1",
//            FLD_TASK_PARA2 = "para2",
//            FLD_TASK_PARA3 = "para3",
//            FLD_TASK_RECALL_LIMIT = "recallLimit",
//            FLD_TASK_RECALL_INTERVAL = "recallInterval",
//            FLD_TASK_GROUP_ID = "taskGroupId",
//            FLD_TASK_CYCLE = "cycle",
//            FLD_TASK_PRIO_LVL = "prioLvl",
//            FLD_TASK_IF_RECALL = "ifRecall",
//            FLD_TASK_TIMEOUT = "timeout",
//            FLD_TASK_IF_PRE = "ifPre",
//            FLD_TASK_IF_WAIT = "ifWait",
//            FLD_TASK_IF_VAL = "ifVal",
//            FLD_TASK_TYPE = "type",
//            FLD_TASK_OFFSET = "offset",
//            FLD_TASK_OFFSET_TYPE = "offsetType",
//            FLD_TASK_FREQ = "freq",
//            FLD_TASK_OWNER = "owner",
//            FLD_TASK_WAIT_CODE = "waitCode",
//            FLD_TASK_RECALL_CODE = "recallCode",
//            FLD_TASK_SUCCESS_CODE = "successCode",
//            FLD_TASK_STATUS = "status",
//            FLD_TASK_ADD_USER = "addUser",
//            FLD_TASK_UPDATE_USER = "updateUser",
//            FLD_ONLYSELF = "onlyself";
//
//    public static final String FLD_AUTOETL_DATASOURCE_TYPE = "datasourceType",
//            FLD_AUTOETL_DATABASE_NAME = "databaseName",
//            FLD_AUTOETL_SCHEMA_NAME = "schemaName",
//            FLD_AUTOETL_TABLE_NAME = "tableName",
//            FLD_AUTOETL_TARGET_IS_ACTIVE_SCHEDULE = "targetIsActiveSchedule",
//            FLD_AUTOETL_TARGET_DATASOURCE_TYPE = "targetDatasourceType",
//            FLD_AUTOETL_TARGET_SCHEMA_TABLE = "targetSchemaTable",
//            FLD_AUTOETL_TARGET_TABLE_TYPE = "targetTableType",
//            FLD_AUTOETL_TARGET_SEGMENT_COLUMN = "targetSegmentColumn",
//            FLD_AUTOETL_WRITE_TYPE = "targetWriteType";
//
//    public static final String FLD_AUTOBUILDTAB_DATASOURCE_NAME = "databaseName",
//            FLD_AUTOBUILDTAB_TABLE_NAME = "tableName",
//            FLD_AUTOBUILDTAB_OWNER = "owner",
//            FLD_AUTOBUILDTAB_STORAGE_CYCLE = "storageCycle",
//            FLD_AUTOBUILDTAB_TABLE_COMMENT = "tableComment",
//            FLD_AUTOBUILDTAB_COLUMN_COMMENT = "columnComment",
//            FLD_AUTOBUILDTAB_COLUMN_SIZE = "columnSize",
//            FLD_AUTOBUILDTAB_PARTITION_COLUMN_COMMENT = "partitionColumnComment",
//            FLD_AUTOBUILDTAB_PARTITION_COLUMN_SIZE = "partitionColumnSize",
//            FLD_AUTOBUILDTAB_PARTITION_AUTH_ONLINE_GROUP = "auth_online",
//            FLD_AUTOBUILDTAB_PARTITION_AUTH_OFFLINE_GROUP = "auth_offline",
//            FLD_AUTOBUILDTAB_PARTITION_LOCATION = "location",
//            FLD_AUTOBUILDTAB_MAIL = "mail";
//
//
//    //关联任务
//    public static final String FLD_PRE_DEPENDS = "preDepends",
//            FLD_DEP_TASK_ID = "depTaskId",
//            FLD_DEP_TASK_GROUP_ID = "depTaskGroupId",
//            FLD_DEP_TASK_NAME = "depTaskName",
//            FLD_DEP_CYCLE_GAP = "depCycleGap",
//            FLD_DEP_REMARK = "depRemark";
//
//    public static final String FLD_TASK_RELA_STATUS_ID = "taskStatusId";


    //拉链表
    public static enum EXTRACT_COL_ZIPPER {
        dw_status, valid_start_dt, valid_end_dt, dw_ins_date, hp_valid_end_dt
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
        dw_add_ts, hp_statdate
    }

    public static enum DDL_TYPE {
        greenplum_increment, greenplum_complete, hive_zipper, hive_snapshot
    }

    public static final String PARTITION_VAR = "${partitionName}",
            HDFS_DIR_VAR = "${hdfs_dir}";

    public static final String COOKIE_TOKEN = "tk",
            COOKIE_LOGINID = "loginID",
            COOKIE_EMPLOYPINYIN = "employee_pinyin",
            REQUEST_ATTR_USER = "user";

    /**
     * 任务运行实例状态
     */
    public static final Integer TASK_STATUS_UNKOWN = -3,
            TASK_STATUS_UNSUCCESS = -2,
            TASK_STATUS_FAIL = -1,
            TASK_STATUS_INIT = 0,
            TASK_STATUS_SUCCESS = 1,
            TASK_STATUS_RUNNING = 2,
            TASK_STATUS_SUSPEND = 3,
            TASK_STATUS_INIT_ERROR = 4,
            TASK_STATUS_WAIT = 5,
            TASK_STATUS_READY = 6,
            TASK_STATUS_TIMEOUT = 7;

    public static final int IF_YES = 1,
            IF_NO = 0;

    public static final String DATEBASE_TYPE_GPREPORT = "gp_report",
            DATEBASE_TYPE_GPANALYSIS = "gp_analysis";

}
