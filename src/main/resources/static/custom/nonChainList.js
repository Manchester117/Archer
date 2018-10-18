/**
 * @description 未归档API列表
 * @type {*|jQuery|HTMLElement}
 */
var $non_chain_api_list = $('#non_chain_api_list');
$non_chain_api_list.bootstrapTable({
    url: '/getNonChainApiList',
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
        return {
            offset: params.offset,
            limit: params.limit,
            apiName: $('#search_name').val()
        }
    },
    columns: [
        {
            field: 'id',
            title: '编号',
            align: 'center',
            formatter: function(value, row, index) {                                                // 以列表的序列来标号
                var pageSize = $non_chain_api_list.bootstrapTable('getOptions').pageSize;           // 通过表的#id 可以得到每页多少条
                var pageNumber = $non_chain_api_list.bootstrapTable('getOptions').pageNumber;       // 通过表的#id 可以得到当前第几页
                return pageSize * (pageNumber - 1) + index + 1;                                     // 返回每条的序号： 每页条数 * (当前页 - 1) + 序号
            }
        }, {
            field: 'apiName',
            title: '接口名称',
            align: 'center'
        }, {
            field: 'protocol',
            title: '使用协议',
            align: 'center',
            formatter: function(value, row, index) {
                if (value === 1)
                    return "HTTP";
                else if (value === 2)
                    return "HTTPS";
            }
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
            }
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
            }
        }, {
            field: 'id',
            title: '操作',
            align: 'center',
            formatter: function (value, row, index) {
                return [
                    '<a href="javascript:chainService(' + value + ')">' +
                    '<i class="fa fa-pencil"></i>归档' +
                    '</a>',
                    '&nbsp&nbsp&nbsp&nbsp' +
                    '<a href="javascript:delNonChainConfirm(' + value + ')">' +
                    '<i class="fa fa-times"></i>删除' +
                    '</a>'
                ].join('');
            }
        }
    ]
});

/**
 * @description 按服务名称执行查询
 * @type {*|jQuery|HTMLElement}
 */
var $search_btn = $("#search_btn");
$search_btn.click(
    function() {
        $non_chain_api_list.bootstrapTable("selectPage", 1);
    }
);

/**
 * @description 重置搜索条件
 * @type {*|jQuery|HTMLElement}
 */
var $reset_btn = $("#reset_btn");
$reset_btn.click(
    function() {
        $('.form-group :text').val("");
        $non_chain_api_list.bootstrapTable('selectPage', 1);
    }
);

function delNonChainConfirm(apiId) {
    bootbox.confirm({
        message: "是否要删除未关联接口?",
        buttons: {
            confirm: {
                label: '是',
                className: 'btn-danger'
            },
            cancel: {
                label: '否',
                className: 'btn-default'
            }
        },
        callback: function (result) {
            if (result === true)
                delNonChainApiInfo(apiId);
        }
    })
}

/**
 * @description 删除未归档API方法
 * @param apiId
 */
function delNonChainApiInfo(apiId) {
    $.ajax({
        type: "post",
        url: "/deleteApiAndVerifyInfo",
        data: "apiId=" + apiId,
        dataType: "json",
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        success: function(result) {
            if (result['deleteApiFlag'] === 1) {                            // 如果从库中删除接口成功
                bootbox.alert({
                    title: '提示',
                    message: "删除接口成功"
                });
            } else {
                bootbox.alert({
                    title: '警告',
                    message: "删除接口失败"
                });
            }
            $non_chain_api_list.bootstrapTable('selectPage', 1);            // 删除后刷新项目列表
        }
    });
}

var $create_chain_modal = $('#create_chain_modal');
var $project_select_non_chain = $("#project_select_non_chain");
var $service_select_non_chain = $("#service_select_non_chain");
function chainService(apiId) {
    $("#api_id").val(apiId);
    /**
     * @description 给接口列表的项目下拉菜单里添加项目
     * @type {*|jQuery|HTMLElement}
     */
    $.ajax({
        type: "post",
        url: "/getAllProjectList",
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        success: function (projectResult) {
            $project_select_non_chain.empty();                                 // 重启打开模态框时需要清空下拉列表中的所有选项
            $project_select_non_chain.append("<option value=''>请选择</option>");
            for (var i = 0; i < projectResult.length; i++) {
                $project_select_non_chain.append("<option value=" + projectResult[i].id + ">" + projectResult[i].projectName + "</option>");
            }
            $project_select_non_chain.selectpicker("refresh");
            $project_select_non_chain.selectpicker("render");
        }
    });

    /**
     * @description 项目->服务的级联选择
     */
    $project_select_non_chain.on('changed.bs.select', function (e, clickedIndex, isSelected, previousValue) {
        if (clickedIndex !== 0 && clickedIndex !== undefined) {                 // 必须要有undefined的条件(因为重置搜索条件会导致project_select发生changed.bs.select事件,所以会先走if语句,但clickedIndex处于未定义的状态)
            $service_select_non_chain.prop('disabled', false);                  // 如果项目下拉菜单的值是实际项目,则启用服务下拉菜单
            var projectId = $project_select_non_chain.selectpicker("val");
            $.ajax({
                type: "post",
                url: "/getServiceListByProjectId",
                data: "projectId=" + projectId,
                dataType: "json",
                contentType: "application/x-www-form-urlencoded; charset=utf-8",
                success: function (result) {
                    $service_select_non_chain.empty();
                    for (var i = 0; i < result.length; i++) {
                        $service_select_non_chain.append("<option value=" + result[i].id + ">" + result[i].serviceName + "</option>")
                    }
                    if ($service_select_non_chain.children().length === 0) {
                        $service_select_non_chain.append("<option value=''>当前项目没有服务</option>");
                        $service_select_non_chain.prop('disabled', true);
                    }
                    $service_select_non_chain.selectpicker("refresh");
                    $service_select_non_chain.selectpicker("render");
                }
            });
        } else {
            $service_select_non_chain.prop('disabled', true);
            $service_select_non_chain.empty();
            $service_select_non_chain.append("<option value=''>请选择</option>");
            $service_select_non_chain.selectpicker("refresh");
            $service_select_non_chain.selectpicker("render");
        }
    });
    $create_chain_modal.modal('show');
}

$create_chain_modal.on('hide.bs.modal', function() {
    $service_select_non_chain.empty();
    $service_select_non_chain.append("<option value=''>请选择</option>");
    $service_select_non_chain.selectpicker("refresh");
    $service_select_non_chain.selectpicker("render");
});

var $create_chain_submit = $("#create_chain_submit");
$create_chain_submit.click(
    function () {
        if ($service_select_non_chain.selectpicker('val') === '') {
            bootbox.alert("归档服务不存在!");
        } else {
            var apiId = $("#api_id").val();
            var serviceId = $service_select_non_chain.selectpicker('val');
            $.ajax({
                type: "post",
                url: "/modifyApiService?apiId=" + apiId + "&serviceId=" + serviceId,
                dataType: "json",
                contentType: "application/x-www-form-urlencoded; charset=utf-8",
                success: function(result) {
                    if (result['updateApiFlag'] === 1) {
                        bootbox.alert("接口归档成功");
                    } else {
                        bootbox.alert("接口归档失败");
                    }
                    $create_chain_modal.modal('hide');
                    $non_chain_api_list.bootstrapTable('selectPage', 1);
                }
            });
        }
    }
);