/**
 * @description 用例列表
 * @type {*|jQuery|HTMLElement}
 */
var $case_info_table = $('#case_info_table');
$case_info_table.bootstrapTable({
    url: '/getCaseListWithProject',
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
            projectId: $('#search_project_name').selectpicker('val')
        }
    },
    columns: [
        {
            field: 'id',
            title: '编号',
            align: 'center',
            formatter: function(value, row, index) {                                        // 以列表的序列来标号
                var pageSize = $case_info_table.bootstrapTable('getOptions').pageSize;      // 通过表的#id 可以得到每页多少条
                var pageNumber = $case_info_table.bootstrapTable('getOptions').pageNumber;  // 通过表的#id 可以得到当前第几页
                return pageSize * (pageNumber - 1) + index + 1;                             // 返回每条的序号： 每页条数 * （当前页 - 1 ）+ 序号
            },
            width: 50
        }, {
            field: 'caseName',
            title: '用例名称',
            align: 'center',
            cellStyle: {
                css: {
                    "overflow": "hidden",
                    "text-overflow": "ellipsis",
                    "white-space": "nowrap"
                }
            },
            width: 400
        }, {
            field: 'projectName',
            title: '所属项目',
            align: 'center'
        }, {
            field: 'id',
            title: '操作',
            align: 'center',
            formatter: function (value, row, index) {
                return [
                    '<a href="javascript:modCaseInfo(' + value + ')">' +
                    '<i class="fa fa-pencil"></i>编辑' +
                    '</a>',
                    '&nbsp&nbsp&nbsp&nbsp' +
                    '<a href="javascript:delCaseConfirm(' + value + ')">' +
                    '<i class="fa fa-times"></i>删除' +
                    '</a>',
                    '&nbsp&nbsp&nbsp&nbsp' +
                    '<a href="javascript:getApiInCase(' + value + ')">' +
                    '<i class="fa fa-link"></i>接口' +
                    '</a>'
                ].join('');
            },
            width: 200
        }
    ]
});

/**
 * @description 给用例配置的项目下拉菜单里添加项目
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
 * @description 按服务名称执行查询
 * @type {*|jQuery|HTMLElement}
 */
var $search_btn = $("#search_btn");
$search_btn.click(
    function() {
        $case_info_table.bootstrapTable("selectPage", 1);
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
        $case_info_table.bootstrapTable('selectPage', 1);
    }
);

/**
 * @description 项目所属(新建/编辑接口和新建/编辑用例共用)
 * @type {*|jQuery|HTMLElement}
 */
var $member_of_project = $("#member_of_project");
$.ajax({
    type: "post",
    url: "/getAllProjectList",
    dataType: "json",
    contentType: "application/json; charset=utf-8",
    success: function (result) {
        for (var i = 0; i < result.length; i++) {
            $member_of_project.append("<option value=" + result[i].id + ">" + result[i].projectName + "</option>")
        }
        $member_of_project.selectpicker("refresh");
        $member_of_project.selectpicker("render");
    }
});

/**
 * @description 创建用例
 * @type {*|jQuery|HTMLElement}
 */
var $create_case_submit = $("#create_case_submit");
$create_case_submit.click(
    function() {
        var caseName = $("#create_case_name").val();
        var projectId = $("#member_of_project").val();
        // 通过Ajax提交实体
        $.ajax({
            type: "post",
            url: "/createCase",
            data: JSON.stringify({"caseName": caseName, "projectId": projectId}),
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            success: function(result) {                                         // 提交成功后关闭模态框
                var $create_case_modal = $("#create_case_modal");
                $create_case_modal.modal("hide");                               // 先将模态框隐藏
                $create_case_modal.on('hidden.bs.modal', function(){            // 在清空模态框中的表单数据
                    $("#create_case_form").resetForm();
                });
                $case_info_table.bootstrapTable('selectPage', 1);               // 关闭后刷新项目列表
                if (result['flag'] === 1) {                                     // 如果向库中添加用例成功
                    bootbox.alert({
                        title: '提示',
                        message: "创建用例成功"
                    });
                } else {
                    bootbox.alert({
                        title: '警告',
                        message: "创建用例失败"
                    });
                }
            }
        });
    }
);

/**
 * @description 关闭模态框时需要做值的清空
 */
$('#create_case_modal').on('hide.bs.modal', function () {
    $("#create_case_name").attr("value", "");
    $("#member_of_project").selectpicker('refresh');
});

/**
 * @description 用例的接口选择(左右Select菜单)
 */
jQuery(document).ready(function($) {
    $('#multiselect').multiselect({
        sort: false,                                                        // 按用户的选择顺序进行排序
        startUp: false
    });
});

/**
 * @description 新建完成后编辑测试用例(罗列出当前项目下的所有接口,以供用例选择使用).
 * @param caseId
 */
