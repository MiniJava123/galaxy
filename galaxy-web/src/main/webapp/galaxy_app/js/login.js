var Global = {};
Global.domain = null;
Global.realName = null;

Global.isIE = !!window.ActiveXObject;
Global.isIE6 = Global.isIE && !window.XMLHttpRequest;
Global.isIE8 = Global.isIE && !!document.documentMode;
Global.isIE7 = Global.isIE && !Global.isIE6 && !Global.isIE8;

Global.isOnline = function () {
    var host = window.location.host,
        reg = /(.*\.dp)|(10\.*)/;

    return reg.test(host);
};

Global.getLogoutUrl = function () {
    if (Global.isOnline()) {
        return 'http://sso.dper.com/logout?service=http%3A%2F%2Fdev.data.dp';
    } else {
        return 'https://sso.a.alpha.dp:8443/logout?service=http%3A%2F%2F127.0.0.1%3A8080%2F';
    }

};

Global.jsonToString = function (obj) {
    var THIS = this;
    switch (typeof (obj)) {
        case 'string':
            return '"' + obj.replace(/(["\\])/g, '\\$1') + '"';
        case 'array':
            return '[' + obj.map(THIS.jsonToString).join(',') + ']';
        case 'object':
            if (obj instanceof Array) {
                var strArr = [];
                var len = obj.length;
                for (var i = 0; i < len; i++) {
                    strArr.push(THIS.jsonToString(obj[i]));
                }
                return '[' + strArr.join(',') + ']';
            } else if (obj == null) {
                return 'null';

            } else {
                var string = [];
                for (var property in obj)
                    string.push(THIS.jsonToString(property) + ':'
                        + THIS.jsonToString(obj[property]));
                return '{' + string.join(',') + '}';
            }
        case 'number':
            return obj;
        case false:
            return obj;
    }
};

Global.getTeamMember = function (role_id) {
    var data = [];

    $.ajax({
        url: 'http://data.dp/pluto/json/getAclAuthuserInfo',
        data: {
            info: Global.jsonToString({"role_id": role_id, "is_valid": 1})
        },
        async: false,
        dataType: 'json',
        type: 'post',
        success: function (cb) {
            if (cb && cb.code == 200) {
                $(cb.msg).each(function (i, e) {
                    data.push([e.employee_email.indexOf('@') > 0 ? e.employee_email.substring(0, e.employee_email.indexOf('@')).toLowerCase() : '',
                        e.employee_name]);
                });
            }
        }
    });

    return data;
};

Global.unique = function (values) {
    var n = {}, r = []; // n为hash表，r为临时数组
    for (var i = 0; i < values.length; i++) // 遍历当前数组
    {
        if (!n[values[i][0]]) // 如果hash表中没有当前项
        {
            n[values[i][0]] = true; // 存入hash表
            r.push(values[i]); // 把当前数组的当前项push到临时数组里面
        }
    }
    return r;
};

//sort by employee surname
Global.teamSort = function (x, y) {
    var x1 = x[0].substring(x[0].indexOf('.')).toLowerCase();
    var y1 = y[0].substring(y[0].indexOf('.')).toLowerCase();

    if (x1 > y1) {
        return 1;
    } else {
        return -1;
    }
}

Global.AdminTeam = Global.getTeamMember(12);
Global.DWTeam = Global.getTeamMember(6);
Global.BiTeam = Global.getTeamMember(7);
Global.UserTeam = Global.getTeamMember(11);
Global.AllTeam = Global.unique(Global.AdminTeam.concat(Global.DWTeam.concat(Global.UserTeam.concat(Global.BiTeam)))).sort(Global.teamSort);

