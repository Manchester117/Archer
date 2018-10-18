package com.archer.source.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class CaseApiInfo {
    private Integer id;
    private Integer apiId;
    private String apiName;
    private Integer protocol;
    private String url;
    private Integer method;
    private String header;
    private String body;
    private Date createTime;
    private Integer waitMillis;
    private Integer isMock;
    private Integer serviceId;
    private Integer caseId;
}