function modCaseInfo(caseId) {
    $.ajax({
        type: "post",
        url: "/getCaseInfo",
        data: "caseId=" + caseId,
        dataType: "json",
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        success: function(result) {
            $("#modify_case_id").attr("value", result["id"]);
            $("#modify_case_name").attr("value", result["caseName"]);
            var $project_select = $("#project_select_mod_case");
            $.ajax({
                type: "post",
                url: "/getAllProjectList",
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                success: function (projectList) {
                    for (var i = 0; i < projectList.length; i++) {
                        $project_select.append("<option value=" + projectList[i].id + ">" + projectList[i].projectName + "</option>")
                    }
                    $project_select.selectpicker("val", result["projectId"]);
                    $project_select.selectpicker("refresh");
                    $project_select.selectpicker("render");
                }
            });

            $('#modify_case_modal').modal('show');
            // 通过Ajax获取当前项目下所有的接口和用例两种已经存在接口
            $.ajax({
                type: "post",
                url: "/getCurrentCaseApiWithProject",
                data: "caseId=" + caseId,
                dataType: "json",
                contentType: "application/x-www-form-urlencoded; charset=utf-8",
                success: function(result) {
                    var projectApiJson = result['projectApiJson'];
                    var caseApiJson = result['caseApiJson'];
                    for (var p_api_idx = 0; p_api_idx < projectApiJson.length; ++p_api_idx) {
                        $("#multiselect").append("<option value='" + result.projectApiJson[p_api_idx].id + "'>" + result.projectApiJson[p_api_idx].apiName + "</option>");
                    }
                    for (var c_api_idx = 0; c_api_idx < caseApiJson.length; ++c_api_idx) {
                        $("#multiselect_to").append("<option value='" + result.caseApiJson[c_api_idx].id + "'>" + result.caseApiJson[c_api_idx].apiName + "</option>");
                    }
                }
            });
        }
    });
}

/**
 * @description 关闭模态框时需要做值的清空
 */
$('#modify_case_modal').on('hide.bs.modal', function () {
    $("#modify_case_name").attr("value", "");
    $("#service_select_mod_case").selectpicker('refresh');
    $("#multiselect").empty();
    $("#multiselect_to").empty();
});

/**
 * @description 提交修改后的用例信息
 * @type {*|jQuery|HTMLElement}
 */
var $modify_case_submit = $('#modify_case_submit');
$modify_case_submit.click(
    function() {
        var caseId = $("#modify_case_id").val();
        var caseName = $("#modify_case_name").val();
        var serviceId = $("#service_select_mod_case").val();
        var apiSequence = [];
        $("#multiselect_to option").each(function () {
            var opt_val = $(this).val();
            if (opt_val !== '')
                apiSequence.push(parseInt(opt_val));
        });

        // 通过Ajax提交实体
        $.ajax({
            type: "post",
            url: "/modifyCase",
            data: JSON.stringify({"id": caseId, "caseName": caseName, "serviceId": serviceId, "apiSequence": apiSequence}),
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            success: function(result) {                                     // 提交成功后关闭模态框
                var $modify_case_modal = $("#modify_case_modal");
                $modify_case_modal.modal("hide");                           // 先将模态框隐藏
                $modify_case_modal.on('hidden.bs.modal', function(){        // 在清空模态框中的表单数据
                    $("#modify_case_form").resetForm();
                });
                $case_info_table.bootstrapTable('selectPage', 1);           // 关闭后刷新项目列表
                if (result['flag'] === 1) {                                 // 如果向库中添加项目成功
                    bootbox.alert({
                        title: '提示',
                        message: "编辑用例成功"
                    });
                } else {
                    bootbox.alert({
                        title: '警告',
                        message: "编辑用例失败"
                    });
                }
                getApiInCase(caseId);                                       // 刷新用例接口列表
            }
        });
    }
);


function delCaseConfirm(caseId) {
    bootbox.confirm({
        message: "是否要删除当前用例?",
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
                delCaseInfo(caseId);
        }
    })
}


function delCaseInfo(caseId) {
    $.ajax({
        type: "post",
        url: "/deleteCase",
        data: "caseId=" + caseId,
        dataType: "json",
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        success: function(result) {
            if (result['flag'] === 1) {                                     // 如果从库中删除接口成功
                bootbox.alert({
                    title: '提示',
                    message: "删除用例成功"
                });
            } else {
                bootbox.alert({
                    title: '警告',
                    message: "删除用例失败"
                });
            }
            $case_info_table.bootstrapTable('selectPage', 1);               // 删除后刷新项目列表
        }
    });
}

/**
 * @description 根据CaseId获取当前Case的所有接口,以表格形式显示
 * @type {*|jQuery|HTMLElement}
 */
function getApiInCase(caseId) {
    var $api_in_case_table = $('#api_in_case_table');
    $api_in_case_table.empty();                                             // 要保证表格初始化需要使用empty()和bootstrapTable("destroy")
    $api_in_case_table.bootstrapTable("destroy");

    $('#current_case_id').val(caseId);                                      // 当点选[接口]时,需要给隐藏域传递当前的caseId

    $api_in_case_table.bootstrapTable({
        url: '/getCaseApiInfoByCaseId',
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
                caseId: caseId
            }
        },
        columns: [
            {
                field: 'id',
                title: '编号',
                align: 'center',
                formatter: function(value, row, index) {                                        // 以列表的序列来标号
                    var pageSize = $case_info_table.bootstrapTable('getOptions').pageSize;      // 通过表的#id 可以得到每页多少条
                    var pageNumber = $case_info_table.bootstrapTable('getOptions').pageNumber;  // 通过表的#id 可以得到当前第几页
                    return pageSize * (pageNumber - 1) + index + 1;                             // 返回每条的序号： 每页条数 * （当前页 - 1 ）+ 序号
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
                width: 350
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
                field: 'id',
                title: '操作',
                align: 'center',
                formatter: function (value, row, index) {
                    return [
                        '<a href="javascript:apiInfoView(' + value + ')">' +
                        '<i class="fa fa-puzzle-piece"></i>关联参数' +
                        '</a>'
                    ].join('');
                },
                width: 100
            }
        ]
    });
}

/**
 * @description 给模态框提供接口信息
 * @param caseApiId
 */
