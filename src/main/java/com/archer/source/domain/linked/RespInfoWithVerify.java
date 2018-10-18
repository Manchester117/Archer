package com.archer.source.domain.linked;

import lombok.Data;

@Data
public class RespInfoWithVerify {
    private Integer id;
    private String apiName;
    private String url;
    private Integer statusCode;
    private Long respTime;
    private String successMark;
}
