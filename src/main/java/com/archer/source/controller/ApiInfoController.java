package com.archer.source.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.archer.source.domain.entity.ApiInfo;
import com.archer.source.domain.entity.RespInfo;
import com.archer.source.domain.entity.ServiceInfo;
import com.archer.source.domain.entity.VerifyInfo;
import com.archer.source.engine.ExecutionComponent;
import com.archer.source.engine.except.ArcherException;
import com.archer.source.engine.snatch.SnatchApi;
import com.archer.source.service.*;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;


@Controller
public class ApiInfoController {
    @Autowired
    private ServiceInfoService service;
    @Autowired
    private ApiInfoService apiService;
    @Autowired
    private VerifyInfoService verifyService;
    @Autowired
    private RespInfoService respService;
    @Autowired
    private ExecutionComponent execute;
    @Autowired
    private SnatchApi snatchApi;

    @ApiOperation(value = "进入接口列表页", notes = "根据serviceId获取接口列表")
    @ApiImplicitParam(name = "serviceId", value = "服务ID", required = false, dataType = "Integer", paramType = "")
    @GetMapping(path = "/apiList")
    public String apiList(Model model, @RequestParam(value = "serviceId", required = false) Integer serviceId) {
        model.addAttribute("serviceId", serviceId);
        return "apiList";
    }

    @PostMapping(path = "/getApiInfo")
    public @ResponseBody JSONObject getApiInfo(@RequestParam("apiId") Integer apiId) {
        ApiInfo apiInfo = apiService.getApiInfo(apiId);
        String apiInfoJson = JSONObject.toJSONString(apiInfo);
        return JSON.parseObject(apiInfoJson);
    }

    @PostMapping(path = "/getApiWithServiceList")
    public @ResponseBody JSONObject getApiWithServiceList(@RequestBody String requestParams) {
        JSONObject apiInfoJson = JSONObject.parseObject(requestParams);
        Integer offset = apiInfoJson.getInteger("offset");
        Integer limit = apiInfoJson.getInteger("limit");
        String apiName = apiInfoJson.getString("apiName");
        Integer serviceId = apiInfoJson.getInteger("serviceId");

        return apiService.getApiInfoWithServiceList(offset, limit, apiName, serviceId);
    }

    @GetMapping(path = "/apiAddPage")
    public String apiAddPage(Model model, @RequestParam("serviceId") Integer serviceId){
        model.addAttribute("serviceId", serviceId);
        return "apiAddPage";
    }
    
    /** 
    * @description: 创建接口,包含接口基本信息和验证点(apiAddPage.js)
    * @author:      Zhao.Peng
    * @date:        2018/9/10 
    * @time:        14:06 
    * @param:       接口信息+验证点的JSON
    * @return:      
    */
    @PostMapping(path = "/createApiAndVerifyInfo")
    public @ResponseBody JSONObject createApiAndVerifyInfo(@RequestBody String requestParams) {
        JSONObject apiAndVerifyJson = JSONObject.parseObject(requestParams);

        ApiInfo apiInfo = new ApiInfo();
        apiInfo.setApiName(apiAndVerifyJson.getString("apiName"));
        apiInfo.setProtocol(apiAndVerifyJson.getInteger("protocol"));
        apiInfo.setUrl(apiAndVerifyJson.getString("url"));
        apiInfo.setMethod(apiAndVerifyJson.getInteger("method"));
        apiInfo.setHeader(apiAndVerifyJson.getString("header"));
        apiInfo.setBody(apiAndVerifyJson.getString("body"));
        apiInfo.setCreateTime(new Date());
        apiInfo.setIsMock(apiAndVerifyJson.getInteger("isMock"));
        apiInfo.setServiceId(apiAndVerifyJson.getInteger("serviceId"));

        Integer insertApiSuccessFlag = apiService.insertApiInfo(apiInfo);
        Integer apiId = apiInfo.getId();

        // 存储多个验证点
        JSONArray verifyInfoArray = apiAndVerifyJson.getJSONArray("verifyInfoList");
        List<VerifyInfo> verifyInfoList = verifyInfoArray.toJavaList(VerifyInfo.class);
        Integer insertVerifySuccessFlag = 1;
        for (VerifyInfo verifyInfo: verifyInfoList) {
            verifyInfo.setApiId(apiId);
            Integer insertVerifyFlag = verifyService.insertVerifyInfo(verifyInfo);
            if (Objects.equals(insertVerifyFlag, 0))
                insertVerifySuccessFlag = 0;
        }

        JSONObject insertMessage = new JSONObject();
        insertMessage.put("insertApiSuccessFlag", insertApiSuccessFlag);            // 接口插入成功标志
        insertMessage.put("apiId", apiId);                                          // 接口主键
        insertMessage.put("insertVerifySuccessFlag", insertVerifySuccessFlag);      // 接口验证点插入成功标志
        return insertMessage;
    }
    
