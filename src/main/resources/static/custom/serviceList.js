/**
 * @description 服务列表
 * @type {*|jQuery|HTMLElement}
 */
var $service_list = $('#service_list');
$service_list.bootstrapTable({
    url: '/getServiceList',
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
            serviceName: $('#search_name').val(),
            projectId: $('#project_id').val()
        }
    },
    columns: [
        {
            field: 'id',
            title: '编号',
            align: 'center',
            formatter: function(value, row, index) {                                        // 以列表的序列来标号
                var pageSize = $service_list.bootstrapTable('getOptions').pageSize;         // 通过表的#id 可以得到每页多少条
                var pageNumber = $service_list.bootstrapTable('getOptions').pageNumber;     // 通过表的#id 可以得到当前第几页
                return pageSize * (pageNumber - 1) + index + 1;                             // 返回每条的序号: 每页条数 * (当前页 - 1)+ 序号
            },
            width: 50
        }, {
            field: 'serviceName',
            title: '服务名称',
            align: 'center',
            width: 300
        }, {
            field: 'baseUrl',
            title: '根URL',
            align: 'center'
        }, {
            field: 'type',
            title: '服务类型',
            align: 'center',
            formatter: function(value, row, index) {
                if (value === 1) {
                    return "Service";
                } else if (value === 2) {
                    return "App"
                } else if (value === 3) {
                    return "Web"
                }
            }
        }, {
            field: 'version',
            title: '版本',
            align: 'center'
        }, {
            field: 'createTime',
            title: '创建时间',
            align: 'center',
            formatter: function (value, row, index) {
                return convertDateFormat(value);
            },
            width: 160
        }, {
            field: 'id',
            title: '操作',
            align: 'center',
            formatter: function (value, row, index) {
                return [
                    '<a href="javascript:modService(' + value + ')">' +
                    '<i class="fa fa-pencil"></i>编辑' +
                    '</a>',
                    '&nbsp&nbsp&nbsp&nbsp' +
                    '<a href="/apiList?serviceId=' + value + '">' +
                    '<i class="fa fa-table"></i>接口' +
                    '</a>',
                    '&nbsp&nbsp&nbsp&nbsp' +
                    '<a href="javascript:delServiceConfirm(' + value + ')">' +
                    '<i class="fa fa-times"></i>删除' +
                    '</a>'
                ].join('');
            },
            width: 200
        }
    ]
});

/**
 * @description 按服务名称执行查询
 * @type {*|jQuery|HTMLElement}
 */
var $search_btn = $('#search_btn');
$search_btn.click(
    function() {
        $service_list.bootstrapTable("selectPage", 1);
    }
);

/**
 * @description 重置搜索条件
 * @type {*|jQuery|HTMLElement}
 */
var $reset_btn = $('#reset_btn');
$reset_btn.click(
    function() {
        $('.form-group :text').val("");
        $service_list.bootstrapTable("selectPage", 1);
    }
);

/**
 * @description 创建服务
 * @type {*|jQuery|HTMLElement}
 */
var $create_service_submit = $("#create_service_submit");
$create_service_submit.click(
    function() {
        var serviceName = $("#create_service_name").val();
        var baseUrl = $("#create_base_url").val();
        var version = $("#create_version").val();
        var type = $("#create_type").val();
        var description = $("#create_description").val();
        var projectId = $("#project_id").val();
        // 通过Ajax提交实体
        $.ajax({
            type: "post",
            url: "/createService",
            data: JSON.stringify({"serviceName": serviceName, "baseUrl": baseUrl, "version": version, "type": type, "description": description, "projectId": projectId}),
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            success: function(result) {
                var $create_service_modal = $("#create_service_modal");
                $create_service_modal.modal("hide");                        // 先将模态框隐藏
                $create_service_modal.on('hidden.bs.modal', function(){     // 在清空模态框中的表单数据
                    $("#create_service_form").resetForm();
                });
                if (result['flag'] === 1) {                                 // 如果向库中添加项目成功
                    bootbox.alert({
                        title: '提示',
                        message: "创建服务成功"
                    });
                } else {
                    bootbox.alert({
                        title: '警告',
                        message: "创建服务失败"
                    });
                }
                $service_list.bootstrapTable('refresh');                    // 关闭后刷新项目列表
            }
        });
    }
);

