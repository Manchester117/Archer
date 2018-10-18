package com.archer.source.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.archer.source.domain.entity.ServiceInfo;

import java.util.List;

public interface ServiceInfoService {
    ServiceInfo getServiceInfo(Integer id);

    JSONArray getAllServiceInfoList();

    JSONObject getServiceInfoByPageList(Integer offset, Integer limit, String serviceName, Integer projectId);

    List<ServiceInfo> getServiceInfoListByProjectId(Integer projectId);

    Integer insertServiceInfo(ServiceInfo serviceInfo);

    Integer updateServiceInfo(ServiceInfo serviceInfo);

    Integer deleteServiceInfo(Integer id);
}
