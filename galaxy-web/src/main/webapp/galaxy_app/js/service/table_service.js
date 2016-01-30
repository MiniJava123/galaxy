angular.module('table.service', ['ngResource'])
    .factory('TableService', ['$resource', '$routeParams', 'serverAddress',
        function ($resource, $routeParams, serverAddress) {
            return $resource(serverAddress + "/table/:action",
                {},
                {
                    //查询源介质类型
                    queryDBTypes: {
                        method: 'GET',
                        params: {
                            action: 'queryDBTypes'
                        }
                    },
                    //根据源介质类型查询数据库
                    queryDBs: {
                        method: 'GET',
                        params: {
                            action: 'queryDBs',
                            DBType: '@DBType'
                        }
                    },
                    //根据条件查询表
                    queryTables: {
                        method: 'GET',
                        params: {
                            action: 'queryTables',
                            sourceDBType: '@sourceDBType',
                            sourceDBName: '@sourceDBName',
                            sourceTableName: '@sourceTableName'
                        }
                    },
                    //获取黑名单表
                    getTableBlackList: {
                        method: 'GET',
                        params: {
                            action: 'getTableBlackList'
                        }
                    },
                    //根据源介质类型获取目标介质类型
                    queryTargetDBTypes: {
                        method: 'GET',
                        params: {
                            action: 'queryTargetDBTypes',
                            sourceDBType: '@sourceDBType',
                            sourceDBName: '@sourceDBName',
                            sourceTableName: '@sourceTableName',
                            type: '@type'
                        }
                    },
                    //获取表的列信息
                    getTableColumns: {
                        method: 'GET',
                        params: {
                            action: 'getTableColumns',
                            sourceDBType: '@sourceDBType',
                            sourceDBName: '@sourceDBName',
                            sourceSchemaName: '@sourceSchemaName',
                            sourceTableName: '@sourceTableName'
                        }
                    },
                    //查询表的信息
                    getTableInfo: {
                        method: 'GET',
                        params: {
                            action: 'getTableInfo',
                            DBType: '@DBType',
                            DBName: '@DBName',
                            tableName: '@tableName'
                        }
                    },
                    //查询建表语句
                    queryCreateTableSql: {
                        method: 'POST',
                        params: { action: 'queryCreateTableSql' }
                    },
                    //建表
                    buildTable: {
                        method: 'POST',
                        params: { action: 'buildTable' }
                    },
                    //获取表基本信息,表名,数据库名,schema名,刷新类型等
                    getTableBasicInfo: {
                        method: 'GET',
                        params: {
                            action: 'getTableBasicInfo',
                            taskId: '@taskId'
                        }
                    },
                    //获取schema名
                    getSchemaName: {
                        method: 'GET',
                        params: {
                            action: 'getSchemaName',
                            sourceDBType: '@sourceDBType',
                            sourceSchemaName: '@sourceSchemaName',
                            sourceTableName: '@sourceTableName',
                            targetDBType: '@targetDBType',
                            type: '@type'
                        }
                    },
                    //获取任务的配置信息
                    getTaskInfo: {
                        method: 'GET',
                        params: {
                            action: 'getTaskInfo',
                            sourceDBType: '@sourceDBType',
                            sourceDBName: '@sourceDBName',
                            sourceSchemaName: '@sourceSchemaName',
                            sourceTableName: '@sourceTableName',
                            targetDBType: '@targetDBType',
                            targetDBName: '@targetDBName',
                            targetSchemaName: '@targetSchemaName',
                            targetTableName: '@targetTableName',
                            targetTableType: '@targetTableType',
                            targetPartitionName: '@targetPartitionName',
                            dateType: '@dateType',
                            dateNumber: '@dateNumber',
                            dateOffset: '@dateOffset',
                            owner: '@owner',
                            method: '@method',
                            taskId: '@taskID'
                        }
                    },
                    //更新传输任务高级配置信息
                    updateTransferAdvance: {
                        method: 'POST',
                        params: {
                            action: 'updateTransferAdvance'
                        }
                    }
                }
            );
        }
    ]);