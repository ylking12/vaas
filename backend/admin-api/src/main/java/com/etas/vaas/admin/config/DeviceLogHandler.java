/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.admin.component.RedisSubscriber
 *  com.etas.vaas.admin.config.DeviceLogHandler
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.lang.NonNull
 *  org.springframework.stereotype.Component
 *  org.springframework.web.socket.CloseStatus
 *  org.springframework.web.socket.TextMessage
 *  org.springframework.web.socket.WebSocketMessage
 *  org.springframework.web.socket.WebSocketSession
 *  org.springframework.web.socket.handler.TextWebSocketHandler
 */
package com.etas.vaas.admin.config;

import com.etas.vaas.admin.component.RedisSubscriber;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class DeviceLogHandler
extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(DeviceLogHandler.class);
    @Resource
    private RedisSubscriber redisSubscriber;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket connected: {}", (Object)session.getId());
        session.sendMessage((WebSocketMessage)new TextMessage((CharSequence)"connected"));
        this.scheduler.scheduleAtFixedRate(() -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage((WebSocketMessage)new TextMessage((CharSequence)"heartbeat"));
                }
            }
            catch (IOException e) {
                log.error("Error sending heartbeat", (Throwable)e);
            }
        }, 5L, 5L, TimeUnit.SECONDS);
        this.redisSubscriber.getSink().asFlux().subscribe(msg -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage((WebSocketMessage)new TextMessage((CharSequence)msg));
                }
            }
            catch (IOException e) {
                log.error("Error sending Redis message", (Throwable)e);
            }
        });
    }

    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.debug("Client[{}] says: {}", (Object)session.getId(), message.getPayload());
    }

    @NonNull
    public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus status) {
        log.info("WebSocket closed: {}", (Object)session.getId());
    }
}

