package com.archer.source.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.archer.source.domain.entity.CaseApiInfo;
import com.archer.source.mapper.CaseApiInfoMapper;
import com.archer.source.service.CaseApiInfoService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CaseApiInfoServiceImpl implements CaseApiInfoService{
    @Autowired
    private CaseApiInfoMapper caseApiMapper;

    @Override
    public CaseApiInfo getCaseApiInfo(Integer id) {
        return caseApiMapper.getCaseApiInfo(id);
    }

    @Override
    public CaseApiInfo getSingleCaseApiInfo(Integer caseId, Integer apiId) {
        return caseApiMapper.getSingleCaseApiInfo(caseId, apiId);
    }

    @Override
    public List<CaseApiInfo> getCaseApiInfoByCaseId(Integer caseId) {
        return caseApiMapper.getCaseApiInfoByCaseId(caseId);
    }

    @Override
    public JSONObject getCaseApiInfoByCaseId(Integer offset, Integer limit, Integer caseId) {
        JSONObject result = new JSONObject();
        Page<Object> page = PageHelper.offsetPage(offset, limit);
        List<CaseApiInfo> caseApiInfoList = caseApiMapper.getCaseApiInfoByCaseId(caseId);

        JSONArray apiJsonArray = JSONArray.parseArray(JSON.toJSONString(caseApiInfoList));
        result.put("total", page.getTotal());
        result.put("rows", apiJsonArray);
        return result;
    }

    @Override
    public Integer insertCaseApiInfo(CaseApiInfo caseApiInfo) {
        return caseApiMapper.insertCaseApiInfo(caseApiInfo);
    }

    @Override
    public Integer updateCaseApiInfo(CaseApiInfo caseApiInfo) {
        return caseApiMapper.updateCaseApiInfo(caseApiInfo);
    }

    @Override
    public Integer deleteCaseApiInfo(Integer id) {
        return caseApiMapper.deleteCaseApiInfo(id);
    }

    @Override
    public Integer deleteCaseApiInfoByCaseId(Integer caseId) {
        return caseApiMapper.deleteCaseApiInfoByCaseId(caseId);
    }
}
