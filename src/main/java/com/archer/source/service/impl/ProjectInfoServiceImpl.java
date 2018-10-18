package com.archer.source.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.archer.source.domain.entity.ProjectInfo;
import com.archer.source.mapper.ProjectInfoMapper;
import com.archer.source.service.ProjectInfoService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectInfoServiceImpl implements ProjectInfoService{
    @Autowired
    private ProjectInfoMapper projectMapper;

    @Override
    public ProjectInfo getProjectInfo(Integer id) {
        return projectMapper.getProjectInfo(id);
    }

    @Override
    public JSONObject getProjectInfoByPageList(Integer offset, Integer limit, String projectName) {
        JSONObject result = new JSONObject();
        Page<Object> page = PageHelper.offsetPage(offset, limit);
        List<ProjectInfo> projectInfoList = projectMapper.getProjectInfoList(projectName);
        JSONArray projectJsonArray = JSONArray.parseArray(JSON.toJSONString(projectInfoList));
        result.put("total", page.getTotal());
        result.put("rows", projectJsonArray);
        return result;
    }

    @Override
    public JSONArray getAllProjectInfo() {
        List<ProjectInfo> projectInfoList = projectMapper.getProjectInfoList(null);
        return JSONArray.parseArray(JSON.toJSONString(projectInfoList));
    }

    @Override
    public Integer insertProjectInfo(ProjectInfo projectInfo) {
        return projectMapper.insertProjectInfo(projectInfo);
    }

    @Override
    public Integer updateProjectInfo(ProjectInfo projectInfo) {
        return projectMapper.updateProjectInfo(projectInfo);
    }

    @Override
    public Integer deleteProjectInfo(Integer id) {
        return projectMapper.deleteProjectInfo(id);
    }
}
