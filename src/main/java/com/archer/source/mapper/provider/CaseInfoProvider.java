package com.archer.source.mapper.provider;

import com.archer.source.domain.entity.CaseInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;
import org.apache.logging.log4j.util.Strings;

import java.util.Map;
import java.util.Objects;

public class CaseInfoProvider {
    public String getCaseInfoListByProjectId(Map<String, Object> param) {
        return new SQL() {
            {
                String searchCondition = null;
                if (Objects.nonNull(param.get("projectId"))) {
                    String projectId = param.get("projectId").toString();
                    if (Strings.isNotBlank(projectId))
                        searchCondition = StringUtils.join("projectId = ", projectId);
                }
                SELECT("id", "caseName", "apiSequence", "runBatTimes", "projectId");
                FROM("caseInfo");
                if (Objects.nonNull(searchCondition))
                    WHERE(searchCondition);
            }
        }.toString();
    }

    public String getCaseWithProjectList(Map<String, Object> param) {
        return new SQL() {
            {
                String searchCondition = null;
                if (Objects.nonNull(param.get("projectId"))) {
                    String projectId = param.get("projectId").toString();
                    if (Strings.isNotBlank(projectId))
                        searchCondition = StringUtils.join("projectId = ", projectId);
                }

                SELECT("c.id", "c.caseName", "c.apiSequence", "c.runBatTimes", "p.projectName");
                FROM("caseInfo c");
                LEFT_OUTER_JOIN("ProjectInfo p ON c.projectId = p.id");
                if (Objects.nonNull(searchCondition))
                    WHERE(searchCondition);
            }
        }.toString();
    }

    public String insertCase(CaseInfo caseInfo) {
        return new SQL() {
            {
                INSERT_INTO("CaseInfo");
                if (Objects.nonNull(caseInfo.getCaseName()))
                    VALUES("caseName", "#{caseName}");
                if (Objects.nonNull(caseInfo.getApiSequence()))
                    VALUES("apiSequence", "#{apiSequence}");
                if (Objects.nonNull(caseInfo.getProjectId()))
                    VALUES("projectId", "#{projectId}");
            }
        }.toString();
    }

    public String updateCase(CaseInfo caseInfo) {
        return new SQL() {
            {
                UPDATE("CaseInfo");
                if (Objects.nonNull(caseInfo.getCaseName()))
                    SET("caseName = #{caseName}");
                if (Objects.nonNull(caseInfo.getApiSequence()))
                    SET("apiSequence = #{apiSequence}");
                if (Objects.nonNull(caseInfo.getProjectId()))
                    SET("projectId = #{projectId}");
                if (Objects.nonNull(caseInfo.getId()))
                    WHERE("id = #{id}");
            }
        }.toString();
    }

    public String deleteCase(Integer id) {
        return new SQL() {
            {
                DELETE_FROM("CaseInfo");
                if (Objects.nonNull(id))
                    WHERE("id = #{id}");
            }
        }.toString();
    }
}
