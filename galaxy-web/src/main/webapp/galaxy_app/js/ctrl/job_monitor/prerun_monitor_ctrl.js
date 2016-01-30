'use strict';

//预跑监控页面对应的controller
function PreRunMonitorCtrl($scope, $filter, $modal, JobMonitorService, component, UserService, UtilsService, UIService, TabStateService) {

    /***********************执行开始***********************/
    TabStateService.setTabState('01');
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
        //登陆用户的信息
        $scope.user = UserService.getUser();
        //提示信息
        $scope.alert = {
            type: '',
            msg: '',
            isShow: false
        };
        $scope.originalData = [];
        //是否隐藏查询结果
        $scope.hideTable = true;
        //页面是否正在加载
        $scope.isLoading = false;
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
        //提交查询请求
        $scope.submitSearch = function () {
            $scope.hideTable = true;
            $scope.closeAlert();
            if (!checkQueryCondition())
                return;
            queryPreRunInstances();
        };
        //获得cycle的文件描述
        $scope.getCycleText = function (cycle) {
            return UIService.cycleToText(cycle);
        };
        //获得cycle的样式
        $scope.getExecutionCycleLabel = function (cycle) {
            return UIService.getCycleCss(cycle);
        };
        //获得状态的文字描述
        $scope.getStatusText = function (status) {
            return UIService.statusToText(status);
        };
        //获得开发者的中文名
        $scope.getDevelopChineseName = function (pinyinName) {
            return UtilsService.getDeveloperRealName(pinyinName);
        };
        //查看日志
        $scope.viewLog = function (path) {
            window.open(getLogHref(path));
        };
        //是否可以置为重跑
        $scope.canReRun = function (status) {
            return (status === 1 || status === -1 || status === 3 || status === 5);
        };
        //是否可以置为成功
        $scope.canSuccess = function (status) {
            return (status !== 1);
        };
        //是否可以置为挂起状态
        $scope.canSuspend = function (status) {
            return (status === 0 || status === 6 || status === 7 || status === 5 || status === -1);
        };
        //重跑任务
        $scope.reRunJob = function (taskIndex, instanceIndex) {
            var instance = getInstanceByIndexes(taskIndex, instanceIndex);
            $scope.message = {
                headerText: '提示',
                bodyText: '你是否要重跑 任务实例: ' + instance.instanceID + " ?",
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
                var result = JobMonitorService.recallInstance({
                        'instanceId': instance.instanceID
                    }
                );
                $scope.isLoading = true;
                $scope.closeAlert();
                processStatus(result, 0, taskIndex, instanceIndex);
            }, function () {
            });
        };
        //置为成功
        $scope.successJob = function (taskIndex, instanceIndex) {
            var instance = getInstanceByIndexes(taskIndex, instanceIndex);
            $scope.message = {
                headerText: '提示',
                bodyText: '你是否要将这个任务实例: ' + instance.instanceID + " 设置为成功?",
                actionButtonStyle: 'btn-primary',
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
                var result = JobMonitorService.successInstance({
                        'instanceId': instance.instanceID
                    }
                );
                $scope.isLoading = true;
                $scope.closeAlert();
                processStatus(result, 1, taskIndex, instanceIndex);
            }, function () {
            });
        };
        //挂起任务
        $scope.suspendJob = function (taskIndex, instanceIndex) {
            var instance = getInstanceByIndexes(taskIndex, instanceIndex);
            $scope.message = {
                headerText: '提示',
                bodyText: '你是否要将这个任务实例: ' + instance.instanceID + " 设置为挂起状态?",
                actionButtonStyle: 'btn-primary',
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
                var result = JobMonitorService.suspendInstance({
                        'instanceId': instance.instanceID
                    }
                );
                $scope.isLoading = true;
                $scope.closeAlert();
                processStatus(result, 3, taskIndex, instanceIndex);
            }, function () {
            });
        };

        //任务实例状态分析
        $scope.stateHelp = function (taskIndex, instanceIndex) {
            var job = getInstanceByIndexes(taskIndex, instanceIndex);
            $scope.message = {
                headerText: '实例状态分析',
                instanceId: job.instanceID,
                actionButtonStyle: 'btn-primary',
                showCancel:false
            };
            var modalInstance = $modal.open({
                templateUrl: '/galaxy_app/partials/dialog/message.html',
                controller: InstanceStateAnalyzeCtrl,
                resolve: {
                    msg: function () {
                        return $scope.message;
                    }
                }
            });
        };
    }

    function setUIElements() {
        $scope.developers = UtilsService.getDevelopers();
        var d = new Date(new Date().getTime());
        $scope.startDate = $filter('dateFormat')(d.getFullYear() + "-" + (d.getMonth() + 1) + "-" + d.getDate(), 'yyyy-MM-dd', 0);
        $scope.taskCommitter = '';
        $scope.taskNameOrId = '';
        if ($scope.user.pinyinName != '')
            $scope.taskCommitter = UtilsService.getDeveloperByPinyinName($scope.user.pinyinName);
    }

    function addOnMessages() {
        //响应userVerified事件
        $scope.$on('userVerified', function (e, d) {
            $scope.user = UserService.getUser();
            $scope.taskCommitter = UtilsService.getDeveloperByPinyinName($scope.user.pinyinName);
        });
    }

    function checkQueryCondition() {
        if ($scope.startDate == '') {
            showAlert('warning', '预跑开始时间不能为空');
            return false;
        }
        var currentDate = $filter('dateFormat')(new Date(), "yyyy-MM-dd", 1);
        var curDateForCompare = new Date(
            Date.parse((currentDate).replace(/\-/g, "/")));
        var chooseDateForCompare = new Date(
            Date.parse(($scope.startDate).replace(/\-/g, "/")));
        if (chooseDateForCompare > curDateForCompare) {
            showAlert('warning', '预跑开始时间不能大于当前时间');
            return false;
        }
        var diffDay = parseInt(curDateForCompare.getTime() - chooseDateForCompare.getTime()) / (1000 * 3600 * 24);
        if (diffDay > 7 && !$scope.taskNameOrId) {
            showAlert('warning', '预跑开始时间与当前时间相差7天以上时，请输入taskID或者taskName');
            return false;
        }
        return true;
    }

    function getLogHref(logPath) {
        return '#/jobMonitor/viewLog/' + $filter("StringReplace")(logPath, '\\/', '+');
    }

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

    //查询预跑实例
    function queryPreRunInstances() {
        var result = JobMonitorService.queryPreRunInstances(
            {
                startDate: $scope.startDate,
                taskCommitter: UtilsService.getRealDeveloper($scope.taskCommitter),
                taskNameOrId: $scope.taskNameOrId
            }
        );
        $scope.isLoading = true;
        $scope.closeAlert();
        $scope.hideTable = true;
        process(result);
    }

    //处理查询结果
    function process(result) {
        result.$promise.then(
            function (data) {
                $scope.isLoading = false;
                $scope.hideTable = false;
                if (data.success) {
                    $scope.dataList = data.result;
                    $scope.table = component.getCustomizedTable($scope, $filter);
                    $scope.jobInstanceTable = new Array();
                    for (var i = 0; i < $scope.dataList.length; i++) {
                        $scope.jobInstanceTable.push(component.getSpecTialTable($scope, $filter, $scope.dataList[i].instanceList));
                    }
                    angular.copy($scope.jobInstanceTable, $scope.originalData);
                }
                else {
                    showAlert('warning', data.messages);
                }
            }
        );
    }

    //更新index列的状态
    function processStatus(result, status, taskIndex, instanceIndex) {
        result.$promise.then(
            function (data) {
                showAlert(data.success ? 'success' : 'warning', data.messages);
                $scope.isLoading = false;
                if (data.success) {
                    var instance = getInstanceByIndexes(taskIndex, instanceIndex);
                    instance.status = status;
                }
            });
    }

    function getTaskByIndex(index) {
        return $scope.jobInstanceTable[index + $scope.table.startIndex - 1];
    }

    function getInstanceByIndexes(taskIndex, instanceIndex) {
        var task = getTaskByIndex(taskIndex);
        return task.displayedDataList[instanceIndex + task.startIndex - 1];
    }
}


