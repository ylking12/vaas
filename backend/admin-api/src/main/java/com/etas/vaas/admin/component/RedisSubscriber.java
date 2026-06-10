/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.admin.component.RedisSubscriber
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.data.redis.connection.Message
 *  org.springframework.data.redis.connection.MessageListener
 *  org.springframework.stereotype.Service
 *  reactor.core.publisher.Sinks
 *  reactor.core.publisher.Sinks$Many
 */
package com.etas.vaas.admin.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class RedisSubscriber
implements MessageListener {
    private static final Logger log = LoggerFactory.getLogger(RedisSubscriber.class);
    private final Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

    public void onMessage(Message message, byte[] pattern) {
        String msg = String.valueOf(message.getBody());
        log.trace("receive a log: {}", (Object)msg);
        this.sink.tryEmitNext(msg);
        log.trace("log sent");
    }

    public Sinks.Many<String> getSink() {
        return this.sink;
    }

    public RedisSubscriber() {
    }
}

