package com.archer.source.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.archer.source.domain.entity.*;
import com.archer.source.engine.ExecutionComponent;
import com.archer.source.engine.except.ArcherException;
import com.archer.source.service.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Controller
public class CaseInfoController {
    @Autowired
    private CaseInfoService caseService;
    @Autowired
    private CaseApiInfoService caseApiService;
    @Autowired
    private CaseVerifyInfoService caseVerifyService;
    @Autowired
    private CorrelateInfoService correlateService;
    @Autowired
    private ServiceInfoService service;
    @Autowired
    private ApiInfoService apiService;
    @Autowired
    private VerifyInfoService verifyService;
    @Autowired
    private ExecutionComponent execution;

    @GetMapping(path = "/caseConfig")
    public String caseConfig() {
        return "caseConfig";
    }
    
    /** 
    * @description: 获取用例实体(caseConfig.js)
    * @author:      Zhao.Peng 
    * @date:        2018/8/27 
    * @time:        18:16 
    * @param:       caseId - 用例ID 
    * @return:      用例实体(JSON)
    */
    @PostMapping(path = "/getCaseInfo")
    public @ResponseBody JSONObject getCaseInfo(@RequestParam("caseId") Integer caseId) {
        CaseInfo caseInfo = caseService.getCaseInfo(caseId);
        String caseInfoJson = JSONObject.toJSONString(caseInfo);
        return JSON.parseObject(caseInfoJson);
    }
    
    /** 
    * @description: 用例列表展示--用例信息+项目名称(caseConfig.js)
    * @author:      Zhao.Peng 
    * @date:        2018/9/3 
    * @time:        15:12 
    * @param:       requestParams - 需要配合BootstrapTable插件来进行列表展示 
    * @return:      返回用例列表
    */
    @PostMapping(path = "/getCaseListWithProject")
    public @ResponseBody JSONObject getCaseListWithProject(@RequestBody String requestParams) {
        JSONObject caseInfoJson = JSONObject.parseObject(requestParams);

        Integer offset = caseInfoJson.getInteger("offset");
        Integer limit = caseInfoJson.getInteger("limit");
        Integer projectId = caseInfoJson.getInteger("projectId");

        return caseService.getCaseInfoListWithProject(offset, limit, projectId);
    }
    
    /** 
    * @description: 创建用例(caseConfig.js)
    * @author:      Zhao.Peng
    * @date:        2018/8/27 
    * @time:        18:15 
    * @param:       用例实体的JSON
    * @return:      用例是否入库成功(JSON)
    */
    @PostMapping(path = "/createCase")
    public @ResponseBody JSONObject createCase(@RequestBody String requestParams) {
        JSONObject caseInfoJson = JSONObject.parseObject(requestParams);

        CaseInfo caseInfo = new CaseInfo();
        caseInfo.setCaseName(caseInfoJson.getString("caseName"));
        caseInfo.setProjectId(caseInfoJson.getInteger("projectId"));

        Integer insertFlag = caseService.insertCaseInfo(caseInfo);
        JSONObject insertMessage = new JSONObject();
        insertMessage.put("flag", insertFlag);
        return insertMessage;
    }

