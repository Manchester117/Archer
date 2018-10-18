package com.archer.source.mapper.provider;

import com.archer.source.domain.entity.RespInfo;
import org.apache.ibatis.jdbc.SQL;

import java.util.Objects;

public class RespInfoProvider {
    public String insertResp(RespInfo respInfo) {
        return new SQL() {
            {
                INSERT_INTO("RespInfo");
                if (Objects.nonNull(respInfo.getApiName()))
                    VALUES("apiName", "#{apiName}");
                if (Objects.nonNull(respInfo.getUrl()))
                    VALUES("url", "#{url}");
                if (Objects.nonNull(respInfo.getRespTime()))
                    VALUES("respTime", "#{respTime}");
                if (Objects.nonNull(respInfo.getStatusCode()))
                    VALUES("statusCode", "#{statusCode}");
                if (Objects.nonNull(respInfo.getRespHeader()))
                    VALUES("respHeader", "#{respHeader}");
                if (Objects.nonNull(respInfo.getRespContent()))
                    VALUES("respContent", "#{respContent}");
                if (Objects.nonNull(respInfo.getRunBatTimes()))
                    VALUES("runBatTimes", "#{runBatTimes}");
                if (Objects.nonNull(respInfo.getApiId()))
                    VALUES("apiId", "#{apiId}");
            }
        }.toString();
    }
}
