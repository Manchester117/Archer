package com.archer.source.service;

import com.archer.source.domain.entity.VerifyInfo;

import java.util.List;

public interface VerifyInfoService {
    VerifyInfo getVerifyInfo(Integer id);

    List<VerifyInfo> getVerifyInfoListByApiId(Integer apiId);

    Integer insertVerifyInfo(VerifyInfo verifyInfo);

    Integer updateVerifyInfo(VerifyInfo verifyInfo);

    Integer deleteVerifyInfo(Integer id);

    Integer deleteVerifyInfoByApiId(Integer apiId);
}
