package com.archer.source.service;

import com.archer.source.domain.entity.CorrelateInfo;

import java.util.List;

public interface CorrelateInfoService {
    List<CorrelateInfo> getCorrelateInfoByCaseApiId(Integer caseApiId);

    Integer insertCorrelateInfo(CorrelateInfo correlateInfo);

    Integer updateCorrelateInfo(CorrelateInfo correlateInfo);

    Integer deleteCorrelateInfo(Integer id);

    Integer deleteCorrelateInfoByCaseApiId(Integer caseApiId);
}