function apiInfoView(caseApiId) {
    $.ajax({
        type: "post",
        url: "/caseApiModifyInModal",
        data: "caseApiId=" + caseApiId,
        dataType: "json",
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        success: function(result) {
            $('#case_api_id').val(result['id']);
            $('#api_id').val(result['apiId']);
            $('#case_id').val(result['caseId']);
            $('#api_name').val(result['apiName']);

            // 获取请求协议
            var $protocol_in_case = $("#protocol");
            $protocol_in_case.selectpicker('val', result['protocol']);
            // 获取请求方式
            var $method = $("#method");
            $method.selectpicker('val', result['method']);

            $('#url').val(result['url']);
            $('#wait_millis').val(result['waitMillis']);

            // 获取接口所属服务
            var $service_mod_api = $("#service_mod_api");
            $.ajax({
                type: "post",
                url: "/getServiceInfo",
                data: "serviceId=" + result['serviceId'],
                dataType: "json",
                contentType: "application/x-www-form-urlencoded; charset=utf-8",
                success: function (serviceInfo) {
                    $service_mod_api.val(serviceInfo['serviceName']);
                }
            });
            $('#service_id').val(result['serviceId']);

            // 动态获取Header表单
            var $header_list = $("#header_list");                                       // 拿到要动态生成的tbody
            var headerItems = JSON.parse(result['header']);                             // 拿到存储Header的JSON
            var header_item_index = 0;
            for (var headerItem in headerItems) {
                header_item_index++;                                                    // ID下标累加
                var header_item_row = $("<tr></tr>");                                   // 添加一行

                var header_type_col = $("<td></td>");                                   // 添加类型列
                var $header_type_select = $("<select class='selectpicker header-type-select' id='header_type_select_" + header_item_index + "'></select>");
                $header_type_select.append("<option value='Accept'>Accept</option>");
                $header_type_select.append("<option value='Cache-Control'>Cache-Control</option>");
                $header_type_select.append("<option value='Content-Type'>Content-Type</option>");
                $header_type_select.append("<option value='Cookie'>Cookie</option>");
                $header_type_select.append("<option value='Host'>Host</option>");
                $header_type_select.append("<option value='Referer'>Referer</option>");
                $header_type_select.append("<option value='User-Agent'>User-Agent</option>");
                $header_type_select.append("<option value='X-Requested-With'>X-Requested-With</option>");
                $header_type_select.val(headerItem);                                    // 下拉框设置为从DB中读取的值(注意:直接用val)
                $header_type_select.appendTo(header_type_col);

                $header_type_select.selectpicker('refresh');                            // 一定要加上'refresh'和'render'.否则下拉选择将不显示
                $header_type_select.selectpicker('render');

                var header_content_col = $("<td></td>");                                // 添加内容列
                var $header_content_input = $('<input class="form-control header-value-input" style="width: 100%;" id="header_content_input_' + header_item_index + '" placeholder="header内容"/>');
                $header_content_input.val(headerItems[headerItem]);                     // 填入从DB获取的值
                $header_content_input.appendTo(header_content_col);

                var header_operator_col = $("<td></td>");                               // 添加操作列
                var $header_del_btn = $('<button/>', {
                    "class": "btn btn-default del_header_btn",
                    "id": "header_item_del_button_" + header_item_index,
                    "title": "删除"
                });
                var $iconHeader = $('<i/>', {"class": "glyphicon glyphicon-minus"});
                var $spanHeader = $("<span>&nbsp;删除</span>");
                $iconHeader.appendTo($header_del_btn);
                $spanHeader.appendTo($header_del_btn);
                $header_del_btn.appendTo(header_operator_col);

                header_item_row.append(header_type_col);
                header_item_row.append(header_content_col);
                header_item_row.append(header_operator_col);

                $header_list.append(header_item_row);
            }


            // 动态获取Body表单
            var $param_list = $("#param_list");                                         // 拿到存放参数的tbody
            var bodyJson = JSON.parse(result['body']);                                  // 拿到存储Body的json
            if (headerItems.hasOwnProperty("Content-Type")) {                           // 如果Header中存在Content-Type字段
                if (headerItems['Content-Type'] === 'application/json') {
                    $("#json_body").val(JSON.stringify(bodyJson, null, "\t"));          // JSON格式化显示

                    $("#param_li").removeAttr("class");
                    $("#json_li").attr("class", "active");

                    $("#json").attr("class", "tab-pane fade in active");
                    $("#parameters").attr("class", "tab-pane fade");
                } else if (headerItems['Content-Type'] === 'application/x-www-form-urlencoded') {
                    paramAppend("post_param_del_btn");                                  // 传值时删除按钮的ID前缀

                    $("#json_li").removeAttr("class");
                    $("#param_li").attr("class", "active");

                    $("#json").attr("class", "tab-pane fade");
                    $("#parameters").attr("class", "tab-pane fade in active");
                }
            } else {                                                                    // 如果是其他Content-Type的请求
                paramAppend("get_param_del_btn");                                       // 传值是删除按钮的ID前缀

                $("#json_li").removeAttr("class");
                $("#param_li").attr("class", "active");

                $("#json").attr("class", "tab-pane fade");
                $("#parameters").attr("class", "tab-pane fade in active");
            }

            // 获取Get/Post表单的通用方法
            function paramAppend(param_del_btn) {
                var param_item_index = 0;
                for (var paramItem in bodyJson) {
                    param_item_index++;
                    var param_item_row = $("<tr></tr>");

                    var param_name_col = $("<td></td>");
                    var $param_name_input = $('<input class="form-control param-name-input" style="width: 100%;" id="param_name_input_' + param_item_index + '" placeholder="参数名"/>');
                    $param_name_input.val(paramItem);                                   // 从DB获取参数名
                    $param_name_input.appendTo(param_name_col);

                    var param_value_col = $("<td></td>");
                    var $param_value_input = $('<input class="form-control param-value-input" style="width: 100%;" id="param_value_input_' + param_item_index + '" placeholder="参数值"/>');
                    $param_value_input.val(bodyJson[paramItem]);
                    $param_value_input.appendTo(param_value_col);

                    var param_operator_col = $("<td></td>");                            // 添加操作列
                    var $param_del_btn = $('<button/>', {
                        "class": "btn btn-default del_param_btn",
                        "id": param_del_btn + '_' + param_item_index,
                        "title": "删除"
                    });
                    var $iconParam = $('<i/>', {"class": "glyphicon glyphicon-minus"});
                    var $spanParam = $("<span>&nbsp;删除</span>");
                    $iconParam.appendTo($param_del_btn);
                    $spanParam.appendTo($param_del_btn);
                    $param_del_btn.appendTo(param_operator_col);

                    param_item_row.append(param_name_col);
                    param_item_row.append(param_value_col);
                    param_item_row.append(param_operator_col);

                    $param_list.append(param_item_row);
                }
            }

            // 动态获取验证表单
            var $verify_list = $("#verify_list");
            var verifyList = eval(result['caseVerifyList']);
            for (var v_Index in verifyList) {
                var verify_item_row = $("<tr></tr>");
                var verify_item_id = $('<input type="hidden" class="verify-id" id="verify_id_' + v_Index + '" name="verifyId" value="' + verifyList[v_Index].id + '"/>');

                var verify_name_col = $("<td></td>");
                var $verify_name_input = $('<input class="form-control verify-name-input" style="width: 100%;" id="verify_name_input_' + v_Index + '" placeholder="验证名"/>');
                $verify_name_input.val(verifyList[v_Index].verifyName);
                $verify_name_input.appendTo(verify_name_col);

                var verify_type_col = $("<td></td>");
                var $verify_type_select = $("<select class='selectpicker verify-type-select' id='verify_type_select_" + v_Index + "'></select>");
                $verify_type_select.append("<option value=" + 1 + ">Regex</option>");
                $verify_type_select.append("<option value=" + 2 + ">JSONPath</option>");
                $verify_type_select.append("<option value=" + 3 + ">CSS Selector</option>");
                $verify_type_select.val(verifyList[v_Index].verifyType);
                $verify_type_select.appendTo(verify_type_col);

                $verify_type_select.selectpicker('refresh');
                $verify_type_select.selectpicker('render');

                var verify_expression_col = $("<td></td>");
                var $verify_expression_input = $('<input class="form-control verify-expression-input" style="width: 100%;" id="verify_expression_input_' + v_Index + '" placeholder="表达式"/>');
                $verify_expression_input.val(verifyList[v_Index].expression);
                $verify_expression_input.appendTo(verify_expression_col);

                var verify_expect_col = $("<td></td>");
                var $verify_expect_input = $('<input class="form-control verify-expect-input" style="width: 100%;" id="verify_expect_input_' + v_Index + '" placeholder="期望值"/>');
                $verify_expect_input.val(verifyList[v_Index].expectValue);
                $verify_expect_input.appendTo(verify_expect_col);

                var verify_operator_col = $("<td></td>");
                var $verify_del_btn = $('<button/>', {
                    "class": "btn btn-default del_verify_btn",
                    "id": "verify_item_del_button_" + v_Index,
                    "title": "删除"
                });
                var $vIcon = $('<i/>', {"class": "glyphicon glyphicon-minus"});
                var $vSpanDel = $("<span>&nbsp;删除</span>");
                $vIcon.appendTo($verify_del_btn);
                $verify_del_btn.append($vSpanDel);
                $verify_del_btn.appendTo(verify_operator_col);

                verify_item_row.append(verify_item_id);
                verify_item_row.append(verify_name_col);
                verify_item_row.append(verify_type_col);
                verify_item_row.append(verify_expression_col);
                verify_item_row.append(verify_expect_col);
                verify_item_row.append(verify_operator_col);

                $verify_list.append(verify_item_row);
            }

            // 动态获取关联表单
            var $correlate_list = $("#correlate_list");
            var correlateList = eval(result['correlateList']);
            for (var c_index in correlateList) {
                var corr_item_row = $("<tr></tr>");
                var corr_item_id = $('<input type="hidden" class="correlate-id" id="correlate_id_' + c_index + '" name="correlateId" value="' + correlateList[c_index].id + '"/>');

                var corr_name_col = $("<td></td>");
                var $corr_name_input = $('<input class="form-control corr-name-input" style="width: 100%;" id="corr_name_input_' + c_index + '" placeholder="关联参数名"/>');
                $corr_name_input.val(correlateList[c_index].corrField);
                $corr_name_input.appendTo(corr_name_col);

                var corr_type_col = $("<td></td>");
                var $corr_type_select = $("<select class='selectpicker corr-type-select' id='corr_type_select_" + c_index + "'></select>");
                $corr_type_select.append("<option value=" + 1 + ">Regex</option>");
                $corr_type_select.append("<option value=" + 2 + ">JSONPath</option>");
                $corr_type_select.append("<option value=" + 3 + ">CSS Selector</option>");
                $corr_type_select.val(correlateList[c_index].corrPattern);
                $corr_type_select.appendTo(corr_type_col);

                $corr_type_select.selectpicker('refresh');
                $corr_type_select.selectpicker('render');

                var corr_expression_col = $("<td></td>");
                var $corr_expression_input = $('<input class="form-control corr-expression-input" style="width: 100%;" id="corr_expression_input_' + c_index + '" placeholder="表达式"/>');
                $corr_expression_input.val(correlateList[c_index].corrExpression);
                $corr_expression_input.appendTo(corr_expression_col);

                var corr_value_col = $("<td></td>");
                var $corr_value_input = $('<input class="form-control corr-value-input" style="width: 100%;" id="corr_value_input_' + c_index + '" placeholder="关联值"/>');
                $corr_value_input.val(correlateList[c_index].corrValue);
                $corr_value_input.appendTo(corr_value_col);

                var corr_operator_col = $("<td></td>");
                var $corr_del_btn = $('<button/>', {
                    "class": "btn btn-default del_corr_btn",
                    "id": "corr_item_del_button_" + c_index,
                    "title": "删除"
                });
                var $cIcon = $('<i/>', {"class": "glyphicon glyphicon-minus"});
                var $cSpanDel = $("<span>&nbsp;删除</span>");
                $cIcon.appendTo($corr_del_btn);
                $corr_del_btn.append($cSpanDel);
                $corr_del_btn.appendTo(corr_operator_col);

                corr_item_row.append(corr_item_id);
                corr_item_row.append(corr_name_col);
                corr_item_row.append(corr_type_col);
                corr_item_row.append(corr_expression_col);
                corr_item_row.append(corr_value_col);
                corr_item_row.append(corr_operator_col);

                $correlate_list.append(corr_item_row);
            }

            $('#modify_api_modal').modal('show');               // 显示接口修改模态框
        }
    });
}

