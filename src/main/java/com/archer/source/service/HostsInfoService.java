package com.archer.source.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.archer.source.domain.entity.HostsInfo;

import java.util.List;

public interface HostsInfoService {
    HostsInfo getHostsInfo(Integer id);

    JSONObject getHostsInfoByPageList(Integer offset, Integer limit, String description);

    JSONArray getAllHostsInfoList();

    Integer insertHostsInfo(HostsInfo hostsInfo);

    Integer updateHostsInfo(HostsInfo hostsInfo);

    Integer deleteHostsInfo(Integer id);
}
