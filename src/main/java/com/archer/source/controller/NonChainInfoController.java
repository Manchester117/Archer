package com.archer.source.controller;

import com.alibaba.fastjson.JSONObject;
import com.archer.source.service.ApiInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class NonChainInfoController {
    @Autowired
    private ApiInfoService apiService;

    @GetMapping(path = "/nonChainApiList")
    public String nonChainApiList() {
        return "nonChainApiList";
    }

    @PostMapping(path = "/getNonChainApiList")
    public @ResponseBody JSONObject getNonChainApiList(@RequestBody String requestParams) {
        JSONObject apiInfoJson = JSONObject.parseObject(requestParams);
        Integer offset = apiInfoJson.getInteger("offset");
        Integer limit = apiInfoJson.getInteger("limit");
        String apiName = apiInfoJson.getString("apiName");

        return apiService.getApiInfoWithServiceList(offset, limit, apiName, 0);
    }
}
