package com.archer.source.engine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.archer.source.domain.entity.ApiInfo;
import com.archer.source.domain.entity.RespInfo;
import com.archer.source.engine.except.ArcherException;
import com.archer.source.engine.utility.OptApiItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.InMemoryDnsResolver;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

@Slf4j      // lombok的日志插入
@Component
@PropertySource(value = {"classpath:application.yml"}, encoding="UTF-8")
public class RequestComponent {
    @Autowired
    private OptApiItem apiItem;
    private static SSLConnectionSocketFactory sslSocketFactory = null;
    @Value("${sysParameter.connectTimeout}")
    private Integer connectTimeout;
    @Value("${sysParameter.socketTimeout}")
    private Integer socketTimeout;

    // 静态块设置HTTPS
    static {
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            });
            sslSocketFactory = new SSLConnectionSocketFactory(builder.build(), new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2"}, null, NoopHostnameVerifier.INSTANCE);
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    /**
     * @description: 实现多Hosts绑定,配置当前HttpClient的Hosts指向
     * @author:      Zhao.Peng
     * @date:        2018/8/16
     * @time:        10:04
     * @param:       hostsItem - 被测系统的Host列表
     * @return:      返回Http连接管理器
     */
    private HttpClientConnectionManager selectHosts(List<HashMap> hostsItem) {
        // 使用自定义的DnsResolver
        InMemoryDnsResolver dnsResolver = new InMemoryDnsResolver();
        try {
            for (HashMap item: hostsItem) {
                dnsResolver.add(String.valueOf(item.get("domain")), Inet4Address.getByName(String.valueOf(item.get("ip"))));
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // 这里需要注意的是如果多人同时使用,但BasicHttpClientConnectionManager不起效果
        // 需要使用PoolingHttpClientConnectionManager
        return new PoolingHttpClientConnectionManager(
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", new PlainConnectionSocketFactory())
                        .register("https", sslSocketFactory)
                        .build(),
                dnsResolver
        );
    }

    /**
     * @description: 获取HttpClient - HTTP 
     * @author:      Zhao.Peng
     * @date:        2018/8/16 
     * @time:        10:02 
     * @param:       config - 请求配置 
     * @return:      httpClient
     */
    private CloseableHttpClient getHttpClient(RequestConfig config, List<HashMap> hostsItem) {
        return HttpClientBuilder.create()
                .setConnectionManagerShared(true)
                .setDefaultRequestConfig(config)
                .setConnectionManager(selectHosts(hostsItem))
                .build();
    }
    
    /** 
    * @description: 获取HttpClient - HTTPS 
    * @author:      Zhao.Peng 
    * @date:        2018/8/16 
    * @time:        10:03 
    * @param:       config - 请求配置 
    * @return:      httpClient
    */
    private CloseableHttpClient getHttpsClient(RequestConfig config, List<HashMap> hostsItem) {
        return HttpClientBuilder.create()
                .setSSLSocketFactory(sslSocketFactory)
                .setConnectionManagerShared(true)
                .setDefaultRequestConfig(config)
                .setConnectionManager(selectHosts(hostsItem))
                .build();
    }
    
    /** 
    * @description: Request的封装方法 
    * @author:      Zhao.Peng 
    * @date:        2018/8/27 
    * @time:        17:23 
    * @param:       ApiInfo     - 请求实体
    * @param:       hostsInfo   - Hosts配置实体
    * @return:      RespInfo    - 响应实体
    */
    public RespInfo requestWrapper(ApiInfo apiInfo, String baseUrl, List<HashMap> hostsItem) throws ArcherException {
        RespInfo respInfo = null;
        // 对HttpClient进行配置,禁止HTTP重定向,设置连接超时和响应超时
        RequestConfig config = RequestConfig.custom()
                .setRedirectsEnabled(false)                                       // 关闭重定向
                .setConnectTimeout(connectTimeout)                                // 默认连接等待时间为5秒
                .setSocketTimeout(socketTimeout)                                  // 默认连接后超时时间为20秒
                .build();
        // 从内存中读取请求实体
        apiItem.setApiInfo(apiInfo);
        // 初始化HttpClient
        CloseableHttpClient httpClient = null;
        if (Objects.equals("HTTP", apiItem.getProtocol())) {                   // 判断协议,如果是HTTP
            httpClient = this.getHttpClient(config, hostsItem);
            respInfo = this.selectRequestMethod(httpClient, baseUrl, "HTTP");
            respInfo.setApiId(apiInfo.getId());                                   // 关联接口ID
        } else if (Objects.equals("HTTPS", apiItem.getProtocol())) {           // 判断协议,如果是HTTPS
            httpClient = this.getHttpsClient(config, hostsItem);
            respInfo = this.selectRequestMethod(httpClient, baseUrl, "HTTPS");
            respInfo.setApiId(apiInfo.getId());                                   // 关联接口ID
        } else {                                                                  // 判断协议,如果不是HTTP/HTTPS
            throw new ArcherException(StringUtils.join(apiInfo.getUrl(), "|", "不是HTTP/HTTPS协议"));
        }
        return respInfo;
    }
    
    /** 
    * @description: 根据请求方式选择对应的请求执行方法 
    * @author:      Zhao.Peng 
    * @date:        2018/8/27 
    * @time:        17:16 
    * @param:       httpClient - 请求 
    * @return:      respInfo - 请求响应实体
    */
    private RespInfo selectRequestMethod(CloseableHttpClient httpClient, String baseUrl, String protocolType) throws ArcherException {
        String reqMethod = apiItem.getMethod();
        RespInfo respInfo = null;
        switch (reqMethod) {
            case "GET":
                respInfo = this.getFunction(httpClient, baseUrl, protocolType);
                break;
            case "POST":
                respInfo = this.postFunction(httpClient, baseUrl, protocolType);
                break;
            case "PUT":
                respInfo = this.putFunction(httpClient, baseUrl, protocolType);
                break;
            case "DELETE":
                respInfo = this.deleteFunction(httpClient, baseUrl, protocolType);
                break;
            default:
                throw new ArcherException(StringUtils.join(apiItem.getUrl(), "|", "未知的请求类型"));
        }
        return respInfo;
    }

    private String constructRequestUriString(String baseUrl, String protocolType) {
        // 设置Get参数
        String requestUrl = null;
        if (Objects.equals("HTTP", protocolType))
            requestUrl = StringUtils.join("http://", baseUrl, apiItem.getUrl());
        else
            requestUrl = StringUtils.join("https://", baseUrl, apiItem.getUrl());

        return requestUrl;
    }

    private URI checkUriString(String requestUrl) throws ArcherException {
        // 判断URL是否合法
        URI uri = null;
        try {
            uri = URI.create(requestUrl);
        } catch (IllegalArgumentException e) {
            log.error("URL不正确", requestUrl);
            throw new ArcherException(StringUtils.join(requestUrl, "|", e.getMessage()));
        }
        return uri;
    }

    private RespInfo getFunction(CloseableHttpClient httpClient, String baseUrl, String protocolType) throws ArcherException {
        HttpGet httpGet = new HttpGet();
        // 设置URL
        String requestUrl = constructRequestUriString(baseUrl, protocolType);
        // 设置Header
        httpHeaderSetter(httpGet);
        // 设置参数
        JSONObject bodyJson = apiItem.getBody();
        if (Objects.nonNull(bodyJson)) {
            // 如果有请求参数
            List<NameValuePair> bodyList = new ArrayList<>();
            for (Map.Entry<String, Object> body : bodyJson.entrySet())
                bodyList.add(new BasicNameValuePair(body.getKey(), body.getValue().toString()));

            // 对参数进行转码
            String bodySequence = null;
            try {
                bodySequence = EntityUtils.toString(new UrlEncodedFormEntity(bodyList, Consts.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 设置请求URL
            requestUrl = StringUtils.join(requestUrl, "?", bodySequence);
        }
        // 判断URL是否合法
        URI uri = checkUriString(requestUrl);
        httpGet.setURI(uri);
        // 执行请求,拿到请求响应
        return executeRequest(httpClient, httpGet);
    }

    private RespInfo postFunction(CloseableHttpClient httpClient, String baseUrl, String protocolType) throws ArcherException {
        HttpPost httpPost = new HttpPost();
        // 设置请求URL
        String requestUrl = constructRequestUriString(baseUrl, protocolType);
        // 判断URL是否合法
        URI uri = checkUriString(requestUrl);
        httpPost.setURI(uri);
        // 设置Header
        httpHeaderSetter(httpPost);

        StringEntity requestEntity = null;
        JSONObject bodyContent = apiItem.getBody();                                                    // 拿到请求体
        // 如果请求体不为空,则需要进一步判断Content-Type, 如果请求体为空,则直接执行请求
        if (!Objects.equals(bodyContent.size(), 0)) {
            JSONObject headerJson = apiItem.getHeader();
            if (Objects.nonNull(headerJson)) {
                Object contentType = headerJson.get("Content-Type");                                   // 从Header中取出Content-Type
                if (Objects.nonNull(contentType)) {
                    String reqContentType = contentType.toString();                                    // 如果请求体是非空,则设置请求实体
                    if (reqContentType.contains("application/x-www-form-urlencoded")) {                // 根据Content-Type来判断是用Form提交还是用JSON提交
                        requestEntity = getFormBody();
                        httpPost.setEntity(requestEntity);
                    } else if (reqContentType.contains("application/json")) {
                        requestEntity = getJsonBody();
                        httpPost.setEntity(requestEntity);
                    } else {
                        log.error("未知的Content-Type: {}", reqContentType);
                        throw new ArcherException(StringUtils.join(requestUrl, "|", "未知的Content-Type"));
                    }
                } else {
                    log.error("有请求体,但没有设置Content-Type");
                    throw new ArcherException(StringUtils.join(requestUrl, "|", "有请求体,未设置Content-Type"));
                }
            } else {
                log.error("有请求体,但Header为空");
                throw new ArcherException(StringUtils.join(requestUrl, "|", "有请求体,但Header为空"));
            }
        }
        // 执行请求,拿到请求响应
        return executeRequest(httpClient, httpPost);
    }

    private RespInfo putFunction(CloseableHttpClient httpClient, String baseUrl, String protocolType) throws ArcherException {
        HttpPut httpPut = new HttpPut();
        // 设置请求URL
        String requestUrl = constructRequestUriString(baseUrl, protocolType);
        // 判断URL是否合法
        URI uri = checkUriString(requestUrl);
        httpPut.setURI(uri);
        // 设置Header
        httpHeaderSetter(httpPut);

        StringEntity requestEntity = null;
        JSONObject bodyContent = apiItem.getBody();
        // 如果请求体不为空,则需要进一步判断Content-Type, 如果请求体为空,则直接执行请求
        if (!Objects.equals(bodyContent.size(), 0)) {
            JSONObject headerJson = apiItem.getHeader();
            if (Objects.nonNull(headerJson)) {
                Object contentType = headerJson.get("Content-Type");
                if (Objects.nonNull(contentType)) {
                    String reqContentType = contentType.toString();
                    if (reqContentType.contains("application/json")) {
                        requestEntity = getJsonBody();
                        httpPut.setEntity(requestEntity);
                    } else {
                        log.error("PUT请求Content-Type需设置为application/json");
                        throw new ArcherException(StringUtils.join(requestUrl, "|", "PUT请求Content-Type需设置为application/json"));
                    }
                } else {
                    log.error("有请求体,未设置Content-Type");
                    throw new ArcherException(StringUtils.join(requestUrl, "|", "未设置Content-Type"));
                }
            } else {
                log.error("有请求体,但Header为空");
                throw new ArcherException(StringUtils.join(requestUrl, "|", "有请求体,但Header为空"));
            }
        }
        // 执行请求,拿到请求响应
        return executeRequest(httpClient, httpPut);
    }

    private RespInfo deleteFunction(CloseableHttpClient httpClient, String baseUrl, String protocolType) throws ArcherException {
        HttpDelete httpDelete = new HttpDelete();
        // 设置请求URL
        String requestUrl = constructRequestUriString(baseUrl, protocolType);
        // 判断URL是否合法
        URI uri = checkUriString(requestUrl);
        httpDelete.setURI(uri);
        // 设置Header
        httpHeaderSetter(httpDelete);
        // 执行请求,拿到请求响应
        return executeRequest(httpClient, httpDelete);
    }

    /**
     * @description: 从DB中取出请求(Header), 设置给请求对象
     * @author: Zhao.Peng
     * @date: 2018/8/13
     * @time: 11:37
     * @param: httpMessage
     * @return:
     */
    private void httpHeaderSetter(AbstractHttpMessage httpMessage) {
        // 设置Header
        JSONObject headerJson = apiItem.getHeader();
        if (Objects.nonNull(headerJson))
            // 如果Header不是空的,则将Header添加到http请求当中
            for (Map.Entry<String, Object> header : headerJson.entrySet())
                // 避免出现Content-Length header already present的问题
                if (!Objects.equals(header.getKey(), "Content-Length"))
                    httpMessage.setHeader(header.getKey(), header.getValue().toString());
    }

    /**
     * @description: 从DB中取出请求(表单)转成StringEntity, 供请求方法使用
     * @author: Zhao.Peng
     * @date: 2018/8/13
     * @time: 11:33
     * @param:
     * @return: bodyEntity/null
     */
    private StringEntity getFormBody() {
        // 设置表单参数
        JSONObject bodyJson = apiItem.getBody();
        if (Objects.nonNull(bodyJson)) {
            List<NameValuePair> bodyList = new ArrayList<>();
            for (Map.Entry<String, Object> body : bodyJson.entrySet())
                bodyList.add(new BasicNameValuePair(body.getKey(), body.getValue().toString()));
            return new UrlEncodedFormEntity(bodyList, Consts.UTF_8);
        } else {
            return null;
        }
    }

    /**
     * @description: 将DB中取出的请求体(JSON)转换成StringEntity, 供请求方法使用
     * @author: Zhao.Peng
     * @date: 2018/8/13
     * @time: 11:32
     * @param:
     * @return: bodyEntity/null
     */
    private StringEntity getJsonBody() {
        // 设置JSON参数
        JSONObject bodyJson = apiItem.getBody();
        if (Objects.nonNull(bodyJson)) {
            StringEntity bodyEntity = new StringEntity(bodyJson.toJSONString(), Consts.UTF_8);
            bodyEntity.setContentEncoding("UTF-8");
            return bodyEntity;
        } else {
            return null;
        }
    }

    /**
     * @description: 执行请求, 返回请求响应
     * @author: Zhao.Peng
     * @date: 2018/8/13
     * @time: 11:17
     * @param: httpClient, httpUriRequest
     * @return: response/null
     */
    private RespInfo executeRequest(CloseableHttpClient httpClient, HttpUriRequest httpUriRequest) throws ArcherException {
        CloseableHttpResponse response = null;
        RespInfo respInfo = null;
        try {
            // 响应时间计算
            long startTime = System.currentTimeMillis();
            response = httpClient.execute(httpUriRequest);
            long endTime = System.currentTimeMillis();
            long respTime = endTime - startTime;

            // 获取响应实体
            HttpEntity responseEntity = response.getEntity();

            // 处理Response Header
            JSONObject respHeaderJson = new JSONObject();
            JSONArray respHeaderArray = new JSONArray();
            Header [] headers = response.getAllHeaders();
            for (Header header: headers) {
                if (!Objects.equals("Set-Cookie", header.getName()))
                    respHeaderJson.put(header.getName(), header.getValue());
                else
                    respHeaderArray.add(header.getValue());
            }
            if (!Objects.equals(0, respHeaderArray.size()))
                respHeaderJson.put("Set-Cookie", respHeaderArray);

            // 获取响应正文
            String respContent = EntityUtils.toString(responseEntity);
            respInfo = new RespInfo();
            respInfo.setApiName(apiItem.getApiName());
            respInfo.setUrl(httpUriRequest.getURI().toString());
            respInfo.setStatusCode(response.getStatusLine().getStatusCode());
            respInfo.setRespHeader(JSON.toJSONString(respHeaderJson));
            respInfo.setRespContent(respContent);
            respInfo.setRespTime(respTime);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ArcherException(StringUtils.join(httpUriRequest.getURI().toString(), "|", e.getMessage()));
        } finally {
            try {
                if (Objects.nonNull(response))      // 断开连接资源
                    response.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return respInfo;
    }
}