    /**
     * @description: 修改测试用例(caseConfig.js)
     * @author:      Zhao.Peng
     * @date:        2018/9/10
     * @time:        9:40
     * @param:       编辑用例用到的JSON参数
     * @return:      用例是否修改成功
     */
    @PostMapping(path = "/modifyCase")
    public @ResponseBody JSONObject modifyCase(@RequestBody String requestParams) {
        // 获取要修改的Case实体
        JSONObject newCaseInfo = JSONObject.parseObject(requestParams);
        // 拿到CaseID
        Integer caseId = newCaseInfo.getInteger("id");
        CaseInfo oldCaseInfo = caseService.getCaseInfo(caseId);
        // 拿到旧的接口执行顺序
        String oldApiSequence = oldCaseInfo.getApiSequence();
        List<Integer> oldApiIdList = JSONArray.parseArray(oldApiSequence, Integer.class);
        // 拿到新的接口执行顺序
        String newApiSequence = newCaseInfo.getString("apiSequence");
        List<Integer> newApiIdList = JSONArray.parseArray(newApiSequence, Integer.class);

        JSONObject updateMsg = new JSONObject();
        Integer updateFlag = 0;

        if (Objects.nonNull(newApiIdList) && Objects.nonNull(oldApiIdList)) {
            // 先求出新旧API列表的合集
            Set<Integer> intersectionSet = Sets.intersection(Sets.newHashSet(newApiIdList), Sets.newHashSet(oldApiIdList));
            List<Integer> intersectionList = Lists.newArrayList(intersectionSet);
            // 对比旧的API列表,取差集
            Set<Integer> differenceOldSet = Sets.difference(Sets.newHashSet(oldApiIdList), Sets.newHashSet(intersectionList));
            List<Integer> differenceOldList = Lists.newArrayList(differenceOldSet);

            // 删掉旧的API列表中的接口以及关联参数和验证点
            for (Integer oldApiId: differenceOldList) {
                // 先通过caseId和oldApiId获取用例列表中旧的差集的接口
                CaseApiInfo oldCaseApiInfo = caseApiService.getSingleCaseApiInfo(caseId, oldApiId);
                // 对旧的差集接口进行删除操作
                caseVerifyService.deleteCaseVerifyInfoByCaseApiId(oldCaseApiInfo.getId());
                correlateService.deleteCorrelateInfoByCaseApiId(oldCaseApiInfo.getId());
                caseApiService.deleteCaseApiInfo(oldCaseApiInfo.getId());
            }

            // 对比新的API列表,取差集
            Set<Integer> differenceNewSet = Sets.difference(Sets.newHashSet(newApiIdList), Sets.newHashSet(intersectionList));
            List<Integer> differenceNewList = Lists.newArrayList(differenceNewSet);

            // 将新的差集接口写入到DB
            for (Integer newApiId: differenceNewList) {
                ApiInfo apiInfo = apiService.getApiInfo(newApiId);
                CaseApiInfo newCaseApiInfo = new CaseApiInfo();
                newCaseApiInfo.setApiId(apiInfo.getId());
                newCaseApiInfo.setApiName(apiInfo.getApiName());
                newCaseApiInfo.setProtocol(apiInfo.getProtocol());
                newCaseApiInfo.setUrl(apiInfo.getUrl());
                newCaseApiInfo.setMethod(apiInfo.getMethod());
                newCaseApiInfo.setHeader(apiInfo.getHeader());
                newCaseApiInfo.setBody(apiInfo.getBody());
                newCaseApiInfo.setCreateTime(apiInfo.getCreateTime());
                newCaseApiInfo.setIsMock(apiInfo.getIsMock());
                newCaseApiInfo.setServiceId(apiInfo.getServiceId());
                newCaseApiInfo.setCaseId(caseId);

                caseApiService.insertCaseApiInfo(newCaseApiInfo);
                Integer newCaseApiInfoId = newCaseApiInfo.getId();

                List<VerifyInfo> newApiVerifyInfoList = verifyService.getVerifyInfoListByApiId(apiInfo.getId());
                for (VerifyInfo newApiVerifyInfo: newApiVerifyInfoList) {
                    CaseVerifyInfo caseVerifyInfo = new CaseVerifyInfo();
                    caseVerifyInfo.setVerifyName(newApiVerifyInfo.getVerifyName());
                    caseVerifyInfo.setVerifyType(newApiVerifyInfo.getVerifyType());
                    caseVerifyInfo.setExpression(newApiVerifyInfo.getExpression());
                    caseVerifyInfo.setExpectValue(newApiVerifyInfo.getExpectValue());
                    caseVerifyInfo.setActualValue(newApiVerifyInfo.getActualValue());
                    caseVerifyInfo.setIsSuccess(newApiVerifyInfo.getIsSuccess());
                    caseVerifyInfo.setCaseApiId(newCaseApiInfoId);
                    caseVerifyService.insertCaseVerifyInfo(caseVerifyInfo);
                }
            }
            oldCaseInfo.setCaseName(newCaseInfo.getString("caseName"));
            oldCaseInfo.setApiSequence(newApiSequence);
            updateFlag = caseService.updateCaseInfo(oldCaseInfo);
        }
        updateMsg.put("flag", updateFlag);
        return updateMsg;
    }

    /** 
    * @description: 删除测试用例(caseConfig.js)
    * @author:      Zhao.Peng 
    * @date:        2018/9/10 
    * @time:        10:08 
    * @param:       caseId - 用例ID 
    * @return:      返回是否删除成功的JSON
    */
    @PostMapping(path = "/deleteCase")
    public @ResponseBody JSONObject deleteCase(@RequestParam("caseId") Integer caseId) {
        List<CaseApiInfo> caseApiInfoList = caseApiService.getCaseApiInfoByCaseId(caseId);
        for (CaseApiInfo caseApiInfo: caseApiInfoList) {
            Integer caseApiId = caseApiInfo.getId();
            caseVerifyService.deleteCaseVerifyInfoByCaseApiId(caseApiId);
            correlateService.deleteCorrelateInfoByCaseApiId(caseApiId);
        }
        caseApiService.deleteCaseApiInfoByCaseId(caseId);
        Integer deleteFlag = caseService.deleteCaseInfo(caseId);
        JSONObject deleteMessage = new JSONObject();
        deleteMessage.put("flag", deleteFlag);
        return deleteMessage;
    }
    
