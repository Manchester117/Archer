package com.archer.source.mapper.provider;

import com.archer.source.domain.entity.CorrelateInfo;
import org.apache.ibatis.jdbc.SQL;

import java.util.Objects;

public class CorrelateInfoProvider {
    public String insertCorrelate(CorrelateInfo correlateInfo) {
        return new SQL() {
            {
                INSERT_INTO("CorrelateInfo");
                if (Objects.nonNull(correlateInfo.getCorrField()))
                    VALUES("corrField", "#{corrField}");
                if (Objects.nonNull(correlateInfo.getCorrPattern()))
                    VALUES("corrPattern", "#{corrPattern}");
                if (Objects.nonNull(correlateInfo.getCorrExpression()))
                    VALUES("corrExpression", "#{corrExpression}");
                if (Objects.nonNull(correlateInfo.getCaseApiId()))
                    VALUES("caseApiId", "#{caseApiId}");
            }
        }.toString();
    }

    public String updateCorrelate(CorrelateInfo correlateInfo) {
        return new SQL() {
            {
                UPDATE("CorrelateInfo");
                if (Objects.nonNull(correlateInfo.getCorrField()))
                    SET("corrField = #{corrField}");
                if (Objects.nonNull(correlateInfo.getCorrPattern()))
                    SET("corrPattern = #{corrPattern}");
                if (Objects.nonNull(correlateInfo.getCorrExpression()))
                    SET("corrExpression = #{corrExpression}");
                if (Objects.nonNull(correlateInfo.getCorrValue()))
                    SET("corrValue = #{corrValue}");
                if (Objects.nonNull(correlateInfo.getCaseApiId()))
                    SET("caseApiId = #{caseApiId}");
                WHERE("id = #{id}");
            }
        }.toString();
    }
}
