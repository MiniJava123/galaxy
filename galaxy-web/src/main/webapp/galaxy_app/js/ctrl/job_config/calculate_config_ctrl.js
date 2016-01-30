/**
 * Created by Sunny on 14-6-9.
 */

//计算任务整体步骤页面对应的controller
function CalculateConfigCtrl($scope, UserService, $routeParams, CommonService) {
    //用户是够有权限查看页面
    $scope.hasPower = false;

    $scope.jobConfigSteps = [
        {
            ID: '1',
            Text: '调度配置'
        },
        {
            ID: '2',
            Text: '同步表结构'
        }
    ];

    $scope.transmitParameter = {
        taskDO: {}
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

//计算任务调度配置页面对应的controller
function CalculateScheduleConfigCtrl($scope, $modal, $routeParams, JobManageService, UserService, UIService, UtilsService, CommonService, TabStateService) {

    /***********************执行开始***********************/
    addOnMessages();
    initScope();
    setUIElements();
    getOriginTask();
    /***********************执行结束***********************/


    /***********************functions***********************/
    function initScope() {
        initParameters();
        initFunctions();
    }

    function initParameters() {
        //提示信息
        $scope.alert = {
            type: '',
            msg: '',
            isShow: false
        };
        //当前登录用户信息
        $scope.user = UserService.getUser();
        //是否显示详细信息
        $scope.showConfig = false;
        //页面是否正在加载
        $scope.isLoading = false;
        //获取galaxy开发组成员
        getDevelopers();
        //依赖的任务
        $scope.dependenceTasks = [];
    }

    function initFunctions() {
        //不显示提示信息
        $scope.closeAlert = function () {
            $scope.alert.isShow = false;
        };
        //回车的响应事件
        $scope.enterPress = function (keyEvent) {
            if (keyEvent.which === 13) {
                $scope.importGit();
            }
        };
        //导入git
        $scope.importGit = function () {
            if (!checkImportGitParameters())
                return;
            $scope.closeAlert();
            showLoading('载入中');
            var result = CommonService.importGit({
                'projectName': $scope.projectName,
                'fileName': $scope.realDolPath
            });
            processImportGit(result);
        };
        //重新导入git
        $scope.reImport = function () {
            if ($scope.$parent.setSaveDisabled)
                $scope.$parent.setSaveDisabled(true, 0);
            $scope.showConfig = false;
            //填入默认选项
            if ($scope.originTask && $scope.originTask.projectName) {
                $scope.projectName = $scope.originTask.projectName;
                $scope.realDolPath = $scope.originTask.fileName;
            }
        };
        //跳过git导入
        $scope.skipGit = function () {
            if (!checkIsDeveloper())
                return;
            $scope.closeAlert();
            $scope.showConfig = true;
            if ($scope.$parent.setSaveDisabled)
                $scope.$parent.setSaveDisabled(false, 0);
        };
        //配置依赖
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
        //删除依赖
        $scope.deleteDependenceTask = function (index) {
            $scope.message = {
                headerText: '提示',
                bodyText: '是否删除依赖任务: ' + $scope.dependenceTasks[index].taskId + ' ?',
                actionButtonStyle: 'btn-danger',
                showCancel:true
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
            }, function () {
            });
        };
        //执行频率的帮助说明
        $scope.frequencyHelp = function () {
            window.open("https://dper-my.sharepoint.cn/personal/ning_sun_dianping_com/_layouts/15/guestaccess.aspx?guestaccesstoken=Eb7tBbKzQY3d7jGpj4XuBm%2bUUr6VciQzo%2bOqR9VkzqU%3d&docid=091b301a695ac4ddb857d18511f776f22");
        };
        //获得cycle的文字描述
        $scope.getCycleText = function (cycle) {
            return UIService.cycleToText(cycle);
        };
        //获得cycle的css样式
        $scope.getExecutionCycleLabel = function (cycle) {
            return UIService.getCycleCss(cycle);
        };
        //根据cycle获得offset的选项
        $scope.getOffsetOptions = function (cycle) {
            return UIService.getOffsetsByCycle(cycle);
        };
    }

    function addOnMessages() {
        //响应保存事件
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

    function setUIElements() {
        $scope.select = {
            cycleOptions: UIService.getCycles(),
            priorityOptions: UIService.getPrioLvls(),
            ifRecallOptions: UIService.getIfOrNot(),
            offsetOptions: UIService.getTaskOffsets(),
            taskGroupOptions: UIService.getTaskGroups(),
            recallLimitOptions: UIService.getRecallLimits(),
            recallIntervalOptions: UIService.getRecallIntervals(),
            projectNameOptions: UIService.getGitProjects(),
            timeoutOptions: UIService.getTaskTimeouts()
        };
    }

    //获取原有的任务
    function getOriginTask() {
        $scope.closeAlert();
        showLoading('载入中');
        var scheduleResult = JobManageService.getTaskByTaskId({
            'taskId': $routeParams.taskId
        });
        processOriginTask(scheduleResult);
    }

    //git import前检查参数
    function checkImportGitParameters() {
        if (!checkIsDeveloper())
            return false;
        if (!checkDolPath()) {
            showAlert('warning', 'dol路径有误：正确的格式为[GROUP名]/[PRODUCT名]/[DOL名]');
            return false;
        }
        return true;
    }

    //检测是否是galaxy开组组成员
    function checkIsDeveloper() {
        if ($scope.user.isDeveloper)
            return true;
        $scope.message = {
            headerText: '提示',
            bodyText: '您不是开发平台用户组成员，请联系管理员！',
            actionButtonStyle: 'btn-primary',
            showCancel:false
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
        return false;
    }

    //获取galaxy开发组成员
    function getDevelopers() {
        $scope.developers = UtilsService.getDevelopers();
    }

    //显示提示信息
    function showAlert(type, msg) {
        $scope.isLoading = false;
        $scope.alert.isShow = true;
        $scope.alert.type = type;
        $scope.alert.msg = msg;
        if (type == 'warning')
            scrollToTop();
    };

    //滚动到顶部
    function scrollToTop() {
        $("body").animate({
            scrollTop: 0
        }, "fast");
    };

    //检测dol路径的格式
    function checkDolPath() {
        if (!$scope.dolPath)
            return false;
        if (!UtilsService.endWith($scope.dolPath, '.dol'))
            return false;
        var names = getPathNames();
        if ($scope.projectName == 'warehouse')
            return checkWareHouseDolPath(names);
        else
            return checkDataAnalysisDolPath(names);
    }

    //project name是warehouse时检测dol path格式
    function checkWareHouseDolPath(names) {
        if (names.length != 2)
            return false;
        else {
            $scope.group = $scope.projectName;
            $scope.product = names[0];
            $scope.dol = names[1];
            $scope.realDolPath = 'warehouse/' + names[0] + '/' + names[1];
            return true;
        }
    }

    //project name是data_analysis时检测dol path格式
    function checkDataAnalysisDolPath(names) {
        if (names.length != 3)
            return false;
        else {
            $scope.group = names[0];
            $scope.product = names[1];
            $scope.dol = names[2];
            $scope.realDolPath = 'data_analysis/' + names[0] + '/' + names[1] + '/' + names[2];
            return true;
        }
    }

    //获得dol路径的层级结构
    function getPathNames() {
        var names = $scope.dolPath.split('/');
        var pathNames = [];
        for (var index in names) {
            if (names[index])
                pathNames.push(names[index]);
        }
        //忽略与project name相同的第一层
        if (names[0] == $scope.projectName)
            names.splice(0, 1);
        return pathNames;
    };

    function saveScheduleConfig() {
        if (!checkScheduleParameters())
            return;
        updateScheduleConfig();
    }

    //检测调度配置相关参数
    function checkScheduleParameters() {
        if (!checkPara1())
            return false;
        if (!checkSuccessCode())
            return false;
        if (!checkDeveloper())
            return false;
        if (!checkSelfDependence())
            return false;
        return true;
    }

    //检测执行命令
    function checkPara1() {
        if (!$scope.para1 || $scope.para1 == '') {
            showAlert('warning', '执行命令不能为空，请检查');
            return false;
        }
        return true;
    }

    //检测成功返回值
    function checkSuccessCode() {
        if (!$scope.successCode || $scope.successCode == '' || !UtilsService.checkSuccessCode($scope.successCode)) {
            showAlert('warning', '成功返回值的格式为单个数字或者多个以;分隔的数字');
            return false;
        }
        return true;
    }

    //检测开发者
    function checkDeveloper() {
        if ($scope.developer.ID == undefined || $scope.developer.ID == '') {
            showAlert('warning', '请输入正确的开发者');
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

    //修改任务时修改调度配置参数
    var updateScheduleConfig = function () {
        $scope.closeAlert();
        showLoading('保存中');
        var taskDO = getTaskDOForUpdate();
        var result = JobManageService.updateCalculateTask({}, taskDO);
        //修改任务时置为不可用
        $scope.$parent.$parent.transmitParameter.isValid = false;
        processSaveScheduleConfig(result);
    };

    //修改任务时封装taskDO
    function getTaskDOForUpdate() {
        var task = {
            'tableName': $scope.originTask.tableName,
            'taskName': $scope.taskName,
            'remark': $scope.originTask.remark,
            'databaseSrc': $scope.originTask.databaseSrc,
            'taskObj': $scope.originTask.taskObj,
            'logHome': $scope.originTask.logHome,
            'logFile': $scope.originTask.logFile,
            'ifPre': $scope.originTask.ifPre,
            'ifWait': $scope.originTask.ifWait,
            'taskGroupId': $scope.taskGroup,
            'cycle': $scope.cycle,
            'prioLvl': $scope.priority,
            'ifRecall': $scope.ifRecall,
            'recallLimit': $scope.recallLimit,
            'recallInterval': $scope.recallInterval,
            'freq': $scope.frequency,
            'successCode': $scope.successCode,
            'waitCode': $scope.waitCode,
            'offset': $scope.offset,
            'para1': $scope.para1,
            'taskId': $scope.originTask.taskId,
            'ifVal': $scope.originTask.ifVal,
            'owner': $scope.developer.ID,
            'relaDOList': UtilsService.getTaskRelaDO($scope.dependenceTasks),
            'type': $scope.originTask.type,
            'offsetType': $scope.originTask.offsetType,
            'timeout': $scope.originTask.timeout,
            'recallCode': $scope.originTask.recallCode,
            'timeout': $scope.timeout
        };
        return task;
    }

    //对保存结果的处理
    function processSaveScheduleConfig(result) {
        result.$promise.then(
            function (data) {
                $scope.isLoading = false;
                showAlert(data.success ? 'success' : 'warning', data.messages);
                if (data.success) {
                    $scope.originTask = data.results[0];
                    //将参数传递给第二个页面
                    $scope.$parent.$parent.transmitParameter.taskDO = $scope.originTask;
                    //置为可用
                    $scope.$parent.$parent.transmitParameter.isValid = true;
                }
            }
        )
    }

    //获得已有任务后的处理
    function processOriginTask(result) {
        result.$promise.then(
            function (data) {
                if (data.success) {
                    $scope.isLoading = false;
                    $scope.showConfig = true;
                    $scope.originTask = data.result;
                    setOptions();
                    //将参数传递给第二个页面
                    $scope.$parent.$parent.transmitParameter.taskDO = $scope.originTask;
                    //置为可用
                    $scope.$parent.$parent.transmitParameter.isValid = true;
                    if ($scope.$parent.setSaveDisabled)
                        $scope.$parent.setSaveDisabled(false, 0);
                }
                else {
                    showAlert('warning', data.messages);
                }
            }
        );
    };

    //对导入结果的处理
    function processImportGit(result) {
        result.$promise.then(
            function (data) {
                $scope.isLoading = false;
                showAlert(data.success ? 'success' : 'warning', data.messages);
                if (data.success) {
                    var sqlParse = data.results[0];
                    if (sqlParse.childTableNames) {
                        if (sqlParse.childTableNames.length > 1) {
                            showAlert('warning', '任务存在多个目标表，请检查DOL文件');
                            return;
                        } else if (sqlParse.childTableNames.length == 1) {
                            $scope.tableName = sqlParse.childTableNames[0];
                            setTaskGroupByTableName($scope.tableName);
                            $scope.taskName = 'hive##bi.' + $scope.tableName;
                        } else { // length == 0
                            $scope.taskName = 'hive##';
                        }
                    }
                    $scope.dependenceTasks = UtilsService.getTaskDO(sqlParse.parentTaskRelaDOs);
                    $scope.para1 = 'sh /data/deploy/sun/bin/ivy.sh velocity -g ' + $scope.group + ' -p ' + $scope.product +
                        ' -dol ' + $scope.dol + ' -d ${cal_dt} -tid ${task_id} -r ${runtime.recallNum}';
                    $scope.showConfig = true;
                    if ($scope.$parent.setSaveDisabled)
                        $scope.$parent.setSaveDisabled(false, 0);
                }
            }
        );
    };

    //根据已有任务的属性设置界面的元素
    function setOptions() {
        $scope.cycle = $scope.originTask.cycle;
        $scope.priority = $scope.originTask.prioLvl;
        $scope.ifRecall = $scope.originTask.ifRecall;
        $scope.offset = $scope.originTask.offset;
        $scope.taskGroup = $scope.originTask.taskGroupId;
        $scope.recallLimit = $scope.originTask.recallLimit;
        $scope.recallInterval = $scope.originTask.recallInterval;
        $scope.taskName = $scope.originTask.taskName;
        $scope.frequency = $scope.originTask.freq;
        $scope.successCode = $scope.originTask.successCode;
        $scope.para1 = $scope.originTask.para1;
        $scope.waitCode = $scope.originTask.waitCode;
        $scope.dependenceTasks = UtilsService.getTaskDO($scope.originTask.relaDOList);
        $scope.projectName = $scope.select.projectNameOptions[0];
        $scope.developer = UtilsService.getDeveloperByPinyinName($scope.originTask.owner);
        $scope.timeout = $scope.originTask.timeout;
    }

    //根据dol解析出来的表名自动设置任务组
    function setTaskGroupByTableName(tableName) {
        var names = tableName.split('_');
        var group = names[0];
        var index = group.indexOf('.');
        if (index != -1)
            group = group.substr(index + 1);
        switch (group) {
            case 'dpods':
                break;
            case 'dpmid':
                $scope.taskGroup = 2;
                break;
            case 'dm':
            case 'dpdm':
                $scope.taskGroup = 3;
                break;
            case 'rpt':
            case'dprpt':
                $scope.taskGroup = 4;
                break;
            case 'mail':
                $scope.taskGroup = 5;
                break;
            case 'dw':
            case 'dpdw':
                $scope.taskGroup = 6;
                break;
            case 'DQ':
                $scope.taskGroup = 7;
                break;
            case 'atom':
                $scope.taskGroup = 8;
                break;
        }
    }


    //显示加载信息
    function showLoading(msg) {
        $scope.isLoading = true;
        $scope.loadingMsg = msg;
    }
};


//传输任务同步表结构页面对应的controller
function CalculateTableConfigCtrl($scope, $filter, $modal, $routeParams, JobManageService, TableService, UIService, component, UserService, UtilsService, TabStateService) {

    /***********************执行开始***********************/
    TabStateService.setTabState(2, 2);
    addOnMessages();
    initScope();
    setUIElements();
    queryTables();
    addWatches();
    getTargetTable();
    /***********************执行结束***********************/


    /***********************functions***********************/
    function initScope() {
        initParameters();
        initFunctions();
        showAlert('warning', '为了系统的稳定性，如果任务需要写入到目标表，请务必在\'同步表结构\'选择框中选择\'是\'，谢谢！');
    }

    function initParameters() {
        //提示信息
        $scope.alert = {
            type: '',
            msg: '',
            isShow: false
        };
        //页面是否正在加载
        $scope.isLoading = false;
        //是否获得了table的信息，
        $scope.hasGetTableInfo = false;
        $scope.user = UserService.getUser();
        $scope.originalData = [];
        $scope.hasTargetTableDisabled = false;
    }

    function initFunctions() {
        //不显示提示信息
        $scope.closeAlert = function () {
            $scope.alert.isShow = false;
        };
        //获得列实际的index
        $scope.getSequenceNum = function (index) {
            return index + ($scope.showColumnTable.currentPage - 1) * $scope.showColumnTable.selectedRecordPerPage;
        };
        //回车的响应
        $scope.enterPress = function (keyEvent) {
            if (keyEvent.which === 13) {
                $scope.submitSearch();
            }
        };
        //提交查询
        $scope.submitSearch = function () {
            if (!$scope.tableName) {
                showAlert('warning', '请输入表名');
                return;
            }
            if ($scope.$parent.setSaveDisabled)
                $scope.$parent.setSaveDisabled(true, 1);
            $scope.closeAlert();
            $scope.hasGetTableInfo = false;
            showLoading('载入中');
            var tableInfoResult = TableService.getTableInfo({
                DBType: $scope.DBType,
                DBName: $scope.DBName,
                tableName: $scope.tableName
            });
            processTableInfo(tableInfoResult);
        };
    }

    function setUIElements() {
        $scope.DBType = 'hive';
        $scope.DBName = 'bi';
        $scope.tableRefreshTypes = UIService.getTableRefreshTypesForQuery();
        $scope.developer = UtilsService.getDeveloperByPinyinName($scope.user.pinyinName);
        //存储所有的table name
        $scope.allTables = [];
        $scope.hasTargetTableOptions = UIService.getIfOrNot();
    }

    function addOnMessages() {
        //save时间的响应
        $scope.$on('save', function (e, d) {
            if (d == 1) {
                updateTask();
            }
        });
        //响应userVerified事件
        $scope.$on('userVerified', function (e, d) {
            $scope.user = UserService.getUser();
            $scope.developer = UtilsService.getDeveloperByPinyinName($scope.user.pinyinName);
        });
    }

    //点击保存后更新任务
    function updateTask() {
        if ($scope.hasTargetTable)
            getSql();
        else {
            showAlert('success', '保存成功');
        }
    }

    //获得建表语句
    function getSql() {
        if (!checkTableInfo())
            return;
        //默认为空，有则填入
        if ($scope.onLineGroupList.length > 1)
            $scope.buildTabParaDO.onLineGroupList = [$scope.onLineGroup];
        else
            $scope.buildTabParaDO.onLineGroupList = [];

        if ($scope.offLineGroupList.length > 1)
            $scope.buildTabParaDO.offLineGroupList = [$scope.offLineGroup];
        else
            $scope.buildTabParaDO.offLineGroupList = [];
        showLoading('载入中');
        $scope.closeAlert();
        //查询建表语句
        var sqlResult = TableService.queryCreateTableSql(
            {}, $scope.buildTabParaDO
        );
        processQuerySql(sqlResult);
    };

    //建表前检测相关参数
    function checkTableInfo() {
        if (!checkTableComment())
            return false;
        if (!checkRefreshType())
            return false;
        if (!checkColumnComment())
            return false;
        if (!checkPartitionColComment())
            return false;
        if (!checkOffLineGroup())
            return false;
        if (!checkOnLineGroup())
            return false;
        return true;
    }

    //查询所有的table
    function queryTables() {
        $scope.closeAlert();
        showLoading('载入中');
        var tableResult = TableService.queryTables({
            'sourceDBType': $scope.DBType,
            'sourceDBName': $scope.DBName,
            'sourceTableName': null
        });
        processQueryTables(tableResult);
    }

    //处理table的查询结果
    function processQueryTables(result) {
        result.$promise.then(
            function (data) {
                $scope.isLoading = false;
                if (data.success) {
                    for (var i = 0; i < data.results.length; i++)
                        $scope.allTables.push(data.results[i].tableInfo.table_name);
                }
            });
    }

    function addWatches() {
        addWatchOnHasTargetTable();
        addWatchOnIsValid();
    }


    function addWatchOnHasTargetTable() {
        $scope.$watch("hasTargetTable", function () {
            if ($scope.hasTargetTable == 0) {
                if ($scope.$parent.setSaveDisabled)
                    $scope.$parent.setSaveDisabled(false, 1);
            }
            else {
                if ($scope.$parent.setSaveDisabled)
                    $scope.$parent.setSaveDisabled(true, 1);
            }
        });
    }

    function addWatchOnIsValid() {
        $scope.$watch("$parent.$parent.transmitParameter.isValid", function () {
            if ($scope.$parent.$parent.transmitParameter.isValid) {
                $scope.taskDO = $scope.$parent.$parent.transmitParameter.taskDO;
            }
        });
    }

    //检测表注释
    function checkTableComment() {
        if (!$scope.buildTabParaDO.tableComment) {
            showAlert('warning', '表的注释必须填写');
            return false;
        }
        return true;
    }

    //检测表的刷新类型
    function checkRefreshType() {
        if ($scope.buildTabParaDO.refreshType == 'any') {
            showAlert('warning', '表的刷新类型必须选择');
            return false;
        }
        return true;
    }

    //检测表的列注释
    function checkColumnComment() {
        var colList = $scope.buildTabParaDO.columnDOList;
        for (var i = 0; i < colList.length; i++) {
            if (!colList[i].columnComment) {
                showAlert('warning', '表的 ' + colList[i].columnName + ' 列的注释必须填写');
                return false;
            }
        }
        return true;
    }

    //检测表的分区列注释
    function checkPartitionColComment() {
        var partitionColList = $scope.buildTabParaDO.partitionColumnList;
        for (var j = 0; j < partitionColList.length; j++) {
            if (!partitionColList[j].columnComment) {
                showAlert('warning', '表的分区列 ' + partitionColList[i].columnName + ' 的注释必须填写');
                return false;
            }
        }
        return true;
    }

    //检测线下组账号
    function checkOffLineGroup() {
        if ($scope.buildTabParaDO.offLineGroupList.length > 1 &&
            $scope.offLineGroup == $scope.buildTabParaDO.offLineGroupList[0]) {
            showAlert('warning', '请选择线下组帐号');
            return false;
        }
        return true;
    }

    //检测线上组账号
    function checkOnLineGroup() {
        if ($scope.buildTabParaDO.onLineGroupList.length > 1 &&
            $scope.onLineGroup == $scope.buildTabParaDO.onLineGroupList[0]) {
            showAlert('warning', '请选择线上组帐号');
            return false;
        }
        return true;
    }

    //显示建表语句窗口
    function openShowSqlDialog() {
        $scope.message = {
            headerText: '是否执行以下建表语句',
            bodyText: $scope.sql ? $scope.sql : '无'
        };
        var modalInstance = $modal.open({
            templateUrl: '/galaxy_app/partials/dialog/showSqlDialog.html',
            controller: MessageCtrl,
            windowClass: 'taskQueryDialog',
            resolve: {
                msg: function () {
                    return $scope.message;
                }
            }
        });
        modalInstance.result.then(
            function (data) {
                buildTable();
            },
            function () {
            }
        );
    };

    //执行建表语句
    function buildTable() {
        $scope.buildTabParaDO.owner = $scope.taskDO.owner;
        var buildTableInfo = {
            buildTabParaDO: $scope.buildTabParaDO,
            sql: $scope.sql,
            taskID: $routeParams.taskId,
            srcTaskIds: getSrcTaskIds()
        };
        $scope.closeAlert();
        showLoading('载入中');
        var buildResult = TableService.buildTable(
            {}, buildTableInfo
        );
        processBuildTable(buildResult);
    }

    //根据taskId获得目标表的信息
    function getTargetTable() {
        $scope.closeAlert();
        showLoading('载入中');
        var targetTable = JobManageService.getTargetTable(
            {
                taskId: $routeParams.taskId
            }
        );
        processGetTargetTable(targetTable);
    }

    //处理获取目标表信息
    function processGetTargetTable(result) {
        result.$promise.then(
            function (data) {
                $scope.isLoading = false;
                if (data.success) {
                    var targetTable = data.result;
                    if (targetTable) {
                        $scope.hasTargetTableDisabled = true;
                        $scope.hasTargetTable = 1;
                        $scope.tableName = targetTable.table_name;
                    }
                }
                else {
                    $scope.hasTargetTable = 0;
                }
            });
    }

    function getSrcTaskIds() {
        var taskIds = [];
        for (var i = 0; i < $scope.taskDO.relaDOList.length; i++)
            taskIds.push($scope.taskDO.relaDOList[i].taskPreId);
        return taskIds;
    }


    //处理获取建表语句
    function processQuerySql(result) {
        result.$promise.then(
            function (data) {
                $scope.isLoading = false;
                if (data.success) {
                    $scope.sql = data.result;
                    openShowSqlDialog();
                }
                else {
                    showAlert('warning', data.messages);
                }
            });
    }

    //处理表查询结果
    function processTableInfo(result) {
        result.$promise.then(
            function (data) {
                $scope.isLoading = false;
                if (data.success) {
                    if ($scope.$parent.setSaveDisabled)
                        $scope.$parent.setSaveDisabled(false, 1);
                    $scope.buildTabParaDO = data.result;
                    $scope.hasGetTableInfo = true;
                    if (!$scope.buildTabParaDO.refreshType) {
                        $scope.buildTabParaDO.refreshType = 'any';
                    }
                    $scope.onLineGroupList = $scope.buildTabParaDO.onLineGroupList;
                    $scope.offLineGroupList = $scope.buildTabParaDO.offLineGroupList;
                    if ($scope.onLineGroupList.length != 0) {
                        $scope.onLineGroupList.push('--请选择--');
                        $scope.onLineGroupList.reverse();
                    }
                    if ($scope.onLineGroupList.length == 2) {
                        $scope.onLineGroup = $scope.onLineGroupList[1];
                    }
                    if ($scope.onLineGroupList.length > 2) {
                        $scope.onLineGroup = $scope.onLineGroupList[0];
                    }
                    if ($scope.offLineGroupList.length != 0) {
                        $scope.offLineGroupList.push('--请选择--');
                        $scope.offLineGroupList.reverse();
                    }
                    if ($scope.offLineGroupList.length == 2) {
                        $scope.offLineGroup = $scope.offLineGroupList[1];
                    }
                    if ($scope.offLineGroupList.length > 2) {
                        $scope.offLineGroup = $scope.offLineGroupList[0];
                    }

                    $scope.showColumnTable = component.getSpecTialTable($scope, $filter, $scope.buildTabParaDO.columnDOList);
                    angular.copy($scope.showColumnTable, $scope.originalData);
                }
                else {
                    showAlert('warning', data.messages);
                }
            });
    }

    //处理建表结果
    function processBuildTable(result) {
        result.$promise.then(
            function (data) {
                $scope.isLoading = false;
                showAlert(data.success ? 'success' : 'warning', data.messages);
            });
    }

    //显示提示信息
    function showAlert(type, msg) {
        $scope.isLoading = false;
        $scope.alert.isShow = true;
        $scope.alert.type = type;
        $scope.alert.msg = msg;
        if (type == 'warning')
            scrollToTop();
    }

    //滚动到顶部
    function scrollToTop() {
        $("body").animate({
            scrollTop: 0
        }, "fast");
    }

    //显示加载信息
    function showLoading(msg) {
        $scope.isLoading = true;
        $scope.loadingMsg = msg;
    }
}