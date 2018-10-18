package com.archer.source.domain.entity;

import lombok.Data;

/** 
* @description: Hosts配置实体 - 可对单独会话设置Hosts指向
* @author:      Zhao.Peng 
* @date:        2018/8/14
* @time:        11:05
* @param:
* @return:
*/
@Data
public class HostsInfo {
    private Integer id;
//    private String ipAddress;
//    private String domain;
    private String hostsItem;
    private String description;
    private Integer status;
}
