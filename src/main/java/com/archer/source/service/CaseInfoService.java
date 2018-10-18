package com.archer.source.service;

import com.alibaba.fastjson.JSONObject;
import com.archer.source.domain.entity.CaseInfo;

import java.util.List;

public interface CaseInfoService {
    CaseInfo getCaseInfo(Integer id);

    List<CaseInfo> getCaseInfoListByProjectId(Integer projectId);

    JSONObject getCaseInfoListWithProject(Integer offset, Integer limit, Integer projectId);

    Integer insertCaseInfo(CaseInfo caseInfo);

    Integer updateCaseInfo(CaseInfo caseInfo);

    Integer updateCaseInfoRunBatTimes(Integer caseId);

    Integer deleteCaseInfo(Integer id);

    Integer deleteCaseInfoByProjectId(Integer projectId);
}