/**
 * @description 模态框关闭后需要清空模态框中的表单
 */
$('#modify_api_modal').on('hide.bs.modal', function() {
    $('#api_id').attr("value", "");
    $('#api_name_in_case').attr("value", "");
    $('#url_in_case').attr("value", "");
    $('#wait_millis').val("");

    $('#header_list').empty();
    $('#param_list').empty();
    $('#json_body').val("");
    $('#verify_list').empty();
    $('#correlate_list').empty();
});

/**
 * @description 动态添加关联参数
 * @type {*|jQuery|HTMLElement}
 */
var corr_item_index = 0;
var $add_correlate_item = $("#add_correlate_item");
$add_correlate_item.click(
    function () {
        corr_item_index++;
        var $correlate_list = $("#correlate_list");
        // 添加一行 - 关联点
        var corr_item_row = $("<tr></tr>");

        // 添加一列 - 关联参数名
        var corr_item_name_col = $("<td></td>");
        // 添加输入框
        var $corr_item_name = $('<input class="form-control corr-name-input" style="width: 100%;" id="corr_item_name_' + corr_item_index + '" placeholder="关联参数名"/>');
        $corr_item_name.appendTo(corr_item_name_col);


        // 添加一列 - 类型
        var corr_item_type_col = $("<td></td>");
        // 添加bootstrap-select
        var $corr_item_select = $("<select class='selectpicker corr-type-select' id='corr_item_select_" + corr_item_index + "'></select>");
        $corr_item_select.append("<option value=" + 1 + ">Regex</option>");
        $corr_item_select.append("<option value=" + 2 + ">JSONPath</option>");
        $corr_item_select.append("<option value=" + 3 + ">CSS Selector</option>");
        $corr_item_select.appendTo(corr_item_type_col).selectpicker('refresh');

        // 添加一列 - 表达式
        var corr_item_expression_col = $("<td></td>");
        // 添加输入框
        var $corr_item_expression = $('<input class="form-control corr-expression-input" style="width: 100%;" id="corr_item_expression_' + corr_item_index + '" placeholder="表达式"/>');
        $corr_item_expression.appendTo(corr_item_expression_col);

        // 添加一列 - 关联值
        var corr_item_value_col = $("<td></td>");
        // 添加输入框
        var $corr_item_value = $('<input class="form-control corr-value-input" style="width: 100%;" id="corr_item_value_' + corr_item_index + '" placeholder="关联值"/>');
        $corr_item_value.appendTo(corr_item_value_col);

        // 添加一列 - 操作
        var corr_item_operator_col = $("<td></td>");
        // 删除Button
        var $corr_del_item_btn = $('<button/>', {
            "class": "btn btn-default del_corr_btn",
            "id": "corr_item_del_button_" + corr_item_index,
            "title": "删除"
        });
        var $icon = $('<i/>', {"class": "glyphicon glyphicon-minus"});
        var span_del = $("<span>&nbsp;删除</span>");
        $icon.appendTo($corr_del_item_btn);
        $corr_del_item_btn.append(span_del);
        $corr_del_item_btn.appendTo(corr_item_operator_col);

        corr_item_row.append(corr_item_name_col);
        corr_item_row.append(corr_item_type_col);
        corr_item_row.append(corr_item_expression_col);
        corr_item_row.append(corr_item_value_col);
        corr_item_row.append(corr_item_operator_col);

        $correlate_list.append(corr_item_row);
    }
);

