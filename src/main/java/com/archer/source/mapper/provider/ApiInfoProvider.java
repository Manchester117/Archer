package com.archer.source.mapper.provider;

import com.archer.source.domain.entity.ApiInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;
import org.apache.logging.log4j.util.Strings;

import java.util.Map;
import java.util.Objects;

public class ApiInfoProvider {
    public String getApiList(Map<String, Object> param) {
        return new SQL() {
            {
                String apiNameCondition = null;
                if (Objects.nonNull(param.get("apiName"))) {
                    String apiBaseKeyword = param.get("apiName").toString();
                    if (Strings.isNotBlank(apiBaseKeyword))
                        apiNameCondition = StringUtils.join("apiName LIKE '%", apiBaseKeyword, "%'");
                }

                String serviceIdCondition = null;
                if (Objects.nonNull(param.get("serviceId"))) {
                    String serviceId = param.get("serviceId").toString();
                    if (Strings.isNotBlank(serviceId))
                        serviceIdCondition = StringUtils.join("serviceId = ", serviceId);
                }

                SELECT("id", "apiName", "protocol", "url", "method", "header", "body", "createTime", "isMock", "caseId", "serviceId");
                FROM("ApiInfo");
                if (Objects.nonNull(apiNameCondition)) {
                    WHERE(apiNameCondition);
                }
                if (Objects.nonNull(serviceIdCondition)) {
                    AND();
                    WHERE(serviceIdCondition);
                }
                ORDER_BY("createTime DESC");
            }
        }.toString();
    }

    public String getApiWithServiceList(Map<String, Object> param) {
        return new SQL() {
            {
                String apiNameCondition = null;
                if (Objects.nonNull(param.get("apiName"))) {
                    String apiBaseKeyword = param.get("apiName").toString();
                    if (Strings.isNotBlank(apiBaseKeyword))
                        apiNameCondition = StringUtils.join("apiName LIKE '%", apiBaseKeyword, "%'");
                }

                String serviceIdCondition = null;
                if (Objects.nonNull(param.get("serviceId"))) {
                    String serviceId = param.get("serviceId").toString();
                    if (Strings.isNotBlank(serviceId))
                        serviceIdCondition = StringUtils.join("a.serviceId = ", serviceId);
                }
                SELECT("a.id", "a.apiName", "a.protocol", "a.url", "a.method", "a.createTime", "s.serviceName");
                FROM("ApiInfo a");
                LEFT_OUTER_JOIN("ServiceInfo s ON a.serviceId = s.id");
                if (Objects.nonNull(apiNameCondition)) {
                    AND();
                    WHERE(apiNameCondition);
                }
                if (Objects.nonNull(serviceIdCondition)) {              // 未归档接口的条件
                    AND();
                    WHERE(serviceIdCondition);
                } else {                                                // 已归档接口的条件
                    AND();
                    WHERE("a.serviceId != 0");
                }
                ORDER_BY("a.createTime DESC");
            }
        }.toString();
    }

    public String insertApi(ApiInfo apiInfo) {
        return new SQL() {
            {
                INSERT_INTO("ApiInfo");
                if (Objects.nonNull(apiInfo.getApiName()))
                    VALUES("apiName", "#{apiName}");
                if (Objects.nonNull(apiInfo.getProtocol()))
                    VALUES("protocol", "#{protocol}");
                if (Objects.nonNull(apiInfo.getUrl()))
                    VALUES("url", "#{url}");
                if (Objects.nonNull(apiInfo.getMethod()))
                    VALUES("method", "#{method}");
                if (Objects.nonNull(apiInfo.getHeader()))
                    VALUES("header", "#{header}");
                if (Objects.nonNull(apiInfo.getBody()))
                    VALUES("body", "#{body}");
                if (Objects.nonNull(apiInfo.getCreateTime()))
                    VALUES("createTime", "#{createTime}");
                if (Objects.nonNull(apiInfo.getIsMock()))
                    VALUES("isMock", "#{isMock}");
                if (Objects.nonNull(apiInfo.getServiceId()))
                    VALUES("serviceId", "#{serviceId}");
            }
        }.toString();
    }

    public String updateApi(ApiInfo apiInfo) {
        return new SQL() {
            {
                UPDATE("ApiInfo");
                if (Objects.nonNull(apiInfo.getApiName()))
                    SET("apiName = #{apiName}");
                if (Objects.nonNull(apiInfo.getProtocol()))
                    SET("protocol = #{protocol}");
                if (Objects.nonNull(apiInfo.getUrl()))
                    SET("url = #{url}");
                if (Objects.nonNull(apiInfo.getMethod()))
                    SET("method = #{method}");
                if (Objects.nonNull(apiInfo.getHeader()))
                    SET("header = #{header}");
                if (Objects.nonNull(apiInfo.getBody()))
                    SET("body = #{body}");
                if (Objects.nonNull(apiInfo.getCreateTime()))
                    SET("createTime = #{createTime}");
                if (Objects.nonNull(apiInfo.getIsMock()))
                    SET("isMock = #{isMock}");
                if (Objects.nonNull(apiInfo.getServiceId()))
                    SET("serviceId = #{serviceId}");
                if (Objects.nonNull(apiInfo.getId()))
                    WHERE("id = #{id}");
            }
        }.toString();
    }

    public String deleteApi(Integer id) {
        return new SQL() {
            {
                DELETE_FROM("ApiInfo");
                if (Objects.nonNull(id))
                    WHERE("id = #{id}");
            }
        }.toString();
    }
}
