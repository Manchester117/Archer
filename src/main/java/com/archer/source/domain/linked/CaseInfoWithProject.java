package com.archer.source.domain.linked;

import lombok.Data;

@Data
public class CaseInfoWithProject {
    private Integer id;
    private String caseName;
    private String apiSequence;
    private Integer runBatTimes;
    private String projectName;
}