/**
 * @description 动态删除关联参数
 * @type {*|jQuery|HTMLElement}
 */
$(document).on('click', '.del_corr_btn', function () {
    $(this).parent().parent().remove();
});

function getCaseApiInfoJsonModifyData() {
    var headerItemJson = {};
    var contentType = null;
    var $header_list = $("#header_list");
    var headerItemLength = $header_list.find("tr").length;
    var headerNameList = $header_list.find(".selectpicker");
    var headerValueList = $header_list.find(".form-control");
    for (var headerIndex = 0; headerIndex < headerItemLength; ++headerIndex) {
        var headerName = headerNameList[headerIndex].value;                                 // 如果是动态添加SelectPicker则直接用value取值
        var headerValue = headerValueList[headerIndex].value;

        if (headerName === 'Content-Type')
            contentType = headerValue;
        headerItemJson[headerName] = headerValue;
    }

    var bodyJson = {};
    var $param_list;
    var bodyItemLength;
    var bodyNameList;
    var bodyValueList;
    var methodType = $("#method").selectpicker('val');
    if (methodType === "2" || methodType === "3") {                                         // 如果是POST或者PUT请求,参数从Parameter和JSON中获取
        if (contentType === "application/x-www-form-urlencoded" || contentType === null) {  // 根据ContentType来判断提交的Body类型
            // 将页面中的HTTP请求参数属性整合为JSON
            $param_list = $("#param_list");
            bodyItemLength = $param_list.find("tr").length;
            bodyNameList = $param_list.find(".param-name-input");
            bodyValueList = $param_list.find(".param-value-input");
            for (var paramIndex = 0; paramIndex < bodyItemLength; ++paramIndex) {
                var paramName = bodyNameList[paramIndex].value;
                var paramValue = bodyValueList[paramIndex].value;
                bodyJson[paramName] = paramValue
            }
        } else if (contentType === "application/json") {
            var json_body_content = $("#json_body").val();
            // 判断请求体是否正确的JSON格式
            try {
                bodyJson = JSON.parse(json_body_content);
            } catch (e) {
                bootbox.alert({
                    title: '警告',
                    message: "请求体不是正确的JSON格式"
                });
            }
        }
    } else if (methodType === "1" || methodType === "4") {                                  // 如果是GET或者DELETE请求,参数从Parameter获取
        // 将页面中的HTTP请求参数属性整合为JSON
        $param_list = $("#param_list");
        bodyItemLength = $param_list.find("tr").length;
        bodyNameList = $param_list.find(".param-name-input");
        bodyValueList = $param_list.find(".param-value-input");
        for (var paramIndex = 0; paramIndex < bodyItemLength; ++paramIndex) {
            var paramName = bodyNameList[paramIndex].value;
            var paramValue = bodyValueList[paramIndex].value;
            bodyJson[paramName] = paramValue
        }
    }

    // 将页面中的请求验证点整合为JSON
    var verifyInfoList = [];
    var $verify_list = $("#verify_list");
    if ($verify_list.children().length > 0) {
        var verifyItemLength = $verify_list.find("tr").length;
        var verifyNameList = $verify_list.find(".verify-name-input");
        var verifyTypeList = $verify_list.find(".selectpicker");
        var verifyExpressionList = $verify_list.find(".verify-expression-input");
        var verifyExpectList = $verify_list.find(".verify-expect-input");
        for (var verifyIndex = 0; verifyIndex < verifyItemLength; ++verifyIndex) {
            var verifyItem = {
                'id': $verify_list.find("#verify_id_" + verifyIndex).val(),
                'verifyName': verifyNameList[verifyIndex].value,
                'verifyType': verifyTypeList[verifyIndex].value,
                'expression': verifyExpressionList[verifyIndex].value,
                'expectValue': verifyExpectList[verifyIndex].value
            };
            verifyInfoList.push(verifyItem);
        }
    }

    // 将页面中的请求关联参数整合为JSON
    var correlateInfoList = [];
    var $correlate_list = $("#correlate_list");
    if ($correlate_list.children().length > 0) {
        var correlateItemLength = $correlate_list.find("tr").length;
        var correlateNameList = $correlate_list.find('.corr-name-input');
        var correlateTypeList = $correlate_list.find('.selectpicker');
        var correlateExpressionList = $correlate_list.find('.corr-expression-input');
        var correlateValueList = $correlate_list.find('.corr-value-input');
        for (var corrIndex = 0; corrIndex < correlateItemLength; ++corrIndex) {
            var corrItem = {
                'id': $correlate_list.find("#correlate_id_" + corrIndex).val(),
                'corrField': correlateNameList[corrIndex].value,
                'corrPattern': correlateTypeList[corrIndex].value,
                'corrExpression': correlateExpressionList[corrIndex].value,
                'corrValue': correlateValueList[corrIndex].value
            };
            correlateInfoList.push(corrItem);
        }
    }

    // 拼装接口的JSON
    var apiJson = {
        "caseApiId": $("#case_api_id").val(),
        "apiId": $("#api_id").val(),
        "apiName": $("#api_name").val(),
        "protocol": $("#protocol").selectpicker('val'),
        "method": $("#method").selectpicker('val'),
        "url": $("#url").val(),
        "waitMillis": $("#wait_millis").val(),
        "header": headerItemJson,
        "body": bodyJson,
        "verifyInfoList": verifyInfoList,
        "correlateInfoList": correlateInfoList,
        "isMock": 0,
        "serviceId": $("#service_id").val(),
        "caseId": $("#case_id").val()
    };

    return apiJson;
}

