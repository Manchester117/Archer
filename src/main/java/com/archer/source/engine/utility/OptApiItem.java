package com.archer.source.engine.utility;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.archer.source.domain.entity.ApiInfo;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

@Component
public class OptApiItem {
    private ApiInfo apiInfo;

    /**
    * @description: 通过实体获取请求实体
    * @author:      Zhao.Peng
    * @date:        2018/8/20
    * @time:        14:17
    * @param:       请求实体
    * @return:
    */
    public void setApiInfo(ApiInfo apiInfo) {
        this.apiInfo = apiInfo;
    }

    /**
    * @description: 获取接口名称
    * @author:      Zhao.Peng
    * @date:        2018/8/15
    * @time:        13:16
    * @param:
    * @return:      返回接口名
    */
    public String getApiName() {
        return this.apiInfo.getApiName();
    }

    /**
    * @description: 获取协议类型
    * @author:      Zhao.Peng
    * @date:        2018/8/15
    * @time:        13:22
    * @param:
    * @return:      返回协议类型
    */
    public String getProtocol() {
        Integer protocolMark = this.apiInfo.getProtocol();
        if (Objects.equals(protocolMark, 1))
            return "HTTP";
        else if (Objects.equals(protocolMark, 2))
            return "HTTPS";
        else
            return null;
    }

    /**
    * @description: 获取接口URL
    * @author:      Zhao.Peng
    * @date:        2018/8/15
    * @time:        13:24
    * @param:
    * @return:      返回接口URL
    */
    public String getUrl() {
        return this.apiInfo.getUrl();
    }

    /**
    * @description: 获取请求方式
    * @author:      Zhao.Peng
    * @date:        2018/8/15
    * @time:        13:26
    * @param:
    * @return:      返回四种请求方式
    */
    public String getMethod() {
        Integer methodMark = this.apiInfo.getMethod();
        if (Objects.equals(methodMark, 1))
            return "GET";
        else if (Objects.equals(methodMark, 2))
            return "POST";
        else if (Objects.equals(methodMark, 3))
            return "PUT";
        else if (Objects.equals(methodMark, 4))
            return "DELETE";
        else
            System.out.println("位置的请求方式");
            return null;
    }

    /**
    * @description: 获取请求头
    * @author:      Zhao.Peng
    * @date:        2018/8/20
    * @time:        13:33
    * @param:
    * @return:      请求头的JSON格式
    */
    public JSONObject getHeader() {
        String header = this.apiInfo.getHeader();
        return JSON.parseObject(header);
    }

    /**
    * @description: 获取请求体
    * @author:      Zhao.Peng
    * @date:        2018/8/20
    * @time:        13:33
    * @param:
    * @return:      请求体的JSON格式
    */
    public JSONObject getBody() {
        String body = this.apiInfo.getBody();
        return JSON.parseObject(body);
    }

    /**
    * @description: 获取接口创建日期
    * @author:      Zhao.Peng
    * @date:        2018/8/20
    * @time:        13:33
    * @param:
    * @return:      接口创建时间
    */
    public Date getCreateTime() {
        return this.apiInfo.getCreateTime();
    }

    /**
    * @description: 获取'是否MOCK'
    * @author:      Zhao.Peng
    * @date:        2018/8/20
    * @time:        13:34
    * @param:
    * @return:      是否MOCK
    */
    public Integer getIsMock() {
        return this.apiInfo.getIsMock();
    }
}
