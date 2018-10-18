/**
 * @description hosts列表
 * @type {*|jQuery|HTMLElement}
 */
var $hosts_list = $('#hosts_list');
$hosts_list.bootstrapTable({
    url: '/getHostsList',
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
            description: $('#search_name').val()
        }
    },
    columns: [
        {
            field: 'id',
            title: '编号',
            align: 'center',
            formatter: function(value, row, index) {                                    // 以列表的序列来标号
                var pageSize = $hosts_list.bootstrapTable('getOptions').pageSize;       // 通过表的#id 可以得到每页多少条
                var pageNumber = $hosts_list.bootstrapTable('getOptions').pageNumber;   // 通过表的#id 可以得到当前第几页
                return pageSize * (pageNumber - 1) + index + 1;                         // 返回每条的序号: 每页条数 * (当前页 - 1)+ 序号
            },
            width: 120
        }, {
            field: 'description',
            title: '描述',
            align: 'center'
        }, {
            field: 'id',
            title: '操作',
            align: 'center',
            formatter: function (value, row, index) {
                return [
                    '<a href="javascript:modHosts(' + value + ')">' +
                    '<i class="fa fa-pencil"></i>编辑' +
                    '</a>',
                    '&nbsp&nbsp&nbsp&nbsp' +
                    '<a href="javascript:delHosts(' + value + ')">' +
                    '<i class="fa fa-times"></i>删除' +
                    '</a>'
                ].join('');
            },
            width: 240
        }
    ]
});

/**
 * @description 按项目名称执行查询
 * @type {*|jQuery|HTMLElement}
 */
var $search_btn = $("#search_btn");
$search_btn.click(
    function() {
        $hosts_list.bootstrapTable("selectPage", 1);
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
        $hosts_list.bootstrapTable('selectPage', 1);
    }
);

var mod_hosts_item = $(".mod-hosts-btn");
mod_hosts_item.click(function() {
    var host_item_list = $("#mod_host_item_list");
    // 添加一行
    var host_item_row = $("<tr></tr>");
    // 添加HostIP列
    var host_ip = $("<td></td>");
    var host_ip_input = $('<input class="form-control host-ip-input" style="width: 100%;" placeholder="IP地址"/>');
    host_ip_input.appendTo(host_ip);

    // 添加HostDomain列
    var host_domain = $("<td></td>");
    var host_domain_input = $('<input class="form-control host-domain-input" style="width: 100%;" placeholder="域名"/>');
    host_domain_input.appendTo(host_domain);

    // 添加删除列
    var host_delete = $("<td></td>");
    var host_delete_button = $('<button/>', {
        "class": "btn btn-default del_hosts_btn",
        "title": "删除"
    });
    var icon = $('<i/>', {"class": "glyphicon glyphicon-minus"});
    var span_del = $("<span>&nbsp;删除</span>");
    icon.appendTo(host_delete_button);
    span_del.appendTo(host_delete_button);
    host_delete_button.appendTo(host_delete);

    host_item_row.append(host_ip);
    host_item_row.append(host_domain);
    host_item_row.append(host_delete);

    host_item_list.append(host_item_row);
});

/**
 * @description 动态删除hostItem
 * @type {*|jQuery|HTMLElement}
 */
$(document).on('click', '.del_hosts_btn', function () {
    $(this).parent().parent().remove();
});

/**
 * @description 创建项目
 * @type {*|jQuery|HTMLElement}
 */
var $create_hosts_submit = $("#create_hosts_submit");
$create_hosts_submit.click(
    function() {
        var hostsItem = [];
        var host_item_list = $("#add_host_item_list");
        if (host_item_list.children().length > 0) {
            var hostsItemLength = host_item_list.find("tr").length;
            var hostsIpList = host_item_list.find(".host-ip-input");
            var hostsDomainList = host_item_list.find(".host-domain-input");
            for (var index = 0; index < hostsItemLength; ++index) {
                var hostItem = {
                    'ip': hostsIpList[index].value,
                    'domain': hostsDomainList[index].value
                };
                if (hostsIpList[index].value !== '' && hostsDomainList[index].value !== '')
                    hostsItem.push(hostItem);
            }
        }

        var description = $("#create_description").val();
        // 通过Ajax提交实体
        $.ajax({
            type: "post",
            url: "/createHosts",
            data: JSON.stringify({"hostsItem": hostsItem, "description": description}),
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            success: function(result) {                                     // 提交成功后关闭模态框
                var $create_hosts_modal = $("#create_hosts_modal");
                $create_hosts_modal.modal("hide");                          // 先将模态框隐藏
                if (result['flag'] === 1) {                                 // 如果向库中添加项目成功
                    bootbox.alert({
                        title: '提示',
                        message: "创建Hosts成功"
                    });
                } else {
                    bootbox.alert({
                        title: '警告',
                        message: "创建Hosts失败"
                    });
                }
                $hosts_list.bootstrapTable('refresh');                // 关闭后刷新项目列表
            }
        });
    }
);

/**
 * @description 关闭模态框时需要做值的清空
 */
$('#create_hosts_modal').on('hide.bs.modal', function () {
    $("#add_host_item_list").empty();
    $("#create_description").val("");
});

