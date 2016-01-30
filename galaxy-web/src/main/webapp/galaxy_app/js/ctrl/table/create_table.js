/**
 * Created by mt on 2014/6/17.
 */
'use strict';

function CreateTableCtrl($scope, $filter, $modal, TableService, component, UserService, UtilsService, UIService, TabStateService) {

    /***********************执行开始***********************/
    TabStateService.setTabState('30');
    addOnMessages();
    initScope();
    setUIElements();
    queryDBs();
    /***********************执行结束***********************/


    /***********************functions***********************/
    function initScope() {
        initParameters();
        initFunctions();
    }

    function initParameters() {
        //当前登录用户的信息
        $scope.user = UserService.getUser();
        //提示信息
        $scope.alert = {
            type: '',
            msg: '',
            isShow: false
        };
        //页面是否正在加载
        $scope.isLoading = false;
        //判断是否获取了table info
        $scope.hasGetTableInfo = false;
        //存储所有的table name
        $scope.allTables = [];
    }

    function initFunctions() {
        //不显示提示信息
        $scope.closeAlert = function () {
            $scope.alert.isShow = false;
        };
        //回车的响应事件
        $scope.enterPress = function (keyEvent) {
            if (keyEvent.which === 13) {
                $scope.submitSearch();
            }
        };
        //查询table的信息
        $scope.submitSearch = function () {
            if (!$scope.searchCondition.sourceTableName) {
                showAlert('warning', '请输入表名');
                return;
            }
            $scope.closeAlert();
            $scope.hasGetTableInfo = false;
            showLoading('载入中');
            var tableInfoResult = TableService.getTableInfo({
                DBType: $scope.searchCondition.sourceDBType,
                DBName: $scope.searchCondition.sourceDBName,
                tableName: $scope.searchCondition.sourceTableName
            });
            processTableInfo(tableInfoResult);
        };
        //点击提交获取建表语句
        $scope.submitToGetSql = function () {
            if (!checkTableInfo())
                return;
            $scope.buildTableInfo.owner = $scope.developer.ID;
            if ($scope.onLineGroupList.length > 1)
                $scope.buildTableInfo.onLineGroupList = [$scope.onLineGroup];
            else
                $scope.buildTableInfo.onLineGroupList = [];

            if ($scope.offLineGroupList.length > 1)
                $scope.buildTableInfo.offLineGroupList = [$scope.offLineGroup];
            else
                $scope.buildTableInfo.offLineGroupList = [];
            showLoading('载入中');
            $scope.closeAlert();
            var sqlResult = TableService.queryCreateTableSql(
                {}, $scope.buildTableInfo
            );
            processQuerySql(sqlResult);
        };
        //获得列实际的index
        $scope.getSequenceNum = function (index) {
            return index + ($scope.table.currentPage - 1) * $scope.table.selectedRecordPerPage;
        };
    }

    function setUIElements() {
        $scope.searchCondition = {
            sourceDBType: 'hive',  //源介质，hive中建表
            sourceDBName: '',
            sourceTableName: ''
        };
        $scope.tableRefreshTypes = UIService.getTableRefreshTypesForQuery();
        //所有galaxy的开发者
        $scope.developers = UtilsService.getDevelopers();
        $scope.developer = UtilsService.getDeveloperByPinyinName($scope.user.pinyinName);
    }


    function addOnMessages() {
        //响应userVerified事件
        $scope.$on('userVerified', function (e, d) {
            $scope.user = UserService.getUser();
            $scope.developer = UtilsService.getDeveloperByPinyinName($scope.user.pinyinName);
        });
    }

    //查询所有的db
    function queryDBs() {
        $scope.closeAlert();
        showLoading('载入中');
        var result = TableService.queryDBs({
            'DBType': $scope.searchCondition.sourceDBType
        });
        processQueryDBs(result);
    }

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

    //检测表注释
    function checkTableComment() {
        if (!$scope.buildTableInfo.tableComment) {
            showAlert('warning', '表的注释必须填写');
            return false;
        }
        return true;
    }

    //检测表的刷新类型
    function checkRefreshType() {
        if ($scope.buildTableInfo.refreshType == 'any') {
            showAlert('warning', '表的刷新类型必须选择');
            return false;
        }
        return true;
    }

    //检测表的列注释
    function checkColumnComment() {
        var colList = $scope.buildTableInfo.columnDOList;
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
        var partitionColList = $scope.buildTableInfo.partitionColumnList;
        for (var j = 0; j < partitionColList.length; j++) {
            if (!partitionColList[j].columnComment) {
                showAlert('warning', '表的分区列 ' + partitionColList[j].columnName + ' 的注释必须填写');
                return false;
            }
        }
        return true;
    }

    //检测线下组账号
    function checkOffLineGroup() {
        if ($scope.buildTableInfo.offLineGroupList.length > 1 &&
            $scope.offLineGroup == $scope.buildTableInfo.offLineGroupList[0]) {
            showAlert('warning', '请选择线下组帐号');
            return false;
        }
        return true;
    }

    //检测线上组账号
    function checkOnLineGroup() {
        if ($scope.buildTableInfo.onLineGroupList.length > 1 &&
            $scope.onLineGroup == $scope.buildTableInfo.onLineGroupList[0]) {
            showAlert('warning', '请选择线上组帐号');
            return false;
        }
        return true;
    }

    //弹出对话框显示建表语句
    var openShowSqlDialog = function () {
        $scope.message = {
            headerText: '建表语句',
            bodyText: $scope.sql
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
        var createTableInfo = {
            buildTabParaDO: $scope.buildTableInfo,
            sql: $scope.sql
        };
        modalInstance.result.then(
            function (data) {
                $scope.closeAlert();
                showLoading('保存中');
                var buildResult = TableService.buildTable(
                    {}, createTableInfo
                );
                process(buildResult);
            },
            function () {
            }
        );
    };

    //处理DB的查询结果
    function processQueryDBs(result) {
        result.$promise.then(
            function (data) {
                $scope.isLoading = false;
                if (data.success) {
                    $scope.sourceDBNames = data.result;
                    $scope.searchCondition.sourceDBName = $scope.sourceDBNames[0];
//                    showLoading('载入中');
//                    $scope.closeAlert();
                    var tableResult = TableService.queryTables({
                        'sourceDBType': $scope.searchCondition.sourceDBType,
                        'sourceDBName': $scope.searchCondition.sourceDBName,
                        'sourceTableName': null
                    });
                    processQueryTables(tableResult);
                }
                else {
                    showAlert('warning', data.messages);
                }
            });
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

    //处理table信息的查询结果
    function processTableInfo(result) {
        result.$promise.then(
            function (data) {
                $scope.isLoading = false;
                if (data.success) {
                    $scope.buildTableInfo = data.result;
                    $scope.hasGetTableInfo = true;
                    if (!$scope.buildTableInfo.refreshType) {
                        $scope.buildTableInfo.refreshType = 'any';
                    }

                    $scope.onLineGroupList = $scope.buildTableInfo.onLineGroupList;
                    $scope.offLineGroupList = $scope.buildTableInfo.offLineGroupList;
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

                    $scope.dataList = $scope.buildTableInfo.columnDOList;
                    $scope.table = component.getCustomizedTable($scope, $filter);
                    $scope.collectionDelim = $scope.buildTableInfo.table.parameters['colelction.delim'];
                    $scope.mapkeyDelim = $scope.buildTableInfo.table.parameters['mapkey.delim'];
                    $scope.serializationFormat = $scope.buildTableInfo.table.parameters['serialization.format'];
                    $scope.lineDelim = $scope.buildTableInfo.table.parameters['line.delim'];
                    $scope.fieldDelim = $scope.buildTableInfo.table.parameters['field.delim'];
                }
                else {
                    showAlert('warning', data.messages);
                }
            });
    }

    //处理查询sql的结果
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
