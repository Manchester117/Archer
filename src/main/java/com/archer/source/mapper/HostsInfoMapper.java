package com.archer.source.mapper;

import com.archer.source.domain.entity.HostsInfo;
import com.archer.source.mapper.provider.HostsInfoProvider;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface HostsInfoMapper {
    @Select("SELECT * FROM HostsInfo WHERE id = #{id}")
    HostsInfo getHostsInfo(@Param("id") Integer id);

    @SelectProvider(type = HostsInfoProvider.class, method = "getHostsList")
    List<HostsInfo> getHostsInfoList(@Param("description") String description);

    @Select("SELECT * FROM HostsInfo")
    List<HostsInfo> getAllHostsInfoList();

    @InsertProvider(type = HostsInfoProvider.class, method = "insertHosts")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insertHostsInfo(HostsInfo hostsInfo);

    @UpdateProvider(type = HostsInfoProvider.class, method = "updateHosts")
    Integer updateHostsInfo(HostsInfo hostsInfo);

    @DeleteProvider(type = HostsInfoProvider.class, method = "deleteHosts")
    Integer deleteHostsInfo(Integer id);
}
