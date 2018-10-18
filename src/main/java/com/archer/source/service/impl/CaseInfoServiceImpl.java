package com.archer.source.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.archer.source.domain.entity.CaseInfo;
import com.archer.source.domain.linked.CaseInfoWithProject;
import com.archer.source.mapper.CaseInfoMapper;
import com.archer.source.service.CaseInfoService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CaseInfoServiceImpl implements CaseInfoService {
    @Autowired
    private CaseInfoMapper caseMapper;

    @Override
    public CaseInfo getCaseInfo(Integer id) {
        CaseInfo caseInfo = caseMapper.getCaseInfo(id);
        if (Objects.nonNull(caseInfo))
            return caseInfo;
        else
            return null;
    }

    @Override
    public List<CaseInfo> getCaseInfoListByProjectId(Integer projectId) {
        return caseMapper.getCaseInfoListByProjectId(projectId);
    }

    @Override
    public JSONObject getCaseInfoListWithProject(Integer offset, Integer limit, Integer projectId) {
        JSONObject result = new JSONObject();
        Page<Object> page = PageHelper.offsetPage(offset, limit);
        List<CaseInfoWithProject> caseInfoWithProjectList = caseMapper.getCaseInfoListWithProject(projectId);

        JSONArray caseJsonArray = JSONArray.parseArray(JSON.toJSONString(caseInfoWithProjectList));
        result.put("total", page.getTotal());
        result.put("rows", caseJsonArray);
        return result;
    }

    @Override
    public Integer insertCaseInfo(CaseInfo caseInfo) {
        return caseMapper.insertCaseInfo(caseInfo);
    }

    @Override
    public Integer updateCaseInfo(CaseInfo caseInfo) {
        return caseMapper.updateCaseInfo(caseInfo);
    }

    @Override
    public Integer updateCaseInfoRunBatTimes(Integer caseId) {
        return caseMapper.updateCaseInfoRunBatTimes(caseId);
    }

    @Override
    public Integer deleteCaseInfo(Integer id) {
        return caseMapper.deleteCaseInfo(id);
    }

    @Override
    public Integer deleteCaseInfoByProjectId(Integer projectId) {
        return caseMapper.deleteCaseInfoByProjectId(projectId);
    }
}
