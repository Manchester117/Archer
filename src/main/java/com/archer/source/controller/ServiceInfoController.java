package com.archer.source.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.archer.source.domain.entity.ApiInfo;
import com.archer.source.domain.entity.ProjectInfo;
import com.archer.source.domain.entity.ServiceInfo;
import com.archer.source.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
public class ServiceInfoController {
    @Autowired
    private ProjectInfoService projectService;
    @Autowired
    private ServiceInfoService service;
    @Autowired
    private ApiInfoService apiService;

    @GetMapping(path = "/serviceList")
    public String serviceList(ModelMap modelMap, @RequestParam("projectId") Integer projectId) {
        ProjectInfo projectInfo = projectService.getProjectInfo(projectId);
        modelMap.addAttribute("projectId", projectId);
        modelMap.addAttribute("projectName", projectInfo.getProjectName());
        return "serviceList";
    }

    @PostMapping(path = "/getServiceInfo")
    public @ResponseBody JSONObject getServiceInfo(@RequestParam("serviceId") Integer serviceId) {
        ServiceInfo serviceInfo = service.getServiceInfo(serviceId);
        String serviceInfoJson = JSONObject.toJSONString(serviceInfo);
        return JSON.parseObject(serviceInfoJson);
    }

    @PostMapping(path = "/getServiceListByProjectId")
    public @ResponseBody JSONArray getServiceListByProjectId(@RequestParam(value = "projectId", required = false) Integer projectId) {
        List<ServiceInfo> serviceInfoList = service.getServiceInfoListByProjectId(projectId);
        return JSONArray.parseArray(JSON.toJSONString(serviceInfoList));
    }

    @PostMapping(path = "/getServiceList")
    public @ResponseBody JSONObject getServiceList(@RequestBody String requestParams) {
        JSONObject serviceListJson = JSONObject.parseObject(requestParams);

        Integer offset = serviceListJson.getInteger("offset");
        Integer limit = serviceListJson.getInteger("limit");
        String serviceName = serviceListJson.getString("serviceName");
        Integer projectId = serviceListJson.getInteger("projectId");

        return service.getServiceInfoByPageList(offset, limit, serviceName, projectId);
    }

    @PostMapping(path = "/getAllServiceList")
    public @ResponseBody JSONArray getAllServiceList() {
        return service.getAllServiceInfoList();
    }

    @PostMapping(path = "/createService")
    public @ResponseBody JSONObject createService(@RequestBody String requestParams) {
        JSONObject serviceInfoJson = JSONObject.parseObject(requestParams);

        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setServiceName(serviceInfoJson.getString("serviceName"));
        serviceInfo.setBaseUrl(serviceInfoJson.getString("baseUrl"));
        serviceInfo.setVersion(serviceInfoJson.getString("version"));
        serviceInfo.setType(serviceInfoJson.getInteger("type"));
        serviceInfo.setCreateTime(new Date());
        serviceInfo.setDescription(serviceInfoJson.getString("description"));
        serviceInfo.setProjectId(serviceInfoJson.getInteger("projectId"));

        Integer insertFlag = service.insertServiceInfo(serviceInfo);
        JSONObject insertMessage = new JSONObject();
        insertMessage.put("flag", insertFlag);
        return insertMessage;
    }

    @PostMapping(path = "/modifyService")
    public @ResponseBody JSONObject modifyService(@RequestBody String requestParams) {
        JSONObject serviceInfoJson = JSONObject.parseObject(requestParams);

        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setId(serviceInfoJson.getInteger("id"));
        serviceInfo.setServiceName(serviceInfoJson.getString("serviceName"));
        serviceInfo.setBaseUrl(serviceInfoJson.getString("baseUrl"));
        serviceInfo.setVersion(serviceInfoJson.getString("version"));
        serviceInfo.setType(serviceInfoJson.getInteger("type"));
        serviceInfo.setDescription(serviceInfoJson.getString("description"));

        Integer updateFlag = service.updateServiceInfo(serviceInfo);
        JSONObject updateMessage = new JSONObject();
        updateMessage.put("flag", updateFlag);
        return updateMessage;
    }

    @PostMapping(path = "/deleteService")
    public @ResponseBody JSONObject deleteService(@RequestParam("serviceId") Integer serviceId) {
        // 找到当前服务下的所有接口
        List<ApiInfo> apiInfoList = apiService.getApiInfoListByServiceId(serviceId);

        // 将接口关联的ServiceId置为0(作为没有关联任何服务的接口)
        for (ApiInfo apiInfo: apiInfoList) {
            apiInfo.setServiceId(0);
            apiService.updateApiInfo(apiInfo);
        }
        // 删除当前服务
        Integer deleteFlag = service.deleteServiceInfo(serviceId);
        JSONObject deleteMessage = new JSONObject();
        deleteMessage.put("flag", deleteFlag);
        return deleteMessage;
    }
}