    /** 
    * @description: 将待修改的接口信息传回到apiModPage
    * @author:      Zhao.Peng 
    * @date:        2018/9/4 
    * @time:        16:05 
    * @param:       apiId - 接口ID 
    * @return:      apiModPage - 修改接口页面
    */
    @GetMapping(path = "/apiModifyPage")
    public String apiModifyPage(Model model, @RequestParam("apiId") Integer apiId) {
        ApiInfo apiInfo = apiService.getApiInfo(apiId);
        List<VerifyInfo> verifyInfoList = verifyService.getVerifyInfoListByApiId(apiId);

        JSONObject apiAndVerifyInfoJson = JSONObject.parseObject(JSON.toJSONString(apiInfo));
        JSONArray verifyInfoJson = JSONArray.parseArray(JSON.toJSONString(verifyInfoList));
        apiAndVerifyInfoJson.put("verifyList", verifyInfoJson);

        // 如果projectId为空则根据ApiInfo获取projectId
        Integer apiServiceId = apiInfo.getServiceId();
        ServiceInfo serviceInfo = service.getServiceInfo(apiServiceId);
        Integer projectId = serviceInfo.getProjectId();

        model.addAttribute("projectId", projectId);
        model.addAttribute("apiAndVerifyInfo", apiAndVerifyInfoJson);
        return "apiModPage";
    }
    
    /** 
    * @description: 提交修改后的接口信息(apiModPage.js)
    * @author:      Zhao.Peng
    * @date:        2018/10/10 
    * @time:        18:43 
    * @param:       修改后的接口信息
    * @return:      修改是否成功的标志位
    */
    @PostMapping(path = "/modifyApiSubmit")
    public @ResponseBody JSONObject modifyApiSubmit(@RequestBody String requestParams) {
        JSONObject apiAndVerifyJson = JSONObject.parseObject(requestParams);

        ApiInfo apiInfo = new ApiInfo();
        apiInfo.setId(apiAndVerifyJson.getInteger("id"));
        apiInfo.setApiName(apiAndVerifyJson.getString("apiName"));
        apiInfo.setProtocol(apiAndVerifyJson.getInteger("protocol"));
        apiInfo.setUrl(apiAndVerifyJson.getString("url"));
        apiInfo.setMethod(apiAndVerifyJson.getInteger("method"));
        apiInfo.setHeader(apiAndVerifyJson.getString("header"));
        apiInfo.setBody(apiAndVerifyJson.getString("body"));
        apiInfo.setIsMock(apiAndVerifyJson.getInteger("isMock"));
        apiInfo.setServiceId(apiAndVerifyJson.getInteger("serviceId"));
        Integer updateApiFlag = apiService.updateApiInfo(apiInfo);

        // 更新多个验证点
        JSONArray verifyInfoArray = apiAndVerifyJson.getJSONArray("verifyInfoList");
        verifyService.deleteVerifyInfoByApiId(apiInfo.getId());                                             // 删除原有的验证点
        if (!Objects.equals(verifyInfoArray.size(), 0)) {                                                   // 如果有验证点
            List<VerifyInfo> verifyInfoList = verifyInfoArray.toJavaList(VerifyInfo.class);
            for (VerifyInfo verifyInfo : verifyInfoList) {
                verifyInfo.setApiId(apiInfo.getId());                                                       // 设置验证点ID
                verifyService.insertVerifyInfo(verifyInfo);                                                 // 直接插入新的验证点
            }
        }

        JSONObject modifyMessage = new JSONObject();
        modifyMessage.put("apiId", apiInfo.getId());
        modifyMessage.put("updateApiFlag", updateApiFlag);
        return modifyMessage;
    }

