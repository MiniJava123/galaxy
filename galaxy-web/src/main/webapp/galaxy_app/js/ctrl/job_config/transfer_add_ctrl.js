/**
 * Created by Sunny on 14-6-9.
 */

//传输任务整体步骤页面对应的controller
function TransferAddCtrl($scope, UserService) {
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
        //管理员或者开发者才有权限
        if ($scope.user.isAdmin || $scope.user.isDeveloper) {
            $scope.hasPower = true;
        }
    });
}

//传输任务调度配置页面对应的controller
function TransferScheduleAddCtrl($scope, $filter, $routeParams, $modal, component, TableService, UserService, UtilsService, UIService) {

    /***********************执行开始***********************/
    addOnMessages();
    initScope();
    setUIElements();
    addWatches();
    getSourceTableInfo();
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
        //根据基础信息获得调度配置信息
        $scope.setBasic = function () {
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
                'method': 'taskAdd',
                'taskId': $routeParams.taskId
            });
            processSetBasic(result);
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
        //不显示具体信息需要重新点击确定
        $scope.hideConfig = function () {
            $scope.showConfig = false;
            if ($scope.$parent.setNextDisabled)
                $scope.$parent.setNextDisabled(true, 0);
        }
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
        $scope.dateNumber = $scope.dateNumbers[0];
        $scope.developer = UtilsService.getDeveloperByPinyinName($scope.user.pinyinName);
    }

    //获取galaxy开发组成员
    function getDevelopers() {
        $scope.developers = UtilsService.getDevelopers();
    }

    //添加watch
    function addWatches() {
        addWatchOnTargetDBType();
        addWatchOnTargetTableType();
        addWatchOnDataType();
    }

    //添加消息响应
    function addOnMessages() {
        $scope.$on('save', function (e, d) {
            if (d == 0) {
                //新增时不做任何操作，只是把参数传到第二个步骤
                setTransmitParameter();
            }
        });
        //响应userVerified事件
        $scope.$on('userVerified', function (e, d) {
            $scope.user = UserService.getUser();
            $scope.developer = UtilsService.getDeveloperByPinyinName($scope.user.pinyinName);
        });
    }

    //传递参数给第二个步骤页
    function setTransmitParameter() {
        if (!checkScheduleConfig())
            return;
        $scope.starShuttleDO.taskDO.relaDOList = UtilsService.getTaskRelaDO($scope.dependenceTasks);
        $scope.$parent.$parent.transmitParameter.starShuttleDO = $scope.starShuttleDO;
        $scope.$parent.$parent.transmitParameter.starShuttleParasDO = getStarShuttleParasDO();
        $scope.$parent.$parent.transmitParameter.table = $scope.table;
        $scope.$parent.nextStep();
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

    function addWatchOnTargetDBType() {
        /*根据targetDBType、sourceTableType确定 targetTableType的类型范围
         * 1. sourceDBType: hive,  targetDBType: GpReport,GpAnalysis, 则targetTableType的类型范围根据sourceTableType而定
         * 2. sourceDBType: mysql/sql server,  targetDBType: hive, 则targetTableType的类型范围为四种
         * */
        $scope.$watch("targetDBType", function () {
            if ($scope.targetDBType == 'gp_report' || $scope.targetDBType == 'gp_analysis' || $scope.targetDBType == 'gp_olap') {
                $scope.targetTableTypes = UIService.getTargetTableTypes(1);
                $scope.targetDBName = 'dianpingdw';
            }
            else if ($scope.targetDBType == 'mysql') {
                $scope.targetTableTypes = UIService.getTargetTableTypes(1);
                $scope.targetDBName = 'DWOutput';
            }
            else {
                $scope.targetTableTypes = UIService.getTargetTableTypes(0);
                $scope.targetDBName = 'bi';
            }
            if (!$scope.targetTableType)
                $scope.targetTableType = $scope.targetTableTypes[0].ID;
            $scope.showConfig = false;
            if ($scope.$parent.setNextDisabled)
                $scope.$parent.setNextDisabled(true, 0);
        });
    }

    function addWatchOnTargetTableType() {
        //监控目标表类型是否为增量表、拉链表，如果是，则显示偏移选择信息
        $scope.$watch('targetTableType', function () {
            if ($scope.targetTableType == 'append') {
                $scope.showPartitionInput = true;
                $scope.zipperShow = true;
                $scope.append_or_zipper_name = '增量字段';
            }
            else if ($scope.targetTableType == 'zipper') {
                $scope.showPartitionInput = true;
                $scope.zipperShow = false;
                $scope.append_or_zipper_name = '拉链字段';
            }
            else {
                $scope.showPartitionInput = false;
            }
            $scope.showConfig = false;
            if ($scope.$parent.setNextDisabled)
                $scope.$parent.setNextDisabled(true, 0);
            getTargetTableColumns();
        });
    }

    function addWatchOnDataType() {
        $scope.$watch('dateType', function () {
            if ($scope.dateType == 'D') {
                $scope.dateOffsets = UIService.getDateOffsetsByDateType($scope.dateType);
                $scope.dateOffset = $scope.dateOffsets[0];
                $scope.isDisable = false;
            }
            if ($scope.dateType == 'M') {
                $scope.dateOffsets = UIService.getDateOffsetsByDateType($scope.dateType);
                $scope.dateOffset = $scope.dateOffsets[0];
                $scope.isDisable = false;
            }
            if ($scope.dateType == 'MTD') {
                $scope.dateNumber = $scope.dateNumbers[0];
                $scope.dateOffset = $scope.dateOffsets[0];
                $scope.isDisable = true;
            }
            $scope.showConfig = false;
            if ($scope.$parent.setNextDisabled)
                $scope.$parent.setNextDisabled(true, 0);
        });
    }

    //获得目标数据库类型
    function getTargetDBTypes() {
        $scope.closeAlert();
        showLoading('加载中');
        var result = TableService.queryTargetDBTypes({
            'sourceDBType': $scope.sourceDBType,
            'sourceDBName': $scope.sourceDBName,
            'sourceTableName': $scope.sourceTableName,
            'type': 'add'
        });
        processGetTargetDBTypes(result);
    }

    function processGetTargetDBTypes(result) {
        result.$promise.then(
            function (data) {
                closeLoading();
                if (data.success) {
                    $scope.targetDBTypes = data.result;
                    $scope.targetDBType = $scope.targetDBTypes[0];
                    getTargetSchemaName();
                }
            });
    };

    //通过url路径获得源表的信息
    function getSourceTableInfo() {
        $scope.sourceDBType = $routeParams.sourceDBType;
        $scope.sourceDBName = $routeParams.sourceDBName;
        $scope.sourceTableName = $routeParams.sourceTableName;
        $scope.sourceSchemaName = $routeParams.sourceSchemaName === 'null' ? null : $filter('lowercase')($routeParams.sourceSchemaName);
        $scope.sourceTableType = $routeParams.sourceTableType === 'null' ? null : $filter('lowercase')($routeParams.sourceTableType);
        getTargetDBTypes();
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
            'type': 'add'

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
                }
                else {
                    showAlert('warning', data.messages);
                }
            });
    }

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
                    $scope.targetPartitionName = $scope.targetTableColumns[0].column_name;
                }
            });
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

    //新增任务时获取默认的调度配置信息
    function processSetBasic(result) {
        result.$promise.then(
            function (data) {
                closeLoading();
                if (data.success) {
                    $scope.starShuttleDO = data.result; //所有信息，用来修改并保存
                    $scope.dataList = $scope.starShuttleDO.columns; //show 表字段信息
                    $scope.table = component.getCustomizedTable($scope, $filter);
                    $scope.dependenceTasks = UtilsService.getTaskDO($scope.starShuttleDO.taskDO.relaDOList);
                    $scope.showConfig = true;
                    if ($scope.$parent.setNextDisabled)
                        $scope.$parent.setNextDisabled(false, 0);
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

    //检测调度配置信息
    function checkScheduleConfig() {
        if (!checkSuccessCode())
            return false;
        if (!checkDeveloper())
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
};

//传输任务高级配置页面对应的controller
function TransferAdvanceAddCtrl($scope, $modal, JobManageService) {

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
            $scope.starShuttleDO = $scope.$parent.$parent.transmitParameter.starShuttleDO;
            $scope.starShuttleParasDO = $scope.$parent.$parent.transmitParameter.starShuttleParasDO;
            $scope.table = $scope.$parent.$parent.transmitParameter.table;
            $scope.showConfig = true;
        }, true);
    }

    //添加消息响应
    function addOnMessages() {
        $scope.$on('save', function (e, d) {
            if (d == 1) {
                addTask();
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

    //新增任务
    function addTask() {
        var info = '是否新增任务: ' + $scope.starShuttleDO.taskDO.taskName + '?';
        if ($scope.starShuttleParasDO.targetDBType == 'mysql')
            info += ' (目标表类型为mysql，建表请联系DBA)';
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
            var result = JobManageService.addTransferTask({'saveMethod': 'add'}, saveData);
            processSave(result);
        }, function () {
            console.log('Modal dismissed at: ' + new Date());
        });
    }

    function processSave(result) {
        result.$promise.then(
            function (data) {
                closeLoading();
                showAlert(data.success ? 'success' : 'warning', data.messages);
            });
    }
};