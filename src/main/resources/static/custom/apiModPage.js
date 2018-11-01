// 获取当前项目下的所有服务
var $service_select_mod_api = $("#service_select_mod_api");
var projectId = $("#project_id").val();
if (projectId !== undefined) {
    $.ajax({
        type: "post",
        url: "/getServiceListByProjectId",
        data: "projectId=" + projectId,
        dataType: "json",
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        success: function (result) {
            for (var i = 0; i < result.length; i++) {
                $service_select_mod_api.append("<option value=" + result[i].id + ">" + result[i].serviceName + "</option>")
            }
            $service_select_mod_api.selectpicker('val', $('#service_id').val());
            $service_select_mod_api.selectpicker("refresh");
            $service_select_mod_api.selectpicker("render");
        }
    });
}

/**
 * @description 将页面中的HTTP请求头各属性整合为JSON
 * @returns {{id: *|jQuery, apiName: *|jQuery, protocol: *|jQuery, method: *|jQuery, url: *|jQuery, isMock: number, serviceId: *|jQuery, header: {}, body: {}, verifyInfoList: Array}}
 */
function getApiInfoJsonModifyData() {
    var headerItemJson = {};
    var contentType = null;
    var $header_list = $("#header_list");
    var headerItemLength = $header_list.find("tr").length;
    var headerNameList = $header_list.find(".selectpicker");
    var headerValueList = $header_list.find(".form-control");
    for (var headerIndex = 0; headerIndex < headerItemLength; ++headerIndex) {
        var headerName = headerNameList[headerIndex].value;                             // 如果是动态添加SelectPicker则直接用value取值
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
        "id": $("#api_id").val(),
        "apiName": $("#api_name").val(),
        "protocol": $("#protocol").selectpicker('val'),
        "method": $("#method").selectpicker('val'),
        "url": $("#url").val(),
        "isMock": 0,
        "serviceId": $("#service_select_mod_api").selectpicker('val'),
        "header": headerItemJson,
        "body": bodyJson,
        "verifyInfoList": verifyInfoList,
        "correlateInfoList": correlateInfoList
    };

    return apiJson;
}

// 执行修改提交
var $submit_modify_btn = $("#submit_modify_btn");
$submit_modify_btn.click(
    function () {
        var apiJson = getApiInfoJsonModifyData();
        $.ajax({
            type: "post",
            url: "/modifyApiSubmit",
            data: JSON.stringify(apiJson),
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            success: function(result) {
                if (result['updateApiFlag'] === 1) {
                    bootbox.alert({
                        title: '提示',
                        message: "更新接口成功",
                        callback: function () {
                            $api_list.bootstrapTable("selectPage", 1);
                        }
                    });
                } else {
                    bootbox.alert({
                        title: '警告',
                        message: "更新接口失败"
                    });
                }
            }
        });
    }
);

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


var $execute_request_submit = $('#execute_request_submit');
$execute_request_submit.click(
    function() {
        var apiId = $("#hidden_api_id").val();
        var hostsId = $("#hidden_host_id").val();
        $("#select_hosts_modal").modal('hide');                                 // 隐藏模态框

        $('.modal').on('hidden.bs.modal', function (){                          // 解决多层模态框显示导致当前模态框无法滚动的问题
            $("body").addClass("modal-open");                                   // 当上一个模态消失的时候重置Body的样式
        });

        $.ajax({
            type: "post",
            url: "/runApi",
            data: "apiId=" + apiId + "&hostsId=" + hostsId,
            dataType: "json",
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            beforeSend: function () {
                $('#waitRespModal').modal('show');
            },
            success: function(result) {
                if (result.hasOwnProperty('errorMsg')) {                // 如果选择Hosts不正确则给出Hosts选择不正确的弹出框
                    var msgArray = result['errorMsg'].split("|");   // 对后端返回的异常进行分割
                    $('#except_textarea').val(msgArray[0]);
                    $('#except_message').val(msgArray[1]);
                    $('#run_api_except_modal').modal('show');
                } else {
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

                    $('#response_verify_modal').modal('show');                              // 显示响应结果
                }
            },
            complete: function() {
                $('#waitRespModal').modal('hide');
            }
        });
    }
);

/**
 * @description 关闭模态框时恢复页面滚动
 */
$("#run_api_except_modal").on('hidden.bs.modal', function() {
    setTimeout(function() {
        $("body").removeAttr('class');
    }, 500);
});

/**
 * @description 关闭模态框时恢复页面滚动
 */
$("#response_verify_modal").on('hidden.bs.modal', function() {
    setTimeout(function() {
        $("body").removeAttr('class');
    }, 500);
});