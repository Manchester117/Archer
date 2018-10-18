package com.archer.source.mapper;

import com.archer.source.domain.entity.ProjectInfo;
import com.archer.source.mapper.provider.ProjectInfoProvider;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ProjectInfoMapper {
    @Select("SELECT * FROM ProjectInfo WHERE id = #{id}")
    ProjectInfo getProjectInfo(@Param("id") Integer id);

    @SelectProvider(type = ProjectInfoProvider.class, method = "getProjectList")
    List<ProjectInfo> getProjectInfoList(@Param("projectName") String projectName);

    @InsertProvider(type = ProjectInfoProvider.class, method = "insertProject")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insertProjectInfo(ProjectInfo projectInfo);

    @UpdateProvider(type = ProjectInfoProvider.class, method = "updateProject")
    Integer updateProjectInfo(ProjectInfo projectInfo);

    @DeleteProvider(type = ProjectInfoProvider.class, method = "deleteProject")
    Integer deleteProjectInfo(Integer id);
}
