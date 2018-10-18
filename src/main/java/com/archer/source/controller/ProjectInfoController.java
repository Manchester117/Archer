package com.archer.source.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.archer.source.domain.entity.*;
import com.archer.source.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
public class ProjectInfoController {
    @Autowired
    private ProjectInfoService projectService;
    @Autowired
    private ServiceInfoService service;
    @Autowired
    private ApiInfoService apiService;
    @Autowired
    private VerifyInfoService verifyService;
    @Autowired
    private CorrelateInfoService correlateService;
    @Autowired
    private RespInfoService respService;
    @Autowired
    private CaseInfoService caseService;
    @Autowired
    private CaseApiInfoService caseApiService;
    @Autowired
    private CaseVerifyInfoService caseVerifyService;

    @GetMapping(path = "/")
    public String index() {
        return "projectList";
    }

    @PostMapping(path = "/getProjectList")
    public @ResponseBody JSONObject getProjectList(@RequestBody String requestParams) {
        JSONObject projectListJson = JSONObject.parseObject(requestParams);

        Integer offset = projectListJson.getInteger("offset");
        Integer limit = projectListJson.getInteger("limit");
        String projectName = projectListJson.getString("projectName");

        return projectService.getProjectInfoByPageList(offset, limit, projectName);
    }

    @PostMapping(path = "/getProjectInfo")
    public @ResponseBody JSONObject getProjectInfo(@RequestParam("projectId") Integer projectId) {
        ProjectInfo projectInfo = projectService.getProjectInfo(projectId);
        String projectInfoJson = JSONObject.toJSONString(projectInfo);
        return JSON.parseObject(projectInfoJson);
    }

    @PostMapping(path = "/getAllProjectList")
    public @ResponseBody JSONArray getAllProjectList() {
        return projectService.getAllProjectInfo();
    }

    @PostMapping(path = "/createProject")
    public @ResponseBody JSONObject createProject(@RequestBody String requestParams) {
        JSONObject projectInfoJson = JSONObject.parseObject(requestParams);

        ProjectInfo projectInfo = new ProjectInfo();
        projectInfo.setProjectName(projectInfoJson.getString("projectName"));
        projectInfo.setCreateTime(new Date());
        projectInfo.setDescription(projectInfoJson.getString("description"));

        Integer insertFlag = projectService.insertProjectInfo(projectInfo);
        JSONObject insertMessage = new JSONObject();
        insertMessage.put("flag", insertFlag);
        return insertMessage;
    }

    @PostMapping(path = "/modifyProject")
    public @ResponseBody JSONObject modifyProject(@RequestBody String requestParams) {
        JSONObject projectInfoJson = JSONObject.parseObject(requestParams);

        ProjectInfo projectInfo = new ProjectInfo();
        projectInfo.setId(projectInfoJson.getInteger("id"));
        projectInfo.setProjectName(projectInfoJson.getString("projectName"));
        projectInfo.setDescription(projectInfoJson.getString("description"));

        Integer updateFlag = projectService.updateProjectInfo(projectInfo);
        JSONObject updateMessage = new JSONObject();
        updateMessage.put("flag", updateFlag);
        return updateMessage;
    }

    @PostMapping(path = "/deleteProject")
    public @ResponseBody JSONObject deleteProject(@RequestParam("projectId") Integer projectId) {
        // 找到当前项目下的所有服务
        List<ServiceInfo> serviceInfoList = service.getServiceInfoListByProjectId(projectId);

        for (ServiceInfo serviceInfo: serviceInfoList) {
            Integer serviceId = serviceInfo.getId();
            // 找到当前服务下的所有接口
            List<ApiInfo> apiInfoList = apiService.getApiInfoListByServiceId(serviceId);
            // 删除当前服务下的所有接口以及相关联的信息
            for (ApiInfo apiInfo : apiInfoList) {
                verifyService.deleteVerifyInfoByApiId(apiInfo.getId());
                respService.deleteRespInfoByApiId(apiInfo.getId());
            }
            apiService.deleteApiInfoByServiceId(serviceId);
            // 删除当前服务
            service.deleteServiceInfo(serviceId);
        }
        List<CaseInfo> caseInfoList = caseService.getCaseInfoListByProjectId(projectId);
        for (CaseInfo caseInfo: caseInfoList) {
            // 获取原有用例的所有接口ID,用于删除验证点和关联值
            List<CaseApiInfo> caseApiInfoList = caseApiService.getCaseApiInfoByCaseId(caseInfo.getId());
            for (CaseApiInfo caseApiInfo: caseApiInfoList) {
                caseVerifyService.deleteCaseVerifyInfoByCaseApiId(caseApiInfo.getId());
                correlateService.deleteCorrelateInfoByCaseApiId(caseApiInfo.getId());
            }
            // 将原有用例的所有接口删除
            caseApiService.deleteCaseApiInfoByCaseId(caseInfo.getId());
        }
        // 删除当前项目下的所有用例
        caseService.deleteCaseInfoByProjectId(projectId);

        Integer deleteFlag = projectService.deleteProjectInfo(projectId);
        JSONObject deleteMessage = new JSONObject();
        deleteMessage.put("flag", deleteFlag);
        return deleteMessage;
    }
}
