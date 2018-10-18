package com.archer.source.mapper;

import com.archer.source.domain.entity.VerifyInfo;
import com.archer.source.mapper.provider.VerifyInfoProvider;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface VerifyInfoMapper {
    @Select("SELECT * FROM VerifyInfo WHERE id = #{id}")
    VerifyInfo getVerifyInfo(@Param("id") Integer id);

    @Select("SELECT * FROM VerifyInfo WHERE apiId = #{apiId}")
    List<VerifyInfo> getVerifyInfoListByApiId(@Param("apiId") Integer apiId);

    @InsertProvider(type = VerifyInfoProvider.class, method = "insertVerify")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insertVerifyInfo(VerifyInfo verifyInfo);

    @UpdateProvider(type = VerifyInfoProvider.class, method = "updateVerify")
    Integer updateVerifyInfo(VerifyInfo verifyInfo);

    @Delete("DELETE FROM VerifyInfo WHERE id = #{id}")
    Integer deleteVerifyInfo(@Param("id") Integer id);

    @Delete("DELETE FROM VerifyInfo WHERE apiId = #{apiId}")
    Integer deleteVerifyInfoByApiId(@Param("apiId") Integer apiId);
}
