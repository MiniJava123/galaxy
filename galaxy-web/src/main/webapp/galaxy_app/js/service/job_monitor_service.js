angular.module('job_monitor.service', ['ngResource'])
    .factory('JobMonitorService', ['$resource', 'serverAddress',
        function ($resource, serverAddress) {
            return $resource(serverAddress + '/jobMonitor/:action',
                {},
                {
                    //通过查询条件查询instance
                    queryInstances: {
                        method: 'GET',
                        params: {
                            action: 'queryInstances',
                            startDate: '@startDate',
                            endDate: '@endDate',
                            executionCycle: '@executionCycle',
                            developer: '@developer',
                            executionStatus: '@executionStatus',
                            executionPriority: '@executionPriority',
                            isSelf: '@isSelf',
                            taskNameOrId: '@taskNameOrId',
                            displayType: '@displayType'
                        }
                    },
                    //根据instanceId获取instance
                    getInstanceByInstanceId: {
                        method: 'GET',
                        params: {
                            action: 'getInstanceByInstanceId',
                            instanceId: '@instanceId'
                        }
                    },
                    //根据instanceId显示其自身以及所有的post tasks
                    getSelfAndPostInstances: {
                        method: 'GET',
                        params: {
                            action: 'getSelfAndPostInstances',
                            instanceId: '@instanceId'
                        }
                    },
                    //重跑任务
                    recallInstance: {
                        method: 'POST',
                        params: {
                            action: 'recallInstance',
                            instanceId: '@instanceId'
                        }
                    },
                    //批量重跑
                    recallInstances: {
                        method: 'POST',
                        params: {
                            action: 'recallInstances'
                        }
                    },
                    //挂起任务
                    suspendInstance: {
                        method: 'POST',
                        params: {
                            action: 'suspendInstance',
                            instanceId: '@instanceId'
                        }
                    },
                    //批量挂起
                    suspendInstances: {
                        method: 'POST',
                        params: {
                            action: 'suspendInstances'
                        }
                    },
                    //置为成功任务
                    successInstance: {
                        method: 'POST',
                        params: {
                            action: 'successInstance',
                            instanceId: '@instanceId'
                        }
                    },
                    //批量置为成功
                    successInstances: {
                        method: 'POST',
                        params: {
                            action: 'successInstances'
                        }
                    },
                    //直接依赖
                    queryDirectRelation: {
                        method: 'GET',
                        params: {
                            action: 'queryDirectRelation',
                            instanceId: '@taskStatusId'
                        }
                    },
                    //所有依赖
                    queryAllRelation: {
                        method: 'GET',
                        params: {
                            action: 'queryAllRelation',
                            instanceId: '@taskStatusId'
                        }
                    },
                    //查看最长路径
                    getLongestPath: {
                        method: 'GET',
                        params: {
                            action: 'getLongestPath',
                            instanceId: '@taskStatusId'
                        }
                    },
                    //批量停止任务
                    batchStopTask: {
                        method: 'POST',
                        params: {
                            action: 'batchStopTask',
                            taskId: '@taskId'
                        }
                    },
                    //快速通道
                    raisePriorityInstance: {
                        method: 'POST',
                        params: {
                            action: 'raisePriorityInstance',
                            instanceId: '@instanceId'
                        }
                    },
                    //级联重跑获得需要重跑的节点
                    recallCascadeGetInstances: {
                        method: 'GET',
                        params: {
                            action: 'recallCascadeGetInstances'
                        }
                    },
                    //级联重跑
                    recallCascade: {
                        method: 'POST',
                        params: {
                            action: 'recallCascade'
                        }
                    },
                    //查看指定路径的log
                    viewLog: {
                        method: 'GET',
                        params: {
                            action: 'viewLog',
                            logPath: '@logPath'
                        }
                    },
                    //查询预跑instances
                    queryPreRunInstances: {
                        method: 'GET',
                        params: {
                            action: 'queryPreRunInstances',
                            startDate: '@startDate',
                            taskCommitter: '@taskCommitter',
                            taskNameOrId: '@taskNameOrId'
                        }
                    },
                    //实例状态分析
                    instanceStatusAnalyze: {
                        method: 'GET',
                        params: {
                            action: 'instanceStatusAnalyze',
                            instanceId: '@instanceId'
                        }
                    }
                });
        }
    ]);