/**
 * Created by Sunny on 14-8-26.
 * 各个Dialog对应的controllers
 */
'use strict';

//受影响的任务列表的Dialog的controller
var InfluencedTaskCtrl = function ($scope, $filter, component, $modalInstance, msg, JobManageService, UIService, UtilsService) {
    /***********************执行开始***********************/
    initScope();
    setUIElements();
    getInfluencedTasks();
    /***********************执行结束***********************/


    /***********************functions***********************/
    function initScope() {
        initParameters();
        initFunctions();
    }

    function initParameters() {
        //展示的信息
        $scope.msg = msg;
        //页面是否正在加载
        $scope.isLoading = false;
        //提示信息
        $scope.alert = {
            type: '',
            msg: '',
            isShow: false
        };
    }

    function initFunctions() {
        //确定按钮的响应
        $scope.ok = function () {
            $modalInstance.close('ok');
        };
        //取消按钮的响应
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
        //获得cycle的文字描述
        $scope.getCycleText = function (cycle) {
            return UIService.cycleToText(cycle);
        };
        //获取cycle的css样式
        $scope.getExecutionCycleLabel = function (cycle) {
            return UIService.getCycleCss(cycle);
        };
        //获得开发者的中文名
        $scope.getDevelopChineseName = function (pinyinName) {
            return UtilsService.getDeveloperRealName(pinyinName);
        };
        //不显示提示信息
        $scope.closeAlert = function () {
            $scope.alert.isShow = false;
        };
    }

    function setUIElements() {
        $scope.developers = UtilsService.getDevelopers();
    }

    //获取受影响的任务列表
    function getInfluencedTasks() {
        var result = JobManageService.getInfluencedTasksByTaskId({
            'taskId': $scope.msg.taskId
        });
        $scope.isLoading = true;
        $scope.closeAlert();
        process(result);
    }

    //处理获得的受影响的任务
    function process(result) {
        result.$promise.then(
            function (data) {
                $scope.isLoading = false;
                if (data.success) {
                    $scope.dataList = data.results;
                    $scope.table = component.getCustomizedTable($scope, $filter);
                    $scope.table.predicate = 'owner';
                    $scope.table.reverse = !$scope.table.reverse;
                }
                else {
                    showAlert('warning', data.messages)
                }
            }
        );
    };

    //显示提示信息
    function showAlert(type, msg) {
        $scope.alert.msg = msg;
        $scope.alert.type = type;
        $scope.alert.isShow = true;
    }
};

//同源任务列表的Dialog的controller
var SameSourceTaskCtrl = function ($scope, $filter, component, $modalInstance, msg, JobManageService, UIService, UtilsService) {
    /***********************执行开始***********************/
    initScope();
    setUIElements();
    getSameSourceTasks();
    /***********************执行结束***********************/


    /***********************functions***********************/
    function initScope() {
        initParameters();
        initFunctions();
    }

    function initParameters() {
        //展示的信息
        $scope.msg = msg;
        //页面是否正在加载
        $scope.isLoading = false;
        //提示信息
        $scope.alert = {
            type: '',
            msg: '',
            isShow: false
        };
    }

    function initFunctions() {
        //确定按钮的响应
        $scope.ok = function () {
            $modalInstance.close('ok');
        };
        //取消按钮的响应
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
        //获得cycle的文字描述
        $scope.getCycleText = function (cycle) {
            return UIService.cycleToText(cycle);
        };
        //获取cycle的css样式
        $scope.getExecutionCycleLabel = function (cycle) {
            return UIService.getCycleCss(cycle);
        };
        //获得开发者的中文名
        $scope.getDevelopChineseName = function (pinyinName) {
            return UtilsService.getDeveloperRealName(pinyinName);
        };
        //不显示提示信息
        $scope.closeAlert = function () {
            $scope.alert.isShow = false;
        };
    }

    function setUIElements() {
        $scope.developers = UtilsService.getDevelopers();
    }

    //获取同源任务列表
    function getSameSourceTasks() {
        var result = JobManageService.getSameSourceTasksByTaskId({
            'taskId': $scope.msg.taskId
        });
        $scope.isLoading = true;
        $scope.closeAlert();
        process(result);
    }

    //处理获得的同源任务
    function process(result) {
        result.$promise.then(
            function (data) {
                $scope.isLoading = false;
                if (data.success) {
                    $scope.dataList = data.results;
                    $scope.table = component.getCustomizedTable($scope, $filter);
                    $scope.table.predicate = 'owner';
                    $scope.table.reverse = !$scope.table.reverse;
                }
                else {
                    showAlert('warning', data.messages)
                }
            }
        );
    };

    //显示提示信息
    function showAlert(type, msg) {
        $scope.alert.msg = msg;
        $scope.alert.type = type;
        $scope.alert.isShow = true;
    }
};

