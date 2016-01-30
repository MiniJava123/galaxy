'use strict';

//意见反馈页面对应的controller
function ReplyCtrl($scope, CommonService, $timeout, UserService) {
    /***********************执行开始***********************/
    initScope();
    $('textarea.limited').inputlimiter({
        limit: 1000,
        remText: '还可以填写 %n 个字,',
        limitText: '最多允许: %n 个字.'
    });
    /***********************执行结束***********************/


    /***********************functions***********************/
    function initScope() {
        initParameters();
        initFunctions();
        addOnMessages();
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
        $scope.submit = function () {
            $scope.alert.isShow = false;
            if (!$scope.reply) {
                var n = noty(
                    {
                        text: '反馈信息不能为空！请重新填写',
                        layout: 'center',
                        type: 'warning'
                    });
                $timeout(function () {
                    n.close();
                }, 3000, false);
            } else {
                $scope.isLoading = true;
                $scope.closeAlert();
                var result = CommonService.sendReply({
                    'reply': $scope.reply,
                    'email': $scope.user.email
                });
                process(result);
            }
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

    function addOnMessages() {
        //响应userVerified消息
        $scope.$on('userVerified', function (e, d) {
            $scope.user = UserService.getUser();
        });
    }


    //显示提示信息
    function showAlert(type, msg) {
        $scope.alert.msg = msg;
        $scope.alert.type = type;
        $scope.alert.isShow = true;
    }
}