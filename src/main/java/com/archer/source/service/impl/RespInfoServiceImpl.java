package com.archer.source.service.impl;

import com.archer.source.domain.entity.RespInfo;
import com.archer.source.mapper.RespInfoMapper;
import com.archer.source.service.RespInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class RespInfoServiceImpl implements RespInfoService{
    @Autowired
    private RespInfoMapper respMapper;

    @Override
    public RespInfo getRespInfo(Integer id) {
        RespInfo respInfo = respMapper.getRespInfo(id);
        if (Objects.nonNull(respInfo))
            return respInfo;
        else
            return null;
    }

    @Override
    public RespInfo getLastRespInfo(Integer apiId) {
        return respMapper.getLastRespInfo(apiId);
    }

    @Override
    public RespInfo getRespInfoByRunBatTimes(Integer apiId, Integer runBatTimes) {
        return respMapper.getRespInfoByRunBatTimes(apiId, runBatTimes);
    }

    @Override
    public List<RespInfo> getRespInfoListByApiId(Integer apiId) {
        return respMapper.getRespInfoListByApiId(apiId);
    }

    @Override
    public Integer insertRespInfo(RespInfo respInfo) {
        return respMapper.insertRespInfo(respInfo);
    }

    @Override
    public Integer deleteRespInfo(Integer id) {
        return respMapper.deleteRespInfo(id);
    }

    @Override
    public Integer deleteRespInfoByApiId(Integer apiId) {
        return respMapper.deleteRespInfoByApiId(apiId);
    }
}
