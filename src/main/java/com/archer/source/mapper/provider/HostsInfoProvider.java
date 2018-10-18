package com.archer.source.mapper.provider;

import com.archer.source.domain.entity.HostsInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;
import java.util.Objects;

public class HostsInfoProvider {
    public String getHostsList(Map<String, Object> params) {
        return new SQL() {
            {
                String searchCondition = null;
                if (Objects.nonNull(params.get("description"))) {
                    String descKeyword = params.get("description").toString();
                    if (StringUtils.isNoneBlank(descKeyword))
                        searchCondition = StringUtils.join("description LIKE '%", descKeyword, "%'");
                }

                SELECT("id, hostsItem, description, status");
                FROM("HostsInfo");
                if (Objects.nonNull(searchCondition))
                    WHERE(searchCondition);
            }
        }.toString();
    }

    public String insertHosts(HostsInfo hostsInfo) {
        return new SQL() {
            {
                INSERT_INTO("HostsInfo");
                if (Objects.nonNull(hostsInfo.getHostsItem()))
                    VALUES("hostsItem", "#{hostsItem}");
                if (Objects.nonNull(hostsInfo.getDescription()))
                    VALUES("description", "#{description}");
                if (Objects.nonNull(hostsInfo.getStatus()))
                    VALUES("status", "#{status}");
            }
        }.toString();
    }

    public String updateHosts(HostsInfo hostsInfo) {
        return new SQL() {
            {
                UPDATE("HostsInfo");
                if (Objects.nonNull(hostsInfo.getHostsItem()))
                    SET("hostsItem = #{hostsItem}");
                if (Objects.nonNull(hostsInfo.getDescription()))
                    SET("description = #{description}");
                if (Objects.nonNull(hostsInfo.getStatus()))
                    SET("status = #{status}");
                if (Objects.nonNull(hostsInfo.getId()))
                    WHERE("id = #{id}");
            }
        }.toString();
    }

    public String deleteHosts(Integer id) {
        return new SQL() {
            {
                DELETE_FROM("HostsInfo");
                if (Objects.nonNull(id))
                    WHERE("id = #{id}");
            }
        }.toString();
    }
}
