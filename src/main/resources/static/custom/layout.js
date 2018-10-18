/**
 * @description 判断字符串是否是JSON
 * @param str
 * @returns {boolean}
 */
function isJSON(str) {
    if (typeof str === 'string') {
        try {
            JSON.parse(str);
            return true;
        } catch(e) {
            return false;
        }
    }
}

/**
 * @description 转换日期格式(时间戳转换为datetime格式)
 * @param date_value
 * @returns {string}
 */
function convertDateFormat(date_value) {
    var date_val = date_value + "";
    if (date_value != null) {
        var date = new Date(parseInt(date_val.replace("/Date(", "").replace(")/", ""), 10));
        var month = date.getMonth() + 1 < 10 ? "0" + (date.getMonth() + 1) : date.getMonth() + 1;
        var currentDate = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();

        var hours = date.getHours() < 10 ? "0" + date.getHours() : date.getHours();
        var minutes = date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes();
        var seconds = date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();

        return date.getFullYear() + "-" + month + "-" + currentDate + " " + hours + ":" + minutes + ":" + seconds;
    }
}

/**
 * @description 服务所属(新建/编辑接口和新建/编辑用例共用)
 * @type {*|jQuery|HTMLElement}
 */
var $service_select = $(".service_select");
$.ajax({
    type: "post",
    url: "/getAllServiceList",
    dataType: "json",
    contentType: "application/json; charset=utf-8",
    success: function (result) {
        for (var i = 0; i < result.length; i++) {
            $service_select.append("<option value=" + result[i].id + ">" + result[i].serviceName + "</option>")
        }
        $service_select.selectpicker("refresh");
        $service_select.selectpicker("render");
    }
});

/**
 * @description 选择运行时的Hosts(单接口运行和用例运行共用)
 * @type {*|jQuery|HTMLElement}
 */
var $hosts_select = $("#hosts_select");
$.ajax({
    type: "post",
    url: "/getAllHostsList",
    dataType: "json",
    contentType: "application/json; charset=utf-8",
    success: function (result) {
        for (var i = 0; i < result.length; i++) {
            $hosts_select.append("<option value=" + result[i].id + ">" + result[i].ipAddress + " | "+ result[i].domain + "</option>")
        }
        $hosts_select.selectpicker("refresh");
        $hosts_select.selectpicker("render");
    }
});

/**
 * @description Api列表
 * @type {*|jQuery|HTMLElement}
 */
var $api_list = $('#api_list');
$api_list.bootstrapTable({
    url: '/getApiWithServiceList',
    method: 'post',
    dataType: 'json',
    striped: true,
    pagination: true,
    sidePagination: 'server',
    pageNumber: 1,
    pageSize: 15,
    pageList: [15, 30, 60],
    paginationPreText: '‹',
    paginationNextText: '›',
    locale: 'zh-CN',
    queryParams: function (params) {
        var serviceId = "";
        if ($('#service_select').selectpicker('val') !== "")
            serviceId = $('#service_select').selectpicker('val');
        else if ($('#service_id').val() !== "") {
            serviceId = $('#service_id').val();
        }

        return {
            offset: params.offset,
            limit: params.limit,
            apiName: $('#search_name').val(),
            serviceId: serviceId
        }
    },
    columns: [
        {
            field: 'id',
            title: '编号',
            align: 'center',
            formatter: function(value, row, index) {                                    // 以列表的序列来标号
                var pageSize = $api_list.bootstrapTable('getOptions').pageSize;         // 通过表的#id 可以得到每页多少条
                var pageNumber = $api_list.bootstrapTable('getOptions').pageNumber;     // 通过表的#id 可以得到当前第几页
                return pageSize * (pageNumber - 1) + index + 1;                         // 返回每条的序号： 每页条数 * (当前页 - 1) + 序号
            },
            width: 50
        }, {
            field: 'apiName',
            title: '接口名称',
            align: 'center',
            cellStyle: {
                css: {
                    "overflow": "hidden",
                    "text-overflow": "ellipsis",
                    "white-space": "nowrap"
                }
            },
            width: 300
        }, {
            field: 'protocol',
            title: '使用协议',
            align: 'center',
            formatter: function(value, row, index) {
                if (value === 1)
                    return "HTTP";
                else if (value === 2)
                    return "HTTPS";
            },
            width: 100
        }, {
            field: 'method',
            title: '请求方式',
            align: 'center',
            formatter: function(value, row, index) {
                if (value === 1)
                    return "GET";
                else if (value === 2)
                    return "POST";
                else if (value === 3)
                    return "PUT";
                else if (value === 4)
                    return "DELETE";
            },
            width: 100
        }, {
            field: 'url',
            title: 'URL',
            align: 'center'
        }, {
            field: 'createTime',
            title: '创建时间',
            align: 'center',
            formatter: function(value, row, index) {
                return convertDateFormat(value);
            },
            width: 160
        }, {
            field: 'serviceName',
            title: '所属服务',
            align: 'center',
            cellStyle: {
                css: {
                    "overflow": "hidden",
                    "text-overflow": "ellipsis",
                    "white-space": "nowrap"
                }
            },
            width: 240
        }, {
            field: 'id',
            title: '操作',
            align: 'center',
            formatter: function (value, row, index) {
                return [
                    '<a href="/apiModifyPage?apiId=' + value + '">' +
                    '<i class="fa fa-pencil"></i>编辑' +
                    '</a>',
                    '&nbsp&nbsp&nbsp&nbsp' +
                    '<a href="javascript:delApiConfirm(' + value + ')">' +
                    '<i class="fa fa-times"></i>删除' +
                    '</a>'
                ].join('');
            },
            width: 150
        }
    ]
});


