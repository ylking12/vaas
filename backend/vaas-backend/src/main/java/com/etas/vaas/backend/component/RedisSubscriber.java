/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.component.RedisSubscriber
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.data.redis.connection.Message
 *  org.springframework.data.redis.connection.MessageListener
 *  org.springframework.stereotype.Service
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Sinks
 *  reactor.core.publisher.Sinks$EmitResult
 *  reactor.core.publisher.Sinks$Many
 */
package com.etas.vaas.backend.component;

import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class RedisSubscriber
implements MessageListener {
    private static final Logger log = LoggerFactory.getLogger(RedisSubscriber.class);
    private final Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer(1024, false);

    public void onMessage(Message message, byte[] pattern) {
        String messageBody = new String(((byte[]) ((byte[]) message.getBody())), StandardCharsets.UTF_8);
        log.info("Received Redis message: {}", messageBody);
        Sinks.EmitResult result = this.sink.tryEmitNext(messageBody);
        if (result.isFailure()) {
            log.error("Failed to emit Redis message: {}, message: {}", result, messageBody);
            if (result == Sinks.EmitResult.FAIL_OVERFLOW) {
                this.retryEmit(messageBody);
            }
        }
    }

    private void retryEmit(String message) {
        try {
            Thread.sleep(100L);
            this.sink.tryEmitNext(message);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Retry emit interrupted", e);
        }
    }

    public Flux<String> getMessageFlux() {
        return this.sink.asFlux().onErrorResume(e -> {
            log.error("Error in message flux", e);
            return Flux.empty();
        });
    }

    public RedisSubscriber() {
    }
}

