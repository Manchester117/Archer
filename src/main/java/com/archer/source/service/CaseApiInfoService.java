package com.archer.source.service;

import com.alibaba.fastjson.JSONObject;
import com.archer.source.domain.entity.CaseApiInfo;

import java.util.List;

public interface CaseApiInfoService {
    CaseApiInfo getCaseApiInfo(Integer id);

    CaseApiInfo getSingleCaseApiInfo(Integer caseId, Integer apiId);

    List<CaseApiInfo> getCaseApiInfoByCaseId(Integer caseId);

    JSONObject getCaseApiInfoByCaseId(Integer offset, Integer limit, Integer caseId);

    Integer insertCaseApiInfo(CaseApiInfo caseApiInfo);

    Integer updateCaseApiInfo(CaseApiInfo caseApiInfo);

    Integer deleteCaseApiInfo(Integer id);

    Integer deleteCaseApiInfoByCaseId(Integer caseId);
}
