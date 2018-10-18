package com.archer.source.service;

import com.archer.source.domain.entity.RespInfo;

import java.util.List;

public interface RespInfoService {
    RespInfo getRespInfo(Integer id);

    RespInfo getLastRespInfo(Integer apiId);

    RespInfo getRespInfoByRunBatTimes(Integer apiId, Integer runBatTimes);

    List<RespInfo> getRespInfoListByApiId(Integer apiId);

    Integer insertRespInfo(RespInfo respInfo);

    Integer deleteRespInfo(Integer id);

    Integer deleteRespInfoByApiId(Integer apiId);
}
