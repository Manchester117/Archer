package com.archer.source.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.archer.source.domain.entity.HostsInfo;
import com.archer.source.service.HostsInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class HostInfoController {
    @Autowired
    private HostsInfoService hostsService;

    @GetMapping(path = "/hostsList")
    public String index() {
        return "hostsList";
    }

    @PostMapping(path = "/getHostsInfo")
    public @ResponseBody JSONObject getHostsInfo(@RequestParam("hostsId") Integer hostsId) {
        HostsInfo hostsInfo = hostsService.getHostsInfo(hostsId);
        String hostsInfoJson = JSONObject.toJSONString(hostsInfo);
        return JSON.parseObject(hostsInfoJson);
    }

    @PostMapping(path = "/getHostsList")
    public @ResponseBody JSONObject getHostsList(@RequestBody String requestParams) {
        JSONObject hostsListJson = JSONObject.parseObject(requestParams);

        Integer offset = hostsListJson.getInteger("offset");
        Integer limit = hostsListJson.getInteger("limit");
        String description = hostsListJson.getString("description");

        return hostsService.getHostsInfoByPageList(offset, limit, description);
    }

    @PostMapping(path = "/getAllHostsList")
    public @ResponseBody JSONArray getAllHostsList() {
        return hostsService.getAllHostsInfoList();
    }

    @PostMapping(path = "/createHosts")
    public @ResponseBody JSONObject createHosts(@RequestBody String requestParams) {
        JSONObject hostsInfoJson = JSONObject.parseObject(requestParams);

        HostsInfo hostsInfo = new HostsInfo();
        hostsInfo.setHostsItem(hostsInfoJson.getString("hostsItem"));
        hostsInfo.setDescription(hostsInfoJson.getString("description"));
        hostsInfo.setStatus(hostsInfoJson.getInteger("status"));

        Integer insertFlag = hostsService.insertHostsInfo(hostsInfo);
        JSONObject insertMessage = new JSONObject();
        insertMessage.put("flag", insertFlag);
        return insertMessage;
    }

    @PostMapping(path = "/modifyHosts")
    public @ResponseBody JSONObject modifyHosts(@RequestBody String requestParams) {
        JSONObject hostsInfoJson = JSONObject.parseObject(requestParams);

        HostsInfo hostsInfo = new HostsInfo();
        hostsInfo.setId(hostsInfoJson.getInteger("id"));
        hostsInfo.setHostsItem(hostsInfoJson.getString("hostsItem"));
        hostsInfo.setDescription(hostsInfoJson.getString("description"));

        Integer updateFlag = hostsService.updateHostsInfo(hostsInfo);
        JSONObject updateMessage = new JSONObject();
        updateMessage.put("flag", updateFlag);
        return updateMessage;
    }

    @PostMapping(path = "/deleteHosts")
    public @ResponseBody JSONObject deleteHosts(@RequestParam("hostsId") Integer hostsId) {
        Integer deleteFlag = hostsService.deleteHostsInfo(hostsId);

        JSONObject deleteMessage = new JSONObject();
        deleteMessage.put("flag", deleteFlag);
        return deleteMessage;
    }
}
