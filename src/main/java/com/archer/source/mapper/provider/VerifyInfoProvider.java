package com.archer.source.mapper.provider;

import com.archer.source.domain.entity.VerifyInfo;
import org.apache.ibatis.jdbc.SQL;

import java.util.Objects;

public class VerifyInfoProvider {
    public String insertVerify(VerifyInfo verifyInfo) {
        return new SQL() {
            {
                INSERT_INTO("VerifyInfo");
                if (Objects.nonNull(verifyInfo.getVerifyName()))
                    VALUES("verifyName", "#{verifyName}");
                if (Objects.nonNull(verifyInfo.getVerifyType()))
                    VALUES("verifyType", "#{verifyType}");
                if (Objects.nonNull(verifyInfo.getExpression()))
                    VALUES("expression", "#{expression}");
                if (Objects.nonNull(verifyInfo.getExpectValue()))
                    VALUES("expectValue", "#{expectValue}");
                if (Objects.nonNull(verifyInfo.getActualValue()))
                    VALUES("actualValue", "#{actualValue}");
                if (Objects.nonNull(verifyInfo.getIsSuccess()))
                    VALUES("isSuccess", "#{isSuccess}");
                if (Objects.nonNull(verifyInfo.getApiId()))
                    VALUES("apiId", "#{apiId}");
            }
        }.toString();
    }

    public String updateVerify(VerifyInfo verifyInfo) {
        return new SQL() {
            {
                UPDATE("VerifyInfo");
                if (Objects.nonNull(verifyInfo.getVerifyName()))
                    SET("verifyName = #{verifyName}");
                if (Objects.nonNull(verifyInfo.getVerifyType()))
                    SET("verifyType = #{verifyType}");
                if (Objects.nonNull(verifyInfo.getExpression()))
                    SET("expression = #{expression}");
                if (Objects.nonNull(verifyInfo.getExpectValue()))
                    SET("expectValue = #{expectValue}");
                if (Objects.nonNull(verifyInfo.getActualValue()))
                    SET("actualValue = #{actualValue}");
                if (Objects.nonNull(verifyInfo.getIsSuccess()))
                    SET("isSuccess = #{isSuccess}");
                if (Objects.nonNull(verifyInfo.getId()))
                    WHERE("id = #{id}");
            }
        }.toString();
    }
}
