package com.archer.source.mapper;

import com.archer.source.domain.entity.CaseApiInfo;
import com.archer.source.mapper.provider.CaseApiInfoProvider;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface CaseApiInfoMapper {
    @Select("SELECT * FROM CaseApiInfo WHERE id = #{id}")
    CaseApiInfo getCaseApiInfo(@Param("id") Integer id);

    @Select("SELECT * FROM CaseApiInfo WHERE caseId = #{caseId} AND apiId = #{apiId}")
    CaseApiInfo getSingleCaseApiInfo(@Param("caseId") Integer caseId, @Param("apiId") Integer apiId);

    @SelectProvider(type = CaseApiInfoProvider.class, method = "getCaseApiInfoByCaseId")
    List<CaseApiInfo> getCaseApiInfoByCaseId(@Param("caseId") Integer caseId);

    @InsertProvider(type = CaseApiInfoProvider.class, method = "insertCaseApiInfo")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insertCaseApiInfo(CaseApiInfo caseApiInfo);

    @UpdateProvider(type = CaseApiInfoProvider.class, method = "updateCaseApiInfo")
    Integer updateCaseApiInfo(CaseApiInfo caseApiInfo);

    @Delete("DELETE FROM CaseApiInfo WHERE id = #{id}")
    Integer deleteCaseApiInfo(@Param("id") Integer id);

    @Delete("DELETE FROM CaseApiInfo WHERE caseId = #{caseId}")
    Integer deleteCaseApiInfoByCaseId(@Param("caseId") Integer caseId);
}
