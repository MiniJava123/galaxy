/**
 * Created by mt on 2014/5/20.
 */
'use strict';

angular.module("galaxy.filter", [])
    .filter("dateFormat", function () {
        return function (input, formatStr, type) {
            Date.prototype.format = function (format) {
                var o = {
                    'M+': this.getMonth() + 1,
                    'd+': this.getDate(),
                    'h+': this.getHours(),
                    'm+': this.getMinutes(),
                    's+': this.getSeconds(),
                    'q+': Math.floor((this.getMonth() + 3) / 3),
                    'S': this.getMilliseconds()
                };
                if (/(y+)/.test(format) || /(Y+)/.test(format)) {
                    format = format.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
                }
                for (var k in o) {
                    if (new RegExp('(' + k + ')').test(format)) {
                        format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length));
                    }
                }
                return format;
            };
            var date = new Date();
            if (type == 0) {
                var ymd = input.split('-');
                date.setFullYear(ymd[0], ymd[1] - 1, ymd[2]);
                return date.format(formatStr);

            }
            else
                return new Date(input).format(formatStr);
        }
    })
    .filter("StringReplace", function () {
        return function (string, regex, dst) {
            var regExp = new RegExp(regex, "g");
            return string === undefined ? '' : string.replace(regExp, dst);
        }
    })



