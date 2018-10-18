package com.archer.source.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectInfo {
    private Integer id;
    private String projectName;
    private Date createTime;
    private String description;
}
