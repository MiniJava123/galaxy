/**
 * Created by mt on 2014/4/30.
 */
'use strict';

//任务管理页面controller
function JobManageCtrl($scope, $filter, $routeParams, $modal, component, JobManageService, UserService, UIService, UtilsService, TabStateService) {

    /***********************执行开始***********************/
    TabStateService.setTabState('20');
    addOnMessages();
    initScope();
    setUIElements();
    if ($routeParams.taskId) {
        searchByUrlTaskId();
    }
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
        $scope.user = UserService.getUser();
        //是否生效过滤项
        $scope.isValid = 'yes';
        //是否隐藏查询结果
        $scope.hideTable = true;
        //页面是否正在加载
        $scope.isLoading = false;
    }

    function initFunctions() {
        //获得周期的文字描述
        $scope.getCycleText = function (cycle) {
            return UIService.cycleToText(cycle);
        };
        //获得周期的css
        $scope.getExecutionCycleLabel = function (cycle) {
            return UIService.getCycleCss(cycle);
        };
        //获得状态的文字描述
        $scope.getStatusText = function (status) {
            return UIService.statusToText(status);
        };
        //获得优先级的文字描述
        $scope.getPrioLvlText = function (prioLvl) {
            return UIService.prioLvlToText(prioLvl);
        };
        //获得状态的css
        $scope.getStatusLabel = function (status) {
            return UIService.getStatusCss(status);
        };
        //回车的响应时间
        $scope.enterPress = function (keyEvent) {
            if (keyEvent.which === 13) {
                $scope.submitSearch();
            }
        };
        //关闭提示信息
        $scope.closeAlert = function () {
            $scope.alert.isShow = false;
        };
        //根据pinyin获得中文名
        $scope.getDevelopChineseName = function (pinyinName) {
            return UtilsService.getDeveloperRealName(pinyinName);
        };
        //提交查询请求
        $scope.submitSearch = function () {
            var queryResult = JobManageService.queryTasks({
                'group': $scope.jobGroup,
                'cycle': $scope.jobExecCycle == 'A' ? null : $scope.jobExecCycle,
                'developer': UtilsService.getRealDeveloper($scope.jobDeveloper),
                'nameOrId': $scope.jobName == '' ? null : $scope.jobName
            });
            $scope.hideTable = true;
            $scope.closeAlert();
            $scope.isLoading = true;
            processQuery(queryResult);
        };
        $scope.editJob = function (index) {
            var job = getJobByIndex(index);
            if (job.type == 2)
                window.open("#/calculateJobConfig/" + job.taskId);
            else
                window.open("#/transferJobConfig/" + job.taskId);
        };
        //预跑任务
        $scope.preRunJob = function (index) {
            var job = getJobByIndex(index);
            $scope.msg = {
                headerText: '设置预跑任务:' + job.taskId + ' 时间',
                actionButtonStyle: 'btn-primary',
                jobExecCycle: job.cycle
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
                    'taskId': job.taskId,
                    'committer': $scope.user.pinyinName,
                    'type': job.type == 2 ? 'calculate' : 'transfer'
                });
                $scope.isLoading = true;
                $scope.closeAlert();
                process(result);
            }, function () {
            });
        };
        //失效任务
        $scope.invalidJob = function (index) {
            var job = getJobByIndex(index);
            $scope.message = {
                headerText: '警告: 以下任务将会受到影响，请确认是否失效任务' + job.taskId,
                actionButtonStyle: 'btn-danger',
                taskId: job.taskId
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
                    'taskId': job.taskId,
                    'type': job.type == 2 ? 'calculate' : 'transfer'
                });
                $scope.isLoading = true;
                $scope.closeAlert();
                processValid(result, 0);
            }, function () {
                console.log('Modal dismissed at: ' + new Date());
            });
        };
        //生效任务
        $scope.validJob = function (index) {
            var job = getJobByIndex(index);
            $scope.message = {
                headerText: '警告: 以下任务将会被置为生效，请确认是否生效任务' + job.taskId,
                actionButtonStyle: 'btn-danger',
                taskId: job.taskId
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
                    'taskId': job.taskId,
                    'type': job.type == 2 ? 'calculate' : 'transfer'
                });
                $scope.isLoading = true;
                $scope.closeAlert();
                processValid(result, 1);
            }, function () {
            });
        };
        //删除任务
        $scope.deleteJob = function (index) {
            var job = getJobByIndex(index);
            $scope.message = {
                headerText: '警告: 以下任务将会被删除，请确认是否删除任务' + job.taskId,
                actionButtonStyle: 'btn-danger',
                taskId: job.taskId
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
                    'taskId': job.taskId,
                    'type': job.type == 2 ? 'calculate' : 'transfer'
                });
                $scope.isLoading = true;
                $scope.closeAlert();
                processDelete(result);
            }, function () {
            });
        };
        //级联预跑
        $scope.preRunTaskCascaded = function (index) {
            var job = getJobByIndex(index);
            $scope.message = {
                headerText: '级联预跑任务: ' + job.taskId,
                actionButtonStyle: 'btn-primary',
                taskId: job.taskId
            };
            var modalInstance = $modal.open({
                templateUrl: '/galaxy_app/partials/dialog/cascadePreRunTasksDialog.html',
                controller: CascadePreRunTasksCtrl,
                windowClass: 'cascadePreRunTasksDialog',
                resolve: {
                    msg: function () {
                        return $scope.message;
                    }
                }
            });
            modalInstance.result.then(function (data) {
                var result = JobManageService.cascadePreRun(
                    {}, data
                );
                $scope.isLoading = true;
                $scope.closeAlert();
                process(result);
            }, function () {
            });
        };
        //由是否生效过滤项改变触发
        $scope.isValidFilter = function () {
            filterByIsValid();
        }
    }

    function addOnMessages() {
        //响应userVerified消息
        $scope.$on('userVerified', function (e, d) {
            $scope.user = UserService.getUser();
            if (!$routeParams.taskId)
                $scope.jobDeveloper = UtilsService.getDeveloperByPinyinName($scope.user.pinyinName);
        });
    }

    function setUIElements() {
        $scope.taskGroups = UIService.getTaskGroupsForQuery();
        $scope.taskPeriods = UIService.getTaskPeriodsForQuery();
        $scope.developers = UtilsService.getDevelopers();
        $scope.jobGroup = $scope.taskGroups[0].ID;
        $scope.jobExecCycle = 'A';
        $scope.jobDeveloper = '';
        $scope.jobName = '';
        if (!$routeParams.taskId)
            $scope.jobDeveloper = UtilsService.getDeveloperByPinyinName($scope.user.pinyinName);
    }

    //根据实际的index获得job
    function getJobByIndex(index) {
        var selectIndex = getSelectedIndex(index);
        return $scope.table.displayedDataList[selectIndex]; //需要设置的任务
    }

    //根据当前页的index获得实际的index
    function getSelectedIndex(index) {
        return  index + ($scope.table.startIndex - 1);
    }

    //根据URL传入的taskId查询
    function searchByUrlTaskId() {
        $scope.jobName = $routeParams.taskId;
        $scope.submitSearch();
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

    //处理设置生效或失效
    function processValid(result, status) {
        result.$promise.then(
            function (data) {
                showAlert(data.success ? 'success' : 'warning', data.messages);
                $scope.isLoading = false;
                if (data.success) {
                    var taskIds = data.results;
                    for (var i = 0; i < $scope.table.displayedDataList.length; i++) {
                        if (contains($scope.table.displayedDataList[i], taskIds)) {
                            $scope.table.displayedDataList[i].ifVal = status;
                        }
                    }
                }
            });
    }

    //处理删除任务
    function processDelete(result) {
        result.$promise.then(
            function (data) {
                showAlert(data.success ? 'success' : 'warning', data.messages);
                $scope.isLoading = false;
                if (data.success) {
                    var taskIds = data.results;
                    deleteTasksFromTable(taskIds);
                }
            });
    }

    //taskIds中是否包含task的task_id
    function contains(task, taskIds) {
        for (var i = 0; i < taskIds.length; i++) {
            if (taskIds[i] == task.taskId) {
                return true;
            }
        }
        return false;
    }

    //处理查询结果
    function processQuery(result) {
        result.$promise.then(
            function (data) {
                $scope.isLoading = false;
                if (data.success) {
                    $scope.allResult = data.results;
                    filterByIsValid();
                    $scope.table = component.getCustomizedTable($scope, $filter);
                    $scope.hideTable = false; //获取成功 则显示数据
                    $scope.isLoading = false;
                }
                else {
                    showAlert('warning', data.messages);
                }
            }
        );
    }

    //处理预跑
    function process(result) {
        result.$promise.then(
            function (data) {
                showAlert(data.success ? 'success' : 'warning', data.messages);
                $scope.isLoading = false;
            });
    }

    //根据是否生效过滤项过滤显示结果
    function filterByIsValid() {
        $scope.dataList = [];
        var text = $scope.isValid == 'yes' ? '1' : '0';
        for (var i = 0; i < $scope.allResult.length; i++) {
            if ($scope.allResult[i].ifVal == text) {
                $scope.dataList.push($scope.allResult[i])
            }
        }
    }

    //给定taskids从table中移除相应的行
    function deleteTasksFromTable(taskIds) {
        for (var i = 0; i < taskIds.length; i++) {
            for (var j = 0; j < $scope.dataList.length; j++) {
                if (taskIds[i] == $scope.dataList[j].taskId) {
                    $scope.dataList.splice(j, 1);
                    break;
                }
            }
        }
        for (var i = 0; i < taskIds.length; i++) {
            for (var j = 0; j < $scope.allResult.length; j++) {
                if (taskIds[i] == $scope.allResult[j].taskId) {
                    $scope.allResult.splice(j, 1);
                    break;
                }
            }
        }
    }
}
