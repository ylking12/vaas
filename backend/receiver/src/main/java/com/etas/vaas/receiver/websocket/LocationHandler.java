/** SOURCE: Decompiled from receiver.jar | ORIGINAL: com.etas.vaas.receiver.websocket.LocationHandler | STATUS: Restored */
package com.etas.vaas.receiver.websocket;

import com.etas.vaas.common.dto.LocationFrame;
import com.etas.vaas.common.utils.JsonUtils;
import com.etas.vaas.receiver.service.PositionService;
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
public class LocationHandler implements WebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(LocationHandler.class);

    @Resource
    private PositionService positionService;

    @NonNull
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(payload -> {
                    log.debug("[LocationChannel] Message: {}", payload);
                    if (StringUtils.isEmpty(payload)) {
                        log.warn("receive a blank msg from location channel");
                        return;
                    }
                    LocationFrame locationFrame = JsonUtils.toObj(payload, LocationFrame.class);
                    if (locationFrame != null) {
                        try {
                            positionService.handlePositionData(locationFrame);
                        } catch (Exception e) {
                            log.error("Unexpected error while handling GPS message. Frame: {}", locationFrame, e);
                        }
                    } else {
                        throw new IllegalArgumentException("locationFrame is null!");
                    }
                })
                .then();
    }
}
