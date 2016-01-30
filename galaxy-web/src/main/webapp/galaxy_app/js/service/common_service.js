/**
 * Created by Sunny on 14-6-10.
 */

angular.module('common.service', ['ngResource'])
    .value('serverAddress', '/galaxy')
    .factory('CommonService', ['$resource', '$routeParams', 'serverAddress',
        function ($resource, $routeParams, serverAddress) {
            return $resource(serverAddress + "/common/:action",
                {},
                {
                    //获得当前值班信息
                    getCurrentMonitor: {
                        method: 'GET',
                        params: {
                            action: 'getCurrentMonitor'
                        }
                    },
                    //获得当前值班信息，强制读取数据库
                    updateMonitorByForce: {
                        method: 'POST',
                        params: {
                            action: 'updateMonitorByForce'
                        }
                    },
                    //发送意见反馈
                    sendReply: {
                        method: 'POST',
                        params: {
                            action: 'sendReply',
                            reply: '@reply',
                            email: '@email'
                        }
                    },
                    //用户是否是管理员
                    isAdmin: {
                        method: 'GET',
                        params: {
                            action: 'isAdmin',
                            resourceId: '@resourceId'
                        }
                    },
                    //用户是否是taskid对应的任务的owner
                    isOwnerByTaskId: {
                        method: 'GET',
                        params: {
                            action: 'isTaskOwner',
                            taskId: '@taskId'
                        }
                    },
                    //导入git
                    importGit: {
                        method: 'GET',
                        params: {
                            action: 'importGit',
                            projectName: '@projectName',
                            fileName: '@fileName'
                        }
                    },
                    //获取当前登陆用户信息
                    getCurrentUser: {
                        method: 'GET',
                        params: {
                            action: 'getCurrentUser'
                        }
                    },
                    //获取登出url
                    getLogoutUrl: {
                        method: 'GET',
                        params: {
                            action: 'getLogoutUrl'
                        }
                    }
                }
            )
        }
    ])
    .factory('UserService', ['$rootScope', 'CommonService', function ($rootScope, CommonService) {
        //登陆用户的信息
        var user = {
            isAdmin: false,
            isDeveloper: false,
            pinyinName: '',
            realName: '',
            isOwner: false,
            email: '',
            userId: '',
            loginId: -1
        };

        getCurrentUser();
        getLogoutUrl();

        //获取当前登陆用户信息
        function getCurrentUser() {
            CommonService.getCurrentUser({
            }).$promise.then(function (data) {
                    if (data.success) {
                        setUser(data.result);
                    }
                }, function (error) {
                });
        };

        //设置当前登陆用户
        function setUser(result) {
            user.pinyinName = result.employPinyin;
            user.realName = result.employeeName;
            user.email = result.employeeEmail;
            user.userId = result.employeeId;
            user.loginId = result.loginId;

            for (var i = 0; i < Global.AllTeam.length; i++) {
                if (Global.AllTeam[i][0] == user.pinyinName) {
                    user.isDeveloper = true;
                    break;
                }
            }
            $("#userName").text(user.realName);

            CommonService.isAdmin({
                'resourceId': '1'
            }).$promise.then(function (data) {
                    user.isAdmin = data.success;
                    $rootScope.$broadcast('userVerified');
                }, function (error) {
                });
        }

        //获取登出url
        function getLogoutUrl() {
            CommonService.getLogoutUrl({
            }).$promise.then(function (data) {
                    if (data.success) {
                        $('.user-menu a').attr('href', data.result);
                    }
                }, function (error) {
                });
        }

        //暴露给外部的接口
        return {
            getUser: function () {
                return user;
            }
        }
    }])
;
