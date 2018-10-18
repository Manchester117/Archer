package com.archer.source;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebSocketConfig {
    /** 
    * @description: 启用WebSocket,注意!部署到Tomcat的时候需要注释掉下面的方法
    * @author:      Zhao.Peng 
    * @date:        2018/8/27 
    * @time:        17:33 
    * @param:
    * @return:      ServerEndpointExporter的实例
    */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
