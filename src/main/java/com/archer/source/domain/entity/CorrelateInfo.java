package com.archer.source.domain.entity;

import lombok.Data;

/** 
* @description: 关联实体 - 如果在用例模式下运行,则需要设置关联参数,将关联参数用于下一个请求
* @author:      Zhao.Peng
* @date:        2018/8/15
* @time:        9:57 
* @param:
* @return:
*/
@Data
public class CorrelateInfo {
    private Integer id;
    private String corrField;           // 关联参数名
    private Integer corrPattern;        // 获取关联参数的模式(三种:正则表达式/JSONPath/CSS Selector)
    private String corrExpression;      // 获取关联参数的表达式
    private String corrValue;           // 获取到的关联值
    private Integer caseApiId;          // 对应的接口ID
}
