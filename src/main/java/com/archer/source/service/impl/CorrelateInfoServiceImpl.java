package com.archer.source.service.impl;

import com.archer.source.domain.entity.CorrelateInfo;
import com.archer.source.mapper.CorrelateInfoMapper;
import com.archer.source.service.CorrelateInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CorrelateInfoServiceImpl implements CorrelateInfoService {
    @Autowired
    private CorrelateInfoMapper correlateMapper;

    @Override
    public List<CorrelateInfo> getCorrelateInfoByCaseApiId(Integer caseApiId) {
        return correlateMapper.getCorrelateInfoByCaseApiId(caseApiId);
    }

    @Override
    public Integer insertCorrelateInfo(CorrelateInfo correlateInfo) {
        return correlateMapper.insertCorrelateInfo(correlateInfo);
    }

    @Override
    public Integer updateCorrelateInfo(CorrelateInfo correlateInfo) {
        return correlateMapper.updateCorrelateInfo(correlateInfo);
    }

    @Override
    public Integer deleteCorrelateInfo(Integer id) {
        return correlateMapper.deleteCorrelateInfo(id);
    }

    @Override
    public Integer deleteCorrelateInfoByCaseApiId(Integer caseApiId) {
        return correlateMapper.deleteCorrelateInfoByCaseApiId(caseApiId);
    }
}
