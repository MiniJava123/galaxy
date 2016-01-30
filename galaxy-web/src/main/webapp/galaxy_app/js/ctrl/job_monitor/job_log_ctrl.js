'use strict';

//查看日志页面的controller
function JobLogCtrl($scope, $routeParams, $sce, JobMonitorService, UserService) {
    /***********************执行开始***********************/
    initScope();
    viewLog();
    /***********************执行结束***********************/


    /***********************functions***********************/
    function initScope() {
        initParameters();
    }

    function initParameters() {
        //获取日志文件是否出错
        $scope.isError = false;
        //日志文件地址
        $scope.logPath = $routeParams.logPath.replace(/\+/g, "/");
        //页面描述信息
        $scope.logTitle = "日志文件：" + $scope.logPath;
    }

    //获取日志
    function viewLog() {
        var result = JobMonitorService.viewLog({logPath: $scope.logPath});
        process(result);
    }

    //显示日志的内容
    function process(result) {
        result.$promise.then(
            function (data) {
                if (data.success) {
                    var content = data.result;
                    var hadoopLog = getHadoopLog(data.result);
                    if (hadoopLog != '')
                        content = 'Hadoop日志：<a href=\"' + hadoopLog + '\">' + hadoopLog + '</a><br><br>' + content;
                    $scope.logContent = $sce.trustAsHtml(content);
                } else {
                    $scope.logContent = $sce.trustAsHtml("<h3>日志文件不存在</h3>");
                }
            },
            function (reason) {
                $scope.logContent = $sce.trustAsHtml("<h3>日志文件不存在</h3>");
                $scope.isError = true;
            }
        );
    }

    function getHadoopLog(log) {
        var lines = log.split('<br>');
        var hadoopLog = '';
        for (var i = 0; i < lines.length; i++) {
            var line = lines[i];
            var index = line.indexOf('Tracking URL =');
            if (index != -1) {
                hadoopLog = line.substr(index + 15);
                line = lines[i + 1];
                if (line.indexOf('[') != 0)
                    hadoopLog += line;
            }
        }
        return hadoopLog;
    }
}