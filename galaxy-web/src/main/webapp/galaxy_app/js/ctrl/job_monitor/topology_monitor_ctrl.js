/**
 * Created by Sunny on 14-5-20.
 */

'use strict';

//任务拓扑图监控Controller
function TopologyMonitorCtrl($scope, $filter, $modal, JobMonitorService, UserService, UIService, UtilsService, TabStateService) {

    /***********************执行开始***********************/
    //拓扑图对象
    var _topology;
    //创建拓扑图所必须的设置
    var _topologyOptions = {
        //layerHeight: 80,
        colorMap: {
            '-1': '#FF3030',
            '1': '#458B00',
            '0': '#E3E3E3',
            '2': '#63B8FF',
            '3': '#878787',
            '4': '#EEEE00',
            '5': '#EEAD0E',
            '6': '#FFFCC7',
            '7': '#0000FF'
        },
        edgeColor: {
            error: '#c0c0c0',
            normal: '#c0c0c0'
        },
        singleMax: 10,
        keepLayerWhenDrag: true,
        layerHeight: 100,
        //点击节点的响应事件
        click: function (e, item) {
            if ($('.info-dialog[node-id=' + item.id + ']').length > 0) {
                return;
            }
            _topology.forEach(function (node) {
                node.view.attr({
                    'stroke': '#C09853',
                    'stroke-width': '0px'
                });
            });
            var eventTarget = e.srcElement ? e.srcElement : e.target; //兼容火狐、IE
            var $infoDiv = $('<div id="info-dialog"/>')
                .css({
                    'top': $(eventTarget).offset().top + 'px',
                    'left': ($(document).width() - $(eventTarget).offset().left < 780
                        ? $(eventTarget).offset().left - 740
                        : $(eventTarget).offset().left) + 'px'
                }).attr('node-id', item.id).appendTo($(document.body));
            getTaskInfo(item, $infoDiv);
            $('#info-dialog').show();
            item.view.attr({ 'stroke-width': '4px' });
            e.stopPropagation();
        }
    };
    addOnMessages();
    initScope();
    setUIElements();
    addWatches();
    /***********************执行开始***********************/


    /***********************functions***********************/
    function initScope() {
        initParameters();
        initFunctions();
    }

    function initParameters() {
        //登陆用户的信息
        $scope.user = UserService.getUser();
        //当前被选中节点
        $scope.node;
        //是否显示节点的详细信息
        $scope.showTaskInfo = false;
        //是否正在加载
        $scope.isLoading = false;
        //提示信息栏
        $scope.alert = {
            type: '',
            msg: '',
            isShow: false
        };
    }

    function initFunctions() {
        //回车按键的响应
        $scope.enterPress = function (keyEvent) {
            if (keyEvent.which === 13) {
                $scope.submitSearch();
            }
        };

        //关闭信息提示栏
        $scope.closeAlert = function () {
            $scope.alert.isShow = false;
        };

        //隐藏task详细信息div
        $scope.hideDetail = function () {
            $scope.showTaskInfo = false;
            if ($scope.node)
                $scope.node.view.attr({ 'stroke-width': '0px' });
        };

        //置为重跑按钮响应
        $scope.instanceReRun = function () {
            $scope.hideDetail();
            $scope.message = {
                headerText: '提示',
                bodyText: '你是否要将这个任务实例: ' + $scope.instance.taskStatusId + " 设置为重跑?",
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
                $scope.isLoading = true;
                $scope.closeAlert();
                var result = JobMonitorService.recallInstance({
                    'instanceId': $scope.instance.taskStatusId
                });
                processInstanceStateUpdated(result, '#E3E3E3');
            }, function () {
            });
        };

        //置为挂起按钮响应
        $scope.instanceSuspend = function () {
            $scope.hideDetail();
            $scope.message = {
                headerText: '提示',
                bodyText: '你是否要将这个任务实例: ' + $scope.instance.taskStatusId + " 设置为挂起?",
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
                $scope.isLoading = true;
                $scope.closeAlert();
                var result = JobMonitorService.suspendInstance({
                    'instanceId': $scope.instance.taskStatusId
                });
                processInstanceStateUpdated(result, '#878787');
            }, function () {
            });
        };

        //置为成功按钮响应
        $scope.instanceSuccess = function () {
            $scope.hideDetail();
            $scope.message = {
                headerText: '提示',
                bodyText: '你是否要将这个任务实例: ' + $scope.instance.taskStatusId + " 设置为成功?",
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
                $scope.isLoading = true;
                $scope.closeAlert();
                var result = JobMonitorService.successInstance({
                    'instanceId': $scope.instance.taskStatusId
                });
                processInstanceStateUpdated(result, '#458B00');
            }, function () {
                console.log('Modal dismissed at: ' + new Date());
            });
        };

        //级联重跑按钮响应
        $scope.reRunJobCascaded = function (index) {
            $scope.hideDetail();
            $scope.message = {
                headerText: '级联重跑任务: ' + $scope.instance.taskStatusId,
                actionButtonStyle: 'btn-primary',
                jobInstanceId: $scope.instance.taskStatusId,
                time_id: $filter('date')($scope.instance.timeId, "yyyy-MM-dd")
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
                $scope.isLoading = true;
                $scope.closeAlert();
                var result = JobMonitorService.recallCascade(
                    {}, data
                );
                process(result);
                $scope.isSelectAllJob = false;
            }, function () {
            });
        };

        //直接依赖按钮响应
        $scope.queryDirectRelation = function () {
            $scope.isLoading = true;
            $scope.closeAlert();
            $scope.hideDetail();
            var result = JobMonitorService.queryDirectRelation({
                'instanceId': $scope.instance.taskStatusId
            });
            processQuery(result);
        };

        //运行历史按钮响应
        $scope.lookOverTask = function () {
            $scope.hideDetail();
            window.open("#/jobMonitor/" + $scope.instance.taskId);
        };

        //任务管理按钮响应
        $scope.manageTask = function () {
            $scope.hideDetail();
            window.open("#/jobManage/" + $scope.instance.taskId);
        };

        //所有依赖按钮响应
        $scope.queryAllRelation = function () {
            $scope.isLoading = true;
            $scope.closeAlert();
            $scope.hideDetail();
            var result = JobMonitorService.queryAllRelation({
                'instanceId': $scope.instance.taskStatusId
            });
            processQuery(result);
        };

        //查看最长路径
        $scope.getLongestPath = function () {
            $scope.isLoading = true;
            $scope.closeAlert();
            $scope.hideDetail();
            var result = JobMonitorService.getLongestPath({
                'instanceId': $scope.instance.taskStatusId
            });
            processQuery(result);
        };

        //停止预跑响应
        $scope.instanceBatchStop = function () {
            $scope.hideDetail();
            $scope.message = {
                headerText: '提示',
                bodyText: '你是否要将这个任务实例: ' + $scope.instance.taskId + " 停止预跑?",
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
                $scope.isLoading = true;
                $scope.closeAlert();
                var result = JobMonitorService.batchStopTask({
                    'taskId': $scope.instance.taskId
                });
                processInstanceStateUpdated(result, '#878787');
            }, function () {
            });
        };

        //快速通道响应
        $scope.instanceRaisePriority = function () {
            $scope.hideDetail();
            $scope.message = {
                headerText: '提示',
                bodyText: '你是否要将这个任务实例: ' + $scope.instance.taskStatusId + " 设置为快速通道?",
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
                $scope.isLoading = true;
                $scope.closeAlert();
                var result = JobMonitorService.raisePriorityInstance({
                    'instanceId': $scope.instance.taskStatusId
                });
                processInstanceStateUpdated(result, null);
            }, function () {
            });
        };

        //任务优先级的描述
        $scope.getPrioLvlText = function (prioLvl) {
            return UIService.prioLvlToText(prioLvl);
        };

        //任务状态的描述
        $scope.getStatusText = function (status) {
            return UIService.statusToText(status);
        };

        //任务周期的描述
        $scope.getExecutionCycleText = function (cycle) {
            return UIService.cycleToText(cycle);
        };

        //根据英文名获得中文名
        $scope.getDevelopChineseName = function (pinyinName) {
            return UtilsService.getDeveloperRealName(pinyinName);
        };

        //提交查询
        $scope.submitSearch = function () {
            $scope.hideDetail();
            if (!checkQueryCondition())
                return;
            $scope.isLoading = true;
            $scope.closeAlert();
            var result = JobMonitorService.queryInstances({
                'startDate': $scope.startDate,
                'endDate': $scope.endDate,
                'executionCycle': $scope.executionCycle == 'A' ? null : $scope.executionCycle,
                'developer': UtilsService.getRealDeveloper($scope.developer),
                'executionStatus': $scope.executionStatus,
                'executionPriority': $scope.executionPriority,
                'dependencyIsShow': $scope.dependencyIsShow,
                'taskNameOrId': $scope.taskNameOrId,
                'displayType': 'topology'
            });
            processQuery(result);
        };
    }

    //检测查询条件的输入
    function checkQueryCondition() {
        if (!checkDate())
            return false;
        if (!checkTaskNameOrId())
            return false;
        return true;
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

    function setUIElements() {
        var initDate = UtilsService.dateToStr(new Date(), "yyyy-MM-dd");
        $scope.executionCycleOptions = UIService.getCyclesForQuery();
        $scope.executionStatusOptions = UIService.getStatusesForQuery();
        $scope.executionPriorityOptions = UIService.getPrioLvlsForQuery();
        $scope.dependencyIsShowOptions = UIService.getIfOrNot();
        $scope.developers = UtilsService.getDevelopers();
        $scope.startDate = initDate;
        $scope.endDate = initDate;
        $scope.executionCycle = 'A';
        $scope.executionStatus = $scope.executionStatusOptions[2].ID;
        $scope.executionPriority = $scope.executionPriorityOptions[0].ID;
        $scope.dependencyIsShow = $scope.dependencyIsShowOptions[0].ID;
        $scope.taskNameOrId = '';
        $scope.developer = '';
        $scope.developer = UtilsService.getDeveloperByPinyinName($scope.user.pinyinName);
    }

    function addWatches() {
        //监测选中task的变化，更新isOwner信息
        $scope.$watch('instance', function () {
            if ($scope.user) {
                $scope.user.isOwner = false;
                if (!$scope.user.isAdmin && $scope.instance) {
                    $scope.user.isOwner = ($scope.instance.owner == $scope.user.pinyinName);
                }
            }
        }, true);
    }

    function addOnMessages() {
        //响应topologyShow事件，由列表监控页触发
        $scope.$on('topologyShow', function (e, d) {
            $scope.hideDetail();
            $scope.isLoading = true;
            $scope.taskNameOrId = d.taskId.toString();
            $scope.startDate = d.timeId.toString();
            $scope.endDate = d.timeId.toString();
            $scope.executionStatus = 100;
            $scope.isLoading = true;
            $scope.closeAlert();
            var result = JobMonitorService.getSelfAndPostInstances({
                'instanceId': d.taskStatusId
            });
            processQuery(result);
        });
        //响应userVerified事件
        $scope.$on('userVerified', function (e, d) {
            $scope.user = UserService.getUser();
            $scope.developer = UtilsService.getDeveloperByPinyinName($scope.user.pinyinName);
        });
    }

    //显示task详细信息div
    function showDetail() {
        $scope.showTaskInfo = true;
    };

    //拓扑图鼠标点击节点响应
    function getTaskInfo(item) {
        $scope.node = item;
        $scope.isLoading = true;
        $scope.closeAlert();
        var result = JobMonitorService.getInstanceByInstanceId({
            'instanceId': item.id
        });
        processTaskInfo(result);
    };

    //获取节点具体信息后的处理
    function processTaskInfo(result) {
        result.$promise.then(
            function (data) {
                $scope.isLoading = false;
                if (data.success) {
                    setButtonStatus(data.results[0].status);
                    setBatchStop(data.results[0].taskStatusId);
                    $scope.instance = data.results[0];
                    showDetail();
                }
                else {
                    showAlert('warning', data.messages);
                }
            }
        );
    };

    //节点信息状态局部更新
    function processInstanceStateUpdated(result, color) {
        result.$promise.then(
            function (data) {
                if (color != null) {
                    $scope.node.view.attr({
                        fill: color
                    });
                }
                showAlert(data.success ? 'success' : 'warning', data.messages);
                $scope.isLoading = false;
            }
        );
    };

    //根据status设置信息控制详细信息div中各button状态
    function setButtonStatus(status) {
        $scope.canSuspend = (status === 0 || status === 6 || status === 7 || status === 5 || status === -1);
        $scope.canReRun = (status === 1 || status === -1 || status === 3 || status === 5);
        $scope.canSuccess = (status !== 1);
        $scope.canRaisePriority = (status !== 0 || status === 6);
    };

    //设置信息控制详细信息div中停止预跑button状态
    function setBatchStop(task_status_id) {
        $scope.canBatchStop = task_status_id.substr(0, 4) === "pre_";
    };

    //展示提示信息
    function process(result) {
        result.$promise.then(
            function (data) {
                showAlert(data.success ? 'success' : 'warning', data.messages);
                $scope.isLoading = false;
            }
        );
    };

    //处理查询结果设置刷新拓扑图
    function processQuery(result) {
        result.$promise.then(
            function (data) {
                $scope.isLoading = false;
                showAlert(data.success ? 'success' : 'warning', data.messages);
                if (data.success)
                    refresh(data.results);
            }
        );
    };

    //刷新拓扑图
    function refresh(data) {
        var topologyDiv = angular.element(document.getElementById('topology-container')).html('').get(0);
        _topology = new Venus.Topology(topologyDiv, data, _topologyOptions);
        $scope.hideDetail();
    };

    //显示提示信息
    function showAlert(type, msg) {
        $scope.alert.type = type;
        $scope.alert.msg = msg;
        $scope.alert.isShow = true;
    };

}

