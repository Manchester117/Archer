/**
 * @description 按接口名称执行查询
 * @type {*|jQuery|HTMLElement}
 */
var $api_list = $("#api_list");
var $search_btn = $("#search_btn");
$search_btn.click(
    function() {
        $api_list.bootstrapTable("selectPage", 1);
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
        $('#project_select').selectpicker('val', '');
        // service_select不需要设置,因为project_select的值发生改变导致changed.bs.select事件发生.
        $api_list.bootstrapTable('selectPage', 1);
    }
);

/**
 * @description 如果是从项目->服务->接口,则不显示项目和服务的下拉搜索
 * @type {*|jQuery|HTMLElement}
 */
var $project_search = $("#project_search");
var $service_search = $("#service_search");
var $create_api_btn = $("#create_api_btn");
var service_id = $("#service_id").val();
if (service_id !== '') {                            // 如果从项目->服务->接口进入,则不显示项目和服务的下拉搜索
    $project_search.hide();
    $service_search.hide();
} else {                                            // 如果直接进入接口列表,则不显示新建接口按钮
    $project_search.show();
    $service_search.show();
    $create_api_btn.css("visibility", "hidden");
}

/**
 * @description 给接口列表的项目下拉菜单里添加项目
 * @type {*|jQuery|HTMLElement}
 */
var $project_select = $("#project_select");
$.ajax({
    type: "post",
    url: "/getAllProjectList",
    dataType: "json",
    contentType: "application/json; charset=utf-8",
    success: function (result) {
        for (var i = 0; i < result.length; i++) {
            $project_select.append("<option value=" + result[i].id + ">" + result[i].projectName + "</option>")
        }
        $project_select.selectpicker("refresh");
        $project_select.selectpicker("render");
    }
});

/**
 * @description 项目->服务的级联选择
 */
$project_select.on('changed.bs.select', function (e, clickedIndex, isSelected, previousValue) {
    var $service_select = $("#service_select");
    if (clickedIndex !== 0 && clickedIndex !== undefined) {     // 必须要有undefined的条件(因为重置搜索条件会导致project_select发生changed.bs.select事件,所以会先走if语句,但clickedIndex处于未定义的状态)
        $service_select.prop('disabled', false);                // 如果项目下拉菜单的值是实际项目,则启用服务下拉菜单
        var projectId = $project_select.selectpicker("val");
        $.ajax({
            type: "post",
            url: "/getServiceListByProjectId",
            data: "projectId=" + projectId,
            dataType: "json",
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            success: function (result) {
                $service_select.empty();
                for (var i = 0; i < result.length; i++) {
                    $service_select.append("<option value=" + result[i].id + ">" + result[i].serviceName + "</option>")
                }
                if ($service_select.children().size() === 0) {
                    $service_select.append("<option value=''>请选择</option>");
                }
                $service_select.selectpicker("refresh");
                $service_select.selectpicker("render");
            }
        });
    } else {
        $service_select.prop('disabled', true);
        $service_select.empty();
        $service_select.append("<option value=''>请选择</option>");
        $service_select.selectpicker("refresh");
        $service_select.selectpicker("render");
    }
});

function delApiConfirm(apiId) {
    bootbox.confirm({
        message: "是否要删除当前接口?",
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
                delApiInfo(apiId);
        }
    })
}


/**
 * @description 删除接口方法
 * @param apiId
 */
function delApiInfo(apiId) {
    $.ajax({
        type: "post",
        url: "/deleteApiAndVerifyInfo",
        data: "apiId=" + apiId,
        dataType: "json",
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        success: function(result) {
            if (result['deleteApiFlag'] === 1) {                                 // 如果从库中删除接口成功
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
            $api_list.bootstrapTable('selectPage', 1);                          // 删除后刷新项目列表
        }
    });
}

/**
 * @description 跳转到添加接口页
 */
function forwardAddApiPage() {
    var serviceId = $("#service_id").val();
    window.location.href = "/apiAddPage?serviceId=" + serviceId;
}