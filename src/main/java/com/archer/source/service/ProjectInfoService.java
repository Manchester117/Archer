package com.archer.source.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.archer.source.domain.entity.ProjectInfo;

public interface ProjectInfoService {
    ProjectInfo getProjectInfo(Integer id);

    JSONObject getProjectInfoByPageList(Integer offset, Integer limit, String projectName);

    JSONArray getAllProjectInfo();

    Integer insertProjectInfo(ProjectInfo projectInfo);

    Integer updateProjectInfo(ProjectInfo projectInfo);

    Integer deleteProjectInfo(Integer id);
}
