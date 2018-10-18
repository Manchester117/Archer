package com.archer.source.service;

import com.archer.source.domain.entity.CaseVerifyInfo;

import java.util.List;

public interface CaseVerifyInfoService {
    List<CaseVerifyInfo> getCaseVerifyInfoByCaseApiId(Integer caseApiId);

    Integer insertCaseVerifyInfo(CaseVerifyInfo caseVerifyInfo);

    Integer updateCaseVerifyInfo(CaseVerifyInfo caseVerifyInfo);

    Integer deleteCaseVerifyInfo(Integer id);

    Integer deleteCaseVerifyInfoByCaseApiId(Integer caseApiId);
}