    /** 
    * @description: 修改用例之前获取所属项目的所有接口和用例中已经包含的接口(caseConfig.js)
    * @author:      Zhao.Peng 
    * @date:        2018/9/10 
    * @time:        10:10 
    * @param:       caseId - 用例ID 
    * @return:      返回项目的所有接口和用例包含的接口
    */
    @PostMapping(path = "/getCurrentCaseApiWithProject")
    public @ResponseBody JSONObject getCurrentCaseApiWithProject(@RequestParam("caseId") Integer caseId) {
        CaseInfo caseInfo = caseService.getCaseInfo(caseId);

        List<ApiInfo> apiInServiceList = new ArrayList<>();                                     // 当前项目下的所有接口
        List<ApiInfo> apiSequenceList = new ArrayList<>();                                      // CaseInfo当中的所有接口
        JSONArray apiIdSequence = JSON.parseArray(caseInfo.getApiSequence());
        // 拿到用例所属项目的全部接口和当前用例的全部接口
        JSONArray caseApiJson = null;
        JSONArray projectApiJson = null;
        // 拿到用例所属项目的所有服务
        List<ServiceInfo> serviceInfoList = service.getServiceInfoListByProjectId(caseInfo.getProjectId());
        // 在遍历所有服务,将服务中的接口合并到一个List当中
        for (ServiceInfo serviceInfo: serviceInfoList) {
            List<ApiInfo> apiInService = apiService.getApiInfoListByServiceId(serviceInfo.getId());
            apiInServiceList.addAll(apiInService);
        }

        if (Objects.nonNull(apiIdSequence)) {                                                   // 如果当前用例中有接口
            // 找出当前用例中的所有接口,并放置到apiSequenceList中
            List<Integer> apiIdInCaseList = apiIdSequence.toJavaList(Integer.class);
            for (Integer apiId : apiIdInCaseList)
                apiSequenceList.add(apiService.getApiInfo(apiId));
            // 将用例中的接口转成JSON
            caseApiJson = JSONArray.parseArray(JSON.toJSONString(apiSequenceList));
        }

        apiInServiceList.removeAll(apiSequenceList);                                            // 在项目里剔除掉用例中已经存在的接口
        projectApiJson = JSONArray.parseArray(JSON.toJSONString(apiInServiceList));

        JSONObject caseApiAndProjectApi = new JSONObject();
        caseApiAndProjectApi.put("projectApiJson", projectApiJson);
        caseApiAndProjectApi.put("caseApiJson", caseApiJson);

        return caseApiAndProjectApi;
    }
    
    /** 
    * @description: 根据用例ID获取用例中的接口列表(caseConfig.js)
    * @author:      Zhao.Peng
    * @date:        2018/10/10 
    * @time:        18:37 
    * @param:       分页所需要的参数
    * @return:      用例下的接口列表
    */
    @PostMapping(path = "/getCaseApiInfoByCaseId")
    public @ResponseBody JSONObject getCaseApiInfoByCaseId(@RequestBody String requestParams) {
        JSONObject apiInCaseParams = JSONObject.parseObject(requestParams);

        Integer offset = apiInCaseParams.getInteger("offset");
        Integer limit = apiInCaseParams.getInteger("limit");
        Integer caseId = apiInCaseParams.getInteger("caseId");

        JSONObject caseApiJsonObject = caseApiService.getCaseApiInfoByCaseId(offset, limit, caseId);
        List<CaseApiInfo> caseApiInfoList = caseApiJsonObject.getJSONArray("rows").toJavaList(CaseApiInfo.class);

        CaseInfo caseInfo = caseService.getCaseInfo(caseId);
        String apiSequence = caseInfo.getApiSequence();
        List<Integer> apiSequenceList = JSONArray.parseArray(apiSequence, Integer.class);

        // 对用例接口进行重排序
        JSONArray caseApiArray = new JSONArray();
        for (Integer apiId: apiSequenceList) {
            for (CaseApiInfo caseApiInfo: caseApiInfoList) {
                if (Objects.equals(caseApiInfo.getApiId(), apiId))
                    caseApiArray.add(caseApiInfo);
            }
        }
        caseApiJsonObject.put("rows", caseApiArray);
        return caseApiJsonObject;
    }

    /** 
    * @description: 运行测试用例(caseConfig.js) 
    * @author:      Zhao.Peng 
    * @date:        2018/10/10 
    * @time:        18:32 
    * @param:       caseId    - 用例ID
    * @param:       sessionId - WebSocket连接的会话ID
    * @param:       hostsId   - DNS地址记录
    * @return:      result    - 返回运行结束的标志位
    */
    @PostMapping(path = "/runCase")
    public @ResponseBody JSONObject runCase(@RequestParam("caseId") Integer caseId,
                                            @RequestParam("sessionId") String sessionId,
                                            @RequestParam("hostsId") Integer hostId) throws ArcherException {
        execution.caseRunner(caseId, sessionId, hostId);
        JSONObject result = new JSONObject();
        result.put("flag", "RunEnd");
        return result;
    }
}