var add_hosts_item = $(".add-hosts-btn");
add_hosts_item.click(function() {
    var host_item_list = $("#add_host_item_list");
    // 添加一行
    var host_item_row = $("<tr></tr>");
    // 添加HostIP列
    var host_ip = $("<td></td>");
    var host_ip_input = $('<input class="form-control host-ip-input" style="width: 100%;" placeholder="IP地址"/>');
    host_ip_input.appendTo(host_ip);

    // 添加HostDomain列
    var host_domain = $("<td></td>");
    var host_domain_input = $('<input class="form-control host-domain-input" style="width: 100%;" placeholder="域名"/>');
    host_domain_input.appendTo(host_domain);

    // 添加删除列
    var host_delete = $("<td></td>");
    var host_delete_button = $('<button/>', {
        "class": "btn btn-default del_hosts_btn",
        "title": "删除"
    });
    var icon = $('<i/>', {"class": "glyphicon glyphicon-minus"});
    var span_del = $("<span>&nbsp;删除</span>");
    icon.appendTo(host_delete_button);
    span_del.appendTo(host_delete_button);
    host_delete_button.appendTo(host_delete);

    host_item_row.append(host_ip);
    host_item_row.append(host_domain);
    host_item_row.append(host_delete);

    host_item_list.append(host_item_row);
});


/**
 * @description 在打开编辑Hosts时需要将当前Hosts的值写入到模态框当中
 * @param hostsId
 */
function modHosts(hostsId){
    $.ajax({
        type: "post",
        url: "/getHostsInfo",
        data: "hostsId=" + hostsId,
        dataType: "json",
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        success: function(result) {
            $("#modify_hosts_id").attr("value", result["id"]);

            var hostItemList = eval(result["hostsItem"]);
            hostItemList.forEach(function (value, index) {
                var host_item_list = $("#mod_host_item_list");
                // 添加一行
                var host_item_row = $("<tr></tr>");
                // 添加HostIP列
                var host_ip = $("<td></td>");
                var host_ip_input = $('<input class="form-control host-ip-input" style="width: 100%;" value="' + value["ip"] + '"/>');
                host_ip_input.appendTo(host_ip);

                // 添加HostDomain列
                var host_domain = $("<td></td>");
                var host_domain_input = $('<input class="form-control host-domain-input" style="width: 100%;" value="' + value["domain"] + '"/>');
                host_domain_input.appendTo(host_domain);

                // 添加删除列
                var host_delete = $("<td></td>");
                var host_delete_button = $('<button/>', {
                    "class": "btn btn-default del_hosts_btn",
                    "title": "删除"
                });
                var icon = $('<i/>', {"class": "glyphicon glyphicon-minus"});
                var span_del = $("<span>&nbsp;删除</span>");
                icon.appendTo(host_delete_button);
                span_del.appendTo(host_delete_button);
                host_delete_button.appendTo(host_delete);

                host_item_row.append(host_ip);
                host_item_row.append(host_domain);
                host_item_row.append(host_delete);

                host_item_list.append(host_item_row);
            });

            $("#modify_description").val(result["description"]);
            $("#modify_hosts_modal").modal('show');
        }
    });
}

/**
 * @description 关闭模态框时需要做值的清空
 */
$('#modify_hosts_modal').on('hide.bs.modal', function () {
    $("#mod_host_item_list").empty();
    $("#modify_description").val("");
});

/**
 * @description 提交修改后的Hosts信息
 * @type {*|jQuery|HTMLElement}
 */
var $modify_hosts_submit = $('#modify_hosts_submit');
$modify_hosts_submit.click(
    function() {
        var hostsId = $("#modify_hosts_id").val();

        var hostsItem = [];
        var host_item_list = $("#mod_host_item_list");
        if (host_item_list.children().length > 0) {
            var hostsItemLength = host_item_list.find("tr").length;
            var hostsIpList = host_item_list.find(".host-ip-input");
            var hostsDomainList = host_item_list.find(".host-domain-input");
            for (var index = 0; index < hostsItemLength; ++index) {
                var hostItem = {
                    'ip': hostsIpList[index].value,
                    'domain': hostsDomainList[index].value
                };
                if (hostsIpList[index].value !== '' && hostsDomainList[index].value !== '')
                    hostsItem.push(hostItem);
            }
        }
        var description = $("#modify_description").val();

        // 通过Ajax提交实体
        $.ajax({
            type: "post",
            url: "/modifyHosts",
            data: JSON.stringify({"id": hostsId, "hostsItem": hostsItem, "description": description}),
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            success: function(result) {                                     // 提交成功后关闭模态框
                var $modify_hosts_modal = $("#modify_hosts_modal");
                $modify_hosts_modal.modal("hide");                          // 先将模态框隐藏
                $hosts_list.bootstrapTable('selectPage', 1);                // 关闭后刷新项目列表
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
            }
        });
    }
);

function delHosts(hostsId) {
    $.ajax({
        type: "post",
        url: "/deleteHosts",
        data: "hostsId=" + hostsId,
        dataType: "json",
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        success: function(result) {
            if (result['flag'] === 1) {                                 // 如果向库中添加项目成功
                bootbox.alert({
                    title: '提示',
                    message: "删除Hosts成功"
                });
            } else {
                bootbox.alert({
                    title: '警告',
                    message: "删除Hosts失败"
                });
            }
            $hosts_list.bootstrapTable('selectPage', 1);                // 删除后刷新项目列表
        }
    });
}