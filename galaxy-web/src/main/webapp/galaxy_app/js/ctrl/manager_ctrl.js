'use strict';

//意见反馈页面对应的controller
function ManagerCtrl($scope, CommonService, UserService) {
    /***********************执行开始***********************/
    initScope();
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
    }

    function initFunctions() {
        //发送按钮的响应
        $scope.updateMonitor = function () {
            $scope.alert.isShow = false;
            var result = CommonService.updateMonitorByForce({
            });
            process(result);
        };

        $scope.closeAlert = function () {
            $scope.alert.isShow = false;
        }
    }

    function process(result) {   //处理response 获取数据
        result.$promise.then(
            function (data) {
                $scope.isLoading = false;
                showAlert(data.success ? 'success' : 'warning', data.messages);
            }
        );
    }

    //显示提示信息
    function showAlert(type, msg) {
        $scope.alert.msg = msg;
        $scope.alert.type = type;
        $scope.alert.isShow = true;
    }
}