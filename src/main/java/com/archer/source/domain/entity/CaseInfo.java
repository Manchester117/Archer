package com.archer.source.domain.entity;

import lombok.Data;

/** 
* @description: 用例实体 - 与project关联 
* @author:      Zhao.Peng 
* @date:        2018/8/14
* @time:        10:45
* @param:        
* @return:      
*/
@Data
public class CaseInfo {
    private Integer id;
    private String caseName;
    private String apiSequence;         // 接口执行顺序列表
    private Integer runBatTimes;        // 执行次数(默认0)
    private Integer projectId;
}
