package com.archer.source.domain.linked;

import lombok.Data;

import java.util.Date;

@Data
public class ApiInfoWithService {
    private Integer id;
    private String apiName;
    private Integer protocol;
    private Integer method;
    private String url;
    private Date createTime;
    private String serviceName;
}