//任务预跑Dialog的controller
var PreRunTaskCtrl = function ($scope, $filter, $modalInstance, msg) {
    /***********************执行开始***********************/
    initScope();
    setUIElements();
    /***********************执行结束***********************/


    /***********************functions***********************/
    function initScope() {
        initParameters();
        initFunctions();
    }

    function initParameters() {
        //展示的信息
        $scope.msg = msg;
        //提示信息
        $scope.alert = {
            type: "",
            msg: "",
            isShow: false
        };
        //页面是否正在加载
        $scope.isLoading = false;
        //返回给调用页面的值
        $scope.returnBackTime = {
            startDate: '',
            endDate: ''
        };
    }

    function initFunctions() {
        //取消按钮的响应
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
        //不显示提示信息
        $scope.closeAlert = function () {
            $scope.alert.isShow = false;
        };
        //确认按钮的响应
        $scope.ok = function () {
            var endDateSplit;
            var endTimeSplit;
            var endTimeForCalcu;
            switch ($scope.datePickerStyle) {
                case 'H':
                    //计算返回的时间值
                    endDateSplit = $scope.time.startDate.split("-");
                    endTimeSplit = $scope.time.startTime.split(":");
                    endTimeForCalcu = new Date(endDateSplit[0], endDateSplit[1] - 1, endDateSplit[2], endTimeSplit[0], endTimeSplit[1], endTimeSplit[2]);
                    endTimeForCalcu.setMilliseconds(endTimeForCalcu.getMilliseconds() - 3);
                    $scope.returnBackTime.startDate = $filter("dateFormat")(endTimeForCalcu, "yyyy-MM-dd hh:mm:ss", 1);
                    endDateSplit = $scope.time.endDate.split("-");
                    endTimeSplit = $scope.time.endTime.split(":");
                    endTimeForCalcu = new Date(endDateSplit[0], endDateSplit[1] - 1, endDateSplit[2], endTimeSplit[0], endTimeSplit[1], endTimeSplit[2]);
                    endTimeForCalcu.setHours(endTimeForCalcu.getHours() + 1);
                    $scope.returnBackTime.endDate = $filter("dateFormat")(endTimeForCalcu, "yyyy-MM-dd hh:mm:ss", 1);
                    break;
                case 'D':
                case 'W':
                    $scope.returnBackTime.startDate = $scope.time.startDate;
                    endDateSplit = $scope.time.endDate.split("-");
                    endTimeForCalcu = new Date(endDateSplit[0], endDateSplit[1] - 1, endDateSplit[2]);
                    endTimeForCalcu.setDate(endTimeForCalcu.getDate() + 1);
                    $scope.returnBackTime.endDate = $filter("dateFormat")(endTimeForCalcu, "yyyy-MM-dd", 1);
                    break;
                case 'M':
                    //计算返回的时间值
                    $scope.returnBackTime.startDate = $scope.time.startYear + '-' + $scope.time.startMonth + '-01';
                    endTimeForCalcu = new Date($scope.time.endYear, $scope.time.endMonth - 1, "01");
                    endTimeForCalcu.setMonth(endTimeForCalcu.getMonth() + 1);
                    $scope.returnBackTime.endDate = $filter("dateFormat")(endTimeForCalcu, "yyyy-MM-dd", 1);
                    break;
            }
            var starttime = new Date(
                Date.parse(($scope.returnBackTime.startDate).replace(/\-/g, "/")));
            var endtime = new Date(
                Date.parse(($scope.returnBackTime.endDate).replace(/\-/g, "/")));

            if (starttime > endtime) {
                $scope.alert.isShow = true;
                $scope.alert.type = "danger";
                $scope.alert.msg = "开始时间 大于 结束时间,请修改...";
            } else {
                $modalInstance.close($scope.returnBackTime);
            }
        };

    }

    function setUIElements() {
        $scope.datePickerStyle = $scope.msg.jobExecCycle;
        $scope.time = {
            startDate: '',
            startTime: '',
            endDate: '',
            endTime: '',
            startYear: '',
            startMonth: '',
            endYear: '',
            endMonth: '',
            months: ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10',
                '11', '12']
        };
        setTimeByDatePickerStyle();
    }

    function setTimeByDatePickerStyle() {
        var currentTime = new Date(new Date().getTime());
        switch ($scope.datePickerStyle) {
            case 'H':
                //初始化显示值
                $scope.time.startDate = $filter('dateFormat')(currentTime.getFullYear() + "-" + (currentTime.getMonth() + 1) + "-" + currentTime.getDate(), 'yyyy-MM-dd', 0);
                $scope.time.endDate = $filter('dateFormat')(currentTime.getFullYear() + "-" + (currentTime.getMonth() + 1) + "-" + currentTime.getDate(), 'yyyy-MM-dd', 0);
                $scope.time.startTime = currentTime.getHours() >= 1 && currentTime.getHours() <= 9 ? '0' + currentTime.getHours() + ':00:00' : currentTime.getHours() + ':00:00';
                $scope.time.endTime = currentTime.getHours() >= 1 && currentTime.getHours() <= 9 ? '0' + currentTime.getHours() + ':00:00' : currentTime.getHours() + ':00:00';
                break;
            case 'D':
            case 'W':
                $scope.time.startDate = $filter('dateFormat')(currentTime.getFullYear() + "-" + (currentTime.getMonth() + 1) + "-" + currentTime.getDate(), 'yyyy-MM-dd', 0);
                $scope.time.endDate = $filter('dateFormat')(currentTime.getFullYear() + "-" + (currentTime.getMonth() + 1) + "-" + currentTime.getDate(), 'yyyy-MM-dd', 0);
                break;
            case 'M':
                //初始化显示值
                $scope.time.startYear = currentTime.getFullYear();
                $scope.time.startMonth = (currentTime.getMonth() + 1) >= 1 && (currentTime.getMonth() + 1) <= 9 ? '0' + (currentTime.getMonth() + 1) : (currentTime.getMonth() + 1);
                $scope.time.endYear = currentTime.getFullYear();
                $scope.time.endMonth = (currentTime.getMonth() + 1) >= 1 && (currentTime.getMonth() + 1) <= 9 ? '0' + (currentTime.getMonth() + 1) : (currentTime.getMonth() + 1);
                break;
        }
    }
};

