/**
 * Created by Sunny on 14-6-9.
 */

//传输任务整体步骤页面对应的controller
function TransferConfigCtrl($scope, UserService, CommonService, $routeParams) {
    //用户是否有权限查看页面
    $scope.hasPower = false;

    $scope.jobConfigSteps = [
        {
            ID: '1',
            Text: '调度配置'
        },
        {
            ID: '2',
            Text: '高级配置'
        }
    ];

    $scope.transmitParameter = {
        starShuttleDO: {},
        starShuttleParasDO: {},
        table: {}
    };

    //响应userVerified事件
    $scope.$on('userVerified', function (e, d) {
        $scope.user = UserService.getUser();
        //管理员或者owner才有权限
        if ($scope.user.isAdmin) {
            $scope.hasPower = true;
        }
        else {
            var isOwner = CommonService.isOwnerByTaskId({
                'taskId': $routeParams.taskId
            });
            processIsOwner(isOwner);
        }
    });

    function processIsOwner(result) {
        result.$promise.then(
            function (data) {
                $scope.hasPower = data.success;
            }
        );
    }
}

//传输任务调度配置页面对应的controller
function TransferScheduleConfigCtrl($scope, $filter, $routeParams, $modal, component, JobManageService, TableService, UserService, UtilsService, UIService) {

    /***********************执行开始***********************/
    addOnMessages();
    initScope();
    setUIElements();
    getSourceTableInfo();
    getTargetTableInfo();
    /***********************执行结束***********************/


    /***********************functions***********************/
    function initScope() {
        initParameters();
        initFunctions();
    }

    function initParameters() {
        //页面告警信息
        $scope.alert = {
            type: '',
            msg: '',
            isShow: false
        };
        //页面加载信息
        $scope.loading = {
            data: '',
            isShow: false
        };
        //是否展示具体信息
        $scope.showConfig = false;
        //页面是够正在加载
        $scope.isLoading = false;
        //当前用户信息
        $scope.user = UserService.getUser();
        //任务依赖信息
        $scope.dependenceTasks = new Array();
        //获取galaxy开发组成员
        getDevelopers();
    }

    function initFunctions() {
        //不显示提示信息
        $scope.closeAlert = function () {
            $scope.alert.isShow = false;
        };
        //显示依赖选择框
        $scope.showDependenceDialog = function () {
            $scope.message = {
                headerText: '请选择依赖任务',
                data: $scope.dependenceTasks // 传入数据，dialog的controller进行修改
            };
            var modalInstance = $modal.open({
                templateUrl: '/galaxy_app/partials/dialog/taskDependencyDialog.html',
                controller: TaskDependencyCtrl,
                windowClass: 'taskQueryDialog',
                resolve: {
                    msg: function () {
                        return $scope.message;
                    }
                }
            });
            modalInstance.result.then(function (data) {
                $scope.dependenceTasks = data;
            }, function () {
            });
        };
        //删除依赖信息
        $scope.deleteDependenceTask = function (index) {
            $scope.message = {
                headerText: '提示',
                bodyText: '是否删除依赖任务: ' + $scope.dependenceTasks[index].taskId + ' ?',
                actionButtonStyle: 'btn-danger',
                showCancel: true
            };
            var modalInstance = $modal.open({
                templateUrl: '/galaxy_app/partials/dialog/message.html',
                controller: MessageCtrl,
                resolve: {
                    msg: function () {
                        return $scope.message;
                    }
                }
            });
            modalInstance.result.then(function (data) {
                $scope.dependenceTasks.splice(index, 1);
            });
        };
        //根据不同的cycle设置不同的offset选项
        $scope.getOffsetOptions = function (cycle) {
            return UIService.getOffsetsByCycle(cycle);
        };
        //获得cycle的中文解释
        $scope.getCycleText = function (cycle) {
            return UIService.cycleToText(cycle);
        };
    }

    function setUIElements() {
        $scope.taskGroups = UIService.getTaskGroups();
        $scope.taskPeriods = UIService.getTaskPeriods();
        $scope.taskTimeouts = UIService.getTaskTimeouts();
        $scope.prioLvls = UIService.getPrioLvls();
        $scope.taskOffsets = UIService.getTaskOffsets();
        $scope.recall_limits = UIService.getRecallLimits();
        $scope.recall_intervals = UIService.getRecallIntervals();
        $scope.ifRecalls = UIService.getIfOrNot();
        $scope.dateTypes = UIService.getDateTypes();
        $scope.dateNumbers = UIService.getDateNumbers();
        $scope.dateType = 'D';
        $scope.dateOffsets = UIService.getDateOffsetsByDateType($scope.dateType);
        $scope.dateOffset = $scope.dateOffsets[0];
        $scope.dateNumber = $scope.dateNumbers[0];
    }

    //获取galaxy开发组成员
    function getDevelopers() {
        $scope.developers = UtilsService.getDevelopers();
    }

    //添加消息响应
    function addOnMessages() {
        $scope.$on('save', function (e, d) {
            if (d == 0) {
                //修改时更新任务的调度配置信息
                saveScheduleConfig();
            }
        });
        //响应userVerified事件
        $scope.$on('userVerified', function (e, d) {
            $scope.user = UserService.getUser();
        });
    }

    //根据基础信息获得任务的调度配置信息以及高级配置信息
    function getTaskInfo() {
        if (!checkBasic())
            return;
        $scope.showConfig = false;
        $scope.closeAlert();
        showLoading('加载中');
        var result = TableService.getTaskInfo({
            'sourceDBType': $scope.sourceDBType,
            'sourceDBName': $scope.sourceDBName,
            'sourceTableName': $scope.sourceTableName,
            'sourceSchemaName': $scope.sourceSchemaName,
            'targetDBType': $scope.targetDBType,
            'targetSchemaName': $scope.targetSchemaName,
            'targetTableName': $scope.targetTableName,
            'targetDBName': $scope.targetDBName,
            'targetTableType': $scope.targetTableType,
            'targetPartitionName': $scope.targetPartitionName,
            'dateType': $scope.dateType,
            'dateNumber': $scope.dateNumber,
            'owner': $scope.user.pinyinName,
            'dateOffset': $scope.dateOffset,
            'method': 'taskUpdate',
            'taskId': $routeParams.taskId
        });
        processGetAllInfo(result);
        $scope.showConfig = false;
    };

    //传递参数给第二个步骤页
    function setTransmitParameter() {
        if (!checkScheduleConfig())
            return;
        $scope.starShuttleDO.taskDO.relaDOList = UtilsService.getTaskRelaDO($scope.dependenceTasks);
        $scope.$parent.$parent.transmitParameter.starShuttleDO = $scope.starShuttleDO;
        $scope.$parent.$parent.transmitParameter.starShuttleParasDO = getStarShuttleParasDO();
        $scope.$parent.$parent.transmitParameter.table = $scope.table;
    }

    //拼装getStarShuttleParasDO
    function getStarShuttleParasDO() {
        var starShuttleParasDO = {
            'sourceDBType': $scope.sourceDBType,
            'sourceDBName': $scope.sourceDBName,
            'sourceSchemaName': $scope.sourceSchemaName,
            'sourceTableName': $scope.sourceTableName,
            'targetDBType': $scope.targetDBType,
            'targetDBName': $scope.targetDBName,
            'targetTableType': $scope.targetTableType,
            'targetSchemaName': $scope.targetSchemaName,
            'targetTableName': $scope.targetTableName,
            'owner': $scope.user.pinyinName
        };
        if ($scope.targetTableType === 'zipper') {
            starShuttleParasDO['targetPartitionName'] = $scope.targetPartitionName;
        }
        if ($scope.targetTableType === 'append') {
            starShuttleParasDO['targetPartitionName'] = $scope.targetPartitionName;
            starShuttleParasDO['dateType'] = $scope.dateType;
            if ($scope.dateType != 'MTD') {
                starShuttleParasDO['dateNumber'] = $scope.dateNumber;
                starShuttleParasDO['dateOffset'] = $scope.dateOffset;
            }
        }
        return starShuttleParasDO;
    }

    //获得目标数据库类型
    function getTargetDBTypes() {
        $scope.closeAlert();
        showLoading('加载中');
        var result = TableService.queryTargetDBTypes({
            'sourceDBType': $scope.sourceDBType,
            'sourceDBName': $scope.sourceDBName,
            'sourceTableName': $scope.sourceTableName,
            'type': 'update'
        });
        processGetTargetDBTypes(result);
    }

    function processGetTargetDBTypes(result) {
        result.$promise.then(
            function (data) {
                closeLoading();
                if (data.success) {
                    $scope.targetDBTypes = data.result;
                }
            });
    };

    //获得目标表字段信息
    function getTargetTableColumns() {
        if ($scope.showPartitionInput) {
            $scope.closeAlert();
            showLoading('加载中');
            var result = TableService.getTableColumns({
                'sourceDBType': $scope.sourceDBType,   //目标表的columns信息从源表获取，必要条件：sourceDBType,sourceDBName,sourceTableName
                'sourceDBName': $scope.sourceDBName,
                'sourceSchemaName': $scope.sourceSchemaName,
                'sourceTableName': $scope.sourceTableName
            });
            processGetTargetTableColumns(result);
        }
    }

    function processGetTargetTableColumns(result) {
        result.$promise.then(
            function (data) {
                closeLoading();
                if (data.success) {
                    $scope.targetTableColumns = data.result;
                }
            });
    }

    //通过taskId请求获得源表的信息
    function getSourceTableInfo() {
        $scope.closeAlert();
        showLoading('加载中');
        var result = JobManageService.getSourceTable({
            'taskId': $routeParams.taskId
        });
        processGetSourceTableInfoByTaskId(result);
    }

    //获得源表的信息的处理
    function processGetSourceTableInfoByTaskId(result) {
        result.$promise.then(
            function (data) {
                closeLoading();
                if (data.success) {
                    var sourceTableInfo = data.results[0];
                    $scope.sourceDBType = sourceTableInfo.storage_type;
                    $scope.sourceDBName = sourceTableInfo.db_name;
                    $scope.sourceTableName = sourceTableInfo.table_name;
                    $scope.sourceSchemaName = sourceTableInfo.schema_name === 'null' ? null : $filter('lowercase')(sourceTableInfo.schema_name);
                    $scope.sourceTableType = sourceTableInfo.refresh_type === 'null' ? null : $filter('lowercase')(sourceTableInfo.refresh_type);
                    getTargetDBTypes();
                }
            });
    }

    //通过taskId请求获得目标表信息
    function getTargetTableInfo() {
        $scope.closeAlert();
        showLoading('加载中');
        var result = TableService.getTableBasicInfo({  //根据taskId 发起call 拿到目标表的信息
            'taskId': $routeParams.taskId
        });
        processGetTargetTableInfo(result);
    }

    //获得目标表信息的处理
    function processGetTargetTableInfo(result) {
        result.$promise.then(
            function (data) {
                closeLoading();
                if (!data.success) {
                    showAlert('warning', data.messages);
                    return;
                }
                $scope.targetDBName = data.result.tableInfo.db_name;

                setTargetDBType(data);
                setTargetTableType(data);

            }
        )
    }

    function setTargetDBType(data) {
        $scope.targetDBType = data.result.tableInfo.storage_type;
        if ($scope.targetDBType == 'gp_report' || $scope.targetDBType == 'gp_analysis'
            || $scope.targetDBType == 'gp_olap' || $scope.targetDBType == 'mysql') {
            $scope.targetTableTypes = UIService.getTargetTableTypes(1);
        }
        else {
            $scope.targetTableTypes = UIService.getTargetTableTypes(0);
        }
        getTargetSchemaName();
    }

    //获得目标表名
    function getTargetSchemaName() {
        $scope.closeAlert();
        showLoading('加载中');
        var result = TableService.getSchemaName({
            'sourceDBType': $scope.sourceDBType,
            'sourceSchemaName': $scope.sourceSchemaName,
            'sourceTableName': $scope.sourceTableName,
            'targetDBType': $scope.targetDBType,
            'type': 'update'
        });
        processGetTargetSchemaName(result);
    }

    function processGetTargetSchemaName(result) {
        result.$promise.then(
            function (data) {
                closeLoading();
                if (data.success) {
                    $scope.targetSchemaName = data.result;
                    if ($scope.targetSchemaName == null) {
                        $scope.targetTableName = $scope.sourceTableName;
                    }
                    else {
                        if ($scope.sourceDBType == "hive" || $scope.targetSchemaName == "") {
                            $scope.targetTableName = $scope.sourceTableName;
                        } else {
                            $scope.targetTableName = $scope.targetSchemaName + '_' + $scope.sourceTableName;
                        }
                    }
                    getTaskInfo();
                }
            });
    }

    //设置刷新类型
    function setTargetTableType(data) {
        $scope.targetTableType = data.result.tableInfo.refresh_type;
        switch ($scope.targetTableType) {
            case 'zipper':
                $scope.showPartitionInput = true;
                $scope.zipperShow = false;
                $scope.append_or_zipper_name = '拉链字段';
                $scope.targetPartitionName = data.result.conditionCol;
                break;
            case 'append':
                $scope.showPartitionInput = true;
                $scope.append_or_zipper_name = '增量字段';
                $scope.zipperShow = true;

                $scope.targetPartitionName = data.result.conditionCol;
                $scope.dateType = data.result.tableInfo.refresh_cycle;

                $scope.dateNumber = data.result.tableInfo.refresh_datum_date;
                $scope.dateOffset = data.result.tableInfo.refresh_offset;


                break;
            default :
                $scope.showPartitionInput = false;
                break;
        }
        getTargetTableColumns();
    }

    //检测基础信息
    function checkBasic() {
        if (($scope.targetTableType == 'zipper' || $scope.targetTableType == 'append') &&
            ($scope.targetPartitionName == null || $scope.targetPartitionName == '')) {
            showAlert('warning', '增量字段或者拉链字段为空');
            return false;
        }
        return true;
    }

    //获取已保存的任务的调度配置信息以及高级配置信息
    function processGetAllInfo(result) {
        result.$promise.then(
            function (data) {
                closeLoading();
                if (data.success) {
                    $scope.starShuttleDO = data.result; //所有信息，用来修改并保存
                    $scope.dataList = $scope.starShuttleDO.columns; //show 表字段信息
                    $scope.table = component.getCustomizedTable($scope, $filter);
                    for (var i = 0; i < $scope.developers.length; i++) {
                        if ($scope.developers[i].ID === $scope.starShuttleDO.taskDO.owner) {
                            $scope.starShuttleDO.taskDO.owner = $scope.developers[i].ID;
                            $scope.developer = UtilsService.getDeveloperByPinyinName($scope.starShuttleDO.taskDO.owner);
                        }
                    }
                    $scope.dependenceTasks = UtilsService.getTaskDO($scope.starShuttleDO.taskDO.relaDOList);
                    setTransmitParameter();
                    $scope.showConfig = true;
                    if ($scope.$parent.setSaveDisabled)
                        $scope.$parent.setSaveDisabled(false, 0);
                }
                else {
                    showAlert('warning', data.messages);
                }
            });
    }

    //显示提示信息
    function showAlert(type, msg) {
        $scope.alert.isShow = true;
        $scope.alert.type = type;
        $scope.alert.msg = msg;
    }

    //不显示提示信息
    function closeAlert() {
        $scope.alert.isShow = false;
    }

    //显示加载信息
    function showLoading(msg) {
        $scope.loading.isShow = true;
        $scope.loading.msg = msg;
    }

    //不显示加载信息
    function closeLoading() {
        $scope.loading.isShow = false;
    }

    //滚动到顶部
    function scrollToTop() {
        $("body").animate({
            scrollTop: 0
        }, "fast");
    }

    //修改任务的调度配置信息
    function saveScheduleConfig() {
        if (!checkScheduleConfig())
            return;
        $scope.message = {
            headerText: '提示',
            bodyText: '确定要修改任务(' + $routeParams.taskId + ')的调度配置信息',
            actionButtonStyle: 'btn-danger',
            showCancel: true
        };
        var modalInstance = $modal.open({
            templateUrl: '/galaxy_app/partials/dialog/message.html',
            controller: MessageCtrl,
            resolve: {
                msg: function () {
                    return $scope.message;
                }
            }
        });
        modalInstance.result.then(function (data) {
            showLoading('保存中');
            updateTask();
        }, function () {
            console.log('Modal dismissed at: ' + new Date());
        });
    }

    function updateTask() {
        $scope.closeAlert();
        showLoading('保存中');
        $scope.starShuttleDO.taskDO.owner = $scope.developer.ID;
        $scope.starShuttleDO.taskDO.relaDOList = UtilsService.getTaskRelaDO($scope.dependenceTasks);
        var saveData = {
            'starShuttleParasDO': getStarShuttleParasDO(),
            'starShuttleDO': $scope.starShuttleDO
        };
        var result = JobManageService.updateTransferTask({}, saveData);
        processSaveScheduleConfig(result);
    }

    function processSaveScheduleConfig(result) {
        result.$promise.then(
            function (data) {
                closeLoading();
                showAlert(data.success ? 'success' : 'warning', data.messages);
                setTransmitParameter();
            });
    }

    //检测调度配置信息
    function checkScheduleConfig() {
        if (!checkSuccessCode())
            return false;
        if (!checkDeveloper())
            return false;
        if (!checkSelfDependence())
            return false;
        return true;
    }

    //检验successCode是否符合条件
    function checkSuccessCode() {
        if (!$scope.starShuttleDO.taskDO.successCode || $scope.starShuttleDO.taskDO.successCode == ''
            || !UtilsService.multiDigitalsSeperatedBySemicolon($scope.starShuttleDO.taskDO.successCode)) {
            showAlert('warning', '成功返回值的格式为单个数字或者多个以;分隔的数字')
            scrollToTop();
            return false;
        }
        return true;
    }

    //检验developer是否符合条件
    function checkDeveloper() {
        if (!$scope.developer || $scope.developer.ID == '') {
            showAlert('warning', '请输入正确的开发者');
            scrollToTop();
            return false;
        }
        return true;
    }

    //检测自依赖
    function checkSelfDependence() {
        for (var i in $scope.dependenceTasks) {
            var taskPreId = $scope.dependenceTasks[i].taskId;
            var cycleGap = $scope.dependenceTasks[i].cycleGap;
            if (taskPreId == $routeParams.taskId && cycleGap.substr(1, 1) == '0') {
                showAlert('warning', '任务依赖于自身，请修改！');
                return false;
            }
        }
        return true;
    }
};

