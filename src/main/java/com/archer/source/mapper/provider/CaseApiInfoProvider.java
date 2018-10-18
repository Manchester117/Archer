package com.archer.source.mapper.provider;

import com.archer.source.domain.entity.CaseApiInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;
import org.apache.logging.log4j.util.Strings;

import java.util.Map;
import java.util.Objects;

public class CaseApiInfoProvider {
    public String getCaseApiInfoByCaseId(Map<String, Object> param) {
        return new SQL(){
            {
                String searchCondition = null;
                if (Objects.nonNull(param.get("caseId"))) {
                    String caseId = param.get("caseId").toString();
                    if (Strings.isNotBlank(caseId))
                        searchCondition = StringUtils.join("caseId = ", caseId);
                }
                SELECT("id", "apiId", "apiName", "protocol", "url", "method", "header", "body", "createTime", "waitMillis", "isMock", "serviceId", "caseId");
                FROM("CaseApiInfo");
                if (Objects.nonNull(searchCondition))
                    WHERE(searchCondition);
            }

        }.toString();
    }

    public String insertCaseApiInfo(CaseApiInfo caseApiInfo) {
        return new SQL() {
            {
                INSERT_INTO("CaseApiInfo");
                if (Objects.nonNull(caseApiInfo.getApiId()))
                    VALUES("apiId", "#{apiId}");
                if (Objects.nonNull(caseApiInfo.getApiName()))
                    VALUES("apiName", "#{apiName}");
                if (Objects.nonNull(caseApiInfo.getProtocol()))
                    VALUES("protocol", "#{protocol}");
                if (Objects.nonNull(caseApiInfo.getUrl()))
                    VALUES("url", "#{url}");
                if (Objects.nonNull(caseApiInfo.getMethod()))
                    VALUES("method", "#{method}");
                if (Objects.nonNull(caseApiInfo.getHeader()))
                    VALUES("header", "#{header}");
                if (Objects.nonNull(caseApiInfo.getBody()))
                    VALUES("body", "#{body}");
                if (Objects.nonNull(caseApiInfo.getCreateTime()))
                    VALUES("createTime", "#{createTime}");
                if (Objects.nonNull(caseApiInfo.getIsMock()))
                    VALUES("isMock", "#{isMock}");
                if (Objects.nonNull(caseApiInfo.getWaitMillis()))
                    VALUES("waitMillis", "#{waitMillis}");
                if (Objects.nonNull(caseApiInfo.getServiceId()))
                    VALUES("serviceId", "#{serviceId}");
                if (Objects.nonNull(caseApiInfo.getCaseId()))
                    VALUES("caseId", "#{caseId}");

            }
        }.toString();
    }

    public String updateCaseApiInfo(CaseApiInfo caseApiInfo) {
        return new SQL() {
            {
                UPDATE("CaseApiInfo");
                if (Objects.nonNull(caseApiInfo.getApiId()))
                    SET("apiId = #{apiId}");
                if (Objects.nonNull(caseApiInfo.getApiName()))
                    SET("apiName = #{apiName}");
                if (Objects.nonNull(caseApiInfo.getProtocol()))
                    SET("protocol = #{protocol}");
                if (Objects.nonNull(caseApiInfo.getUrl()))
                    SET("url = #{url}");
                if (Objects.nonNull(caseApiInfo.getMethod()))
                    SET("method = #{method}");
                if (Objects.nonNull(caseApiInfo.getHeader()))
                    SET("header = #{header}");
                if (Objects.nonNull(caseApiInfo.getBody()))
                    SET("body = #{body}");
                if (Objects.nonNull(caseApiInfo.getCreateTime()))
                    SET("createTime = #{createTime}");
                if (Objects.nonNull(caseApiInfo.getIsMock()))
                    SET("isMock = #{isMock}");
                if (Objects.nonNull(caseApiInfo.getServiceId()))
                    SET("serviceId = #{serviceId}");
                if (Objects.nonNull(caseApiInfo.getWaitMillis()))
                    SET("waitMillis = #{waitMillis}");
                if (Objects.nonNull(caseApiInfo.getCaseId()))
                    SET("caseId = #{caseId}");
                if (Objects.nonNull(caseApiInfo.getId()))
                    WHERE("id = #{id}");
            }
        }.toString();
    }
}
