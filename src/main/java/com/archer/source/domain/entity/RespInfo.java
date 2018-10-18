package com.archer.source.domain.entity;

import lombok.Data;

/** 
* @description: 响应实体 - 与接口关联(请求正文会被关联参数逻辑和验证逻辑使用)
* @author:      Zhao.Peng
* @date:        2018/8/15
* @time:        11:03
* @param:
* @return:
*/
@Data
public class RespInfo {
    private Integer id;
    private String apiName;
    private String url;
    private Integer statusCode;
    private String respHeader;
    private String respContent;
    private Long respTime;
    private Integer runBatTimes;
    private Integer apiId;
}
