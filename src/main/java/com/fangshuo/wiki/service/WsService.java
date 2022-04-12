package com.fangshuo.wiki.service;

import com.fangshuo.wiki.websocket.WebSocketServer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
@Service
public class WsService {

    @Resource
    private WebSocketServer webSocketServer;

    @Async
    public void sendInfo(String message){
        // 推送消息, 不要把具体的业务信息写到这里面来，让它只负责发送信息
        webSocketServer.sendInfo(message);
    }

}
