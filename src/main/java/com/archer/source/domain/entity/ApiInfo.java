package com.archer.source.domain.entity;

import lombok.Data;
import java.util.Date;

/** 
* @description: 接口实体,与Project关联
* @author:      Zhao.Peng
* @date:        2018/8/14
* @time:        10:36
*/
@Data
public class ApiInfo {
    private Integer id;
    private String apiName;
    private Integer protocol;
    private Integer method;
    private String url;
    private String header;
    private String body;
    private Date createTime;
    private Integer isMock;
    private Integer serviceId;
}
