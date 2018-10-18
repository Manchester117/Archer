package com.archer.source.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.archer.source.domain.entity.CaseVerifyInfo;
import com.archer.source.domain.entity.RespInfo;
import com.archer.source.domain.entity.VerifyInfo;
import com.archer.source.engine.except.ArcherException;
import com.archer.source.service.CaseVerifyInfoService;
import com.archer.source.service.RespInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class RespInfoController {
    @Autowired
    private RespInfoService respService;
    @Autowired
    private CaseVerifyInfoService caseVerifyService;

    @GetMapping(path = "/getApiResponse")
    public @ResponseBody JSONObject getApiResponse(@RequestParam("apiId") Integer apiId) {
        RespInfo respInfo = respService.getLastRespInfo(apiId);
        String respInfoJson = JSON.toJSONString(respInfo);
        return JSONObject.parseObject(respInfoJson);
    }
    
    /** 
    * @description: 用例流程运行完毕后,查看每个接口的运行结果(caseConfig.js)
    * @author:      Zhao.Peng
    * @date:        2018/10/10 
    * @time:        17:42 
    * @param:       respId      - 响应ID
    * @param:       caseApiId   - 用例接口ID
    * @return:      用例接口的返回信息
    */
    @PostMapping(path = "/getRespVerify")
    public @ResponseBody JSONObject getRespVerify(@RequestParam("respId") Integer respId, @RequestParam("caseApiId") Integer caseApiId) throws ArcherException {
        // 从DB中取出接口信息/响应信息/验证信息
        RespInfo respInfo = respService.getRespInfo(respId);
        List<CaseVerifyInfo> caseVerifyInfoList = caseVerifyService.getCaseVerifyInfoByCaseApiId(caseApiId);

        JSONObject respInfoJson = JSONObject.parseObject(JSON.toJSONString(respInfo));
        JSONArray verifyInfoListJson = JSONArray.parseArray(JSON.toJSONString(caseVerifyInfoList));

        JSONObject resultInfoJson = new JSONObject();
        resultInfoJson.put("respInfo", respInfoJson);
        resultInfoJson.put("verifyInfoList", verifyInfoListJson);

        return resultInfoJson;
    }
}