/**
 * @description 关闭模态框时需要做值的清空
 */
$('#create_service_modal').on('hide.bs.modal', function () {
    $("#create_service_name").attr("value", "");
    $("#create_base_url").val("");
    $("#create_version").attr("value", "");
    $("#create_type").selectpicker('val', "1");
    $("#create_description").val("");
});

/**
 * @description 在打开编辑项目时需要将当前项目的值写入到模态框当中
 * @param serviceId
 */
function modService(serviceId) {
    $.ajax({
        type: "post",
        url: "/getServiceInfo",
        data: "serviceId=" + serviceId,
        dataType: "json",
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        success: function(result) {
            $("#modify_service_id").attr("value", result["id"]);
            $("#modify_service_name").attr("value", result["serviceName"]);
            $("#modify_base_url").val(result["baseUrl"]);
            $("#modify_version").attr("value", result["version"]);
            $("#modify_type").selectpicker('val', result["type"]);
            $("#modify_description").val(result["description"]);
            $('#modify_service_modal').modal('show');                       // Ajax显示模态框
        }
    });
}

/**
 * @description 关闭模态框时需要做值的清空
 */
$('#modify_service_modal').on('hide.bs.modal', function () {
    $("#modify_service_id").attr("value", "");
    $("#modify_service_name").attr("value", "");
    $("#modify_base_url").val("");
    $("#modify_version").attr("value", "");
    $("#modify_type").selectpicker('val', "1");
    $("#modify_description").val("");
});

/**
 * @description 提交修改后的服务信息
 * @type {*|jQuery|HTMLElement}
 */
var $modify_service_submit = $('#modify_service_submit');
$modify_service_submit.click(
    function() {
        var serviceId = $("#modify_service_id").val();
        var serviceName = $("#modify_service_name").val();
        var baseUrl = $("#modify_base_url").val();
        var version = $("#modify_version").val();
        var type = $("#modify_type").val();
        var description = $("#modify_description").val();
        // 通过Ajax提交实体
        $.ajax({
            type: "post",
            url: "/modifyService",
            data: JSON.stringify({"id": serviceId, "serviceName": serviceName, "baseUrl": baseUrl, "version": version, "type": type, "description": description}),
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            success: function(result) {                                     // 提交成功后关闭模态框
                var $modify_service_modal = $("#modify_service_modal");
                $modify_service_modal.on('hidden.bs.modal', function(){     // 在清空模态框中的表单数据
                    $("#modify_service_form").resetForm();
                });
                $modify_service_modal.modal("hide");                        // 先将模态框隐藏
                $service_list.bootstrapTable('selectPage', 1);              // 关闭后刷新项目列表
                if (result['flag'] === 1) {                                 // 如果向库中添加项目成功
                    bootbox.alert({
                        title: '提示',
                        message: "编辑服务成功"
                    });
                } else {
                    bootbox.alert({
                        title: '警告',
                        message: "编辑服务失败"
                    });
                }
            }
        });
    }
);

function delServiceConfirm(serviceId) {
    bootbox.confirm({
        message: "是否要删除整个服务?",
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
                delService(serviceId)
        }
    })
}

/**
 * @description 删除服务
 * @param serviceId
 */
function delService(serviceId) {
    $.ajax({
        type: "post",
        url: "/deleteService",
        data: "serviceId=" + serviceId,
        dataType: "json",
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        success: function(result) {
            if (result['flag'] === 1) {                                  // 如果向库中添加项目成功
                bootbox.alert({
                    title: '提示',
                    message: "删除服务成功"
                });
            } else {
                bootbox.alert({
                    title: '警告',
                    message: "删除服务失败"
                });
            }
            $service_list.bootstrapTable('selectPage', 1);              // 删除后刷新项目列表
        }
    });
}