//传输任务高级配置页面对应的controller
function TransferAdvanceConfigCtrl($scope, $modal, $routeParams, TableService, JobManageService) {

    /***********************执行开始***********************/
    initScope();
    addWatches();
    addOnMessages();
    /***********************执行结束***********************/


    /***********************functions***********************/
    function initScope() {
        initParameters();
        initFunctions();
    }

    function initParameters() {
        $scope.alert = {
            type: '',
            msg: '',
            isShow: false
        };
        //页面加载信息
        $scope.loading = {
            data: '',
            isShow: false
        };
        $scope.showConfig = false;
    }

    function initFunctions() {
        //不显示提示信息
        $scope.closeAlert = function () {
            $scope.alert.isShow = false;
        };
    }

    function addWatches() {
        addWatchOnTransmitParameter();
    }

    //监控来自第一个步骤页传递来的信息的变化
    function addWatchOnTransmitParameter() {
        $scope.$watch("$parent.$parent.transmitParameter", function () {
            if ($scope.$parent.$parent.transmitParameter.starShuttleDO) {
                $scope.starShuttleDO = $scope.$parent.$parent.transmitParameter.starShuttleDO;
                $scope.starShuttleParasDO = $scope.$parent.$parent.transmitParameter.starShuttleParasDO;
                $scope.table = $scope.$parent.$parent.transmitParameter.table;
                if ($scope.starShuttleDO.ddl && $scope.starShuttleDO.ddl.trim() !== '') {
                    showAlert('warning', '提示：点击“保存”，该表字段将发生变更，需要重新刷新该表数据');
                }
                $scope.showConfig = true;
                if ($scope.$parent.setSaveDisabled)
                    $scope.$parent.setSaveDisabled(false, 1);
            }
        }, true);
    }

    //添加消息响应
    function addOnMessages() {
        $scope.$on('save', function (e, d) {
            if (d == 1) {
                updateTask();
            }
        });
    }

    //显示提示信息
    function showAlert(type, msg) {
        $scope.alert.isShow = true;
        $scope.alert.type = type;
        $scope.alert.msg = msg;
    }

    //显示加载信息
    function showLoading(msg) {
        $scope.loading.isShow = true;
        $scope.loading.msg = msg;
    }

    //不显示加载信息
    function closeLoading() {
        $scope.loading.isShow = false;
    }

    //滚动到顶部
    function scrollToTop() {
        $("body").animate({
            scrollTop: 0
        }, "fast");
    }

    function processSave(result) {
        result.$promise.then(
            function (data) {
                closeLoading();
                showAlert(data.success ? 'success' : 'warning', data.messages);
            });
    }

    //修改任务
    function updateTask() {
        var info = '是否修改任务: ' + $scope.starShuttleDO.taskDO.taskName + '的高级配置信息?';
        if ($scope.starShuttleDO.ddl && startsWith($scope.starShuttleDO.ddl, 'drop table'))
            info += " (点击确定将会执行删表操作，表中数据将会丢失)";
        $scope.message = {
            headerText: '提示',
            bodyText: info,
            actionButtonStyle: 'btn-primary',
            showCancel: true
        };
        var modalInstance = $modal.open({
            templateUrl: '/galaxy_app/partials/dialog/message.html',
            controller: MessageCtrl,
            resolve: {
                msg: function () {
                    return $scope.message;
                }
            }
        });
        modalInstance.result.then(function (data) {
            $scope.closeAlert();
            showLoading('保存中');
            var saveData = {
                'starShuttleParasDO': $scope.starShuttleParasDO,
                'starShuttleDO': $scope.starShuttleDO
            };
            var result = TableService.updateTransferAdvance({}, saveData);
            processSave(result);
        }, function () {
            console.log('Modal dismissed at: ' + new Date());
        });
    }

    //src是否以tar开头
    function startsWith(src, tar) {
        var reg = new RegExp("^" + tar);
        return reg.test(src);
    }

};