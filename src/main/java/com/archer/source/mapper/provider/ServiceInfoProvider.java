package com.archer.source.mapper.provider;

import com.archer.source.domain.entity.ServiceInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;
import java.util.Objects;

public class ServiceInfoProvider {
    public String getServiceList(Map<String, Object> params) {
        // 返回SQL拼接的SQL语句
        return new SQL() {
            {
                // SQL的WHERE条件
                String projectIdCondition = null;
                if (Objects.nonNull(params.get("projectId"))) {
                    String projectId = params.get("projectId").toString();
                    if (StringUtils.isNoneBlank(projectId))
                        projectIdCondition = StringUtils.join("projectId = ", projectId);
                }
                String searchCondition = null;
                if (Objects.nonNull(params.get("serviceName"))) {
                    String serviceNameKeyword = params.get("serviceName").toString();
                    if (StringUtils.isNoneBlank(serviceNameKeyword))
                        // 经过两次判空后,如果输入的搜索关键字有内容,则进行条件语句的拼接
                        searchCondition = StringUtils.join("serviceName LIKE '%", serviceNameKeyword, "%'");
                }

                SELECT("id, serviceName, baseUrl, version, type, createTime, description");
                FROM("ServiceInfo");
                if (Objects.nonNull(projectIdCondition))
                    WHERE(projectIdCondition);
                if (Objects.nonNull(searchCondition)) {
                    AND();
                    WHERE(searchCondition);             // 如果WHERE条件不为空则插入
                }
                ORDER_BY("createTime DESC");
            }
        }.toString();
    }

    public String insertService(ServiceInfo serviceInfo) {
        return new SQL() {
            {
                INSERT_INTO("ServiceInfo");
                if (Objects.nonNull(serviceInfo.getServiceName()))
                    VALUES("serviceName", "#{serviceName}");
                if (Objects.nonNull(serviceInfo.getBaseUrl()))
                    VALUES("baseUrl", "#{baseUrl}");
                if (Objects.nonNull(serviceInfo.getVersion()))
                    VALUES("version", "#{version}");
                if (Objects.nonNull(serviceInfo.getType()))
                    VALUES("type", "#{type}");
                if (Objects.nonNull(serviceInfo.getCreateTime()))
                    VALUES("createTime", "#{createTime}");
                if (Objects.nonNull(serviceInfo.getDescription()))
                    VALUES("description", "#{description}");
                if (Objects.nonNull(serviceInfo.getProjectId()))
                    VALUES("projectId", "#{projectId}");
            }
        }.toString();
    }

    public String updateService(ServiceInfo serviceInfo) {
        return new SQL() {
            {
                UPDATE("ServiceInfo");
                if (Objects.nonNull(serviceInfo.getServiceName()))
                    SET("serviceName = #{serviceName}");
                if (Objects.nonNull(serviceInfo.getBaseUrl()))
                    SET("baseUrl = #{baseUrl}");
                if (Objects.nonNull(serviceInfo.getVersion()))
                    SET("version = #{version}");
                if (Objects.nonNull(serviceInfo.getType()))
                    SET("type = #{type}");
                if (Objects.nonNull(serviceInfo.getCreateTime()))
                    SET("createTime = #{createTime}");
                if (Objects.nonNull(serviceInfo.getDescription()))
                    SET("description = #{description}");
                if (Objects.nonNull(serviceInfo.getProjectId()))
                    SET("projectId = #{projectId}");
                if (Objects.nonNull(serviceInfo.getId()))
                    WHERE("id = #{id}");
            }
        }.toString();
    }

    public String deleteService(Integer id) {
        return new SQL() {
            {
                DELETE_FROM("ServiceInfo");
                if (Objects.nonNull(id))
                    WHERE("id = #{id}");
            }
        }.toString();
    }
}