function PreRunWatchCtrl($scope, $filter) {

    /***********************执行开始***********************/
    initScope();
    addWatches();
    /***********************执行结束***********************/


    /***********************functions***********************/
    function initScope() {
        initParameters();
        initFunctions();
    }

    function initFunctions() {
        //根据item的status返回是否是unsuccess
        $scope.unsuccessFilter = function (item) {
            if (item.status !== 1)
                return true;
        };
        //根据item的status返回是否是success
        $scope.successFilter = function (item) {
            if (item.status === 1)
                return true;
        };
    }

    function initParameters() {
        $scope.curIndex = $scope.$parent.$parent.$index + $scope.table.startIndex - 1;
    }

    function addWatches() {
        //当前页的变化
        $scope.$watch('jobInstanceTable[$parent.$parent.$index + table.startIndex - 1].currentPage', function (newval, oldval, scope) {
            $scope.jobInstanceTable[$scope.curIndex].updateIndexes();
        });
        //每页显示条数的变化
        $scope.$watch('jobInstanceTable[$parent.$parent.$index + table.startIndex - 1].selectedRecordPerPage', function (newval, oldval, scope) {
            $scope.jobInstanceTable[$scope.curIndex].currentPage = 1;
            $scope.jobInstanceTable[$scope.curIndex].updateIndexes();
            $scope.jobInstanceTable[$scope.curIndex].updatePageNumber();
        });
        //总条数的变化
        $scope.$watch('jobInstanceTable[$parent.$parent.$index + table.startIndex - 1].total', function (newval, oldval, scope) {
            if ($scope.jobInstanceTable[$scope.curIndex].total == 0) {
                $scope.jobInstanceTable[$scope.curIndex].startIndex = 0;
                $scope.jobInstanceTable[$scope.curIndex].endIndex = 0;
            } else {
                $scope.jobInstanceTable[$scope.curIndex].updateIndexes();
                $scope.jobInstanceTable[$scope.curIndex].updatePageNumber();
            }
        });
        //文字过滤条件的变化
        $scope.$watch('jobInstanceTable[$parent.$parent.$index + table.startIndex - 1].query', function (newval, oldval, scope) {
            $scope.jobInstanceTable[$scope.curIndex].displayedDataList = $filter('filter')($scope.originalData[$scope.curIndex].displayedDataList, $scope.jobInstanceTable[$scope.curIndex].query);
            $scope.jobInstanceTable[$scope.curIndex].total = $scope.jobInstanceTable[$scope.curIndex].displayedDataList.length;
            $scope.jobInstanceTable[$scope.curIndex].currentPage = 1;
        });
        //排序方式的变化
        $scope.$watchCollection('[jobInstanceTable[$parent.$parent.$index + table.startIndex - 1].predicate, jobInstanceTable[$parent.$parent.$index + table.startIndex - 1].reverse]', function (newval, oldval, scope) {
            $scope.jobInstanceTable[$scope.curIndex].displayedDataList = $filter('orderBy')($scope.jobInstanceTable[$scope.curIndex].displayedDataList, $scope.jobInstanceTable[$scope.curIndex].predicate, $scope.jobInstanceTable[$scope.curIndex].reverse);
        });
        //显示数据的变化
        $scope.$watchCollection('jobInstanceTable[$parent.$parent.$index + table.startIndex - 1].displayedDataList', function (newval, oldval, scope) {
            if ($scope.jobInstanceTable[$scope.curIndex].displayedDataList.query) {
                $scope.jobInstanceTable[$scope.curIndex].displayedDataList = $filter('filter')($scope.jobInstanceTable[$scope.curIndex].displayedDataList, $scope.jobInstanceTable[$scope.curIndex].query);
            }
            if ($scope.jobInstanceTable[$scope.curIndex].reverse) {
                $scope.jobInstanceTable[$scope.curIndex].displayedDataList = $filter('orderBy')($scope.jobInstanceTable[$scope.curIndex].displayedDataList, $scope.jobInstanceTable[$scope.curIndex].predicate, $scope.jobInstanceTable[$scope.curIndex].reverse);
            }
            $scope.jobInstanceTable[$scope.curIndex].currentPage = 1;
            $scope.jobInstanceTable[$scope.curIndex].updateIndexes();
            $scope.jobInstanceTable[$scope.curIndex].updatePageNumber();
            $scope.jobInstanceTable[$scope.curIndex].total = $scope.jobInstanceTable[$scope.curIndex].displayedDataList.length;
        });
        //status过滤条件的变化
        $scope.$watch('jobInstanceTable[$parent.$parent.$index + table.startIndex - 1].showStatus', function () {
            var status = $scope.jobInstanceTable[$scope.curIndex].showStatus;
            switch (status) {
                case -3:
                    $scope.jobInstanceTable[$scope.curIndex].displayedDataList = $scope.originalData[$scope.curIndex].displayedDataList;
                    break;
                case -2:
                    $scope.jobInstanceTable[$scope.curIndex].displayedDataList = $filter('filter')($scope.originalData[$scope.curIndex].displayedDataList, $scope.unsuccessFilter, true);
                    break;
                case 1:
                    $scope.jobInstanceTable[$scope.curIndex].displayedDataList = $filter('filter')($scope.originalData[$scope.curIndex].displayedDataList, $scope.successFilter, true);
                    break;
                default :
                    $scope.jobInstanceTable[$scope.curIndex].displayedDataList = $filter('filter')($scope.originalData[$scope.curIndex].displayedDataList, {status: status}, true);
                    break;
            }
        });
    }
}