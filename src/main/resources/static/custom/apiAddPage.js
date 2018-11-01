var serviceId = $("#service_id").val();
var $service_select_add_api = $("#service_select_add_api");

$service_select_add_api.on('loaded.bs.select', function getServiceInfo() {
    $.ajax({
        type: "post",
        url: "/getServiceInfo",
        data: "serviceId=" + serviceId,
        dataType: "json",
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        success: function (result) {
            $service_select_add_api.append("<option value=" + result.id + ">" + result.serviceName + "</option>");
            $service_select_add_api.selectpicker("refresh");
            $service_select_add_api.selectpicker("render");
        }
    });
});

/**
 * @description 动态添加请求头
 * @type {*|jQuery|HTMLElement}
 */
var header_item_index = 0;
var $add_header_item = $("#add_header_item");
$add_header_item.click(
    function () {
        header_item_index++;
        var $header_list = $("#header_list");
        // 添加一行header类型
        var header_item_row = $("<tr></tr>");
        // 添加一列 - 类型
        var header_item_title_col = $("<td></td>");
        // 添加bootstrap-select
        var $header_item_select = $("<select class='selectpicker header-name-select' id='header_item_select_" + header_item_index + "'></select>");
        $header_type_select.append("<option value='Accept'>Accept</option>");
        $header_type_select.append("<option value='Cache-Control'>Cache-Control</option>");
        $header_type_select.append("<option value='Content-Type'>Content-Type</option>");
        $header_type_select.append("<option value='Cookie'>Cookie</option>");
        $header_type_select.append("<option value='Host'>Host</option>");
        $header_type_select.append("<option value='Referer'>Referer</option>")
        $header_type_select.append("<option value='User-Agent'>User-Agent</option>");
        $header_type_select.append("<option value='X-Requested-With'>X-Requested-With</option>");

        $header_item_select.appendTo(header_item_title_col).selectpicker('refresh');

        // 添加一列 - 内容
        var header_item_content_col = $("<td></td>");

        // 添加输入框
        var $header_item_content = $('<input class="form-control header-value-input" style="width: 100%;" id="header_item_content_' + header_item_index + '" placeholder="header内容"/>');
        $header_item_content.appendTo(header_item_content_col);

        // 添加一列 - 操作
        var header_item_operator_col = $("<td></td>");

        // 添加删除Button
        var $header_del_item_btn = $('<button/>', {
            "class": "btn btn-default del_header_btn",
            "id": "header_item_del_button_" + header_item_index,
            "title": "删除"
        });
        var $icon = $('<i/>', {"class": "glyphicon glyphicon-minus"});
        var $span = $("<span>&nbsp;删除</span>");
        $icon.appendTo($header_del_item_btn);
        $span.appendTo($header_del_item_btn);
        $header_del_item_btn.appendTo(header_item_operator_col);

        header_item_row.append(header_item_title_col);
        header_item_row.append(header_item_content_col);
        header_item_row.append(header_item_operator_col);

        $header_list.append(header_item_row);
    }
);

/**
 * @description 动态添加自定义请求头
 * @type {*|jQuery|HTMLElement}
 */
var custom_header_item_index = 0;
var $add_custom_header_item = $("#add_custom_header_item");
$add_custom_header_item.click(
    function () {
        custom_header_item_index++;
        var $custom_header_list = $("#custom_header_list");
        // 添加一行header类型
        var header_item_row = $("<tr></tr>");
        // 添加一列 - 类型
        var header_item_title_col = $("<td></td>");
        // 添加bootstrap-select
        var $header_item_title = $('<input class="form-control header-name-input" style="width: 100%;" id="header_item_content_' + header_item_index + '" placeholder="header类型"/>');
        $header_item_title.appendTo(header_item_title_col);

        // 添加一列 - 内容
        var header_item_content_col = $("<td></td>");

        // 添加输入框
        var $header_item_content = $('<input class="form-control header-value-input" style="width: 100%;" id="header_item_content_' + header_item_index + '" placeholder="header内容"/>');
        $header_item_content.appendTo(header_item_content_col);

        // 添加一列 - 操作
        var header_item_operator_col = $("<td></td>");

        // 添加删除Button
        var $header_del_item_btn = $('<button/>', {
            "class": "btn btn-default del_header_btn",
            "id": "custom_header_item_del_button_" + custom_header_item_index,
            "title": "删除"
        });
        var $icon = $('<i/>', {"class": "glyphicon glyphicon-minus"});
        var $span = $("<span>&nbsp;删除</span>");
        $icon.appendTo($header_del_item_btn);
        $span.appendTo($header_del_item_btn);
        $header_del_item_btn.appendTo(header_item_operator_col);

        header_item_row.append(header_item_title_col);
        header_item_row.append(header_item_content_col);
        header_item_row.append(header_item_operator_col);

        $custom_header_list.append(header_item_row);
    }
);

