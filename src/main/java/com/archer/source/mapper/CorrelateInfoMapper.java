package com.archer.source.mapper;

import com.archer.source.domain.entity.CorrelateInfo;
import com.archer.source.mapper.provider.CorrelateInfoProvider;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface CorrelateInfoMapper {
    @Select("SELECT * FROM CorrelateInfo WHERE caseApiId = #{caseApiId}")
    List<CorrelateInfo> getCorrelateInfoByCaseApiId(@Param("caseApiId") Integer caseApiId);

    @InsertProvider(type = CorrelateInfoProvider.class, method = "insertCorrelate")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insertCorrelateInfo(CorrelateInfo correlateInfo);

    @UpdateProvider(type = CorrelateInfoProvider.class, method = "updateCorrelate")
    Integer updateCorrelateInfo(CorrelateInfo correlateInfo);

    @Delete("DELETE FROM CorrelateInfo WHERE id = #{id}")
    Integer deleteCorrelateInfo(Integer id);

    @Delete("DELETE FROM CorrelateInfo WHERE caseApiId = #{caseApiId}")
    Integer deleteCorrelateInfoByCaseApiId(Integer caseApiId);
}
