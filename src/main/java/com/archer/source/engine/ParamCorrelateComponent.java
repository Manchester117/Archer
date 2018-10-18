package com.archer.source.engine;

import com.archer.source.domain.entity.CaseApiInfo;
import com.archer.source.domain.entity.CorrelateInfo;
import com.archer.source.domain.entity.RespInfo;
import com.archer.source.service.CorrelateInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class ParamCorrelateComponent {
    @Autowired
    private CorrelateInfoService correlateService;
    @Autowired
    private ExtractValueComponent extractComponent;

    /**
    * @description: 在当前请求的响应中抽取关联值
    * @author:      Zhao.Peng
    * @date:        2018/8/20
    * @time:        16:24
    * @param:       apiInfo - 请求实体 / respInfo - 响应实体
    * @return:      返回已经抽取值的关联实体列表
    */
    public List<CorrelateInfo> extractCorrValue(CaseApiInfo caseApiInfo, RespInfo respInfo) {
        // 拿到接口对应的拦截参数
        List<CorrelateInfo> correlateInfoList = correlateService.getCorrelateInfoByCaseApiId(caseApiInfo.getId());
//        List<CorrelateInfo> correlateInfoList = correlateService.getCorrelateInfoListByApiId(apiInfo.getId());
        for (CorrelateInfo correlateInfo: correlateInfoList) {
            Integer patternType = correlateInfo.getCorrPattern();
            String expression = correlateInfo.getCorrExpression();
            String corrValue = null;
            if (Objects.equals(patternType, 1))
                // 使用正则表达式进行文本抽取
                corrValue = extractComponent.extractValueByRegex(expression, respInfo.getRespContent());
            else if (Objects.equals(patternType, 2))
                // 使用JSONPath进行文本抽取
                corrValue = extractComponent.extractValueByJsonPath(expression, respInfo.getRespContent());
            else if (Objects.equals(patternType, 3))
                // 使用CSS Selector进行文本抽取
                corrValue = extractComponent.extractValueBySelector(expression, respInfo.getRespContent());

            // 将抽取关联值写入到实体中
            correlateInfo.setCorrValue(corrValue);
            // 将抽取关联值后的实体更新到DB中
            correlateService.updateCorrelateInfo(correlateInfo);
        }
        return correlateInfoList;
    }

    /**
    * @description: 利用关联值对还没有执行的请求正文进行替换(在请求体中需要使用"@+参数名称+@"进行关联)
    * @author:      Zhao.Peng
    * @date:        2018/8/20
    * @time:        16:28
    * @param:       correlateInfoList - 关联列表 / caseApiInfoList - 接口的用例列表
    * @return:
    */
    public void replaceBodyCorrValue(List<CorrelateInfo> correlateInfoList, List<CaseApiInfo> caseApiInfoList) {
        // 遍历当前接口的所有关联属性
        for (CorrelateInfo corrInfo: correlateInfoList) {
            String corrField = StringUtils.join("@", corrInfo.getCorrField() + "@");
            String corrValue = corrInfo.getCorrValue();
            // 对当前用例下的所有接口进行遍历
            for (CaseApiInfo apiInfo : caseApiInfoList) {
                String bodyContent = apiInfo.getBody();
                // 进行关联值的替换
                if (bodyContent.contains(corrField)) {
                    bodyContent = StringUtils.replaceAll(bodyContent, corrField, corrValue);
                    apiInfo.setBody(bodyContent);
                }
            }
        }
    }

    /** 
    * @description: 利用关联值对还没有执行的请求正文进行替换(在请求的URL中需要使用"@+参数名称+@"进行关联)
    * @author:      Zhao.Peng
    * @date:        2018/8/28 
    * @time:        10:54 
    * @param:        
    * @return:      
    */
    public void replaceUrlCorrValue(List<CorrelateInfo> correlateInfoList, List<CaseApiInfo> caseApiInfoList) {
        // 遍历当前接口的所有关联属性
        for (CorrelateInfo corrInfo: correlateInfoList) {
            String corrField = StringUtils.join("@", corrInfo.getCorrField() + "@");
            String corrValue = corrInfo.getCorrValue();
            // 对当前用例下的所有接口进行遍历
            for (CaseApiInfo apiInfo : caseApiInfoList) {
                String url = apiInfo.getUrl();
                // 进行关联值的替换
                if (url.contains(corrField)) {
                    url = StringUtils.replaceAll(url, corrField, corrValue);
                    apiInfo.setUrl(url);
                }
            }
        }
    }

    /**
     * @description: 利用关联值对还没有执行的请求Header进行替换(在请求的URL中需要使用"@+参数名称+@"进行关联)
     * @author:      Zhao.Peng
     * @date:        2018/9/28
     * @time:        10:29
     * @param:       correlateInfoList - 关联列表 / caseApiInfoList - 接口的用例列表
     * @return:
     */
    public void replaceHeaderCorrValue(List<CorrelateInfo> correlateInfoList, List<CaseApiInfo> caseApiInfoList) {
        for (CorrelateInfo corrInfo: correlateInfoList) {
            String corrField = StringUtils.join("@", corrInfo.getCorrField() + "@");
            String corrValue = corrInfo.getCorrValue();
            // 对当前用例下的所有接口进行遍历
            for (CaseApiInfo apiInfo: caseApiInfoList) {
                String header = apiInfo.getHeader();
                if (header.contains(corrField)) {
                    header = StringUtils.replaceAll(header, corrField, corrValue);
                    apiInfo.setHeader(header);
                }
            }
        }
    }
}