    @PostMapping(path = "/modifyApiService")
    public @ResponseBody JSONObject modifyApiService(@RequestParam("apiId") Integer apiId, @RequestParam("serviceId") Integer serviceId) {
        ApiInfo apiInfo = apiService.getApiInfo(apiId);
        apiInfo.setServiceId(serviceId);
        Integer updateApiFlag = apiService.updateApiInfo(apiInfo);

        JSONObject modifyMessage = new JSONObject();
        modifyMessage.put("updateApiFlag", updateApiFlag);
        return modifyMessage;
    }
    
    /** 
    * @description: 删除接口的操作(apiList.js)
    * @author:      Zhao.Peng 
    * @date:        2018/10/11 
    * @time:        10:26 
    * @param:       apiId - 接口ID
    * @return:      返回删除标志位
    */
    @PostMapping(path = "/deleteApiAndVerifyInfo")
    public @ResponseBody JSONObject deleteApiAndVerifyInfo(@RequestParam("apiId") Integer apiId) {
        Integer deleteVerifyFlag = verifyService.deleteVerifyInfoByApiId(apiId);        // 删除此接口的验证方式
        Integer deleteRespFlag = respService.deleteRespInfoByApiId(apiId);              // 删除此接口的响应信息
        Integer deleteApiFlag = apiService.deleteApiInfo(apiId);                        // 删除此接口

        JSONObject deleteMessage = new JSONObject();
        deleteMessage.put("deleteApiFlag", deleteApiFlag);
        deleteMessage.put("deleteVerifyFlag", deleteVerifyFlag);
        deleteMessage.put("deleteRespFlag", deleteRespFlag);

        return deleteMessage;
    }
    
    /** 
    * @description: 单接口运行(apiModPage.js)
    * @author:      Zhao.Peng 
    * @date:        2018/10/10 
    * @time:        18:46 
    * @param:       apiId - 接口ID 
    * @return:      返回接口运行结果
    */
    @PostMapping(path = "/runApi")
    public @ResponseBody JSONObject runApi(@RequestParam("apiId") Integer apiId,
                                           @RequestParam("hostsId") Integer hostsId) throws ArcherException {
        // 执行请求.并将响应和验证写入到数据库
        execute.apiRunner(apiId, hostsId);
        // 从DB中取出接口信息/响应信息/验证信息
        ApiInfo apiInfo = apiService.getApiInfo(apiId);
        RespInfo respInfo = respService.getLastRespInfo(apiId);
        List<VerifyInfo> verifyInfoList = verifyService.getVerifyInfoListByApiId(apiId);

        JSONObject apiInfoJson = JSONObject.parseObject(JSON.toJSONString(apiInfo));
        JSONObject respInfoJson = JSONObject.parseObject(JSON.toJSONString(respInfo));
        JSONArray verifyInfoListJson = JSONArray.parseArray(JSON.toJSONString(verifyInfoList));

        JSONObject resultInfoJson = new JSONObject();
        resultInfoJson.put("apiInfo", apiInfoJson);
        resultInfoJson.put("respInfo", respInfoJson);
        resultInfoJson.put("verifyInfoList", verifyInfoListJson);

        return resultInfoJson;
    }

    @PostMapping(path = "/importApi")
    public @ResponseBody JSONObject importApi(@RequestParam("serviceId") Integer serviceId,
                                              @RequestParam("swaggerApiUrl") String swaggerApiUrl) throws ArcherException {
        JSONObject importApiInfoJson = snatchApi.getSwaggerApiList(swaggerApiUrl);
        List<ApiInfo> importApiInfoList = snatchApi.setSwaggerApiInfo(importApiInfoJson);
        return snatchApi.importApi(serviceId, importApiInfoList);
    }
}
