package com.archer.source.mapper;

import com.archer.source.domain.entity.CaseVerifyInfo;
import com.archer.source.mapper.provider.CaseVerifyInfoProvider;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface CaseVerifyInfoMapper {
    @Select("SELECT * FROM CaseVerifyInfo WHERE id = #{id}")
    CaseVerifyInfo getCaseVerifyInfo(@Param("id") Integer id);

    @Select("SELECT * FROM CaseVerifyInfo WHERE caseApiId = #{caseApiId}")
    List<CaseVerifyInfo> getCaseVerifyInfoByCaseApiId(@Param("caseApiId") Integer caseApiId);

    @InsertProvider(type = CaseVerifyInfoProvider.class, method = "insertCaseVerify")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insertCaseVerifyInfo(CaseVerifyInfo verifyInfo);

    @UpdateProvider(type = CaseVerifyInfoProvider.class, method = "updateCaseVerify")
    Integer updateCaseVerifyInfo(CaseVerifyInfo verifyInfo);

    @Delete("DELETE FROM CaseVerifyInfo WHERE id = #{id}")
    Integer deleteCaseVerifyInfo(@Param("id") Integer id);

    @Delete("DELETE FROM CaseVerifyInfo WHERE caseApiId = #{caseApiId}")
    Integer deleteCaseVerifyInfoByCaseApiId(@Param("caseApiId") Integer caseApiId);
}