//级联重跑dialog的controller
var CascadeRecallTasksCtrl = function ($scope, $filter, $modalInstance, JobMonitorService, msg) {
    /***********************执行开始***********************/
    initScope();
    setUIElements();
    getRecallCascadeInstances();
    /***********************执行结束***********************/


    /***********************functions***********************/
    function initScope() {
        initParameters();
        initFunctions();
    }

    function initParameters() {
        //展示的信息
        $scope.msg = msg;
        //提示信息
        $scope.alert = {
            type: "",
            msg: "",
            isShow: false
        };
        //页面正在加载
        $scope.isLoading = true;
        //执行过滤 与 取消过滤
        $scope.filterTags = [];
    }

    function initFunctions() {
        //点击确认的响应
        $scope.ok = function () {
            if (new Date($scope.cascadeRecallData.startDate) > new Date($scope.cascadeRecallData.endDate)) {
                showAlert('warning', '开始时间大于结束时间,请修改');
            } else {
                //获取所有选中的instances
                getSelectedNodeBeforeSubmit($scope.treeData);
                if ($scope.cascadeRecallData.instanceIds.length == 0) {
                    showAlert('warning', '重跑实例列表为空');
                    return;
                }
                $modalInstance.close($scope.cascadeRecallData);
            }
        };
        //点击取消的响应
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
        //不显示提示信息
        $scope.closeAlert = function () {
            $scope.alert.isShow = false;
        };
    }

    function setUIElements() {
        //返回给调用页面的信息
        $scope.cascadeRecallData = {
            startDate: '',
            endDate: '',
            taskIds: [],
            instanceIds: []
        };
        //与instance的timeId相同
        var d = new Date($scope.msg.time_id);
        $scope.cascadeRecallData.startDate = $filter("dateFormat")(d.getFullYear() + "-" + (d.getMonth() + 1) + "-" + d.getDate(), 'yyyy-MM-dd', 0);
        $scope.cascadeRecallData.endDate = $filter("dateFormat")(d.getFullYear() + "-" + (d.getMonth() + 1) + "-" + d.getDate(), 'yyyy-MM-dd', 0);
        $scope.treeData = {};
    }

    //获得所有需要重跑的instances
    function getRecallCascadeInstances() {
        var result = JobMonitorService.recallCascadeGetInstances(
            {
                'instanceId': $scope.msg.jobInstanceId,
                'date': $scope.msg.time_id
            }
        );
        $scope.isLoading = true;
        $scope.closeAlert();
        process(result);
    }

    //处理获得的受影响的任务
    function process(result) {
        result.$promise.then(
            function (data) {
                showAlert(data.success ? 'success' : 'warning', data.messages);
                $scope.isLoading = false;
                if (data.success) {
                    $scope.cascadeJobs = data.result;   //获取的 children 任务
                    angular.forEach($scope.cascadeJobs, function (item) {
                        item.filtered = false;
                    });
                    $scope.treeData = getRoot($scope.cascadeJobs);
                }
            }
        );
    }

    //返回树形结构的顶节点
    function getRoot(data) {
        var preIdToInstanceRelas = {};
        for (var i = 0; i < data.length; i++) {
            preIdToInstanceRelas[data[i].pId] = [];
        }
        for (var i = 0; i < data.length; i++) {
            if (!contains(preIdToInstanceRelas, data[i])) //去重
                preIdToInstanceRelas[data[i].pId].push(data[i]);
        }
        var elements = [];
        elements.push(data[0]);
        while (elements.length != 0) {
            var element = elements.splice(0, 1)[0];
            var instanceId = element.id;
            var children = preIdToInstanceRelas[instanceId];
            if (children) {
                for (var i = 0; i < children.length; i++) {
                    if (!element.children)
                        element.children = [];
                    var cloneObj = cloneInstanceRela(children[i]);
                    element.children.push(cloneObj);
                    elements.push(cloneObj);
                }
            }
        }
        return data[0];
    }

    //拷贝InstanceRela对象
    function cloneInstanceRela(obj) {
        var instanceRela = {};
        instanceRela.filtered = obj.filtered;
        instanceRela.taskName = obj.taskName;
        instanceRela.pId = obj.pId;
        instanceRela.id = obj.id;
        instanceRela.taskId = obj.taskId;
        return instanceRela;
    }

    //instanceRela是否在map<instanceRela>中存在
    function contains(map, instanceRela) {
        var children = map[instanceRela.pId];
        for (var i = 0; i < children.length; i++) {
            var child = children[i];
            if (child.id == instanceRela.id)
                return true;
        }
        return false;
    }

    //获取给定节点下所有选中的节点
    function getSelectedNodeBeforeSubmit(node) {
        if (!(node.filtered || node.deleted)) {
            if ($scope.cascadeRecallData.instanceIds.indexOf(node.id) == -1) {
                $scope.cascadeRecallData.instanceIds.push(node.id);
                $scope.cascadeRecallData.taskIds.push(node.taskId);
            }
        }
        if (node.children == undefined && node._children != undefined) {
            for (var i = 0; i < node._children.length; i++) {
                getSelectedNodeBeforeSubmit(node._children[i]);
            }
        }
        if (node.children != undefined && node._children == undefined) {
            for (var i = 0; i < node.children.length; i++) {
                getSelectedNodeBeforeSubmit(node.children[i]);
            }
        }
    };

    //显示提示信息
    function showAlert(type, msg) {
        $scope.alert.msg = msg;
        $scope.alert.type = type;
        $scope.alert.isShow = true;
    }
};