/**
 * @description 动态删除请求头
 * @type {*|jQuery|HTMLElement}
 */
$(document).on('click', '.del_header_btn', function () {
    $(this).parent().parent().remove();
});

/**
 * @description 动态添加参数
 * @type {*|jQuery|HTMLElement}
 */
var param_item_index = 0;
var $add_param_item = $("#add_param_item");
$add_param_item.click(
    function () {
        verify_item_index++;
        var $param_list = $("#param_list");
        // 添加一行参数
        var param_item_row = $("<tr></tr>");
        // 添加一列 - 参数名
        var param_item_name_col = $("<td></td>");
        // 添加输入框
        var $param_item_name = $('<input class="form-control param-name-input" style="width: 100%;" id="param_item_name_' + param_item_index + '" placeholder="参数名"/>');
        $param_item_name.appendTo(param_item_name_col);
        // 添加一列 - 参数值
        var param_item_value_col = $("<td></td>");
        // 添加输入框
        var $param_item_value = $('<input class="form-control param-value-input" style="width: 100%;" id="param_item_value_' + param_item_index + '" placeholder="参数值"/>');
        $param_item_value.appendTo(param_item_value_col);

        // 添加一列 - 操作
        var param_item_operator_col = $("<td></td>");
        // 删除Button
        var $param_del_item_btn = $('<button/>', {
            "class": "btn btn-default del_param_btn",
            "id": "header_item_del_button_" + param_item_index,
            "title": "删除"
        });
        var $icon = $('<i/>', {"class": "glyphicon glyphicon-minus"});
        var span_del = $("<span>&nbsp;删除</span>");
        $icon.appendTo($param_del_item_btn);
        $param_del_item_btn.append(span_del);
        $param_del_item_btn.appendTo(param_item_operator_col);

        param_item_row.append(param_item_name_col);
        param_item_row.append(param_item_value_col);
        param_item_row.append(param_item_operator_col);

        $param_list.append(param_item_row);
    }
);

/**
 * @description 动态删除参数
 * @type {*|jQuery|HTMLElement}
 */
$(document).on('click', '.del_param_btn', function () {
    $(this).parent().parent().remove();
});

/**
 * @description 动态添加验证点
 * @type {*|jQuery|HTMLElement}
 */
var verify_item_index = 0;
var $add_verify_item = $("#add_verify_item");
$add_verify_item.click(
    function () {
        verify_item_index++;
        var $verify_list = $("#verify_list");
        // 添加一行 - 验证点
        var verify_item_row = $("<tr></tr>");

        // 添加一列 - 验证名
        var verify_item_name_col = $("<td></td>");
        // 添加输入框
        var $verify_item_name = $('<input class="form-control verify-name-input" style="width: 100%;" id="verify_item_name_' + verify_item_index + '" placeholder="验证名"/>');
        $verify_item_name.appendTo(verify_item_name_col);


        // 添加一列 - 类型
        var verify_item_type_col = $("<td></td>");
        // 添加bootstrap-select
        var $verify_item_select = $("<select class='selectpicker verify-type-select' id='verify_item_select_" + verify_item_index + "'></select>");
        $verify_item_select.append("<option value=" + 1 + ">Regex</option>");
        $verify_item_select.append("<option value=" + 2 + ">JSONPath</option>");
        $verify_item_select.append("<option value=" + 3 + ">CSS Selector</option>");
        $verify_item_select.appendTo(verify_item_type_col).selectpicker('refresh');

        // 添加一列 - 表达式
        var verify_item_expression_col = $("<td></td>");
        // 添加输入框
        var $verify_item_expression = $('<input class="form-control verify-expression-input" style="width: 100%;" id="verify_item_expression_' + verify_item_index + '" placeholder="表达式"/>');
        $verify_item_expression.appendTo(verify_item_expression_col);

        // 添加一列 - 期望值
        var verify_item_expect_col = $("<td></td>");
        // 添加输入框
        var $verify_item_expect = $('<input class="form-control verify-expect-input" style="width: 100%;" id="verify_item_expect_' + verify_item_index + '" placeholder="期望值"/>');
        $verify_item_expect.appendTo(verify_item_expect_col);

        // 添加一列 - 操作
        var verify_item_operator_col = $("<td></td>");
        // 删除Button
        var $verify_del_item_btn = $('<button/>', {
            "class": "btn btn-default del_verify_btn",
            "id": "verify_item_del_button_" + verify_item_index,
            "title": "删除"
        });
        var $icon = $('<i/>', {"class": "glyphicon glyphicon-minus"});
        var span_del = $("<span>&nbsp;删除</span>");
        $icon.appendTo($verify_del_item_btn);
        $verify_del_item_btn.append(span_del);
        $verify_del_item_btn.appendTo(verify_item_operator_col);

        verify_item_row.append(verify_item_name_col);
        verify_item_row.append(verify_item_type_col);
        verify_item_row.append(verify_item_expression_col);
        verify_item_row.append(verify_item_expect_col);
        verify_item_row.append(verify_item_operator_col);

        $verify_list.append(verify_item_row);
    }
);