/**
 * @description 提交修改后的接口,带参数关联
 * @type {*|jQuery|HTMLElement}
 */
var $submit_modify_with_correlate_btn = $("#submit_modify_with_correlate_btn");
$submit_modify_with_correlate_btn.click(
    function () {
        var apiJson = getCaseApiInfoJsonModifyData();
        $.ajax({
            type: "post",
            url: "/modifyCaseApiSubmit",
            data: JSON.stringify(apiJson),
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            success: function(result) {
                if (result['updateApiFlag'] === 1) {
                    bootbox.alert({
                        title: '提示',
                        message: "更新接口成功",
                        callback: function () {
                            $('#modify_api_modal').modal('hide');                       // 更新完毕后隐藏模态框
                        }
                    });
                } else {
                    bootbox.alert({
                        title: '警告',
                        message: "更新接口失败",
                        callback: function () {
                            $('#modify_api_modal').modal('hide');                       // 更新完毕后隐藏模态框
                        }
                    });
                }
                $('#api_in_case_table').bootstrapTable('selectPage', 1)                 // 更新完毕后刷新接口列表
            }
        });
    }
);

/**
 * @description 显示选择Hosts的模态框,并建立Websocket连接
 * @type {*|jQuery|HTMLElement}
 */
var socket;
var $run_case_btn = $("#run_case_btn");
$run_case_btn.click(
    function() {
        var isRecord = $('#api_in_case_table tbody tr').attr('class');
        if (isRecord === 'no-records-found') {
            bootbox.alert({
                title: '提示',
                message: "未选择要执行的用例或当前用例中没有接口"
            });
        } else {
            var $hosts_list = $('#hosts_list');
            $hosts_list.bootstrapTable({
                url: '/getHostsList',
                method: 'post',
                dataType: 'json',
                striped: true,
                pagination: true,
                sidePagination: 'server',
                pageNumber: 1,
                pageSize: 20,
                pageList: [20, 40, 60],
                paginationPreText: '‹',
                paginationNextText: '›',
                locale: 'zh-CN',
                singleSelect: true,                         // 启用CheckBox
                clickToSelect: true,                        // 单击某行即选中
                onCheck: function (row, $element) {         // 当CheckBox被选中后将HostID传给隐藏域,方便与后面的接口运行
                    $("#hidden_host_id").val(row["id"]);
                },
                queryParams: function (params) {
                    return {
                        offset: params.offset,
                        limit: params.limit,
                        domain: $('#search_name').val()
                    }
                },
                columns: [
                    {
                        // field: 'id',             // 不设置field,则checkbox不会全部选中
                        title: '选择',
                        align: 'center',
                        checkbox: true,
                        width: 75
                    }, {
                        field: 'description',
                        title: '描述',
                        align: 'center'
                    }, {
                        field: 'id',
                        title: '查看',
                        align: 'center',
                        formatter: function (value, row, index) {
                            return [
                                '<a href="javascript:showHostsItem(' + value + ')">' +
                                '<i class="fa fa-book"></i>查看' +
                                '</a>'
                            ].join('');
                        },
                        width: 100
                    }
                ]
            });

            $("#select_hosts_modal").modal('show');                                     // 显示选择Hosts模态框

            if (typeof (WebSocket) === "undefined") {                                   // 以下是WebSocket与后端建立连接
                console.log("当前浏览器不支持WebSocket");
                bootbox.alert({
                    title: '警告',
                    message: "当前浏览器不支持WebSocket"
                });
            } else {
                console.log("当前浏览器支持WebSocket");

                // socket = new WebSocket("ws://192.168.201.235:8080/webSocket");
                socket = new WebSocket("ws://192.168.66.143:8080/webSocket");           // 如果在部署的时候这里需要配置

                socket.onopen = function() {                                            // 连接打开事件
                    console.log("Socket已打开");
                    socket.send("准备接收接口验证结果");
                };

                socket.onmessage = function(msg) {                                      // 收到消息事件
                    console.log(msg.data);
                    var serverMessage = JSON.parse(msg.data);                           // 字符串转JSON
                    $("#session_id").attr("value", serverMessage["sessionId"]);         // 将sessionID传递给页面
                    var resultJson = null;
                    if (isJSON(serverMessage['info'])) {                                // 判断回传的结果是不是JSON
                        resultJson = JSON.parse(serverMessage['info']);
                        var $result = $("#verify_result_modal").find("tbody");
                        var $resultRow = null;
                        if (resultJson['isSuccess'] === 1) {
                            $resultRow = $("<tr>" +
                                                "<td style='width: 80px'>" + resultJson['apiName'] + "</td>" +
                                                "<td style='width: 640px;word-wrap: break-word;white-space:pre-wrap'>" + resultJson['url'] + "</td>" +
                                                "<td style='width: 80px'>" + resultJson['statusCode'] + "</td>" +
                                                "<td style='width: 120px'>" + resultJson['respTime'] + "</td>" +
                                                "<td style='width: 100px'>" +
                                                    "<a href='#' onclick='getRespVerify(" + resultJson['id'] + "," + resultJson['caseApiId'] + ")' class='btn btn-success btn-sm resp-check' data-toggle='modal' data-target='#response_verify_modal'>验证通过</a>" +
                                                "</td>" +
                                           "</tr>");
                        } else {
                            $resultRow = $("<tr>" +
                                                "<td style='width: 80px'>" + resultJson['apiName'] + "</td>" +
                                                "<td style='width: 640px;word-wrap: break-word;white-space:pre-wrap'>" + resultJson['url'] + "</td>" +
                                                "<td style='width: 80px'>" + resultJson['statusCode'] + "</td>" +
                                                "<td style='width: 120px'>" + resultJson['respTime'] + "</td>" +
                                                "<td style='width: 100px'>" +
                                                    "<a href='#' onclick='getRespVerify(" + resultJson['id'] + "," + resultJson['caseApiId'] + ")' class='btn btn-danger btn-sm resp-check' data-toggle='modal' data-target='#response_verify_modal'>验证失败</a>" +
                                                "</td>" +
                                           "</tr>");
                        }
                        $result.append($resultRow);
                    }
                };

                socket.onclose = function() {                                           //连接关闭事件
                    console.log("Socket已关闭");
                };

                socket.onerror = function() {                                           //发生了错误事件
                    alert("Socket发生错误");
                };

                window.unload = function() {                                            //窗口关闭时，关闭连接
                    socket.close();
                };
            }
        }
    }
);

