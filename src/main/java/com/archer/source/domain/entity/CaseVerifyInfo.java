package com.archer.source.domain.entity;

import lombok.Data;

@Data
public class CaseVerifyInfo {
    private Integer id;
    private String verifyName;
    private Integer verifyType;
    private String expression;
    private String expectValue;
    private String actualValue;
    private Integer isSuccess;
    private Integer caseApiId;
}
