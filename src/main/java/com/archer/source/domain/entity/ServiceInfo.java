package com.archer.source.domain.entity;

import lombok.Data;

import java.util.Date;

/** 
* @description: 项目实体 - 每个接口/用例都必须隶属于一个项目 
* @author:      Zhao.Peng 
* @date:        2018/8/14
* @time:        10:50
* @param:        
* @return:      
*/
@Data
public class ServiceInfo {
    private Integer id;
    private String serviceName;
    private String baseUrl;
    private String version;
    private Integer type;
    private Date createTime;
    private String description;
    private Integer projectId;
}
