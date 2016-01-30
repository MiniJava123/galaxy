'use strict';

//传输任务查询页面的controller
function TableListCtrl($scope, $filter, $window, $modal, component, TableService, CommonService, UserService, JobManageService, UIService, TabStateService) {
    /***********************执行开始***********************/
    TabStateService.setTabState('10');
    addOnMessages();
    initScope();
    setUIElements();
    addWatches();
    getBlacklists();
    getSourceDBTypes();
    /***********************执行结束***********************/


    /***********************functions***********************/
    function initScope() {
        initParameters();
        initFunctions();
    }

    function initParameters() {
        //登陆用户的信息
        $scope.user = UserService.getUser();
        //提示信息
        $scope.alert = {
            type: '',
            msg: '',
            isShow: false
        };
        //是否隐藏查询结果
        $scope.hideTable = true;
        //页面是否正在加载
        $scope.isLoading = false;
        //当前选中任务
        $scope.selectTask =
        {
            selectRow: null,
            selectDBType: null
        };
    }

    function initFunctions() {
        //不显示提示信息
        $scope.closeAlert = function () {
            $scope.alert.isShow = false;
        };
        //回车的响应
        $scope.enterPress = function (keyEvent) {
            if (keyEvent.which === 13) {
                $scope.submitSearch();
            }
        };
        //相应任务是否显示
        $scope.isShowTask = function (rowIndex, targetDBType) {
            var table = getTableByIndex(rowIndex);
            var taskId = table.status.taskIdMap[targetDBType];
            var taskExist = table.status.existMap[targetDBType];
            if (taskId === 0 && taskExist === false) {
                return false;
            }
            return true;
        };
        //根据传输任务的状态获得css样式以及可以执行的操作列表
        $scope.getStatusLabel = function (rowIndex, targetDBType) {
            var table = getTableByIndex(rowIndex);
            var taskId = table.status.taskIdMap[targetDBType];
            var taskExist = table.status.existMap[targetDBType];
            if (taskId !== 0 && isBlackTask($scope.blacklistStr, taskId)) {
                $scope.allItems = [$scope.operations[0], $scope.operations[3]];
                return "label-success";
            }
            else if (taskId !== 0 && taskExist === false) {
                $scope.allItems = [$scope.operations[1], $scope.operations[4]];
                return "arrowed del";
            }
            else if (taskId !== 0 && taskExist === true) {
                $scope.allItems = [$scope.operations[0], $scope.operations[2], $scope.operations[3]];
                return "label-success";
            }
            return "arrowed";
        };
        //传输任务的下拉操作列表，更新$scope.selectTask
        $scope.showDistDataBaseOP = function (rowIndex, targetDBType) {
            $scope.selectTask.selectRow = getSelectedIndex(rowIndex);
            $scope.selectTask.selectDBType = targetDBType;
            if ($scope.selectTask.selectRow != null) {
                $scope.user.isOwner = false;
                if (!$scope.user.isAdmin) {
                    getIsTaskOwner();
                }
            }
        };
        //执行下拉列表中的响应操作
        $scope.execTaskOP = function (opIndex) {
            switch (opIndex) {
                case '0':
                    preRunTask();
                    break;
                case '1':
                    validTask();
                    break;
                case '2':
                    invalidTask();
                    break;
                case '3':
                    modifyTask();
                    break;
                case '4':
                    deleteTask();
                    break;
                case '5':
                    viewTask();
                    break;
                default :
                    break;
            }
        };
        //提交查询请求
        $scope.submitSearch = function () {
            if (!checkQueryCondition())
                return;
            $scope.isLoading = true;
            $scope.closeAlert();
            $scope.hideTable = true;
            var result = TableService.queryTables({
                'sourceDBType': $scope.sourceDBType,
                'sourceDBName': $scope.sourceDBName == '--选择全部--' ? null : $scope.sourceDBName,
                'sourceTableName': $scope.sourceTableName
            });
            processQuery(result);
        };
        //新增任务的跳转
        $scope.addTask = function (index) {
            if (!checkAddTaskCondition(index))
                return;
            var table = getTableByIndex(index);
            var sourceDBType = table.tableInfo.storage_type;
            var sourceTableName = table.tableInfo.table_name;
            var sourceDBName = table.tableInfo.db_name;
            var sourceSchemaName = table.tableInfo.schema_name;
            var sourceTableType = table.tableInfo.refresh_type;
            $window.open('/#/transferJobAdd/' + sourceDBType + '/' + sourceDBName + '/' + sourceTableName +
                '/' + sourceSchemaName + '/' + sourceTableType);
        };
    }

    function setUIElements() {
        //源表的名字
        $scope.sourceTableName = '';
        //表可以进行的操作
        $scope.operations = UIService.getTableOperations();
        //非admin和owner可执行的操作
        $scope.items = [$scope.operations[0], $scope.operations[5]];
    }

    function addWatches() {
        //检测源介质的变化更新源数据库
        $scope.$watch('sourceDBType', function () {
            if ($scope.sourceDBTypes != undefined) {
                $scope.closeAlert();
                $scope.isLoading = true;
                var result = TableService.queryDBs({
                    'DBType': $scope.sourceDBType
                });
                processDBs(result);
            }
        });
    }

    function addOnMessages() {
        //响应userVerified事件
        $scope.$on('userVerified', function (e, d) {
            $scope.user = UserService.getUser();
        });
    }

    //获取源介质类型
    function getSourceDBTypes() {
        var queryResult = TableService.queryDBTypes();
        processDBTypes(queryResult);
    }

    //获取到源介质类型后进行显示
    function processDBTypes(result) {
        result.$promise.then(
            function (data) {
                $scope.isLoading = false;
                if (data.success) {
                    $scope.sourceDBTypes = data.result;
                    var index = $scope.sourceDBTypes.indexOf('mysql');
                    if (index != -1)
                        $scope.sourceDBType = $scope.sourceDBTypes[index];
                    else
                        $scope.sourceDBType = $scope.sourceDBTypes[0];
                }
                else {
                    showAlert('warning', data.messages);
                }
            });
    }

    //获取到DBs并进行显示
    function processDBs(result) {
        result.$promise.then(
            function (data) {
                $scope.isLoading = false;
                if (data.success) {
                    $scope.sourceDBNames = data.result;
                    if ($scope.sourceDBType == 'mysql') {
                        data.result.push('--选择全部--');
                        data.result.reverse();
                    }
                    $scope.sourceDBName = $scope.sourceDBNames[0];
                }
                else {
                    showAlert('warning', data.messages);
                }
            });
    }

    //执行完操作后更新响应table的状态
    function processStatus(result, index) {
        result.$promise.then(
            function (data) {
                showAlert(data.success ? 'success' : 'warning', data.messages);
                $scope.isLoading = false;
                if (data.success) {
                    switch (index) {
                        case 0:
                            $scope.dataList[$scope.selectTask.selectRow].status.existMap[$scope.selectTask.selectDBType] = false;
                            break;
                        case 1:
                            $scope.dataList[$scope.selectTask.selectRow].status.existMap[$scope.selectTask.selectDBType] = true;
                            break;
                        case 2:
                            $scope.dataList[$scope.selectTask.selectRow].status.existMap[$scope.selectTask.selectDBType] = false;
                            $scope.dataList[$scope.selectTask.selectRow].status.taskIdMap[$scope.selectTask.selectDBType] = 0;
                            break;
                    }
                }
            });
    }

    //显示查询结果
    function processQuery(result) {
        result.$promise.then(
            function (data) {
                $scope.isLoading = false;
                $scope.hideTable = false;
                if (data.success) {
                    $scope.dataList = data.results;
                    $scope.table = component.getCustomizedTable($scope, $filter);
                    $scope.hideTable = false; //获取成功 则显示数据
                }
                else {
                    showAlert('warning', data.messages);
                }
            });
    }

    //处理黑名单的结果
    function processBlackList(result) {
        result.$promise.then(
            function (data) {
                $scope.isLoading = false;
                if (data.success) {
                    $scope.blacklistStr = data.result;
                }
                else {
                    showAlert('warning', data.messages);
                }
            });
    }

    //显示提示信息
    function process(result) {
        result.$promise.then(
            function (data) {
                $scope.isLoading = false;
                showAlert(data.success ? 'success' : 'warning', data.messages);
            });
    }

    //处理查询任务结果
    function processPreRunTask(result) {
        result.$promise.then(
            function (data) {
                $scope.isLoading = false;
                var taskDO = data.result;
                $scope.preRunTask_Committer = $scope.user.pinyinName;
                $scope.msg = {
                    headerText: '设置预跑任务:' + taskDO.taskId + ' 时间',
                    actionButtonStyle: 'btn-primary',
                    jobExecCycle: taskDO.cycle
                };
                var modalInstance = $modal.open({
                    templateUrl: '/galaxy_app/partials/dialog/preRunDialog.html',
                    controller: PreRunTaskCtrl,
                    resolve: {
                        msg: function () {
                            return $scope.msg;
                        }
                    }
                });
                modalInstance.result.then(function (time) {
                    var result = JobManageService.preRunTask({
                        'startTime': time.startDate,
                        'endTime': time.endDate,
                        'taskId': taskDO.taskId,
                        'committer': $scope.user.pinyinName,
                        'type': 'transfer'
                    });
                    $scope.isLoading = true;
                    $scope.closeAlert();
                    process(result);
                }, function () {
                });
            }
        );
    }

    //当前用户是否是当前选中task的owner
    function getIsTaskOwner() {
        var table = $scope.table.displayedDataList[$scope.selectTask.selectRow];
        var taskId = table.status.taskIdMap[$scope.selectTask.selectDBType];
        var taskExist = table.status.existMap[$scope.selectTask.selectDBType];
        if (taskId === 0 && taskExist === false) {
            return false;
        }
        var isOwnerResult = CommonService.isOwnerByTaskId({
            'taskId': taskId
        });
        $scope.isLoading = true;
        $scope.closeAlert();
        processIsOwner(isOwnerResult)
    }

    //修改任务
    function modifyTask() {
        var table = $scope.table.displayedDataList[$scope.selectTask.selectRow];
        var sourceDBType = $scope.sourceDBType;
        var targetDBType = $scope.selectTask.selectDBType;
        var sourceTableName = table.tableInfo.table_name;
        var sourceDBName = table.tableInfo.db_name;
        var sourceSchemaName = table.tableInfo.schema_name;
        var sourceTableType = table.tableInfo.refresh_type;
        var taskID = table.status.taskIdMap[$scope.selectTask.selectDBType];
        var isModifySkip = true;
        //任意都不可为空值
        if (!sourceDBType || !targetDBType || !sourceTableName || !taskID) {
            showAlert('warning', '源介质，目标介质，源表名 ,taskID之一为空值，请检查');
        } else {
            $window.open('/#/transferJobConfig/' + taskID);
        }
    };
    //失效任务
    function invalidTask() {
        var table = $scope.table.displayedDataList[$scope.selectTask.selectRow];
        var taskID = table.status.taskIdMap[$scope.selectTask.selectDBType];
        $scope.message = {
            headerText: '警告: 以下任务将会受到影响，请确认是否失效任务' + taskID,
            actionButtonStyle: 'btn-danger',
            taskId: taskID
        };
        var modalInstance = $modal.open({
            templateUrl: '/galaxy_app/partials/dialog/influencedTasksDialog.html',
            controller: InfluencedTaskCtrl,
            windowClass: 'taskQueryDialog',
            resolve: {
                msg: function () {
                    return $scope.message;
                }
            }
        });
        modalInstance.result.then(function (data) {
            var result = JobManageService.invalidTask({
                'taskId': taskID,
                'type': 'transfer'
            });
            $scope.isLoading = true;
            $scope.closeAlert();
            processStatus(result, 0);
        }, function () {
        });
    }

    //生效任务
    function validTask() {
        var taskID = $scope.table.displayedDataList[$scope.selectTask.selectRow].status.taskIdMap[$scope.selectTask.selectDBType];
        $scope.message = {
            headerText: '警告: 以下任务将会被置为生效，请确认是否生效任务' + taskID,
            actionButtonStyle: 'btn-danger',
            taskId: taskID
        };
        var modalInstance = $modal.open({
            templateUrl: '/galaxy_app/partials/dialog/influencedTasksDialog.html',
            controller: SameSourceTaskCtrl,
            windowClass: 'taskQueryDialog',
            resolve: {
                msg: function () {
                    return $scope.message;
                }
            }
        });
        modalInstance.result.then(function (data) {
            var result = JobManageService.validTask({
                'taskId': $scope.table.displayedDataList[$scope.selectTask.selectRow].status.taskIdMap[$scope.selectTask.selectDBType],
                'type': 'transfer'
            });
            $scope.isLoading = true;
            $scope.closeAlert();
            processStatus(result, 1);
        }, function () {
        });
    };
    //删除任务
    function deleteTask() {
        var taskID = $scope.table.displayedDataList[$scope.selectTask.selectRow].status.taskIdMap[$scope.selectTask.selectDBType];
        $scope.message = {
            headerText: '警告: 以下任务将会被删除，请确认是否删除任务' + taskID,
            actionButtonStyle: 'btn-danger',
            taskId: taskID
        };
        var modalInstance = $modal.open({
            templateUrl: '/galaxy_app/partials/dialog/influencedTasksDialog.html',
            controller: SameSourceTaskCtrl,
            windowClass: 'taskQueryDialog',
            resolve: {
                msg: function () {
                    return $scope.message;
                }
            }
        });
        modalInstance.result.then(function (data) {
            var result = JobManageService.deleteTask({
                'taskId': taskID,
                'type': 'transfer'
            });
            $scope.isLoading = true;
            $scope.closeAlert();
            processStatus(result, 2);
        }, function () {
        });
    };
    //预跑任务
    function preRunTask() {
        var taskID = $scope.table.displayedDataList[$scope.selectTask.selectRow].status.taskIdMap[$scope.selectTask.selectDBType];
        //根据taskId查询task
        var task = JobManageService.getTaskByTaskId({
            'taskId': taskID
        });
        $scope.isLoading = true;
        $scope.closeAlert();
        processPreRunTask(task);
    };
    //查看任务
    function viewTask() {
        var taskID = $scope.table.displayedDataList[$scope.selectTask.selectRow].status.taskIdMap[$scope.selectTask.selectDBType];
        $window.open('/#/jobView/' + taskID);
    };
    //检测查询条件
    function checkQueryCondition() {
        if ($scope.sourceDBName == '--选择全部--' && !$scope.sourceTableName) {
            showAlert('warning', '请选择数据库名或者表名');
            return false;
        }
        return true;
    }

    //检测是否可以新增任务
    function checkAddTaskCondition(index) {
        return checkIsDeveloper() && checkCanAddTask(index);
    }

    //检测是否是galaxy的开发者
    function checkIsDeveloper() {
        if (!$scope.user.isDeveloper) {
            showAlert('warning', '您不是开发平台用户组成员，请联系管理员！');
            return false;
        }
        return true;
    }

    //检测是否可以新增任务
    function checkCanAddTask(index) {
        if (!canAddTask(index)) {
            showAlert('warning', '所有类型的传输任务已满，您无法继续添加');
            return false;
        }
        return true;
    }

    //index行的table是否可以新增传输任务，传输任务是否已满
    function canAddTask(index) {
        var selectRowIndex = getSelectedIndex(index);
        if ($scope.sourceDBType === 'mysql' || $scope.sourceDBType === 'sqlserver') {
            return ($scope.table.displayedDataList[selectRowIndex].status.taskIdMap['hive'] === undefined ||
                $scope.table.displayedDataList[selectRowIndex].status.taskIdMap['hive'] === 0) ? true : false;
        } else if ($scope.sourceDBType === 'hive') {
            return $scope.table.displayedDataList[selectRowIndex].status.taskIdMap['gp_report'] === undefined ||
                $scope.table.displayedDataList[selectRowIndex].status.taskIdMap['gp_report'] === 0 ||
                $scope.table.displayedDataList[selectRowIndex].status.taskIdMap['gp_analysis'] === undefined ||
                $scope.table.displayedDataList[selectRowIndex].status.taskIdMap['gp_analysis'] === 0 ||
                $scope.table.displayedDataList[selectRowIndex].status.taskIdMap['mysql'] === undefined ||
                $scope.table.displayedDataList[selectRowIndex].status.taskIdMap['mysql'] === 0 ||
                $scope.table.displayedDataList[selectRowIndex].status.taskIdMap['gp_olap'] === undefined ||
                $scope.table.displayedDataList[selectRowIndex].status.taskIdMap['gp_olap'] === 0
        } else {
            return false;
        }
    };

    //滚动到顶部
    function scrollToTop() {
        $("body").animate({
            scrollTop: 0
        }, "fast");
    }

    //显示提示信息
    function showAlert(type, msg) {
        $scope.alert.msg = msg;
        $scope.alert.type = type;
        $scope.alert.isShow = true;
        if (type == 'warning')
            scrollToTop();
    }

    //设置是否是当前选中task的owner
    function processIsOwner(result) {
        result.$promise.then(
            function (data) {
                $scope.isLoading = false;
                $scope.user.isOwner = data.success;
            }
        );
    }

    //根据实际的index获得table
    function getTableByIndex(index) {
        var selectIndex = getSelectedIndex(index);
        return $scope.table.displayedDataList[selectIndex]; //需要设置的任务
    }

    //根据当前页的index获得实际的index
    function getSelectedIndex(index) {
        return  index + ($scope.table.startIndex - 1);
    }

    //获取黑名单
    function getBlacklists() {
        var result = TableService.getTableBlackList();
        $scope.isLoading = true;
        $scope.closeAlert();
        processBlackList(result, 3);
    };


    //判断taskId对应的任务是否在黑名单中
    function isBlackTask(blackList, taskId) {
        for (var i = 0; i < blackList.length; i++) {
            var blackId = blackList[i];
            if (blackId == taskId) {
                return true;
            }
        }
        return false;
    }
}