/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.common.dto.CoordinateFrame
 *  com.etas.vaas.common.utils.JsonUtils
 *  com.etas.vaas.receiver.service.VehicleService
 *  com.etas.vaas.receiver.websocket.CoordinateHandler
 *  jakarta.annotation.Resource
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 *  org.springframework.web.reactive.socket.WebSocketHandler
 *  org.springframework.web.reactive.socket.WebSocketMessage
 *  org.springframework.web.reactive.socket.WebSocketSession
 *  reactor.core.publisher.Mono
 *  reactor.util.annotation.NonNull
 */
package com.etas.vaas.receiver.websocket;

import com.etas.vaas.common.dto.CoordinateFrame;
import com.etas.vaas.common.utils.JsonUtils;
import com.etas.vaas.receiver.service.VehicleService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

@Component
public class CoordinateHandler
implements WebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(CoordinateHandler.class);
    @Resource
    private VehicleService coordinateService;

    @NonNull
    public Mono<Void> handle(WebSocketSession session) {
        return session.receive().map(WebSocketMessage::getPayloadAsText).doOnNext(payload -> {
            log.debug("[CoordinateChannel] Message: {}", payload);
            if (StringUtils.isEmpty((CharSequence)payload)) {
                log.warn("receive a blank msg from coordinate channel");
                return;
            }
            CoordinateFrame coordinateFrame = (CoordinateFrame)JsonUtils.toObj((String)payload, CoordinateFrame.class);
            log.debug("CoordinateFrame obj: {}", (Object)coordinateFrame);
            if (coordinateFrame == null) {
                throw new IllegalArgumentException("CoordinateFrame is null!");
            }
            this.coordinateService.appendCoordinateData(coordinateFrame);
        }).then();
    }
}

