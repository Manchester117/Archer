package com.archer.source.domain.entity;

import lombok.Data;

/** 
* @description: 验证实体 - 与关联参数类似,通过表达式获取待验证的文本,拿到文本后与期望值进行对比
* @author:      Zhao.Peng
* @date:        2018/8/15
* @time:        14:05
* @param:
* @return:
*/
@Data
public class VerifyInfo {
    private Integer id;
    private String verifyName;
    private Integer verifyType;
    private String expression;
    private String expectValue;
    private String actualValue;
    private Integer isSuccess;
    private Integer apiId;
}
