package com.archer.source.mapper;

import com.archer.source.domain.entity.RespInfo;
import com.archer.source.mapper.provider.RespInfoProvider;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface RespInfoMapper {
    @Select("SELECT * FROM RespInfo WHERE id = #{id}")
    RespInfo getRespInfo(@Param("id") Integer id);

    @Select("SELECT * FROM RespInfo WHERE apiId = #{apiId} ORDER BY id desc LIMIT 1")
    RespInfo getLastRespInfo(@Param("apiId") Integer apiId);

    @Select("SELECT * FROM RespInfo WHERE apiId = #{apiId} AND runBatTimes = #{runBatTimes}")
    RespInfo getRespInfoByRunBatTimes(@Param("apiId") Integer apiId, @Param("runBatTimes") Integer runBatTimes);

    @Select("SELECT * FROM RespInfo WHERE apiId = #{apiId}")
    List<RespInfo> getRespInfoListByApiId(@Param("apiId") Integer apiId);

    @InsertProvider(type = RespInfoProvider.class, method = "insertResp")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insertRespInfo(RespInfo respInfo);

    @Delete("DELETE FROM RespInfo WHERE id = #{id}")
    Integer deleteRespInfo(@Param("id") Integer id);

    @Delete("DELETE FROM RespInfo WHERE apiId = #{apiId}")
    Integer deleteRespInfoByApiId(@Param("apiId") Integer apiId);
}
