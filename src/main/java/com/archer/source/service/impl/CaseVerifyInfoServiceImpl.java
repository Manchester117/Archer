package com.archer.source.service.impl;

import com.archer.source.domain.entity.CaseVerifyInfo;
import com.archer.source.mapper.CaseVerifyInfoMapper;
import com.archer.source.service.CaseVerifyInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CaseVerifyInfoServiceImpl implements CaseVerifyInfoService{
    @Autowired
    private CaseVerifyInfoMapper caseVerifyMapper;

    @Override
    public List<CaseVerifyInfo> getCaseVerifyInfoByCaseApiId(Integer caseApiId) {
        return caseVerifyMapper.getCaseVerifyInfoByCaseApiId(caseApiId);
    }

    @Override
    public Integer insertCaseVerifyInfo(CaseVerifyInfo caseVerifyInfo) {
        return caseVerifyMapper.insertCaseVerifyInfo(caseVerifyInfo);
    }

    @Override
    public Integer updateCaseVerifyInfo(CaseVerifyInfo caseVerifyInfo) {
        return caseVerifyMapper.updateCaseVerifyInfo(caseVerifyInfo);
    }

    @Override
    public Integer deleteCaseVerifyInfo(Integer id) {
        return caseVerifyMapper.deleteCaseVerifyInfo(id);
    }

    @Override
    public Integer deleteCaseVerifyInfoByCaseApiId(Integer caseApiId) {
        return caseVerifyMapper.deleteCaseVerifyInfoByCaseApiId(caseApiId);
    }
}
