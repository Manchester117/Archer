package com.archer.source.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.archer.source.domain.entity.*;
import com.archer.source.service.CaseApiInfoService;
import com.archer.source.service.CaseVerifyInfoService;
import com.archer.source.service.CorrelateInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Objects;

@Controller
public class CaseApiInfoController {
    @Autowired
    private CaseApiInfoService caseApiService;
    @Autowired
    private CaseVerifyInfoService caseVerifyService;
    @Autowired
    private CorrelateInfoService correlateService;

    /**
     * @description: 将待修改的接口信息传回到模态框(待参数关联)
     * @author:      Zhao.Peng
     * @date:        2018/9/4
     * @time:        16:15
     * @param:       apiId - 接口ID
     * @return:      整合了验证点和关联参数JSON
     */
    @PostMapping(path = "/caseApiModifyInModal")
    public @ResponseBody JSONObject caseApiModifyInModal(@RequestParam("caseApiId") Integer caseApiId) {
        CaseApiInfo caseApiInfo = caseApiService.getCaseApiInfo(caseApiId);
        List<CaseVerifyInfo> caseVerifyInfoList = caseVerifyService.getCaseVerifyInfoByCaseApiId(caseApiId);
        List<CorrelateInfo> correlateInfoList = correlateService.getCorrelateInfoByCaseApiId(caseApiId);

        JSONObject caseApiVerifyCorrelateJson = JSONObject.parseObject(JSON.toJSONString(caseApiInfo));
        JSONArray caseVerifyInfoJson = JSONArray.parseArray(JSON.toJSONString(caseVerifyInfoList));
        JSONArray correlateInfoJson = JSONArray.parseArray(JSON.toJSONString(correlateInfoList));

        caseApiVerifyCorrelateJson.put("caseVerifyList", caseVerifyInfoJson);
        caseApiVerifyCorrelateJson.put("correlateList", correlateInfoJson);

        return caseApiVerifyCorrelateJson;
    }

    @PostMapping(path = "/modifyCaseApiSubmit")
    public @ResponseBody JSONObject modifyCaseApiSubmit(@RequestBody String requestParams) {
        JSONObject caseApiJson = JSONObject.parseObject(requestParams);

        CaseApiInfo caseApiInfo = new CaseApiInfo();
        caseApiInfo.setId(caseApiJson.getInteger("caseApiId"));
        caseApiInfo.setApiId(caseApiJson.getInteger("apiId"));
        caseApiInfo.setApiName(caseApiJson.getString("apiName"));
        caseApiInfo.setProtocol(caseApiJson.getInteger("protocol"));
        caseApiInfo.setUrl(caseApiJson.getString("url"));
        caseApiInfo.setMethod(caseApiJson.getInteger("method"));
        caseApiInfo.setHeader(caseApiJson.getString("header"));
        caseApiInfo.setBody(caseApiJson.getString("body"));
        caseApiInfo.setIsMock(caseApiJson.getInteger("isMock"));
        caseApiInfo.setWaitMillis(Objects.nonNull(caseApiJson.getInteger("waitMillis")) ? caseApiJson.getInteger("waitMillis") : 0);
        caseApiInfo.setServiceId(caseApiJson.getInteger("serviceId"));
        caseApiInfo.setCaseId(caseApiJson.getInteger("caseId"));

        Integer updateApiFlag = caseApiService.updateCaseApiInfo(caseApiInfo);

        // 更新多个验证点
        JSONArray caseVerifyInfoArray = caseApiJson.getJSONArray("verifyInfoList");
        caseVerifyService.deleteCaseVerifyInfoByCaseApiId(caseApiInfo.getId());                                 // 删除原有的验证点
        if (!Objects.equals(caseVerifyInfoArray.size(), 0)) {                                                // 如果有验证点
            List<CaseVerifyInfo> caseVerifyInfoList = caseVerifyInfoArray.toJavaList(CaseVerifyInfo.class);
            for (CaseVerifyInfo caseVerifyInfo : caseVerifyInfoList) {
                caseVerifyInfo.setCaseApiId(caseApiInfo.getId());                                               // 设置验证点ID
                caseVerifyService.insertCaseVerifyInfo(caseVerifyInfo);                                         // 直接插入新的验证点
            }
        }

        // 更新多个关联参数
        JSONArray correlateInfoArray = caseApiJson.getJSONArray("correlateInfoList");
        correlateService.deleteCorrelateInfoByCaseApiId(caseApiInfo.getId());                                       // 删除原有关联参数
        if (!Objects.equals(correlateInfoArray.size(), 0)) {                                                 // 如果有关联参数
            List<CorrelateInfo> correlateInfoList = correlateInfoArray.toJavaList(CorrelateInfo.class);
            for (CorrelateInfo correlateInfo : correlateInfoList) {
                correlateInfo.setCaseApiId(caseApiInfo.getId());                                                // 设置关联参数ID
                correlateService.insertCorrelateInfo(correlateInfo);                                            // 直接插入新的关联参数
            }
        }

        JSONObject modifyMessage = new JSONObject();
        modifyMessage.put("caseApiId", caseApiInfo.getId());
        modifyMessage.put("updateApiFlag", updateApiFlag);
        return modifyMessage;
    }
}
