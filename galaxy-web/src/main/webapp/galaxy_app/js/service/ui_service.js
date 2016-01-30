/**
 * Created by Sunny on 14-8-21.
 */

angular.module('ui.service', ['ngResource'])
    .factory('UIService', [ function () {
        return {
            //任务组的选项
            getTaskGroups: function () {
                return [
                    {ID: 1, Text: 'dpods'},
                    {ID: 2, Text: 'dpmid'},
                    {ID: 3, Text: 'dm'},
                    {ID: 4, Text: 'rpt'},
                    {ID: 5, Text: 'mail'},
                    {ID: 6, Text: 'dw'},
                    {ID: 7, Text: 'DQ'},
                    {ID: 8, Text: 'atom'}
                ]
            },
            //作为查询条件的任务组选项
            getTaskGroupsForQuery: function () {
                return [
                    {ID: 0, Text: '不限'},
                    {ID: 1, Text: 'dpods'},
                    {ID: 2, Text: 'dpmid'},
                    {ID: 3, Text: 'dm'},
                    {ID: 4, Text: 'rpt'},
                    {ID: 5, Text: 'mail'},
                    {ID: 6, Text: 'dw'},
                    {ID: 7, Text: 'DQ'},
                    {ID: 8, Text: 'atom'}
                ]
            },
            //任务执行周期的选项
            getTaskPeriods: function () {
                return {
                    Y: '年', M: '月', W: '周', D: '日', H: '时'
                }
            },
            //作为查询条件的任务执行周期的选项
            getTaskPeriodsForQuery: function () {
                return {
                    A: '不限', Y: '年', M: '月', W: '周', D: '日', H: '时'
                }
            },
            //任务超时时间的选项
            getTaskTimeouts: function () {
                return [
                    {ID: 30, Text: '30分钟'},
                    {ID: 60, Text: '1小时'},
                    {ID: 90, Text: '1.5小时'},
                    {ID: 120, Text: '2小时'},
                    {ID: 150, Text: '2.5小时'},
                    {ID: 180, Text: '3小时'},
                    {ID: 210, Text: '3.5小时'},
                    {ID: 240, Text: '4小时'}
                ]
            },
            //任务优先级的选项
            getPrioLvls: function () {
                return [
                    {ID: 0, Text: '线上'},
                    {ID: 1, Text: '高'},
                    {ID: 2, Text: '中'},
                    {ID: 3, Text: '低'},
                    {ID: 4, Text: '预跑'}
                ]
            },
            //任务偏移量的选项
            getTaskOffsets: function () {
                return ['D0', 'D1', 'D2', 'D3', 'D4', 'D5', 'D6',
                    'M0', 'M1', 'M2', 'M3', 'M4', 'M5', 'M6']
            },
            //任务重跑上限的选项
            getRecallLimits: function () {
                return [1, 2, 3, 4, 5]
            },
            //任务重跑间隔的选项
            getRecallIntervals: function () {
                return [
                    {ID: 1, Text: '1分钟'},
                    {ID: 2, Text: '2分钟'},
                    {ID: 3, Text: '3分钟'},
                    {ID: 4, Text: '4分钟'},
                    {ID: 5, Text: '5分钟'},
                    {ID: 6, Text: '6分钟'},
                    {ID: 7, Text: '7分钟'},
                    {ID: 8, Text: '8分钟'},
                    {ID: 9, Text: '9分钟'},
                    {ID: 10, Text: '10分钟'}
                ]
            },
            //任务是否重跑的选项
            getIfOrNot: function () {
                return [
                    {ID: 1, Text: '是'},
                    {ID: 0, Text: '否'}
                ]
            },
            //任务偏移类型的选项
            getDateTypes: function () {
                return  {D: '日', M: '月', MTD: 'MTD'}
            },
            //任务基准时间的选项
            getDateNumbers: function () {
                return [0, 1, 2, 3, 4, 5, 6]
            },
            getTargetTableTypes: function (flag) {
                switch (flag) {
                    case 0:
                        return [
                            {ID: 'full', Text: '全量表'},
                            {ID: 'zipper', Text: '拉链表'},
                            {ID: 'snapshot', Text: '快照表'},
                            {ID: 'append', Text: '增量表'}
                        ];
                    case 1:
                        return [
                            {ID: 'full', Text: '全量表'},
                            {ID: 'append', Text: '增量表'}
                        ];
                }
            },
            //获得cycle的中文解释
            cycleToText: function (cycle) {
                switch (cycle) {
                    case 'H':
                        return '小时';
                    case 'D':
                        return '日';
                    case 'W':
                        return '周';
                    case 'M':
                        return '月';
                    case 'mi':
                        return '分';
                }
            },
            statusToText: function (status) {
                switch (status) {
                    case 1:
                        return 'success';
                    case -1:
                        return 'fail';
                    case 0:
                        return 'init';
                    case 4:
                        return 'init-error';
                    case 3:
                        return 'suspend';
                    case 5:
                        return 'wait';
                    case 2:
                        return 'running';
                    case 6:
                        return 'ready';
                    case 7:
                        return 'timeout';
                    default :
                        return '未知';
                }
            },
            prioLvlToText: function (prioLvl) {
                switch (prioLvl) {
                    case 0 :
                        return '线上';
                    case 1 :
                        return '高';
                    case 2 :
                        return '中';
                    case 3 :
                        return '低';
                    default:
                        return prioLvl;
                }
            },

            //根据目标表类型的中文名获取英文名
            textToTargetTableType: function (type) {
                if (type === '拉链表') {
                    return 'zipper';
                }
                if (type === '快照表') {
                    return 'snapshot';
                }
                if (type === '全量表') {
                    return 'full';
                }
                if (type === '增量表') {
                    return 'append';
                }
            },
            //根据周期获得偏移量的选项
            getOffsetsByCycle: function (cycle) {
                switch (cycle) {
                    case 'H':
                        return ['H0', 'H1', 'H2', 'H3', 'H4', 'H5', 'H6', 'H7', 'H8', 'H9', 'H10', 'H11', 'H12', 'H13', 'H14', 'H15', 'H16',
                            'H17', 'H18', 'H19', 'H20', 'H21', 'H22', 'H23', 'H24'
                        ];
                    case 'D':
                        return ['D0', 'D1', 'D2', 'D3', 'D4', 'D5', 'D6', 'D7', 'D8', 'D9', 'D10', 'D11', 'D12', 'D13', 'D14', 'D15', 'D16',
                            'D17', 'D18', 'D19', 'D20', 'D21', 'D22', 'D23', 'D24', 'D25', 'D26', 'D27', 'D28', 'D29', 'D30', 'D31'
                        ];
                    case 'W':
                        return ['W0', 'W1', 'W2', 'W3', 'W4'];
                    case 'M':
                        return ['M0', 'M1', 'M2', 'M3', 'M4', 'M5', 'M6'];
                }
            },
            //根据偏移类型获得偏移量的选项
            getDateOffsetsByDateType: function (dateType) {
                switch (dateType) {
                    case 'D':
                        return ['D1', 'D2', 'D3', 'D4', 'D5', 'D6', 'D7', 'D8', 'D9', 'D10',
                            'D11', 'D12', 'D13', 'D14', 'D15', 'D16', 'D17', 'D18', 'D19', 'D20',
                            'D21', 'D22', 'D23', 'D24', 'D25', 'D26', 'D27', 'D28', 'D29', 'D30', 'D31'
                        ];
                    case 'M':
                        return ['M1', 'M2', 'M3'];

                }
            },
            //任务周期的查询条件
            getCyclesForQuery: function () {
                return {
                    A: '不限', mi: '分', H: '时', D: '日', W: '周', M: '月', Y: '年'
                };
            },
            //任务周期
            getCycles: function () {
                return {
                    mi: '分', H: '时', D: '日', W: '周', M: '月', Y: '年'
                };
            },
            //任务状态的查询条件
            getStatusesForQuery: function () {
                return [
                    {ID: 100, Text: '不限'},
                    {ID: 1, Text: 'success'},
                    {ID: 99, Text: 'unsuccess'},
                    {ID: -1, Text: 'fail'},
                    {ID: 0, Text: 'init'},
                    {ID: 4, Text: 'init-error'},
                    {ID: 3, Text: 'suspend'},
                    {ID: 5, Text: 'wait'},
                    {ID: 2, Text: 'running'},
                    {ID: 6, Text: 'ready'},
                    {ID: 7, Text: 'timeout'}
                ];
            },
            //任务状态
            getStatuses: function () {
                return [
                    {ID: 1, Text: 'success'},
                    {ID: 99, Text: 'unsuccess'},
                    {ID: -1, Text: 'fail'},
                    {ID: 0, Text: 'init'},
                    {ID: 4, Text: 'init-error'},
                    {ID: 3, Text: 'suspend'},
                    {ID: 5, Text: 'wait'},
                    {ID: 2, Text: 'running'},
                    {ID: 6, Text: 'ready'},
                    {ID: 7, Text: 'timeout'}
                ];
            },
            //任务优先级的查询条件
            getPrioLvlsForQuery: function () {
                return [
                    {ID: 100, Text: '不限'},
                    {ID: 0, Text: '线上'},
                    {ID: 1, Text: '高'},
                    {ID: 2, Text: '中'},
                    {ID: 3, Text: '低'}
                ];
            },
            //任务优先级
            getPrioLvls: function () {
                return [
                    {ID: 0, Text: '线上'},
                    {ID: 1, Text: '高'},
                    {ID: 2, Text: '中'},
                    {ID: 3, Text: '低'}
                ];
            },
            //传输任务查询结果中table对应的操作
            getTableOperations: function () {
                return [
                    {ID: '0', Text: '预跑'},
                    {ID: '1', Text: '生效'},
                    {ID: '2', Text: '失效'},
                    {ID: '3', Text: '修改'},
                    {ID: '4', Text: '删除'},
                    {ID: '5', Text: '查看'}
                ];
            },
            //导入的git项目的选项
            getGitProjects: function () {
                return ['warehouse', 'data_analysis'];
            },
            //获取状态对应的css
            getStatusCss: function (text) {
                if (text === 1)
                    return 'label-success';
                else
                    return 'label-light';
            },
            //获得周期对应的css
            getCycleCss: function (text) {
                switch (text) {
                    case 'H':
                        return 'label-warning';
                    case 'D':
                        return 'label-primary';
                    case 'W':
                        return 'label-success';
                    case 'M':
                        return 'label-danger';
                    case 'Y':
                        return 'label-purple';
                }
            },
            //获得launch状态对应的css
            getLaunchStatusCss: function (status) {
                switch (status) {
                    case 0:
                        return 'label-warning';
                    case 1:
                        return 'label-success';
                    case 2:
                        return 'label-danger';
                }
            },
            //获取表的刷新类型
            getTableRefreshTypesForQuery: function () {
                return [
                    {ID: 'any', Text: '--请选择--'},
                    {ID: 'full', Text: '全量'},
                    {ID: 'append', Text: '增量'}
                ];
            }
        }
    }
    ]);
