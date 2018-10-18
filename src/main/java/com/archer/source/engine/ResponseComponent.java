package com.archer.source.engine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.archer.source.domain.entity.CaseVerifyInfo;
import com.archer.source.domain.entity.RespInfo;
import com.archer.source.domain.entity.VerifyInfo;
import com.archer.source.engine.except.ArcherException;
import com.archer.source.service.CaseVerifyInfoService;
import com.archer.source.service.RespInfoService;
import com.archer.source.service.VerifyInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class ResponseComponent {
    @Autowired
    private RespInfoService respService;
    @Autowired
    private VerifyInfoService verifyService;
    @Autowired
    private CaseVerifyInfoService caseVerifyService;
    @Autowired
    private ExtractValueComponent extractComponent;
    @Autowired
    private WebSocketServer socket;
    
    /** 
    * @description: response解析的封装方法 
    * @author:      Zhao.Peng
    * @date:        2018/8/27 
    * @time:        17:26
    * @param:       respInfo    - 响应实体
    * @param:       sessionId   - WebSocket的SessionID,用于在用例运行模式下实时向前端传递验证结果
    * @return:      respContent - 响应正文
    */
    public String responseWrapper(RespInfo respInfo) throws ArcherException {
        // 获取请求的响应体
        String respContent = respInfo.getRespContent();
        // 对响应内容进行存储
        respService.insertRespInfo(respInfo);
        // 获取当前接口的所有验证点
        List<VerifyInfo> verifyInfoList = verifyService.getVerifyInfoListByApiId(respInfo.getApiId());
        // 判断接口验证是否全部成功的列表
//        List<Integer> verifyFlagList = new ArrayList<>();
        for (VerifyInfo verifyInfo : verifyInfoList) {
            Integer verifyType = verifyInfo.getVerifyType();
            String expression = verifyInfo.getExpression();
            String expectValue = verifyInfo.getExpectValue();
            String actualValue = extractValue(verifyType, expression, respContent);
            // 获取是否验证成功的标志位
            Integer isSuccess = Objects.equals(expectValue, actualValue) ? 1 : 0;
            verifyInfo.setActualValue(actualValue);
            verifyInfo.setApiId(respInfo.getApiId());
            verifyInfo.setIsSuccess(isSuccess);
//            verifyFlagList.add(isSuccess);                      // verifyFlagList是一个整形List,如果List中有0则代表没有期望值和实际值不匹配
            verifyService.updateVerifyInfo(verifyInfo);         // 将实际结果和是否通过写回到DB
        }
        return respContent;
    }

    public String responseWrapper(RespInfo respInfo, Integer caseApiId, String sessionId) throws ArcherException {
        // 获取请求的响应体
        String respContent = respInfo.getRespContent();
        // 对响应内容进行存储
        respService.insertRespInfo(respInfo);
        // 获取当前接口的所有验证点
        List<CaseVerifyInfo> caseVerifyInfoList = caseVerifyService.getCaseVerifyInfoByCaseApiId(caseApiId);
        // 判断接口验证是否全部成功的列表
        List<Integer> verifyFlagList = new ArrayList<>();
        for (CaseVerifyInfo caseVerifyInfo : caseVerifyInfoList) {
            Integer verifyType = caseVerifyInfo.getVerifyType();
            String expression = caseVerifyInfo.getExpression();
            String expectValue = caseVerifyInfo.getExpectValue();
            String actualValue = extractValue(verifyType, expression, respContent);
            // 获取是否验证成功的标志位
            Integer isSuccess = Objects.equals(expectValue, actualValue) ? 1 : 0;
            caseVerifyInfo.setActualValue(actualValue);
            caseVerifyInfo.setCaseApiId(caseApiId);
            caseVerifyInfo.setIsSuccess(isSuccess);
            verifyFlagList.add(isSuccess);                                  // verifyFlagList是一个整形List,如果List中有0则代表没有期望值和实际值不匹配
            caseVerifyService.updateCaseVerifyInfo(caseVerifyInfo);         // 将实际结果和是否通过写回到DB
        }
        JSONArray caseVerifyJsonList = JSONArray.parseArray(JSON.toJSONString(caseVerifyInfoList));
        if (Objects.nonNull(sessionId)) {
            JSONObject respJson = new JSONObject();
            respJson.put("id", respInfo.getId());
            respJson.put("apiName", respInfo.getApiName());
            respJson.put("url", respInfo.getUrl());
            respJson.put("statusCode", respInfo.getStatusCode());
            respJson.put("respTime", respInfo.getRespTime());
            respJson.put("verifyList", caseVerifyJsonList);
            respJson.put("apiId", respInfo.getApiId());
            respJson.put("caseApiId", caseApiId);

            if (verifyFlagList.contains(0))
                respJson.put("isSuccess", 0);
            else
                respJson.put("isSuccess", 1);

            String respJsonCharSequence = respJson.toJSONString();
            // WebSocket实时返回接口运行结果
            try {
                socket.sendMessageBySessionId(sessionId, respJsonCharSequence);
            } catch (IOException e) {
                log.error("无法正常返回实时结果: {}", socket.getSessionId());
                e.printStackTrace();
            }
        }
        return respContent;
    }
    
    /** 
    * @description: 从响应中抽取验证值 
    * @author:      Zhao.Peng 
    * @date:        2018/10/10 
    * @time:        16:56 
    * @param:       verifyType  - 验证类型
    * @param:       expression  - 抓取表达式
    * @param:       respContent - 响应正文
    * @return:      actualValue - 实际获取到的值
    */
    private String extractValue(Integer verifyType, String expression, String respContent) throws ArcherException {
        String actualValue = null;
        if (Objects.equals(verifyType, 1)) {
            // 如果验证用正则表达式
            actualValue = extractComponent.extractValueByRegex(expression, respContent);
        } else if (Objects.equals(verifyType, 2)) {
            // 如果验证用JsonPath
            actualValue = extractComponent.extractValueByJsonPath(expression, respContent);
        } else if (Objects.equals(verifyType, 3)) {
            // 如果验证用CSS Selector
            actualValue = extractComponent.extractValueBySelector(expression, respContent);
        } else {
            // 如果不是已有的验证方式
            throw new ArcherException("不支持此验证方式");
        }
        return actualValue;
    }
}
