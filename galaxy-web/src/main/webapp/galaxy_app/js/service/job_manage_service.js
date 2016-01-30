/**
 * Created by mt on 2014/4/30.
 */
angular.module('job_manage.service', ['ngResource'])
    .factory('JobManageService', ['$resource', 'serverAddress',
        function ($resource, serverAddress) {
            return $resource(serverAddress + '/jobManage/:action',
                {},
                {
                    //根据条件查询tasks
                    queryTasks: {
                        method: 'GET',
                        params: {
                            action: 'queryTasks',
                            group: '@group',
                            cycle: '@cycle',
                            developer: '@developer',
                            nameOrId: '@nameOrId'
                        }
                    },
                    //根据taskId获得task
                    getTaskByTaskId: {
                        method: 'GET',
                        params: {
                            action: 'getTaskByTaskId',
                            taskId: '@taskId'
                        }
                    },
                    //根据taskId获得其影响的tasks（后继任务+同源任务58、59）
                    getInfluencedTasksByTaskId: {
                        method: 'GET',
                        params: {
                            action: 'getInfluencedTasksByTaskId',
                            taskId: '@taskId'
                        }
                    },
                    //根据taskId获得同源的tasks
                    getSameSourceTasksByTaskId: {
                        method: 'GET',
                        params: {
                            action: 'getSameSourceTasksByTaskId',
                            taskId: '@taskId'
                        }
                    },
                    //检测task name
                    checkTaskName: {
                        method: 'GET',
                        params: {
                            action: 'checkTaskName',
                            taskName: '@taskName'
                        }
                    },
                    //获取传输任务的id获得任务的源表信息
                    getSourceTable: {
                        method: 'GET',
                        params: {
                            action: 'getSourceTable',
                            taskId: '@taskId'
                        }
                    },
                    //获取任务id获得任务的目标表信息
                    getTargetTable: {
                        method: 'GET',
                        params: {
                            action: 'getTargetTable',
                            taskId: '@taskId'
                        }
                    },
                    //级联预跑获得需要预跑的任务
                    getTasksForCascadePreRun: {
                        method: 'GET',
                        params: {
                            action: 'getTasksForCascadePreRun',
                            taskId: '@taskId'
                        }
                    },
                    //级联重跑
                    cascadePreRun: {
                        method: 'POST',
                        params: {
                            action: 'cascadePreRun'
                        }
                    },
                    //预跑任务
                    preRunTask: {
                        method: 'POST',
                        params: {
                            action: 'preRunTask',
                            startTime: '@startTime',
                            endTime: '@endTime',
                            taskId: '@taskId',
                            committer: '@committer',
                            type: '@type'
                        }
                    },
                    //失效任务
                    invalidTask: {
                        method: 'POST',
                        params: {
                            action: 'invalidTask',
                            taskId: '@taskId',
                            type: '@type'
                        }
                    },
                    //生效任务
                    validTask: {
                        method: 'POST',
                        params: {
                            action: 'validTask',
                            taskId: '@taskId',
                            type: '@type'
                        }
                    },
                    //删除任务
                    deleteTask: {
                        method: 'DELETE',
                        params: {
                            action: 'deleteTask',
                            taskId: '@taskId',
                            type: '@type'
                        }
                    },
                    //计算任务保存调度配置
                    updateCalculateTask: {
                        method: 'POST',
                        params: {
                            action: 'updateCalculateTask'
                        }
                    },
                    //更新传输任务调度配置信息与McTaskInfo
                    updateTransferTask: {
                        method: 'POST',
                        params: {
                            action: 'updateTransferTask'
                        }
                    },
                    //新增传输任务
                    addTransferTask: {
                        method: 'POST',
                        params: {
                            action: 'addTransferTask'
                        }
                    },
                    //新增计算任务
                    addCalculateTask: {
                        method: 'POST',
                        params: {
                            action: 'addCalculateTask'
                        }
                    }
                });
        }
    ]);