/**
 * @description     根据hostsId显示当前HostItem
 * @param hostsId
 */
function showHostsItem(hostsId) {
    $.ajax({
        type: "post",
        url: "/getHostsInfo",
        data: "hostsId=" + hostsId,
        dataType: "json",
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        success: function(result) {
            var hostsItem = eval(result['hostsItem']);
            hostsItem.forEach(function(value){
                var hostsItemRow = $("<tr></tr>");
                var hostsIp = $("<td>" + value["ip"] + "</td>");
                var hostsDomain = $("<td>" + value["domain"] + "</td>");
                hostsItemRow.append(hostsIp);
                hostsItemRow.append(hostsDomain);
                $("#host_item_list").append(hostsItemRow);
            });
            $("#show_host_item_modal").modal('show');
        }
    });
}

$("#show_host_item_modal").on('hidden.bs.modal', function (){
    $("#host_item_list").empty();                                               // 当关闭HostsItem列表时需要清空列表(以便下一次不会出现重复记录)
});

/**
 * @description 执行用例
 * @type {*|jQuery|HTMLElement}
 */
var $run_case = $('#run_case');
$run_case.click(
    function() {
        var currentCaseId = $("#current_case_id").val();
        var sessionId = $("#session_id").val();
        var hostsId = $("#hidden_host_id").val();

        $("#select_hosts_modal").modal('hide');                     // 隐藏Hosts选择的模态

        $('.modal').on('hidden.bs.modal', function (){              // 解决多层模态框显示导致当前模态框无法滚动的问题
            $("body").addClass("modal-open");                       // 当上一个模态消失的时候重置Body的样式
        });

        $("#verify_result_modal").modal('show');                    // 显示用例执行的模态

        $.ajax({
            type: "post",
            url: "/runCase",
            data: "caseId=" + currentCaseId + "&sessionId=" + sessionId + "&hostsId=" + hostsId,
            dataType: "json",
            contentType: "application/x-www-form-urlencoded; charset=utf-8",

            success: function(result) {
                if (result.hasOwnProperty('errorMsg')) {            // 如果选择Hosts不正确则给出Hosts选择不正确的弹出框
                    var msgArray = result['errorMsg'].split("|");   // 对后端返回的异常进行分割
                    $('#except_textarea').val(msgArray[0]);
                    $('#except_message').val(msgArray[1]);
                    $('#run_case_except_modal').modal('show');

                    socket.onclose();                               // 关闭Socket连接
                }
            }
        });
    }
);

