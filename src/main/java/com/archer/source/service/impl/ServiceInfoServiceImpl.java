package com.archer.source.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.archer.source.domain.entity.ServiceInfo;
import com.archer.source.mapper.ServiceInfoMapper;
import com.archer.source.service.ServiceInfoService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ServiceInfoServiceImpl implements ServiceInfoService {
    @Autowired
    private ServiceInfoMapper serviceMapper;

    @Override
    public ServiceInfo getServiceInfo(Integer id) {
        return serviceMapper.getServiceInfo(id);
    }

    @Override
    public JSONArray getAllServiceInfoList() {
        List<ServiceInfo> allServiceInfoList = serviceMapper.getAllServiceInfoList();
        return JSONArray.parseArray(JSON.toJSONString(allServiceInfoList));
    }

    @Override
    public JSONObject getServiceInfoByPageList(Integer offset, Integer limit, String serviceName, Integer projectId) {
        JSONObject result = new JSONObject();
        Page<Object> page = PageHelper.offsetPage(offset, limit);
        List<ServiceInfo> serviceInfoList = serviceMapper.getServiceInfoList(serviceName, projectId);
        JSONArray serviceJsonArray = JSONArray.parseArray(JSON.toJSONString(serviceInfoList));
        result.put("total", page.getTotal());
        result.put("rows", serviceJsonArray);
        return result;
    }

    @Override
    public List<ServiceInfo> getServiceInfoListByProjectId(Integer projectId) {
        return serviceMapper.getServiceInfoListByProjectId(projectId);
    }

    @Override
    public Integer insertServiceInfo(ServiceInfo serviceInfo) {
        return serviceMapper.insertServiceInfo(serviceInfo);
    }

    @Override
    public Integer updateServiceInfo(ServiceInfo serviceInfo) {
        return serviceMapper.updateServiceInfo(serviceInfo);
    }

    @Override
    public Integer deleteServiceInfo(Integer id) {
        return serviceMapper.deleteServiceInfo(id);
    }
}
