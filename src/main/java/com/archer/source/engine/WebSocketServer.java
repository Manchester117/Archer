package com.archer.source.engine;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@ServerEndpoint(value = "/webSocket")
@Component
public class WebSocketServer {
    private static int onlineCount = 0;         // 记录WebSocket连接数
    private static CopyOnWriteArrayList<Session> sessionSet = new CopyOnWriteArrayList<>(); // 存放每个客户端对应的WebSocket对象
    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        sessionSet.add(session);                // 将获取的连接会话存入到Set当中
        addOnlineCount();                       // 在线连接数加1
        log.info("有连接加入，当前连接数为：{}", onlineCount);
        log.info("sessionId: {}", session.getId());
        // 注释掉发送到客户端的方法
        sendMessage(session, "连接成功");
    }

    @OnClose
    public void onClose(Session session) {
        sessionSet.remove(session);             // 在Set中删除当前连接会话
        subOnlineCount();                       // 在线连接数减1
        log.info("关闭了一个连接");
    }

    /**
     * @description: 接收客户端消息后调用的方法
     * @author: Zhao.Peng
     * @date: 2018/8/24
     * @time: 13:21
     * @param: message - 消息 / session - 会话
     * @return:
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("来自客户端的消息：{}", message);
        sendMessage(session, "建立连接:" + message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误：{},Session ID: {}", error.getMessage(), session.getId());
        error.printStackTrace();
    }
    
    /** 
    * @description: 获取WebSocket的SessionID,给验证接口结果使用(通过会话界定来判断返回给哪个客户端)
    * @author:      Zhao.Peng 
    * @date:        2018/8/24 
    * @time:        15:14 
    * @param:
    * @return:      SessionID
    */
    public String getSessionId() {
        System.out.println(this.session.getId());
        return this.session.getId();
    }
    
    /** 
    * @description: 向客户端发送接口运行信息 
    * @author:      Zhao.Peng 
    * @date:        2018/8/27 
    * @time:        11:17 
    * @param:       session - 连接会话 / message - 要发送到客户端的信息
    * @return:
    */
    public void sendMessage(Session session, String message) {
        try {
            JSONObject serverMessage = new JSONObject();
            serverMessage.put("info", message);
            serverMessage.put("sessionId", session.getId());
            session.getBasicRemote().sendText(serverMessage.toJSONString());
            log.info(String.format("%s (From Server,Session ID=%s)", message, session.getId()));
        } catch (IOException e) {
            log.error("发送消息出错:{}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @description: WebSocket - 向客户端发送消息
     * @author: Zhao.Peng
     * @date: 2018/8/24
     * @time: 12:19
     * @param: message - 消息内容
     * @return:
     */
    public void sendMessageBySessionId(String sessionId, String message) throws IOException {
        Session session = null;
        for (Session sessionElement : sessionSet) {
            if (sessionElement.getId().equals(sessionId)) {
                session = sessionElement;
                break;
            }
        }
        if (session != null)
            sendMessage(session, message);
        else
            log.warn("没有找到指定ID的会话:{}", sessionId);

    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}
