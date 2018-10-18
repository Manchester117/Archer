package com.archer.source.engine;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.ReadContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class ExtractValueComponent {
    /**
     * @description: 通过正则表达式获取待验证的文本
     * @author:      Zhao.Peng
     * @date:        2018/8/16
     * @time:        9:41
     * @param:       expression  - Regex正则表达式
     * @param:       respContent - 响应正文
     * @return:      返回待验证文本
     */
    public String extractValueByRegex(String expression, String respContent) {
        String extractValue = null;

        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(respContent);

        if (matcher.find()) {
            try {
                extractValue = matcher.group(1);
            } catch (IndexOutOfBoundsException e) {
                extractValue = "未从响应中获取到值";
                log.info("未从响应正文中获取到值,Regex表达式: {}", expression);
                e.printStackTrace();
            }
        } else {
            extractValue = "未从响应中获取到值";
        }
        return extractValue;
    }

    /**
     * @description: 通过JsonPath获取待验证的文本
     * @author:      Zhao.Peng
     * @date:        2018/8/16
     * @time:        9:43
     * @param:       expression  - JsonPath表达式
     * @param:       respContent - 响应正文
     * @return:      返回待验证文本
     */
    public String extractValueByJsonPath(String expression, String respContent) {
        String extractValue = null;

        ReadContext ctx = JsonPath.parse(respContent);
        // 如果从JSON中解析出来的值是整型,那么就做类型转换
        try {
            Object extractObject = ctx.read(expression);
            extractValue = String.valueOf(extractObject);
        } catch (PathNotFoundException e) {
            extractValue = "未从响应中获取到值";
            log.info("未从响应正文中获取到值,JSON表达式: {}", expression);
        }

        return extractValue;
    }

    /**
     * @description: 通过CSS Selector获取待验证的文本
     * @author:      Zhao.Peng
     * @date:        2018/8/16
     * @time:        9:51
     * @param:       expression  - CSS Selector表达式
     * @param:       respContent - 响应正文
     * @return:      返回待验证文本
     */
    public String extractValueBySelector(String expression, String respContent) {
        String extractValue = null;

        Document doc = Jsoup.parse(respContent);
        Element element = doc.select(expression).first();

        if (element != null)
            extractValue = element.text();
        else
            extractValue = "未从响应中获取到值";
            log.info("未从响应正文中获取到值,CSS Selector表达式: {}", expression);

        return extractValue;
    }
}