//级联预跑dialog的controller
var CascadePreRunTasksCtrl = function ($scope, $filter, $modalInstance, JobManageService, msg) {
    /***********************执行开始***********************/
    initScope();
    setUIElements();
    getRecallCascadeInstances();
    /***********************执行结束***********************/


    /***********************functions***********************/
    function initScope() {
        initParameters();
        initFunctions();
    }

    function initParameters() {
        //展示的信息
        $scope.msg = msg;
        //提示信息
        $scope.alert = {
            type: "",
            msg: "",
            isShow: false
        };
        //页面正在加载
        $scope.isLoading = true;
        $scope.deleteNumber = 0;
        $scope.filterTags = [];
    }

    function initFunctions() {
        //点击确认的响应
        $scope.ok = function () {
            if (new Date($scope.cascadePreRunData.startDate) > new Date($scope.cascadePreRunData.endDate)) {
                showAlert('warning', '开始时间大于结束时间,请修改');
            } else {
                //获取所有选中的instances
                getSelectedNodeBeforeSubmit($scope.treeData);
                if ($scope.cascadePreRunData.taskIds.length == 0) {
                    showAlert('warning', '预跑任务列表为空');
                    return;
                }
                var endDateSplit = $scope.cascadePreRunData.endDate.split("-");
                var endTimeForCalcu = new Date(endDateSplit[0], endDateSplit[1] - 1, endDateSplit[2]);
                endTimeForCalcu.setDate(endTimeForCalcu.getDate() + 1);
                $scope.cascadePreRunData.endDate = $filter("dateFormat")(endTimeForCalcu, "yyyy-MM-dd", 1);
                $modalInstance.close($scope.cascadePreRunData);
            }
        };
        //点击取消的响应
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
        //不显示提示信息
        $scope.closeAlert = function () {
            $scope.alert.isShow = false;
        };
    }

    function setUIElements() {
        //返回给调用页面的信息
        $scope.cascadePreRunData = {
            startDate: '',
            endDate: '',
            taskIds: [],
            instanceIds: []
        };
        var d = new Date(new Date().getTime());
        $scope.cascadePreRunData.startDate = $filter("dateFormat")(d.getFullYear() + "-" + (d.getMonth() + 1) + "-" + d.getDate(), 'yyyy-MM-dd', 0);
        $scope.cascadePreRunData.endDate = $filter("dateFormat")(d.getFullYear() + "-" + (d.getMonth() + 1) + "-" + d.getDate(), 'yyyy-MM-dd', 0);
        $scope.treeData = {};
    }

    //获得所有需要重跑的instances
    function getRecallCascadeInstances() {
        var result = JobManageService.getTasksForCascadePreRun(
            {
                'taskId': $scope.msg.taskId
            }
        );
        $scope.isLoading = true;
        $scope.closeAlert();
        process(result);
    }

    //处理获得的受影响的任务
    function process(result) {
        result.$promise.then(
            function (data) {
                $scope.isLoading = false;
                if (data.success) {
                    $scope.cascadeJobs = data.results;   //获取的 children 任务
                    angular.forEach($scope.cascadeJobs, function (item) {
                        item.filtered = false;
                        item.deleted = false;
                    });
                    $scope.treeData = getRoot($scope.cascadeJobs);
                }
            }
        );
    }

    //返回树形结构的顶节点
    function getRoot(data) {
        var preIdToTaskRelas = {};
        for (var i = 0; i < data.length; i++) {
            preIdToTaskRelas[data[i].taskPreId] = [];
        }
        for (var i = 0; i < data.length; i++) {
            if (!contains(preIdToTaskRelas, data[i])) //去重
                preIdToTaskRelas[data[i].taskPreId].push(data[i]);
        }
        var elements = [];
        elements.push(data[0]);
        while (elements.length != 0) {
            var element = elements.splice(0, 1)[0];
            var taskId = element.taskId;
            var children = preIdToTaskRelas[taskId];
            if (children) {
                for (var i = 0; i < children.length; i++) {
                    if (!element.children)
                        element.children = [];
                    var cloneObj = cloneTaskRela(children[i]);
                    element.children.push(cloneObj);
                    elements.push(cloneObj);
                }
            }
        }
        return data[0];
    }

    //拷贝taskRela对象
    function cloneTaskRela(obj) {
        var taskRela = {};
        taskRela.cycleGap = obj.cycleGap;
        taskRela.filtered = obj.filtered;
        taskRela.owner = obj.owner;
        taskRela.remark = obj.remark;
        taskRela.taskName = obj.taskName;
        taskRela.taskPreId = obj.taskPreId;
        taskRela.taskId = obj.taskId;
        taskRela.timeStamp = obj.timeStamp;
        return taskRela;
    }

    //taskRela是否在map<taskRela>中存在
    function contains(map, taskRela) {
        var children = map[taskRela.taskPreId];
        for (var i = 0; i < children.length; i++) {
            var child = children[i];
            if (child.taskId == taskRela.taskId)
                return true;
        }
        return false;
    }

    //获取给定节点下所有选中的节点
    function getSelectedNodeBeforeSubmit(node) {
        if (!(node.filtered || node.deleted)) {
            if ($scope.cascadePreRunData.taskIds.indexOf(node.taskId) == -1) {
                $scope.cascadePreRunData.taskIds.push(node.taskId);
            }
        }
        if (node.children == undefined && node._children != undefined) {
            for (var i = 0; i < node._children.length; i++) {
                getSelectedNodeBeforeSubmit(node._children[i]);
            }
        }
        if (node.children != undefined && node._children == undefined) {
            for (var i = 0; i < node.children.length; i++) {
                getSelectedNodeBeforeSubmit(node.children[i]);
            }
        }
    };

    //显示提示信息
    function showAlert(type, msg) {
        $scope.alert.msg = msg;
        $scope.alert.type = type;
        $scope.alert.isShow = true;
    }
};

