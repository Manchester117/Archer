package com.archer.source.mapper.provider;

import com.archer.source.domain.entity.ProjectInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;
import java.util.Objects;

public class ProjectInfoProvider {
    public String getProjectList(Map<String, Object> params) {
        return new SQL() {
            {
                String searchCondition = null;
                if (Objects.nonNull(params.get("projectName"))) {
                    String projectNameKeyword = params.get("projectName").toString();
                    if (StringUtils.isNoneBlank(projectNameKeyword))
                        // 经过两次判空后,如果输入的搜索关键字有内容,则进行条件语句的拼接
                        searchCondition = StringUtils.join("projectName LIKE '%", projectNameKeyword, "%'");
                }

                SELECT("id, projectName, createTime, description");
                FROM("ProjectInfo");
                if (Objects.nonNull(searchCondition))
                    // 如果WHERE条件不为空则插入
                    WHERE(searchCondition);
                ORDER_BY("createTime DESC");
            }
        }.toString();
    }

    public String insertProject(ProjectInfo projectInfo) {
        return new SQL() {
            {
                INSERT_INTO("ProjectInfo");
                if (Objects.nonNull(projectInfo.getProjectName()))
                    VALUES("projectName", "#{projectName}");
                if (Objects.nonNull(projectInfo.getCreateTime()))
                    VALUES("createTime", "#{createTime}");
                if (Objects.nonNull(projectInfo.getDescription()))
                    VALUES("description", "#{description}");
            }
        }.toString();
    }

    public String updateProject(ProjectInfo projectInfo) {
        return new SQL() {
            {
                UPDATE("ProjectInfo");
                if (Objects.nonNull(projectInfo.getProjectName()))
                    SET("projectName = #{projectName}");
                if (Objects.nonNull(projectInfo.getCreateTime()))
                    SET("createTime = #{createTime}");
                if (Objects.nonNull(projectInfo.getDescription()))
                    SET("description = #{description}");
                if (Objects.nonNull(projectInfo.getId()))
                    WHERE("id = #{id}");
            }
        }.toString();
    }

    public String deleteProject(Integer id) {
        return new SQL() {
            {
                DELETE_FROM("ProjectInfo");
                if (Objects.nonNull(id))
                    WHERE("id = #{id}");
            }
        }.toString();
    }
}
