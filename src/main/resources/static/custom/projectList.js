/**
 * @description 项目列表
 * @type {*|jQuery|HTMLElement}
 */
var $project_list = $('#project_list');
$project_list.bootstrapTable({
    url: '/getProjectList',
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
            projectName: $('#search_name').val()
        }
    },
    columns: [
        {
            field: 'id',
            title: '编号',
            align: 'center',
            formatter: function(value, row, index) {                                        // 以列表的序列来标号
                var pageSize = $project_list.bootstrapTable('getOptions').pageSize;         // 通过表的#id 可以得到每页多少条
                var pageNumber = $project_list.bootstrapTable('getOptions').pageNumber;     // 通过表的#id 可以得到当前第几页
                return pageSize * (pageNumber - 1) + index + 1;                             // 返回每条的序号: 每页条数 * (当前页 - 1)+ 序号
            },
            width: 50
        }, {
            field: 'projectName',
            title: '项目名称',
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
            field: 'createTime',
            title: '创建时间',
            align: 'center',
            formatter: function (value, row, index) {
                return convertDateFormat(value);
            },
            width: 160
        }, {
            field: 'description',
            title: '项目描述',
            align: 'center'
        }, {
            field: 'id',
            title: '操作',
            align: 'center',
            formatter: function (value, row, index) {
                return [
                    '<a href="javascript:modProject(' + value + ')">' +
                    '<i class="fa fa-pencil"></i>编辑' +
                    '</a>',
                    '&nbsp&nbsp&nbsp&nbsp' +
                    '<a href="/serviceList?projectId=' + value + '">' +
                    '<i class="fa fa-cogs"></i>服务' +
                    '</a>',
                    '&nbsp&nbsp&nbsp&nbsp' +
                    '<a href="javascript:delProjectConfirm(' + value + ')">' +
                    '<i class="fa fa-times"></i>删除' +
                    '</a>'
                ].join('');
            },
            width: 200
        }
    ]
});

/**
 * @description 给接口列表的项目下拉菜单里添加项目
 * @type {*|jQuery|HTMLElement}
 */
var $search_project_name = $("#search_project_name");
$.ajax({
    type: "post",
    url: "/getAllProjectList",
    dataType: "json",
    contentType: "application/json; charset=utf-8",
    success: function (result) {
        for (var i = 0; i < result.length; i++) {
            $search_project_name.append("<option value=" + result[i].id + ">" + result[i].projectName + "</option>")
        }
        $search_project_name.selectpicker("refresh");
        $search_project_name.selectpicker("render");
    }
});

/**
 * @description 按项目名称执行查询
 * @type {*|jQuery|HTMLElement}
 */
var $search_btn = $("#search_btn");
$search_btn.click(
    function() {
        $project_list.bootstrapTable("selectPage", 1);
    }
);

/**
 * @description 重置搜索条件
 * @type {*|jQuery|HTMLElement}
 */
var $reset_btn = $("#reset_btn");
$reset_btn.click(
    function() {
        $search_project_name.selectpicker('val', '');
        $project_list.bootstrapTable('selectPage', 1);
    }
);

/**
 * @description 创建项目
 * @type {*|jQuery|HTMLElement}
 */
var $create_project_submit = $("#create_project_submit");
$create_project_submit.click(
    function() {
        var projectName = $("#create_project_name").val();
        var description = $("#create_description").val();
        // 通过Ajax提交实体
        $.ajax({
            type: "post",
            url: "/createProject",
            data: JSON.stringify({"projectName": projectName, "description": description}),
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            success: function(result) {                                     // 提交成功后关闭模态框
                var $create_project_modal = $("#create_project_modal");
                $create_project_modal.modal("hide");                        // 先将模态框隐藏
                $create_project_modal.on('hidden.bs.modal', function(){     // 在清空模态框中的表单数据
                    $("#create_project_form").resetForm();
                });
                if (result['flag'] === 1) {                                 // 如果向库中添加项目成功
                    bootbox.alert({
                        title: '提示',
                        message: "创建项目成功"
                    });
                } else {
                    bootbox.alert({
                        title: '警告',
                        message: "创建项目失败"
                    });
                }
                $project_list.bootstrapTable('refresh');                    // 关闭后刷新项目列表
            }
        });
    }
);

/**
 * @description 关闭模态框时需要做值的清空
 */
$('#create_project_modal').on('hide.bs.modal', function () {
    $("#create_project_name").attr("value", "");
    $("#create_description").val("");
});

function modProject(projectId) {
    $.ajax({
        type: "post",
        url: "/getProjectInfo",
        data: "projectId=" + projectId,
        dataType: "json",
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        success: function(result) {
            $("#modify_project_id").attr("value", result["id"]);
            $("#modify_project_name").attr("value", result["projectName"]);
            $("#modify_description").val(result["description"]);
            $('#modify_project_modal').modal('show');                       // Ajax显示模态框
        }
    });
}

/**
 * @description 提交修改后的项目信息
 * @type {*|jQuery|HTMLElement}
 */
var $modify_project_submit = $('#modify_project_submit');
$modify_project_submit.click(
    function() {
        var projectId = $("#modify_project_id").val();
        var projectName = $("#modify_project_name").val();
        var description = $("#modify_description").val();

        // 通过Ajax提交实体
        $.ajax({
            type: "post",
            url: "/modifyProject",
            data: JSON.stringify({"id": projectId, "projectName": projectName, "description": description}),
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            success: function(result) {                                     // 提交成功后关闭模态框
                var $modify_project_modal = $("#modify_project_modal");
                $modify_project_modal.modal("hide");                        // 先将模态框隐藏
                $modify_project_modal.on('hidden.bs.modal', function(){     // 在清空模态框中的表单数据
                    $("#modify_project_form").resetForm();
                });
                if (result['flag'] === 1) {                                 // 如果向库中添加项目成功
                    bootbox.alert({
                        title: '提示',
                        message: "编辑项目成功"
                    });
                } else {
                    bootbox.alert({
                        title: '警告',
                        message: "编辑项目失败"
                    });
                }
                $project_list.bootstrapTable('selectPage', 1);              // 关闭后刷新项目列表
            }
        });
    }
);

function delProjectConfirm(projectId) {
    bootbox.confirm({
        message: "是否要删除整个项目?",
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
                delProject(projectId);
        }
    })
}

/**
 * @description 删除项目
 * @param projectId
 */
function delProject(projectId) {
    $.ajax({
        type: "post",
        url: "/deleteProject",
        data: "projectId=" + projectId,
        dataType: "json",
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        success: function(result) {
            if (result['flag'] === 1) {                                 // 如果向库中添加项目成功
                bootbox.alert({
                    title: '提示',
                    message: "删除项目成功"
                });
            } else {
                bootbox.alert({
                    title: '警告',
                    message: "删除项目失败"
                });
            }
            $project_list.bootstrapTable('selectPage', 1);              // 删除后刷新项目列表
        }
    });
}