'use strict';

//任务查看页面的controller
function JobViewCtrl($scope, $routeParams, JobManageService, UtilsService, UIService, UserService) {

    /***********************执行开始***********************/
    initScope();
    setUIElements();
    getTaskInfo();
    /***********************执行结束***********************/


    /***********************functions***********************/
    function initScope() {
        initFunctions();
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
        //获得偏移量的选项
        $scope.getOffsetOptions = function (cycle) {
            return UIService.getOffsetsByCycle(cycle);
        };
    }

    function setUIElements() {
        $scope.developers = UtilsService.getDevelopers();
        $scope.select = {
            cycleOptions: UIService.getCycles(),
            priorityOptions: UIService.getPrioLvls(),
            ifRecallOptions: UIService.getIfOrNot(),
            offsetOptions: UIService.getTaskOffsets(),
            taskGroupOptions: UIService.getTaskGroups(),
            recallLimitOptions: UIService.getRecallLimits(),
            recallIntervalOptions: UIService.getRecallIntervals(),
            gitAddressOptions: UIService.getGitProjects(),
            timeoutOptions: UIService.getTaskTimeouts()
        };
        $scope.select.recallLimitOptions.push(10);
    }

    //获取源任务信息
    function getTaskInfo() {
        var scheduleResult = JobManageService.getTaskByTaskId({
            'taskId': $routeParams.taskId
        });
        processOriginTask(scheduleResult);
    }

    function processOriginTask(result) {
        result.$promise.then(
            function (data) {
                $scope.originTask = data.result;
                setOptions();
            }
        );
    };

    function setOptions() {
        $scope.cycle = $scope.originTask.cycle;
        $scope.priority = $scope.originTask.prioLvl;
        $scope.ifRecall = $scope.originTask.ifRecall;
        $scope.offset = $scope.originTask.offset;
        $scope.taskGroup = $scope.originTask.taskGroupId;
        $scope.recallLimit = $scope.originTask.recallLimit;
        $scope.recallInterval = $scope.originTask.recallInterval;
        $scope.developer = UtilsService.getDeveloperByPinyinName($scope.originTask.owner);
        $scope.taskName = $scope.originTask.taskName;
        $scope.frequency = $scope.originTask.freq;
        $scope.successCode = $scope.originTask.successCode;
        $scope.para1 = $scope.originTask.para1;
        $scope.waitCode = $scope.originTask.waitCode;
        $scope.dependenceTasks = UtilsService.getTaskDO($scope.originTask.relaDOList);
        $scope.isOnline = $scope.originTask.ifVal;
        $scope.timeout = $scope.originTask.timeout;
    }
}