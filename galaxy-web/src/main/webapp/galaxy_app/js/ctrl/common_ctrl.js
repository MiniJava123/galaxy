/**
 * Created by Sunny on 14-6-9.
 */

//值班信息的controller
function MonitorCtrl($scope, CommonService) {

    /***********************执行开始***********************/
    initScope();
    getCurrentMonitor();
    /***********************执行结束***********************/


    /***********************functions***********************/
    function initScope() {
        initParameters();
    }

    function initParameters() {
        //值班信息的文本
        $scope.monitorMessage = '';
    }

    //提交请求查询当前值班人员
    function getCurrentMonitor() {
        var result = CommonService.getCurrentMonitor({
        });
        process(result);
    }

    //处理值班人员信息
    function process(result) {
        result.$promise.then(
            function (data) {
                if (data.success) {
                    $scope.monitor = data.results[0];
                    $scope.monitorMessage = '本周值班: ' + $scope.monitor.userName + ' ' + $scope.monitor.mobileNo;
                    if ($scope.monitor.officeNo != '0')
                        $scope.monitorMessage += ' ' + $scope.monitor.officeNo;
                }
            });
    }
}

//控制侧边栏状态的controller
function SidebarCtrl($scope, TabStateService) {
    //当前所处的子tab
    var activeSubTab = '';

    $scope.$watch(function () {
        return TabStateService.getTabState();
    }, function () {
        var tab = TabStateService.getTabState();
        if (tab != '') {
            var main = tab[0];
            //是否已经是展开状态
            if (!$('#tabState' + main).hasClass('open')) {
                $('#tab' + main).trigger('click');
            }
            if (activeSubTab != tab) {
                if (activeSubTab != '')
                    $('#subTabs' + activeSubTab).removeClass('active');
                $('#subTabs' + tab).addClass('active');
                activeSubTab = tab;
            }
        }
    }, true);

}




