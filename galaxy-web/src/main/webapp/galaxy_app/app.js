'use strict';


// Declare app level module which depends on filters, and services
angular.module('galaxyApp', [
    'ngRoute',
    'component.service',
    'common.service',
    'utils.service',
    'ui.service',
    'job_manage.service',
    'job_monitor.service',
    'table.service',
    'data_exchange.service',
    'galaxy.jobConfig',
    'galaxy.filter',
    'ui.bootstrap',
    'galaxy.cascadeTree',
    'pasvaz.bindonce',
    'ngTagsInput'
]).config(['$routeProvider', function ($routeProvider) {
    //传输查询页面
    $routeProvider.when('/tablelist', {templateUrl: '/galaxy_app/partials/common/table_list.html'});
    //预跑结果页面
    $routeProvider.when('/jobPreRunMonitor', {templateUrl: '/galaxy_app/partials/job_monitor/prerun_monitor.html'});
    //实例监控页面
    $routeProvider.when('/jobMonitor', {templateUrl: '/galaxy_app/partials/job_monitor/job_monitor.html' });
    //查看日志页面
    $routeProvider.when('/jobMonitor/viewLog/:logPath', {templateUrl: '/galaxy_app/partials/job_monitor/job_log.html'});
    //实例监控页面
    $routeProvider.when('/jobMonitor/:taskId', {templateUrl: '/galaxy_app/partials/job_monitor/job_monitor.html'});
    //任务管理页面
    $routeProvider.when('/jobManage', {templateUrl: '/galaxy_app/partials/job_manage/job_manage.html'});
    //任务管理页面
    $routeProvider.when('/jobManage/:taskId', {templateUrl: '/galaxy_app/partials/job_manage/job_manage.html'});
    //未生效任务管理页面
    $routeProvider.when('/jobIncomplete', {templateUrl: '/galaxy_app/partials/job_manage/job_manage_incomplete.html'});
    //计算任务配置页面
    $routeProvider.when('/calculateJobAdd', {templateUrl: '/galaxy_app/partials/job_config/calculate_add.html'});
    //计算任务配置页面
    $routeProvider.when('/calculateJobConfig/:taskId', {templateUrl: '/galaxy_app/partials/job_config/calculate_config.html'});
    //新的传输任务配置页面
    $routeProvider.when('/transferJobConfig/:taskId', {templateUrl: '/galaxy_app/partials/job_config/transfer_config.html'});
    //新的传输任务配置页面
    $routeProvider.when('/transferJobAdd/:sourceDBType/:sourceDBName/:sourceTableName/:sourceSchemaName/:sourceTableType', {templateUrl: '/galaxy_app/partials/job_config/transfer_add.html'});
    //任务调度查看页面
    $routeProvider.when('/jobView/:taskId', {templateUrl: '/galaxy_app/partials/job_manage/job_view.html'});
    //建表页面
    $routeProvider.when('/createTable', {templateUrl: '/galaxy_app/partials/common/create_table.html'});
    //意见反馈页面
    $routeProvider.when('/reply', {templateUrl: '/galaxy_app/partials/common/reply.html'});
    //意见反馈页面
    $routeProvider.when('/manager', {templateUrl: '/galaxy_app/partials/common/manager.html'});
    //默认页面
    $routeProvider.when('/', {templateUrl: '/galaxy_app/partials/job_monitor/job_monitor.html'});
    //其他页面跳转
    $routeProvider.otherwise({
        redirectTo: '/'
    });
}]);
