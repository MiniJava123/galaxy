/**
 * Created by Sunny on 14-8-21.
 */

angular.module('utils.service', ['ngResource'])
    .factory('UtilsService', [ function () {
        var developers = new Array();
        initDevelopers();
        //初始化developers
        function initDevelopers() {
            for (var i = 0; i < Global.AllTeam.length; i++) {
                var developObj = new Object();
                developObj['ID'] = Global.AllTeam[i][0];
                developObj['Text'] = Global.AllTeam[i][1];
                developers.push(developObj);
            }
        };
        return {
            //检验输入是够是单个数字或者以分号分割开的多个数字
            multiDigitalsSeperatedBySemicolon: function (text) {
                var reg = /^\d+$/;
                if (text.indexOf(";") != -1) {
                    var codes = new Array();
                    codes = text.split(";");
                    for (var i = 0; i < codes.length; i++) {
                        if (!codes[i].match(reg)) {
                            return false;
                        }
                    }
                    return true;
                }
                else {
                    return text.match(reg);
                }
            },
            //获取galaxy开发组成员
            getDevelopers: function () {
                if (developers.length != 0)
                    return developers;
                for (var i = 0; i < Global.AllTeam.length; i++) {
                    var developObj = new Object();
                    developObj['ID'] = Global.AllTeam[i][0];
                    developObj['Text'] = Global.AllTeam[i][1];
                    developers.push(developObj);
                }
            },
            //根据pinyinName获得developer
            getDeveloperByPinyinName: function (pinyinName) {
                for (var i = 0; i < Global.AllTeam.length; i++) {
                    if (Global.AllTeam[i][0] == pinyinName) {
                        var developObj = new Object();
                        developObj['ID'] = Global.AllTeam[i][0];
                        developObj['Text'] = Global.AllTeam[i][1];
                        return developObj;
                    }
                }
                return '';
            },
            //根据中文名获得developer
            getDeveloperByRealName: function (realName) {
                for (var i = 0; i < Global.AllTeam.length; i++) {
                    if (Global.AllTeam[i][1] == realName) {
                        var developObj = new Object();
                        developObj['ID'] = Global.AllTeam[i][0];
                        developObj['Text'] = Global.AllTeam[i][1];
                        return developObj;
                    }
                }
                return null;
            },
            //根据英文名获得中文名
            getDeveloperRealName: function (pinyinName) {
                var text = pinyinName;
                for (var i = 0; i < developers.length; i++) {
                    if (developers[i].ID === pinyinName) {
                        text = developers[i].Text;
                    }
                }
                return text;
            },
            //由taskDO转taskRelaDO
            getTaskRelaDO: function (dependenceTasks) {
                var dependences = [];
                for (var i in dependenceTasks) {
                    var dependence = {
                        taskPreId: dependenceTasks[i].taskId,
                        taskName: dependenceTasks[i].taskName,
                        cycleGap: dependenceTasks[i].cycleGap,
                        owner: dependenceTasks[i].owner
                    };
                    dependences.push(dependence);
                }
                return dependences;
            },

            //由taskRelaDO转TaskDO
            getTaskDO: function (dependenceTasks) {
                var dependences = [];
                for (var i in dependenceTasks) {
                    var dependence = {
                        taskId: dependenceTasks[i].taskPreId,
                        taskName: dependenceTasks[i].taskName,
                        cycleGap: dependenceTasks[i].cycleGap,
                        owner: dependenceTasks[i].owner,
                        cycle: dependenceTasks[i].cycleGap[0]
                    };
                    dependences.push(dependence);
                }
                return dependences;
            },
            //检验输入的taskNameOrId是否符合条件，taskId是以,分割开的数字
            checkTaskNameOrId: function (taskNameOrId) {
                if (taskNameOrId.indexOf(",") != -1) {
                    var taskIds = new Array();
                    taskIds = taskNameOrId.split(",");
                    for (var i = 0; i < taskIds.length; i++) {
                        var reg = /^\d+$/;
                        if (!taskIds[i].match(reg)) {
                            return false;
                        }
                    }
                }
                return true;
            },
            //日期转str，x为Date,y为格式
            dateToStr: function (x, y) {
                var z = {M: x.getMonth() + 1, d: x.getDate(), h: x.getHours(), m: x.getMinutes(), s: x.getSeconds()};
                y = y.replace(/(M+|d+|h+|m+|s+)/g, function (v) {
                    return ((v.length > 1 ? "0" : "") + eval('z.' + v.slice(-1))).slice(-2)
                });
                return y.replace(/(y+)/g, function (v) {
                    return x.getFullYear().toString().slice(-v.length)
                });
            },
            //检测successCode是否是以;分割的数字
            checkSuccessCode: function (successCode) {
                var reg = /^\d+$/;
                if (successCode.indexOf(";") != -1) {
                    var codes = new Array();
                    codes = successCode.split(";");
                    for (var i = 0; i < codes.length; i++) {
                        if (!codes[i].match(reg)) {
                            return false;
                        }
                    }
                    return true;
                }
                else {
                    return successCode.match(reg);
                }
            },
            //通过task name获得table name，即去掉前面的hive##
            getTableNameByTaskName: function (taskName) {
                return taskName.substring(6);
            },
            //src是否以tar结尾
            endWith: function (src, tar) {
                if (src.lastIndexOf(tar) == (src.length - tar.length))
                    return true;
                return false;
            },
            //根据传入的developer获得真正的developer的pinyin
            getRealDeveloper: function (developer) {
                if (developer == '')
                    return null;
                if (!developer.ID) {
                    var developer = this.getDeveloperByRealName(developer);
                    if (developer == null)
                        return null;
                    else
                        return developer.ID;
                }
                return developer.ID
            }
        }
    }
    ])
;