//展示信息的dialog的controller
var MessageCtrl = function ($scope, $modalInstance, msg) {
    /***********************执行开始***********************/
    initScope();
    /***********************执行结束***********************/


    /***********************functions***********************/
    function initScope() {
        initParameters();
        initFunctions();
    }

    function initParameters() {
        //展示的信息
        $scope.msg = msg;
    }

    function initFunctions() {
        //点击确认按钮
        $scope.ok = function () {
            $modalInstance.close('ok');
        };
        //点击取消按钮
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    }
};

//任务实例状态分析的dialog的controller
var InstanceStateAnalyzeCtrl = function ($scope, $modalInstance, msg, JobMonitorService) {
    /***********************执行开始***********************/
    initScope();
    stateAnalyze();
    /***********************执行结束***********************/


    /***********************functions***********************/
    function initScope() {
        initParameters();
        initFunctions();
    }

    function initParameters() {
        //展示的信息
        $scope.msg = msg;
    }

    function initFunctions() {
        //点击确认按钮
        $scope.ok = function () {
            $modalInstance.close('ok');
        };
        //点击取消按钮
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    }

    function stateAnalyze() {
        var result = JobMonitorService.instanceStatusAnalyze({
                'instanceId': $scope.msg.instanceId
            }
        );
        process(result);
    }

    //处理查询结果
    function process(result) {
        result.$promise.then(
            function (data) {
                $scope.isLoading = false;
                if (data.success) {
                    $scope.msg.bodyText = data.result;
                }
            }
        );
    }

};

