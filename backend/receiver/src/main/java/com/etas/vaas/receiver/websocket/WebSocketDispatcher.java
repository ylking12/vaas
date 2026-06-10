/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.receiver.websocket.CoordinateHandler
 *  com.etas.vaas.receiver.websocket.MotionHandler
 *  com.etas.vaas.receiver.websocket.WebSocketConfig
 *  com.etas.vaas.receiver.websocket.WebSocketDispatcher
 *  jakarta.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.web.reactive.HandlerMapping
 *  org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
 *  org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
 */
package com.etas.vaas.receiver.websocket;

import com.etas.vaas.receiver.websocket.CoordinateHandler;
import com.etas.vaas.receiver.websocket.MotionHandler;
import com.etas.vaas.receiver.websocket.WebSocketConfig;
import jakarta.annotation.Resource;
import java.util.HashMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

@Configuration
public class WebSocketDispatcher {
    @Resource
    private WebSocketConfig webSocketConfig;

    @Bean
    public HandlerMapping webSocketMapping(CoordinateHandler coordinateHandler, MotionHandler motionHandler) {
        HashMap<String, Object> handlerMap = new HashMap<String, Object>();
        handlerMap.put(this.webSocketConfig.getLocationPath(), coordinateHandler);
        handlerMap.put(this.webSocketConfig.getMotionPath(), motionHandler);
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(handlerMap);
        mapping.setOrder(-1);
        return mapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}

