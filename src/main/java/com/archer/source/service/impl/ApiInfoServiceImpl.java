package com.archer.source.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.archer.source.domain.entity.ApiInfo;
import com.archer.source.domain.entity.CaseInfo;
import com.archer.source.domain.linked.ApiInfoWithService;
import com.archer.source.mapper.ApiInfoMapper;
import com.archer.source.mapper.CaseInfoMapper;
import com.archer.source.service.ApiInfoService;
import com.archer.source.service.ServiceInfoService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ApiInfoServiceImpl implements ApiInfoService {
    @Autowired
    private ApiInfoMapper apiMapper;
    @Autowired
    private CaseInfoMapper caseMapper;

    @Override
    public ApiInfo getApiInfo(Integer id) {
        ApiInfo apiInfo = apiMapper.getApiInfo(id);
        if (Objects.nonNull(apiInfo))
            return apiInfo;
        else
            return null;
    }

    @Override
    public JSONObject getApiInfoByPageList(Integer offset, Integer limit, String apiName) {
        JSONObject result = new JSONObject();
        Page<Object> page = PageHelper.offsetPage(offset, limit);
        List<ApiInfo> apiInfoList = apiMapper.getApiInfoListByName(apiName);

        JSONArray apiJsonArray = JSONArray.parseArray(JSON.toJSONString(apiInfoList));
        result.put("total", page.getTotal());
        result.put("rows", apiJsonArray);
        return result;
    }

    @Override
    public JSONObject getApiInfoWithServiceList(Integer offset, Integer limit, String apiName, Integer serviceId) {
        JSONObject result = new JSONObject();
        Page<Object> page = PageHelper.offsetPage(offset, limit);
        List<ApiInfoWithService> apiInfoWithServiceList = apiMapper.getApiWithService(apiName, serviceId);

        JSONArray apiJsonArray = JSONArray.parseArray(JSON.toJSONString(apiInfoWithServiceList));
        result.put("total", page.getTotal());
        result.put("rows", apiJsonArray);
        return result;
    }

    @Override
    public JSONObject getApiInfoWithCaseList(Integer offset, Integer limit, Integer caseId) {
        JSONObject result = new JSONObject();
        Page<Object> page = PageHelper.offsetPage(offset, limit);

        CaseInfo caseInfo = caseMapper.getCaseInfo(caseId);
        JSONArray apiIdSequence = JSON.parseArray(caseInfo.getApiSequence());

        List<Integer> apiIdInCaseList = null;
        List<ApiInfo> apiInfoList = new ArrayList<>();
        if (Objects.nonNull(apiIdSequence)) {
            apiIdInCaseList = apiIdSequence.toJavaList(Integer.class);
            for (Integer apiId: apiIdInCaseList)
                apiInfoList.add(apiMapper.getApiInfo(apiId));
        }

        JSONArray apiJsonArray = JSONArray.parseArray(JSON.toJSONString(apiInfoList));
        result.put("total", page.getTotal());
        result.put("rows", apiJsonArray);
        return result;
    }

    @Override
    public List<ApiInfo> getApiInfoListByName(String apiName) {
        return apiMapper.getApiInfoListByName(apiName);
    }

    @Override
    public List<ApiInfo> getApiInfoListByServiceId(Integer serviceId) {
        return apiMapper.getApiInfoListByServiceId(serviceId);
    }

    @Override
    public Integer insertApiInfo(ApiInfo apiInfo) {
        return apiMapper.insertApiInfo(apiInfo);
    }

    @Override
    public Integer updateApiInfo(ApiInfo apiInfo) {
        return apiMapper.updateApiInfo(apiInfo);
    }

    @Override
    public Integer deleteApiInfo(Integer id) {
        // 这里不能仅是删除接口,如果被删除的接口已经被用在用例当中,则需要将用例中关联的ID删除
        List<CaseInfo> caseInfoList = caseMapper.getCaseInfoListByProjectId(null);
        for (CaseInfo caseInfo: caseInfoList) {
            String apiSequence = caseInfo.getApiSequence();
            JSONArray apiSequenceJson = JSONArray.parseArray(apiSequence);
            if (apiSequenceJson.remove(id)) {
                caseInfo.setApiSequence(JSONArray.toJSONString(apiSequenceJson));
                caseMapper.updateCaseInfo(caseInfo);
            }
        }
        return apiMapper.deleteApiInfo(id);
    }

    @Override
    public Integer deleteApiInfoByServiceId(Integer serviceId) {
        return apiMapper.deleteApiInfoByServiceId(serviceId);
    }
}
