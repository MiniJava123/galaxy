package com.dianping.data.warehouse.core.common;

/**
 * Created by hongdi.tang on 14-2-17.
 */
public class ReaderParameters {

    public static enum GreenplumReaderParas{
        connectProps(true),encoding(true),params(false),preCheck(false),sql(false),
        tableName(false),where(false),columns(false),concurrency(true),needSplit(false),
        partitionName(false),getPartitionValue(false),countSql(false);

        private boolean flag;
        private GreenplumReaderParas(boolean flag){
            this.flag = flag;
        }

        public boolean isReadOnly(){
            return this.flag;
        }

    }

    public static enum HiveReaderParas{
        plugin(),connectProps(true),path(true),sql(true),mode(true),dataDir(true),reduceNumber(false),
        concurrency(false);

        private boolean readonly;
        private HiveReaderParas(){
        }
        private HiveReaderParas(boolean readonly){
            this.readonly = readonly;
        }
        public boolean isReadonly(){
            return this.readonly;
        }
    }

    public static enum SqlServerReaderParas{
        plugin,connectProps,encoding,params,preCheck,sql,needSplit,
        concurrency,blockSize,tableName,autoIncKey,columns,
        where,countSql
    }

    public static enum MongoReaderParas{
        connectProps,input_uri,input_fields,input_query,input_sort,input_limit,
        need_split,split_key_pattern,split_size,concurrency,field_need_split,
        field_split_char,dataTransformClass
    }

    public static enum MysqlReaderParas{
        plugin,connectProps,encoding,params,preCheck,sql,needSplit,
        concurrency,blockSize,tableName,autoIncKey,columns,
        where,countSql
    }

    public static enum SaleforceReaderParas{
        connectProps,entity,extractionSOQL,encryptionKeyFile
    }



}
