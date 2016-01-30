package com.dianping.data.warehouse.core.common;

/**
 * Created by hongdi.tang on 14-2-17.
 */
public class WriterParameters {

    public static enum GreenplumWriterParas{
        plugin,connectProps,priority,encoding,params,tableName,columns,
        pre,post,countSql,rollback,logErrorTable,failedlinesthreshold
    }

    public static enum HiveWriterParas{
        connectProps,htable,autoFlush,writebufferSize,writeAheadLog,rowKeyIndex,columnsName,
        deleteMode,rollbackMode,concurrency,write_sleep,wait_time,
        num_to_wait,hoursDecTimeStamp,idDeleteData
    }

    public static enum HdfsWriterParas{
        plugin,connectProps,dir,prefix_filename,field_split,line_split,encoding,
        nullchar,replace_char,codec_class,buffer_size,file_type,
        concurrency,hive_table_add_partition_switch,hive_table_add_partition_condition,
        dataTransformClass,dataTransformParams,
        createLzoIndexFile
    }

    public static enum MongoWriterParas{
        connectProps,output_uri,output_fields,bulk_insert_line,concurrency,dropCollectionBeforeInsertionSwitch
    }

    public static enum MysqlWriterParas{
        connectProps,priority,encoding,params,loadFile,tableName,
        columns,pre,post,replace,rollback,failedlinesthreshold,
        countsql,plugin
    }

}
