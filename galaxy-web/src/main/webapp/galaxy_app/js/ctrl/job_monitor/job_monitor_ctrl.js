'use strict';

//任务列表监控的controller
function JobMonitorCtrl($scope, $routeParams, $rootScope, $filter, $modal, JobMonitorService, component, UserService, UtilsService, UIService, TabStateService) {

    /***********************执行开始***********************/
    TabStateService.setTabState('00');
    addOnMessages();
    initScope();
    setUIElements();
    addWatches();
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
        //登陆用户的信息
        $scope.user = UserService.getUser();
        //提示信息
        $scope.alert = {
            type: '',
            msg: '',
            isShow: false
        };
        //是否隐藏结果列表
        $scope.hideTable = true;
        //是否正在加载
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

        //根据pinyin获得中文名
        $scope.getDevelopChineseName = function (pinyinName) {
            return UtilsService.getDeveloperRealName(pinyinName);
        };

        //响应回车事件
        $scope.enterPress = function (keyEvent) {
            if (keyEvent.which === 13) {
                $scope.submitSearch();
            }
        };

        //关闭提示信息
        $scope.closeAlert = function () {
            $scope.alert.isShow = false;
        };

        //提交查询
        $scope.submitSearch = function () {
            $scope.hideTable = true;
            if (!checkQueryCondition())
                return;
            $scope.isLoading = true;
            $scope.closeAlert();
            var result = JobMonitorService.queryInstances({
                'startDate': $scope.startDate,
                'endDate': $scope.endDate,
                'executionCycle': $scope.execCycle == 'A' ? null : $scope.execCycle,
                'developer': UtilsService.getRealDeveloper($scope.developer),
                'executionStatus': $scope.jobStatus,
                'executionPriority': $scope.executionPriority,
                'dependencyIsShow': $scope.dependencyIsShow,
                'taskNameOrId': $scope.taskNameOrId,
                'displayType': 'list'
            });
            processQuery(result);
        };

        //重跑任务
        $scope.reRunJob = function (index) {
            var job = getJobByIndex(index);
            $scope.message = {
                headerText: '提示',
                bodyText: '你是否要重跑任务实例: ' + job.taskStatusId + " ?",
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
                        'instanceId': job.taskStatusId
                    }
                );
                $scope.isLoading = true;
                $scope.closeAlert();
                processStatus(result, 0, index);
            }, function () {
                console.log('Modal dismissed at: ' + new Date());
            });
        };

        //级联重跑任务
        $scope.reRunJobCascaded = function (index) {
            var job = getJobByIndex(index);
            $scope.message = {
                headerText: '级联重跑任务: ' + job.taskStatusId,
                actionButtonStyle: 'btn-danger',
                jobInstanceId: job.taskStatusId,
                time_id: $filter('date')(job.timeId, "yyyy-MM-dd")
            };
            var modalInstance = $modal.open({
                templateUrl: '/galaxy_app/partials/dialog/cascadeRecallTasksDialog.html',
                controller: CascadeRecallTasksCtrl,
                windowClass: 'cascadeRecallTasksDialog',
                resolve: {
                    msg: function () {
                        return $scope.message;
                    }
                }
            });
            modalInstance.result.then(function (data) {
                var result = JobMonitorService.recallCascade(
                    {}, data
                );
                $scope.isLoading = true;
                $scope.closeAlert();
                processStatus(result, 0, index);
            }, function () {
            });
        };

        //置为成功
        $scope.successJob = function (index) {
            var job = getJobByIndex(index);
            $scope.message = {
                headerText: '提示',
                bodyText: '你是否要将这个Job: ' + job.taskStatusId + " 设置为成功?",
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
                        'instanceId': job.taskStatusId
                    }
                );
                $scope.isLoading = true;
                $scope.closeAlert();
                processStatus(result, 1, index);
            }, function () {
            });
        };

        //挂起任务
        $scope.suspendJob = function (index) {
            var job = getJobByIndex(index);
            $scope.message = {
                headerText: '提示',
                bodyText: '你是否要将这个任务实例: ' + job.taskStatusId + " 设置为挂起状态?",
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
                        'instanceId': job.taskStatusId
                    }
                );
                $scope.isLoading = true;
                $scope.closeAlert();
                processStatus(result, 3, index);
            }, function () {
                console.log('Modal dismissed at: ' + new Date());
            });
        };

        //跳转到拓扑图显示页面
        $scope.topologyShow = function (index) {
            var job = getJobByIndex(index);
            var el = document.getElementById('topologyTab').getElementsByTagName('a');
            angular.element(el).triggerHandler('click');
            $('#topologyTab a').trigger('click');
            $rootScope.$broadcast('topologyShow', job);
        };

        //跳转到任务管理页面
        $scope.jobManage = function (index) {
            var job = getJobByIndex(index);
            window.open("#/jobManage/" + job.taskId);
        };

        //任务实例状态分析
        $scope.stateHelp = function (index) {
            var job = getJobByIndex(index);
            $scope.message = {
                headerText: '实例状态分析',
                instanceId: job.taskStatusId,
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

        //批量重跑
        $scope.reRunJobsBySelected = function () {
            var instanceIds = getSelectedIdsByAuthority($scope.table.displayedDataList);
            if (instanceIds.length <= 0) {
                showAlert('warning', '所有选中记录均无权限置为重跑');
                return;
            }
            $scope.message = {
                headerText: '提示',
                bodyText: '你是否要重跑以上 ' + instanceIds.length + ' 个任务实例' + " ?",
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
                var result = JobMonitorService.recallInstances(
                    {}, instanceIds
                );
                $scope.isLoading = true;
                $scope.closeAlert();
                processSelectedStatus(result, 0);
            }, function () {
            });
        };

        //批量置为挂起
        $scope.suspendJobsBySelected = function () {
            var instanceIds = getSelectedIdsByAuthority($scope.table.displayedDataList);
            if (instanceIds.length <= 0) {
                showAlert('warning', '所有选中记录均无权限置为挂起');
                return;
            }
            $scope.message = {
                headerText: '提示',
                bodyText: '你是否要将以上 ' + instanceIds.length + ' 个任务实例设置为挂起状态 ?',
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
                var result = JobMonitorService.suspendInstances(
                    {}, instanceIds
                );
                $scope.isLoading = true;
                $scope.closeAlert();
                processSelectedStatus(result, 3);
            }, function () {
                console.log('Modal dismissed at: ' + new Date());
            });
        };

        //批量置为成功
        $scope.successJobsBySelected = function () {
            var instanceIds = getSelectedIdsByAuthority($scope.table.displayedDataList);
            if (instanceIds.length <= 0) {
                showAlert('warning', '所有选中记录均无权限置为成功');
                return;
            }
            $scope.message = {
                headerText: '提示',
                bodyText: '你是否要将以上 ' + instanceIds.length + ' 个任务实例置为成功 ?',
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
                var result = JobMonitorService.successInstances(
                    {}, instanceIds
                );
                $scope.isLoading = true;
                $scope.closeAlert();
                processSelectedStatus(result, 1);
            }, function () {
            });
        };

        //是否可以只为重跑
        $scope.canReRun = function (status) {
            return (status === 1 || status === -1 || status === 3 || status === 5);
        };

        //是否可以置为挂起
        $scope.canSuspend = function (status) {
            return (status === 0 || status === 6 || status === 7 || status === 5 || status === -1);
        };

        //是否可以置为成功
        $scope.canSuccess = function (status) {
            return (status !== 1);
        };
    }

    function setUIElements() {
        var initDate = UtilsService.dateToStr(new Date(), "yyyy-MM-dd");
        $scope.executionCycleOptions = UIService.getCyclesForQuery();
        $scope.executionStatusOptions = UIService.getStatusesForQuery();
        $scope.executionPriorityOptions = UIService.getPrioLvlsForQuery();
        $scope.dependencyIsShowOptions = UIService.getIfOrNot();
        $scope.developers = UtilsService.getDevelopers();
        $scope.startDate = initDate;
        $scope.endDate = initDate;
        $scope.execCycle = 'A';
        $scope.jobStatus = $scope.executionStatusOptions[2].ID;
        $scope.executionPriority = $scope.executionPriorityOptions[0].ID;
        $scope.dependencyIsShow = $scope.dependencyIsShowOptions[1].ID;
        $scope.taskNameOrId = '';
        $scope.developer = '';
        $scope.isSelectAllJob = false;
        if (!$routeParams.taskId)
            $scope.developer = UtilsService.getDeveloperByPinyinName($scope.user.pinyinName);
    }

    function addWatches() {
        addWatchOnSelectedAll();
    }

    //监控全选选择框
    function addWatchOnSelectedAll() {
        $scope.$watch('isSelectAllJob', function () {
            if (typeof($scope.table) != 'undefined') {
                for (var i = $scope.table.startIndex - 1; i < $scope.table.endIndex; i++) {
                    if ($scope.isSelectAllJob)
                        $scope.table.displayedDataList[i].selected = true;
                    else
                        $scope.table.displayedDataList[i].selected = false;
                }
            }
        });
    }

    function addOnMessages() {
        //响应userVerified事件
        $scope.$on('userVerified', function (e, d) {
            $scope.user = UserService.getUser();
            if (!$routeParams.taskId)
                $scope.developer = UtilsService.getDeveloperByPinyinName($scope.user.pinyinName);
        });
    }

    //显示提示信息
    function showAlert(type, msg) {
        $scope.alert.msg = msg;
        $scope.alert.type = type;
        $scope.alert.isShow = true;
        if (type == 'warning')
            scrollToTop();
    }

    //检测查询条件的输入
    function checkQueryCondition() {
        if (!checkDate())
            return false;
        if (!checkTaskNameOrId())
            return false;
        return true;
    }

    //根据URL传入的taskId查询
    function searchByUrlTaskId() {
        var d = new Date();
        $scope.startDate = $filter("dateFormat")(d.getFullYear() + "-" + (d.getMonth() - 2) + "-" + d.getDate(), 'yyyy-MM-dd', 0);
        $scope.taskNameOrId = $routeParams.taskId;
        $scope.jobStatus = $scope.executionStatusOptions[0].ID;
        $scope.submitSearch();
    }

    //检测开始时间和结束时间
    function checkDate() {
        if ($scope.startDate != 0 && $scope.endDate != 0 && $scope.startDate
            > $scope.endDate) {
            showAlert('warning', '开始时间晚于结束时间');
            return false;
        }
        if ($scope.startDate != $scope.endDate && $scope.taskNameOrId == 0) {
            showAlert('warning', '如果要跨日期查询，请输入任务名称或ID');
            return false;
        }
        return true;
    }

    //检测任务名/ID
    function checkTaskNameOrId() {
        //去除空格
        var reg = /\s/g;
        $scope.taskNameOrId = $scope.taskNameOrId.replace(reg, "");
        if (!UtilsService.checkTaskNameOrId($scope.taskNameOrId)) {
            showAlert('warning', '请检查任务名称或ID输入框，任务名称只能输入一个，任务ID可输入多个并以,分割');
            return false;
        }
        return true;
    }

    //通过权限筛选用户选中的列的id
    var getSelectedIdsByAuthority = function (dataList) {
        var instanceIds = new Array();
        for (var i = 0; i < dataList.length; i++) {
            if (dataList[i].selected && ($scope.user.isAdmin || dataList[i].owner == $scope.user.pinyinName))
                instanceIds.push(dataList[i].taskStatusId);
        }
        return instanceIds;
    };

    //更新选中列的状态
    function updateSelectedStatus(status) {
        angular.forEach($scope.table.displayedDataList, function (job) {
            if (job.selected == true) {
                job.selected = !job.selected;
                job.status = status;
            }
        })
    };

    //根据实际的index获得job
    function getJobByIndex(index) {
        var selectIndex = getSelectedIndex(index);
        return $scope.table.displayedDataList[selectIndex]; //需要设置的任务
    }

    //根据当前页的index获得实际的index
    function getSelectedIndex(index) {
        return  index + ($scope.table.startIndex - 1);
    }

    //滚动到顶部
    function scrollToTop() {
        $("body").animate({
            scrollTop: 0
        }, "fast");
    }

    //处理查询结果设置刷新结果列表
    function processQuery(result) {
        result.$promise.then(
            function (data) {
                $scope.isLoading = false;
                if (data.success) {
                    $scope.dataList = data.results;
                    $scope.table = component.getCustomizedTable($scope, $filter);
                    $scope.table.predicate = 'startTime';
                    $scope.table.reverse=!$scope.table.reverse
                    angular.forEach($scope.table.displayedDataList, function (jobInstance) {
                        jobInstance.selected = false;
                    });
                    $scope.hideTable = false; //获取成功 则显示数据
                }
                else {
                    showAlert('warning', data.messages);
                }
            }
        );
    }

    //更新index列的状态
    function processStatus(result, status, index) {
        result.$promise.then(
            function (data) {
                showAlert(data.success ? 'success' : 'warning', data.messages);
                $scope.isLoading = false;
                if (data.success) {
                    $scope.table.displayedDataList[getSelectedIndex(index)].status = status;
                }
            });
    }

    //更新选中列的状态
    function processSelectedStatus(result, status) {
        result.$promise.then(
            function (data) {
                showAlert(data.success ? 'success' : 'warning', data.messages);
                $scope.isLoading = false;
                if (data.success) {
                    updateSelectedStatus(status);
                }
                $scope.isSelectAllJob = false;
            });
    }
}

