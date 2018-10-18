package com.archer.source.mapper;

import com.archer.source.domain.entity.CaseInfo;
import com.archer.source.domain.linked.CaseInfoWithProject;
import com.archer.source.mapper.provider.CaseInfoProvider;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface CaseInfoMapper {
    @Select("SELECT * FROM CaseInfo WHERE id = #{id}")
    CaseInfo getCaseInfo(@Param("id") Integer id);

    @SelectProvider(type = CaseInfoProvider.class, method = "getCaseInfoListByProjectId")
    List<CaseInfo> getCaseInfoListByProjectId(@Param("projectId") Integer projectId);

    @SelectProvider(type = CaseInfoProvider.class, method = "getCaseWithProjectList")
    List<CaseInfoWithProject> getCaseInfoListWithProject(@Param("projectId") Integer projectId);

    @InsertProvider(type = CaseInfoProvider.class, method = "insertCase")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insertCaseInfo(CaseInfo caseInfo);

    @UpdateProvider(type = CaseInfoProvider.class, method = "updateCase")
    Integer updateCaseInfo(CaseInfo caseInfo);

    @Update("UPDATE CaseInfo SET runBatTimes = runBatTimes + 1 WHERE id = #{id}")
    Integer updateCaseInfoRunBatTimes(@Param("id") Integer id);

    @DeleteProvider(type = CaseInfoProvider.class, method = "deleteCase")
    Integer deleteCaseInfo(Integer id);

    @Delete("DELETE FROM CaseInfo WHERE projectId = #{projectId}")
    Integer deleteCaseInfoByProjectId(@Param("projectId") Integer projectId);
}
