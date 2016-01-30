/**
 * Created by Sunny on 14-9-10.
 */

angular.module('data_exchange.service', ['ngResource'])
    .factory('TabStateService', [ function () {
        var activeTabs = '';

        //暴露给外部的接口
        return {
            setTabState: function (tab) {
                activeTabs = tab;
            },
            getTabState: function () {
                return activeTabs;
            }
        }
    }
    ]);
