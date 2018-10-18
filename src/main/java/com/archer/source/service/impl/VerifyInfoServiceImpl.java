package com.archer.source.service.impl;

import com.archer.source.domain.entity.VerifyInfo;
import com.archer.source.mapper.VerifyInfoMapper;
import com.archer.source.service.VerifyInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class VerifyInfoServiceImpl implements VerifyInfoService{
    @Autowired
    private VerifyInfoMapper verifyMapper;

    @Override
    public VerifyInfo getVerifyInfo(Integer id) {
        VerifyInfo verifyInfo = verifyMapper.getVerifyInfo(id);
        if (Objects.nonNull(verifyInfo))
            return verifyInfo;
        else
            return null;
    }

    @Override
    public List<VerifyInfo> getVerifyInfoListByApiId(Integer apiId) {
        return verifyMapper.getVerifyInfoListByApiId(apiId);
    }

    @Override
    public Integer insertVerifyInfo(VerifyInfo verifyInfo) {
        return verifyMapper.insertVerifyInfo(verifyInfo);
    }

    @Override
    public Integer updateVerifyInfo(VerifyInfo verifyInfo) {
        return verifyMapper.updateVerifyInfo(verifyInfo);
    }

    @Override
    public Integer deleteVerifyInfo(Integer id) {
        return verifyMapper.deleteVerifyInfo(id);
    }

    @Override
    public Integer deleteVerifyInfoByApiId(Integer apiId) {
        return verifyMapper.deleteVerifyInfoByApiId(apiId);
    }
}
