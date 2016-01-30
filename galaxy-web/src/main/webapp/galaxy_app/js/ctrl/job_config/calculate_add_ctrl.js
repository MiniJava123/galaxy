/**
 * Created by Sunny on 14-6-9.
 */

//计算任务整体步骤页面对应的controller
function CalculateAddCtrl($scope) {

    $scope.jobConfigSteps = [
        {
            ID: '1',
            Text: '调度配置'
        },
        {
            ID: '2',
            Text: '目标表配置'
        }
    ];

    $scope.transmitParameter = {
        isValid: false,
        taskDO: {},
        //-1表示未进行git导入，是否有目标表未知；
        // 0表示进行了git导入，且不存在目标表
        // 1表示进行了git导入，且存在目标表
        hasTargetTable: -1
    };
}

//计算任务调度配置页面对应的controller
function CalculateScheduleAddCtrl($scope, $modal, JobManageService, UserService, UIService, UtilsService, CommonService, TabStateService) {

    /***********************执行开始***********************/
    TabStateService.setTabState('21');
    addOnMessages();
    initScope();
    setUIElements();
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
            $scope.showConfig = false;
            if ($scope.$parent.setNextDisabled)
                $scope.$parent.setNextDisabled(true, 0);
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
            if ($scope.$parent.setNextDisabled)
                $scope.$parent.setNextDisabled(false, 0);
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
        //检查task name
        $scope.checkTaskName = function () {
            if ($scope.taskName.lastIndexOf('hive##') != 0 || $scope.taskName.indexOf('.') < 7 || $scope.taskName.indexOf('.') == $scope.taskName.length - 1) {
                showAlert('warning', '任务名的格式为：hive##数据库.表或hive##模式.表，请检查');
                return;
            }
            var result = JobManageService.checkTaskName({
                'taskName': $scope.taskName
            });
            process(result);
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
                //不做任何操作，只是把参数传到第二个步骤
                setTransmitParameter();
            }
        });
        //响应userVerified事件
        $scope.$on('userVerified', function (e, d) {
            $scope.user = UserService.getUser();
            $scope.developer = UtilsService.getDeveloperByPinyinName($scope.user.pinyinName);
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
        $scope.developer = UtilsService.getDeveloperByPinyinName($scope.user.pinyinName);
        $scope.cycle = 'D';
        $scope.priority = $scope.select.priorityOptions[3].ID;
        $scope.ifRecall = $scope.select.ifRecallOptions[0].ID;
        $scope.offset = $scope.select.offsetOptions[0];
        $scope.taskGroup = $scope.select.taskGroupOptions[1].ID;
        $scope.timeout = $scope.select.timeoutOptions[2].ID;
        $scope.recallLimit = $scope.select.recallLimitOptions[2];
        $scope.recallInterval = $scope.select.recallIntervalOptions[9].ID;
        $scope.projectName = $scope.select.projectNameOptions[0];
        $scope.frequency = '0 5 0 * * ?';
        $scope.successCode = '0';
        $scope.para1 = '';
        $scope.waitCode = '';
        $scope.showConfig = false;
        $scope.taskName = 'hive##';
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
        if ($scope.user.isDeveloper) {
            return true;
        }
        else {
            showAlert('warning', '您不是开发平台用户组成员，请联系管理员！');
            return false;
        }
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

    function setTransmitParameter() {
        if (!checkScheduleParameters())
            return;
        if ($scope.taskName.lastIndexOf('hive##') != 0 || $scope.taskName.indexOf('.') < 7 || $scope.taskName.indexOf('.') == $scope.taskName.length - 1) {
            showAlert('warning', '任务名的格式为：hive##数据库.表或hive##模式.表，请检查');
            return;
        }
        $scope.$parent.$parent.transmitParameter.taskDO = getTaskDOForAdd();
        $scope.$parent.$parent.transmitParameter.isValid = true;
        $scope.$parent.nextStep();
    }

    //检测调度配置相关参数
    function checkScheduleParameters() {
        if (!checkPara1())
            return false;
        if (!checkSuccessCode())
            return false;
        if (!checkDeveloper())
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

    //新增任务时封装taskDO
    function getTaskDOForAdd() {
        var task = {
            'tableName': $scope.tableName ? $scope.tableName : UtilsService.getTableNameByTaskName($scope.taskName),
            'taskName': $scope.taskName,
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
            'taskId': -1,
            'ifVal': 1,
            'owner': $scope.developer.ID,
            'relaDOList': UtilsService.getTaskRelaDO($scope.dependenceTasks),
            'timeout': $scope.timeout
        };
        return task;
    }

    //根据处理结果显示提示信息
    function process(result) {
        result.$promise.then(
            function (data) {
                $scope.isLoading = false;
                showAlert(data.success ? 'success' : 'warning', data.messages);
            }
        )
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
                            $scope.taskName = 'hive##bi.' + $scope.tableName;
                            setTaskGroupByTableName($scope.tableName);
                            $scope.$parent.$parent.transmitParameter.hasTargetTable = 1;
                        } else { // length == 0
                            $scope.taskName = 'hive##';
                            $scope.$parent.$parent.transmitParameter.hasTargetTable = 0;
                        }
                    }
                    $scope.dependenceTasks = UtilsService.getTaskDO(sqlParse.parentTaskRelaDOs);
                    $scope.para1 = 'sh /data/deploy/sun/bin/ivy.sh velocity -g ' + $scope.group + ' -p ' + $scope.product +
                        ' -dol ' + $scope.dol + ' -d ${cal_dt} -tid ${task_id}';
                    $scope.showConfig = true;
                    if ($scope.$parent.setNextDisabled)
                        $scope.$parent.setNextDisabled(false, 0);
                }
            }
        );
    };

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
function CalculateTableAddCtrl($scope, $filter, $modal, JobManageService, TableService, UIService, component, UserService, UtilsService, TabStateService) {

    /***********************执行开始***********************/
    addOnMessages();
    addWatches();
    initScope();
    setUIElements();
    queryTables();
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
            if ($scope.$parent.setNextDisabled)
                $scope.$parent.setNextDisabled(true, 1);
            if (!$scope.tableName) {
                showAlert('warning', '请输入表名');
                return;
            }
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
        //存储所有的table name
        $scope.allTables = [];
        $scope.hasTargetTableOptions = UIService.getIfOrNot();
    }


    function addOnMessages() {
        //save时间的响应
        $scope.$on('save', function (e, d) {
            if (d == 1) {
                addTask();
            }
        });
        //响应userVerified事件
        $scope.$on('userVerified', function (e, d) {
            $scope.user = UserService.getUser();
            $scope.developer = UtilsService.getDeveloperByPinyinName($scope.user.pinyinName);
        });
    }

    function addWatches() {
        addWatchOnIsValid();
        addWatchOnHasTargetTable();
    }

    //点击保存后新增任务
    function addTask() {
        if ($scope.hasTargetTable)
            getSql();
        else
            addTaskWithoutSyncTable();
    }

    //直接新增任务，不同步表结构
    function addTaskWithoutSyncTable() {
        var calculateTaskAddDO = {
            taskDO: $scope.$parent.$parent.transmitParameter.taskDO,
            buildTableInfoDO: null
        };
        showLoading('保存中');
        $scope.closeAlert();
        var buildResult = JobManageService.addCalculateTask(
            {}, calculateTaskAddDO
        );
        process(buildResult);
    }

    function addTaskWithSyncTable() {
        $scope.buildTabParaDO.owner = $scope.$parent.$parent.transmitParameter.taskDO.owner;
        var buildTableInfoDO = {
            buildTabParaDO: $scope.buildTabParaDO,
            sql: $scope.sql,
            taskID: parseInt($scope.taskId)
        };
        var calculateTaskAddDO = {
            taskDO: $scope.$parent.$parent.transmitParameter.taskDO,
            buildTableInfoDO: buildTableInfoDO
        };
        showLoading('保存中');
        $scope.closeAlert();
        var buildResult = JobManageService.addCalculateTask(
            {}, calculateTaskAddDO
        );
        process(buildResult);
    }

    //获取建表语句
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
        $scope.closeAlert();
        showLoading('载入中');
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

    function addWatchOnIsValid() {
        $scope.$watch("$parent.$parent.transmitParameter.isValid", function () {
            if ($scope.$parent.$parent.transmitParameter.isValid) {
                $scope.taskId = $scope.$parent.$parent.transmitParameter.taskDO.taskId;
                var name = $scope.$parent.$parent.transmitParameter.taskDO.tableName;
                $scope.tableName = name.indexOf(".") != -1 ? name.split(".")[1] : name;
                $scope.hasTargetTable = $scope.$parent.$parent.transmitParameter.hasTargetTable == 0 ? 0 : 1;
            }
        });
    }

    function addWatchOnHasTargetTable() {
        $scope.$watch("hasTargetTable", function () {
            if ($scope.hasTargetTable == 0) {
                if ($scope.$parent.setNextDisabled)
                    $scope.$parent.setNextDisabled(false, 1);
            }
            else {
                if ($scope.$parent.setNextDisabled)
                    $scope.$parent.setNextDisabled(true, 1);
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
            headerText: '是否新增任务并执行以下建表语句',
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
                addTaskWithSyncTable();
            },
            function () {
            }
        );
    };

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
                    if ($scope.$parent.setNextDisabled)
                        $scope.$parent.setNextDisabled(false, 1);
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

    //根据处理结果显示提示信息
    function process(result) {
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

//同步表结构步骤中的一些watch的controller
function SyncTableToolWatchCtrl($scope, $filter) {

    $scope.$watch('showColumnTable.currentPage', function (newval, oldval, scope) {
        if ($scope.showColumnTable) {
            $scope.showColumnTable.updateIndexes();
        }
    });
    $scope.$watch('showColumnTable.selectedRecordPerPage', function (newval, oldval, scope) {
        if ($scope.showColumnTable) {
            $scope.showColumnTable.currentPage = 1;
            $scope.showColumnTable.updateIndexes();
            $scope.showColumnTable.updatePageNumber();
        }
    });
    $scope.$watch('showColumnTable.total', function (newval, oldval, scope) {
        if ($scope.showColumnTable) {
            if ($scope.showColumnTable.total == 0) {
                $scope.showColumnTable.startIndex = 0;
                $scope.showColumnTable.endIndex = 0;
            } else {
                $scope.showColumnTable.updateIndexes();
                $scope.showColumnTable.updatePageNumber();
            }
        }
    });

    $scope.$watch('showColumnTable.query', function (newval, oldval, scope) {
        if ($scope.showColumnTable) {
            $scope.showColumnTable.displayedDataList = $filter('filter')($scope.originalData.displayedDataList, $scope.showColumnTable.query);
            $scope.showColumnTable.total = $scope.showColumnTable.displayedDataList.length;
            $scope.showColumnTable.currentPage = 1;
        }
    });
    $scope.$watchCollection('[showColumnTable.predicate, showColumnTable.reverse]', function (newval, oldval, scope) {
        if ($scope.showColumnTable) {
            $scope.showColumnTable.displayedDataList = $filter('orderBy')($scope.showColumnTable.displayedDataList, $scope.showColumnTable.predicate, $scope.showColumnTable.reverse);
        }
    });
    $scope.$watchCollection('showColumnTable.displayedDataList', function (newval, oldval, scope) {
        if ($scope.showColumnTable) {
            if ($scope.showColumnTable.displayedDataList.query) {
                $scope.showColumnTable.displayedDataList = $filter('filter')($scope.showColumnTable.displayedDataList, $scope.showColumnTable.query);
            }
            if ($scope.showColumnTable.reverse) {
                $scope.showColumnTable.displayedDataList = $filter('orderBy')($scope.showColumnTable.displayedDataList, $scope.showColumnTable.predicate, $scope.showColumnTable.reverse);
            }
            $scope.showColumnTable.total = $scope.showColumnTable.displayedDataList.length;
        }
    });
}