//任务依赖选择对话框的dialog
var TaskDependencyCtrl = function ($scope, $filter, $modalInstance, msg, component, JobManageService, UIService, UtilsService) {
    /***********************执行开始***********************/
    initScope();
    setUIElements();
    /***********************执行结束***********************/


    /***********************functions***********************/
    function initScope() {
        initParameters();
        initFunctions();
    }

    function initParameters() {
        //展示的信息
        $scope.msg = msg;
        //依赖信息
        $scope.dependenceTasks = msg.data;
        //提示信息
        $scope.alert = {
            type: '',
            msg: '',
            isShow: false
        };
        //是否隐藏详细信息
        $scope.hideTable = true;
    }

    function initFunctions() {
        //不显示提示信息
        $scope.closeAlert = function () {
            $scope.alert.isShow = false;
        };
        //根据条件进行查询
        $scope.submitQuery = function () {
            $scope.hideTable = true;
            $scope.isLoading = true;
            $scope.closeAlert();
            var result = JobManageService.queryTasks({
                'group': $scope.query.group,
                'cycle': $scope.query.cycle == 'A' ? null : $scope.query.cycle,
                'developer': $scope.query.developer == '' ? null : $scope.query.developer.ID,
                'nameOrId': $scope.query.taskName
            });
            processQuery(result);
        };
        //点击确认的响应
        $scope.ok = function () {
            $modalInstance.close($scope.dependenceTasks);
        };
        //获得cycle的文字描述
        $scope.getCycleText = function (cycle) {
            return UIService.cycleToText(cycle);
        };
        //根据pinyinName获取中文名
        $scope.getDevelopChineseName = function (pinyinName) {
            return UtilsService.getDeveloperRealName(pinyinName);
        };
        //获取cycle的css样式
        $scope.getExecutionCycleLabel = function (cycle) {
            return UIService.getCycleCss(cycle);
        };
        //点击依赖选择框的响应
        $scope.setDependence = function (index) {
            //检查依赖是否已经被添加
            var flag = 1;
            var selectRowIndex = index + ($scope.table.startIndex - 1);
            var job = $scope.table.displayedDataList[selectRowIndex];
            if (job.isSelected) {
                deleteDependency(job);
            } else {
                addDependency(job);
            }
            job.isSelected = !job.isSelected;
        }
    }

    function setUIElements() {
        $scope.taskGroups = UIService.getTaskGroupsForQuery();
        $scope.taskPeriods = UIService.getTaskPeriodsForQuery();
        $scope.developers = UtilsService.getDevelopers();
        $scope.query = {
            group: $scope.taskGroups[0].ID,
            cycle: 'A',
            developer: '',
            taskName: ''
        };
    }

    //删除依赖
    function deleteDependency(job) {
        var index = getDependenceIndex(job.taskId);
        if (index != -1) {
            $scope.dependenceTasks.splice(index, 1);
            showAlert('success', '成功删除依赖(taskId: ' + job.taskId + ', taskName: ' + job.taskName + ')');
        }
        else {
            showAlert('warning', '查找任务失败');
        }
    }

    //添加依赖
    function addDependency(job) {
        var taskRela = {
            taskId: job.taskId,
            taskName: job.taskName,
            cycle: job.cycle,
            owner: job.owner,
            cycleGap: job.cycle + '0'
        };
        $scope.dependenceTasks.push(taskRela);
//        showAlert('success', '成功添加依赖(taskId: ' + job.taskId + ', taskName: ' + job.taskName + ')');
    }

    //根据taskId获得其依赖列表中的index
    var getDependenceIndex = function (taskId) {
        for (var i = 0; i < $scope.dependenceTasks.length; i++) {
            if ($scope.dependenceTasks[i].taskId == taskId) {
                return i;
            }
        }
        return -1;
    };

    //处理查询结果
    function processQuery(result) {
        result.$promise.then(
            function (data) {
                $scope.isLoading = false;
                if (data.success) {
                    $scope.dataList = getValidTasks(data.results);
                    $scope.table = component.getCustomizedTable($scope, $filter);
                    setDefaultSelected();
                    $scope.hideTable = false; //获取成功 则显示数据
                }
                else {
                    showAlert('warning', data.messages);
                }
            }
        );
    }

    //从所有的任务中获取生效的任务返回
    function getValidTasks(tasks) {
        var validTasks = [];
        for (var i = 0; i < tasks.length; i++) {
            if (tasks[i].ifVal == 1)
                validTasks.push(tasks[i]);
        }
        return validTasks;
    }

    //根据传入的依赖信息设置默认的选择状态
    function setDefaultSelected() {
        //将所有的任务都设置为未选中
        angular.forEach($scope.table.displayedDataList, function (job) {
            job.isSelected = false;
        });
        for (var i = 0; i < $scope.dependenceTasks.length; i++) {
            var index = getQueryResultIndex($scope.dependenceTasks[i].taskId);
            if (index != -1)
                $scope.table.displayedDataList[index].isSelected = true;
        }
    }

    //根据taskId获得其查询结果列表中的index
    var getQueryResultIndex = function (taskId) {
        for (var i = 0; i < $scope.table.displayedDataList.length; i++) {
            if ($scope.table.displayedDataList[i].taskId == taskId) {
                return i;
            }
        }
        return -1;
    };

    //显示提示信息
    function showAlert(type, msg) {
        $scope.alert.msg = msg;
        $scope.alert.type = type;
        $scope.alert.isShow = true;
    }
};

