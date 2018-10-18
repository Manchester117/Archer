package com.archer.source.service;

import com.alibaba.fastjson.JSONObject;
import com.archer.source.domain.entity.ApiInfo;

import java.util.List;

public interface ApiInfoService {
    ApiInfo getApiInfo(Integer id);

    JSONObject getApiInfoByPageList(Integer offset, Integer limit, String apiName);

    JSONObject getApiInfoWithServiceList(Integer offset, Integer limit, String apiName, Integer serviceId);

    JSONObject getApiInfoWithCaseList(Integer offset, Integer limit, Integer caseId);

    List<ApiInfo> getApiInfoListByName(String apiName);

    List<ApiInfo> getApiInfoListByServiceId(Integer serviceId);

    Integer insertApiInfo(ApiInfo apiInfo);

    Integer updateApiInfo(ApiInfo apiInfo);

    Integer deleteApiInfo(Integer id);

    Integer deleteApiInfoByServiceId(Integer serviceId);
}
