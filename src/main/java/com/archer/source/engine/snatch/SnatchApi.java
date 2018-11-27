package com.archer.source.engine.snatch;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.archer.source.domain.entity.ApiInfo;
import com.archer.source.domain.entity.CaseApiInfo;
import com.archer.source.engine.except.ArcherException;
import com.archer.source.service.ApiInfoService;
import com.archer.source.service.CaseApiInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
public class SnatchApi {
    @Autowired
    private ApiInfoService apiService;
    @Autowired
    private CaseApiInfoService caseApiService;

    public JSONObject getSwaggerApiList(String swaggerApiUrl) throws ArcherException {
        if (!swaggerApiUrl.startsWith("http://"))
            swaggerApiUrl += "http://";
        // 设置请求Swagger页面的HttpClient
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 设置请求超时时间
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setConnectTimeout(2000).setSocketTimeout(5000).build();
        HttpGet httpGet = new HttpGet(swaggerApiUrl);
        httpGet.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        String entityContent = null;
        JSONObject respJson = null;
        try {
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            entityContent = EntityUtils.toString(entity);
            respJson = JSONObject.parseObject(entityContent);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ArcherException(StringUtils.join(swaggerApiUrl, "|", e.getMessage()));
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return respJson;
    }

    public List<ApiInfo> setSwaggerApiInfo(JSONObject respJson) {
        JSONObject apiPaths = respJson.getJSONObject("paths");                             // 从SwaggerUI的接口JSON中拿到所有的接口

        String protocol = String.valueOf(respJson.getJSONArray("schemes").get(0));     // 暂时都以HTTP的形式导入

        List<ApiInfo> newApiInfoList = new ArrayList<>();
        for (Map.Entry<String, Object> pathsEntry: apiPaths.entrySet()) {
            ApiInfo apiInfo = new ApiInfo();
            // 设置URL
            apiInfo.setUrl(pathsEntry.getKey());
            // 设置请求协议
            if (Objects.equals(protocol, "http"))
                apiInfo.setProtocol(1);
            else if (Objects.equals(protocol, "https"))
                apiInfo.setProtocol(2);

            String apiItemString = JSONObject.toJSONString(pathsEntry.getValue());
            JSONObject apiItems = JSONObject.parseObject(apiItemString);

            for (Map.Entry<String, Object> attrItem: apiItems.entrySet()) {
                // 设置请求方式
                if (Objects.equals(attrItem.getKey(), "get"))
                    apiInfo.setMethod(1);
                else if (Objects.equals(attrItem.getKey(), "post"))
                    apiInfo.setMethod(2);
                else if (Objects.equals(attrItem.getKey(), "put"))
                    apiInfo.setMethod(3);
                else if (Objects.equals(attrItem.getKey(), "delete"))
                    apiInfo.setMethod(4);

                String apiAttrString = JSONObject.toJSONString(attrItem.getValue());
                JSONObject apiAttrs = JSONObject.parseObject(apiAttrString);

                // 设置apiName
                String summary = apiAttrs.getString("summary");
                if (Objects.nonNull(summary))
                    apiInfo.setApiName(summary);
                else
                    apiInfo.setApiName("");

                // 设置createTime
                apiInfo.setCreateTime(new Date());
                // 设置是否Mock
                apiInfo.setIsMock(0);

                this.setHeaderAndParameter(apiAttrs, apiInfo);
            }
            newApiInfoList.add(apiInfo);
        }
        return newApiInfoList;
    }

    private void setHeaderAndParameter(JSONObject apiAttrs, ApiInfo apiInfo) {
        JSONArray parameters = apiAttrs.getJSONArray("parameters");
        JSONObject apiParams = new JSONObject();
        JSONObject headerItem = new JSONObject();
        if (Objects.nonNull(parameters)) {
            for (int i = 0; i < parameters.size(); ++i) {
                // 设置Header中的Content-Type以及请求参数
                JSONObject parameter = parameters.getJSONObject(i);
                // 获取参数的使用范围(表单/请求头)
                String in = parameter.getString("in");
                // 获取参数类型
                String type = parameter.getString("type");

                if (Objects.equals(in, "query") || Objects.equals(in, "body")) {
                    // 如果是请求参数
                    if (Objects.nonNull(type)) {
                        // 如果参数中type不为空(key-value参数)
                        if (Objects.equals(apiInfo.getMethod(), 2) || Objects.equals(apiInfo.getMethod(), 3))
                            // 如果是POST请求,且为表单提交
                            headerItem.put("Content-Type", "application/x-www-form-urlencoded");
                        // 设置表单参数
                        String paramName = parameter.getString("name");
                        apiParams.put(paramName, "");
                    }
                    // JSON格式的POST
                    Object schema = parameter.get("schema");
                    if (Objects.nonNull(schema)) {
                        // 如果参数中schema不为空(JSON参数)
                        if (Objects.equals(apiInfo.getMethod(), 2) || Objects.equals(apiInfo.getMethod(), 3))
                            // 如果请求参数的key为schema,且value不为空,则代表使用JSON方式提交
                            headerItem.put("Content-Type", "application/json");
                        // 暂不做JSON参数导入
                    }
                } else if (Objects.equals(in, "header")){
                    // 如果是请求头的属性
                    String headerItemName = parameter.getString("name");
                    headerItem.put(headerItemName, "");
                }
                // 如果导入的参数属性是path则不做任何处理
            }
            // 设置请求头
            apiInfo.setHeader(headerItem.toJSONString());

            if (Objects.nonNull(apiInfo.getHeader()))
                // 如果不是GET请求
                apiInfo.setBody(apiParams.toJSONString());
            else
                // 如果是GET请求
                apiInfo.setBody(apiParams.toJSONString());
        } else {
            apiInfo.setHeader("{}");
            apiInfo.setBody("{}");
        }
    }

    public JSONObject importApi(Integer serviceId, List<ApiInfo> newApiInfoList) {
        Integer insertCount = 0;
        Integer updateCount = 0;

        List<ApiInfo> originalApiInfoList = apiService.getApiInfoListByServiceId(serviceId);
        if (Objects.isNull(originalApiInfoList)) {
            // 如果服务下没有接口
            for (ApiInfo apiInfo: newApiInfoList) {
                // 给即将导入的接口设置serviceId
                apiInfo.setServiceId(serviceId);
                insertCount += apiService.insertApiInfo(apiInfo);
            }
        } else {
            // 为了更新接口的时候同时要获取全部的用例接口,方便进行用例接口更新
            List<CaseApiInfo> caseApiInfoList = caseApiService.getCaseApiInfoByCaseId(null);
            // 如果服务下存在接口
            List<String> repeatUrlList = new ArrayList<>();
            for (ApiInfo newApiInfo: newApiInfoList) {
                for (ApiInfo oriApiInfo: originalApiInfoList) {
                    if (Objects.equals(oriApiInfo.getUrl(), newApiInfo.getUrl())) {
                        // 提前做一个接口记录
                        repeatUrlList.add(newApiInfo.getUrl());
                        // 先搞定有重复的接口
                        oriApiInfo.setProtocol(newApiInfo.getProtocol());
                        oriApiInfo.setMethod(newApiInfo.getMethod());
                        // 如果请求是POST或者PUT,则提取当前请求的Content-Type,对header进行更新
                        if (Objects.equals(oriApiInfo.getMethod(), 2) || Objects.equals(oriApiInfo.getMethod(), 3)) {
                            JSONObject oriHeader = JSONObject.parseObject(oriApiInfo.getHeader());
                            JSONObject newHeader = JSONObject.parseObject(newApiInfo.getHeader());
                            oriHeader.put("Content-Type", String.valueOf(newHeader.get("Content-Type")));
                            oriApiInfo.setHeader(oriHeader.toJSONString());
                        }
                        // 除了JSON的body不做替换,其他表单参数都做替换
                        JSONObject oriHeader = JSONObject.parseObject(oriApiInfo.getHeader());
                        if (!Objects.equals(oriHeader.getString("Content-Type"), "application/json"))
                            oriApiInfo.setBody(newApiInfo.getBody());
                        // 进行用例接口更新
                        this.updateCaseApiInfo(oriApiInfo, caseApiInfoList);
                        // 更新接口个数累加
                        updateCount += apiService.updateApiInfo(oriApiInfo);
                    }
                }
            }
            // 给每一个新添加的接口设置serviceId
            for (ApiInfo newApiInfo: newApiInfoList) {
                if (!repeatUrlList.contains(newApiInfo.getUrl())) {
                    newApiInfo.setServiceId(serviceId);
                    insertCount += apiService.insertApiInfo(newApiInfo);
                }
            }
        }
        JSONObject importMsg = new JSONObject();
        importMsg.put("insertCount", insertCount);
        importMsg.put("updateCount", updateCount);
        return importMsg;
    }

    private void updateCaseApiInfo(ApiInfo oriApiInfo, List<CaseApiInfo> caseApiInfoList) {
        for (CaseApiInfo caseApiInfo: caseApiInfoList) {
            if (Objects.equals(caseApiInfo.getApiId(), oriApiInfo.getId())) {
                caseApiInfo.setUrl(oriApiInfo.getUrl());
                caseApiInfo.setProtocol(oriApiInfo.getProtocol());
                caseApiInfo.setMethod(oriApiInfo.getMethod());
                caseApiInfo.setHeader(oriApiInfo.getHeader());
                caseApiInfo.setBody(oriApiInfo.getBody());
                // 进行用例中的接口更新
                caseApiService.updateCaseApiInfo(caseApiInfo);
            }
        }
    }
}