/**
 * @description 获取验证结果
 * @param respId
 * @param caseApiId
 */
function getRespVerify(respId, caseApiId) {
    $.ajax({
        type: "post",
        url: "/getRespVerify",
        data: "respId=" + respId + "&caseApiId=" + caseApiId,
        dataType: "json",
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        success: function (result) {
            // 给模态框中的元素赋值(响应结果)
            $("#result_api_name").val(result['respInfo'].apiName);
            $("#result_url").val(result['respInfo'].url);
            $("#result_status_code").val(result['respInfo'].statusCode);
            $("#result_resp_time").val(result['respInfo'].respTime);
            $("#resp_header").val(JSON.stringify(JSON.parse(result['respInfo'].respHeader), null, 4));           // JSON格式化输出到textarea

            if (isJSON(result['respInfo'].respContent)) {
                $("#resp_body").val(JSON.stringify(JSON.parse(result['respInfo'].respContent), null, 4));
            } else {
                $("#resp_body").val(result['respInfo'].respContent);
            }
            // 验证点
            var $result_verify_list = $("#result_verify_list");
            $result_verify_list.empty();                                    // 删除子元素
            var verifyInfoList = eval(result['verifyInfoList']);
            for (var verify_index = 0; verify_index < verifyInfoList.length; ++verify_index) {
                // 添加一行 - 验证点
                var verify_item_row = $("<tr></tr>");

                var verify_name_col = $("<td></td>");
                verify_name_col.html(verifyInfoList[verify_index].verifyName);

                var verify_expect_col = $("<td></td>");
                verify_expect_col.html(verifyInfoList[verify_index].expectValue);

                var verify_actual_col = $("<td></td>");
                verify_actual_col.html(verifyInfoList[verify_index].actualValue);

                var is_success_col = $("<td></td>");
                if (verifyInfoList[verify_index].isSuccess === 1) {
                    var success = $('<span class="label label-success">验证成功</span>');
                    is_success_col.append(success);
                } else if (verifyInfoList[verify_index].isSuccess === 0) {
                    var fail = $('<span class="label label-danger">验证失败</span>');
                    is_success_col.append(fail);
                }

                verify_item_row.append(verify_name_col);
                verify_item_row.append(verify_expect_col);
                verify_item_row.append(verify_actual_col);
                verify_item_row.append(is_success_col);

                $result_verify_list.append(verify_item_row);
            }
        }
    });
}

/**
 * @description 运行结果列表模态框关闭后清空所有显示的内容,并关闭Socket连接
 */
$("#verify_result_modal").on('hidden.bs.modal', function() {
    $('#result_tbody').empty();
    socket.onclose();
    setTimeout(function() {
        $("body").removeAttr('class');
    }, 500);
});

/**
 * @description 运行响应结果模态框关闭后清空所有显示的内容,并关闭Socket连接
 */
$("#response_verify_modal").on('hidden.bs.modal', function() {
    setTimeout(function() {
        $("body").attr('class', 'modal-open');
    }, 500);
});

/**
 * @description bootbox关闭后重置body的class
 */
$(".bootbox").on('hidden.bs.modal', function() {
    setTimeout(function() {
        $("body").removeAttr('class');
    }, 500);
});