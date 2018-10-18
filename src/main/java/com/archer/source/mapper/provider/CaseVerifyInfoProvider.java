package com.archer.source.mapper.provider;

import com.archer.source.domain.entity.CaseVerifyInfo;
import org.apache.ibatis.jdbc.SQL;

import java.util.Objects;

public class CaseVerifyInfoProvider {
    public String insertCaseVerify(CaseVerifyInfo caseVerifyInfo) {
        return new SQL() {
            {
                INSERT_INTO("CaseVerifyInfo");
                if (Objects.nonNull(caseVerifyInfo.getVerifyName()))
                    VALUES("verifyName", "#{verifyName}");
                if (Objects.nonNull(caseVerifyInfo.getVerifyType()))
                    VALUES("verifyType", "#{verifyType}");
                if (Objects.nonNull(caseVerifyInfo.getExpression()))
                    VALUES("expression", "#{expression}");
                if (Objects.nonNull(caseVerifyInfo.getExpectValue()))
                    VALUES("expectValue", "#{expectValue}");
                if (Objects.nonNull(caseVerifyInfo.getActualValue()))
                    VALUES("actualValue", "#{actualValue}");
                if (Objects.nonNull(caseVerifyInfo.getIsSuccess()))
                    VALUES("isSuccess", "#{isSuccess}");
                if (Objects.nonNull(caseVerifyInfo.getCaseApiId()))
                    VALUES("caseApiId", "#{caseApiId}");
            }
        }.toString();
    }

    public String updateCaseVerify(CaseVerifyInfo caseVerifyInfo) {
        return new SQL() {
            {
                UPDATE("CaseVerifyInfo");
                if (Objects.nonNull(caseVerifyInfo.getVerifyName()))
                    SET("verifyName = #{verifyName}");
                if (Objects.nonNull(caseVerifyInfo.getVerifyType()))
                    SET("verifyType = #{verifyType}");
                if (Objects.nonNull(caseVerifyInfo.getExpression()))
                    SET("expression = #{expression}");
                if (Objects.nonNull(caseVerifyInfo.getExpectValue()))
                    SET("expectValue = #{expectValue}");
                if (Objects.nonNull(caseVerifyInfo.getActualValue()))
                    SET("actualValue = #{actualValue}");
                if (Objects.nonNull(caseVerifyInfo.getIsSuccess()))
                    SET("isSuccess = #{isSuccess}");
                if (Objects.nonNull(caseVerifyInfo.getId()))
                    WHERE("id = #{id}");
            }
        }.toString();
    }
}