/**
 * @description 动态删除验证点
 * @type {*|jQuery|HTMLElement}
 */
$(document).on('click', '.del_verify_btn', function () {
    $(this).parent().parent().remove();
});

/**
 * @description 接口信息提交
 * @type {*|jQuery|HTMLElement}
 */
var $submit_add_btn = $('#submit_add_btn');
$submit_add_btn.click(
    function() {
        var apiJson = getApiInfoJsonData();
        $.ajax({
            type: "post",
            url: "/createApiAndVerifyInfo",
            data: JSON.stringify(apiJson),
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            success: function(result) {
                if (result['insertApiSuccessFlag'] === 1) {     // 如果向库中添加项目成功
                    bootbox.alert({
                        title: '提示',
                        message: "创建接口成功",
                        callback: function () {
                            $api_list.bootstrapTable("selectPage", 1);
                            window.location.href = "/apiList?serviceId=" + serviceId;  // 返回接口列表页
                        }
                    });
                } else {
                    bootbox.alert({
                        title: '警告',
                        message: "创建接口失败"
                    });
                }
            }
        });
    }
);

function getApiInfoJsonData() {
    // 将页面中的HTTP请求头各属性整合为JSON
    var headerItemJson = {};
    var contentType = null;                                                         // 定义一个ContentType的变量,以便在提交不同类型的Body时使用
    var $header_list = $("#header_list");
    var headerItemLength = $header_list.find("tr").length;
    var headerNameList = $header_list.find(".selectpicker");
    var headerValueList = $header_list.find(".form-control");
    for (var headerIndex = 0; headerIndex < headerItemLength; ++headerIndex) {
        var headerName = headerNameList[headerIndex].value;                         // 如果是动态添加SelectPicker则直接用value取值
        var headerValue = headerValueList[headerIndex].value;

        if (headerName === 'Content-Type')
            contentType = headerValue;
        headerItemJson[headerName] = headerValue;
    }

    var $custom_header_list = $("#custom_header_list");
    var customHeaderItemLength = $custom_header_list.find("tr").length;
    var customHeaderNameList = $custom_header_list.find(".header-name-input");
    var customHeaderValueList = $custom_header_list.find(".header-value-input");
    for (var customHeaderIndex = 0; customHeaderIndex < customHeaderItemLength; ++customHeaderIndex) {
        var customHeaderName = customHeaderNameList[customHeaderIndex].value;
        var customHeaderValue = customHeaderValueList[customHeaderIndex].value;
        headerItemJson[customHeaderName] = customHeaderValue;
    }

    var bodyJson = {};
    var $param_list;
    var bodyItemLength;
    var bodyNameList;
    var bodyValueList;
    var methodType = $("#method").selectpicker('val');
    if (methodType === "2" || methodType === "3") {                                     // 如果是POST或者PUT请求,参数从Parameter和JSON中获取
        if (contentType === "application/x-www-form-urlencoded") {                      // 根据ContentType来判断提交的Body类型
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
                return;                 // 如果提交JSON格式不正确,则不做提交
            }
        }
    } else if (methodType === "1" || methodType === "4") {                              // 如果是GET或者DELETE请求,参数从Parameter获取
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
    var verifyItemLength = $verify_list.find("tr").length;

    var verifyNameList = $verify_list.find(".verify-name-input");
    var verifyTypeList = $verify_list.find(".selectpicker");
    var verifyExpressionList = $verify_list.find(".verify-expression-input");
    var verifyExpectList = $verify_list.find(".verify-expect-input");
    for (var verifyIndex = 0; verifyIndex < verifyItemLength; ++verifyIndex) {
        var verifyItem = {
            'verifyName': verifyNameList[verifyIndex].value,
            'verifyType': verifyTypeList[verifyIndex].value,
            'expression': verifyExpressionList[verifyIndex].value,
            'expectValue': verifyExpectList[verifyIndex].value
        };
        verifyInfoList.push(verifyItem)
    }

    // 拼装接口的JSON
    var apiJson = {
        "apiName": $("#api_name").val(),
        "protocol": $("#protocol").selectpicker('val'),
        "method": $("#method").selectpicker('val'),
        "url": $("#url").val(),
        "isMock": 0,
        "serviceId": $("#service_select_add_api").selectpicker('val'),
        "header": headerItemJson,
        "body": bodyJson,
        "verifyInfoList": verifyInfoList
    };

    return apiJson;
}

// 切换Body类型的逻辑(新建和修改共用)
$(function() {
    $("a[data-toggle='tab']").on('shown.bs.tab', function (e) {
        var activeTab = $(e.target).text();
        if (activeTab === 'Parameters')
            $('#body_type').val("params");
        else if (activeTab === 'JSON')
            $('#body_type').val("json");
    });
});