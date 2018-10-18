package com.archer.source.mapper;

import com.archer.source.domain.entity.ServiceInfo;
import com.archer.source.mapper.provider.ServiceInfoProvider;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ServiceInfoMapper {
    @Select("SELECT * FROM ServiceInfo WHERE id = #{id}")
    ServiceInfo getServiceInfo(@Param("id") Integer id);

    @Select("SELECT * FROM ServiceInfo ORDER BY createTime DESC")
    List<ServiceInfo> getAllServiceInfoList();

    @Select("SELECT * FROM ServiceInfo WHERE projectId = #{projectId}")
    List<ServiceInfo> getServiceInfoListByProjectId(@Param("projectId") Integer projectId);

    @SelectProvider(type = ServiceInfoProvider.class, method = "getServiceList")
    List<ServiceInfo> getServiceInfoList(@Param("serviceName") String serviceName, @Param("projectId") Integer projectId);

    @InsertProvider(type = ServiceInfoProvider.class, method = "insertService")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insertServiceInfo(ServiceInfo serviceInfo);

    @UpdateProvider(type = ServiceInfoProvider.class, method = "updateService")
    Integer updateServiceInfo(ServiceInfo serviceInfo);

    @DeleteProvider(type = ServiceInfoProvider.class, method = "deleteService")
    Integer deleteServiceInfo(Integer id);
}
