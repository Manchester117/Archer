package com.archer.source.mapper;

import com.archer.source.domain.entity.ApiInfo;
import com.archer.source.domain.linked.ApiInfoWithService;
import com.archer.source.mapper.provider.ApiInfoProvider;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ApiInfoMapper {
    @Select("SELECT * FROM ApiInfo WHERE id = #{id}")
    ApiInfo getApiInfo(@Param("id") Integer id);

    @SelectProvider(type = ApiInfoProvider.class, method = "getApiList")
    List<ApiInfo> getApiInfoListByName(@Param("apiName") String apiName);

    @SelectProvider(type = ApiInfoProvider.class, method = "getApiWithServiceList")
    List<ApiInfoWithService> getApiWithService(@Param("apiName") String apiName, @Param("serviceId") Integer serviceId);

    @Select("SELECT * FROM ApiInfo WHERE serviceId = #{serviceId}")
    List<ApiInfo> getApiInfoListByServiceId(@Param("serviceId") Integer serviceId);

    @InsertProvider(type = ApiInfoProvider.class, method = "insertApi")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insertApiInfo(ApiInfo apiInfo);

    @UpdateProvider(type = ApiInfoProvider.class, method = "updateApi")
    Integer updateApiInfo(ApiInfo apiInfo);

    @DeleteProvider(type = ApiInfoProvider.class, method = "deleteApi")
    Integer deleteApiInfo(Integer id);

    @Delete("DELETE FROM ApiInfo WHERE serviceId = #{serviceId}")
    Integer deleteApiInfoByServiceId(Integer serviceId);
}
