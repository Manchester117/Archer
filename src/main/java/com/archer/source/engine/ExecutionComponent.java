package com.archer.source.engine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.archer.source.domain.entity.*;
import com.archer.source.engine.except.ArcherException;
import com.archer.source.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class ExecutionComponent {
    @Autowired
    private RequestComponent requestComponent;
    @Autowired
    private ResponseComponent responseComponent;
    @Autowired
    private CaseApiInfoService caseApiService;
    @Autowired
    private CaseInfoService caseService;
    @Autowired
    private ServiceInfoService service;
    @Autowired
    private ApiInfoService apiService;
    @Autowired
    private ParamCorrelateComponent paramCorrComponent;
    @Autowired
    private HostsInfoService hostsService;

    private List<CorrelateInfo> correlateInfoList;              // 关联参数的实体列表

    /** 
    * @description: 接口执行方法 
    * @author:      Zhao.Peng 
    * @date:        2018/8/21 
    * @time:        20:23 
    * @param:       apiInfo     -   接口实体
    * @param:       isCase      -   是否以用例模式运行
    * @param:       caseApiId   -   接口所属的用例ID
    * @param:       hostsItem   -   Host列表
    * @param:       sessionId   -   WebSocket的会话ID
    * @param:       hostsId     -   发送请求的Hosts配置
    * @return:
    */
    private void runEngine(ApiInfo apiInfo,
                           Boolean isCase,
                           Integer caseApiId,
                           List<HashMap> hostsItem,
                           String sessionId,
                           Integer runBatTimes) throws ArcherException {

        // 获取当前执行接口的BaseUrl
        Integer serviceId = apiInfo.getServiceId();
        ServiceInfo serviceInfo = service.getServiceInfo(serviceId);
        String baseUrl = serviceInfo.getBaseUrl();

        // 执行请求获得请求响应
        RespInfo respInfo = requestComponent.requestWrapper(apiInfo, baseUrl, hostsItem);
        respInfo.setRunBatTimes(runBatTimes);
        // 如果是以用例模式运行
        if (isCase) {
            // 这里为了和单接口的运行兼容,做了重新的封装.将apiInfo + caseApiId = CaseApiInfo
            CaseApiInfo caseApiInfo = new CaseApiInfo();
            caseApiInfo.setId(caseApiId);
            caseApiInfo.setApiId(apiInfo.getId());
            caseApiInfo.setApiName(apiInfo.getApiName());
            caseApiInfo.setProtocol(apiInfo.getProtocol());
            caseApiInfo.setUrl(apiInfo.getUrl());
            caseApiInfo.setMethod(apiInfo.getMethod());
            caseApiInfo.setHeader(apiInfo.getHeader());
            caseApiInfo.setBody(apiInfo.getBody());
            caseApiInfo.setCreateTime(apiInfo.getCreateTime());
            caseApiInfo.setIsMock(apiInfo.getIsMock());
            caseApiInfo.setServiceId(apiInfo.getServiceId());
            // 返回参数关联的实体列表(用于后面的参数关联)
            correlateInfoList = paramCorrComponent.extractCorrValue(caseApiInfo, respInfo);
            // 返回响应正文(用于后面的关联参数)
            responseComponent.responseWrapper(respInfo, caseApiId, sessionId);
        } else {
            responseComponent.responseWrapper(respInfo);
        }
    }
    
    /** 
    * @description: 以单接口模式运行 
    * @author:      Zhao.Peng 
    * @date:        2018/8/21 
    * @time:        20:25 
    * @param:       apiId   - 接口ID
    * @param:       hostsId - 发送请求的Hosts配置
    * @return:
    */
    public void apiRunner(Integer apiId, Integer hostsId) throws ArcherException {
        HostsInfo hostsInfo = hostsService.getHostsInfo(hostsId);
        String hostsItemRange = null;
        List<HashMap> hostsItem = null;
        ApiInfo apiInfo = null;
        try {
            hostsItemRange = hostsInfo.getHostsItem();
            hostsItem = JSON.parseArray(hostsItemRange, HashMap.class);       // 直接从字符串转成List
            apiInfo = apiService.getApiInfo(apiId);
            // Boolean.FALSE - 以单接口的模式运行
            // caseApiId = 0 - 以单接口的模式运行时没有CaseApiId
            this.runEngine(apiInfo, Boolean.FALSE, 0, hostsItem, null, null);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    
    /** 
    * @description: 以用例模式运行 
    * @author:      Zhao.Peng
    * @date:        2018/8/21 
    * @time:        20:22 
    * @param:       caseId      - 用例ID
    * @param:       sessionId   - WebSocket的会话ID
    * @param:       hostsId     - 发送请求的Hosts配置
    * @return:
    */
    public void caseRunner(Integer caseId, String sessionId, Integer hostsId) throws ArcherException {
        // 用例运行前更新用例运行批次
        caseService.updateCaseInfoRunBatTimes(caseId);
        // 获得Hosts配置信息/Case用例信息
        HostsInfo hostsInfo = hostsService.getHostsInfo(hostsId);
        String hostsItemRange = hostsInfo.getHostsItem();
        List<HashMap> hostsItem = JSON.parseArray(hostsItemRange, HashMap.class);       // 直接从字符串转成List

        CaseInfo caseInfo = caseService.getCaseInfo(caseId);
        // 获得本次用例运行批次
        Integer runBatTimes = caseInfo.getRunBatTimes();
        // 获取用例的接口执行顺序
        String apiSequence = caseInfo.getApiSequence();
        List<Integer> apiIdList = JSONArray.parseArray(apiSequence, Integer.class);
        // 执行请求后进行参数关联
        List<CaseApiInfo> caseApiInfoList = caseApiService.getCaseApiInfoByCaseId(caseId);
        // 按照用例中的接口顺序重排接口
        List<CaseApiInfo> runCaseApiInfoList = new ArrayList<>();
        for (Integer apiId: apiIdList) {
            for (CaseApiInfo caseApiInfo: caseApiInfoList) {
                if (Objects.equals(caseApiInfo.getApiId(), apiId))
                    runCaseApiInfoList.add(caseApiInfo);
            }
        }

        for (CaseApiInfo caseApiInfo: runCaseApiInfoList) {
            ApiInfo apiInfo = new ApiInfo();
            apiInfo.setId(caseApiInfo.getApiId());
            apiInfo.setApiName(caseApiInfo.getApiName());
            apiInfo.setProtocol(caseApiInfo.getProtocol());
            apiInfo.setUrl(caseApiInfo.getUrl());
            apiInfo.setMethod(caseApiInfo.getMethod());
            apiInfo.setHeader(caseApiInfo.getHeader());
            apiInfo.setBody(caseApiInfo.getBody());
            apiInfo.setCreateTime(caseApiInfo.getCreateTime());
            apiInfo.setIsMock(caseApiInfo.getIsMock());
            apiInfo.setServiceId(caseApiInfo.getServiceId());
            Integer caseApiId = caseApiInfo.getId();
            Integer waitMillis = caseApiInfo.getWaitMillis();
            if (Objects.nonNull(waitMillis)) {
                try {
                    Thread.sleep(waitMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 执行接口请求,并获得当前接口下的所有关联参数实体(Boolean.TRUE - 以流程用例的模式运行)
            this.runEngine(apiInfo, Boolean.TRUE, caseApiId, hostsItem, sessionId, runBatTimes);
            // 扫描当前用例下的每一个接口,并对关联参数进行替换
            // 对URL中的参数进行替换
            paramCorrComponent.replaceUrlCorrValue(correlateInfoList, caseApiInfoList);
            // 对Post请求中的参数进行替换
            paramCorrComponent.replaceBodyCorrValue(correlateInfoList, caseApiInfoList);
            // 对Header进行参数替换
            paramCorrComponent.replaceHeaderCorrValue(correlateInfoList, caseApiInfoList);
        }
    }
}
