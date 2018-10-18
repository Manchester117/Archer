package com.archer.source.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.archer.source.domain.entity.HostsInfo;
import com.archer.source.mapper.HostsInfoMapper;
import com.archer.source.service.HostsInfoService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class HostsInfoServiceImpl implements HostsInfoService{
    @Autowired
    private HostsInfoMapper hostsMapper;

    @Override
    public HostsInfo getHostsInfo(Integer id) {
        HostsInfo hostsInfo = hostsMapper.getHostsInfo(id);
        if (Objects.nonNull(hostsInfo))
            return hostsInfo;
        else
            return null;
    }

    @Override
    public JSONObject getHostsInfoByPageList(Integer offset, Integer limit, String description) {
        JSONObject result = new JSONObject();
        Page<Object> page = PageHelper.offsetPage(offset, limit);
        List<HostsInfo> hostsInfoList = hostsMapper.getHostsInfoList(description);
        JSONArray hostsJsonArray = JSONArray.parseArray(JSON.toJSONString(hostsInfoList));
        result.put("total", page.getTotal());
        result.put("rows", hostsJsonArray);
        return result;
    }

    @Override
    public JSONArray getAllHostsInfoList() {
        List<HostsInfo> allHostsInfoList = hostsMapper.getAllHostsInfoList();
        return JSONArray.parseArray(JSON.toJSONString(allHostsInfoList));
    }

    @Override
    public Integer insertHostsInfo(HostsInfo hostsInfo) {
        return hostsMapper.insertHostsInfo(hostsInfo);
    }

    @Override
    public Integer updateHostsInfo(HostsInfo hostsInfo) {
        return hostsMapper.updateHostsInfo(hostsInfo);
    }

    @Override
    public Integer deleteHostsInfo(Integer id) {
        return hostsMapper.deleteHostsInfo(id);
    }
}
