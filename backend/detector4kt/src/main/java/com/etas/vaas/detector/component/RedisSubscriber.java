/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.detector.common.Consumer4Kt
 *  com.etas.vaas.detector.component.RedisSubscriber
 *  com.etas.vaas.detector.config.ConsumerDispatcher
 *  jakarta.annotation.PostConstruct
 *  jakarta.annotation.Resource
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.data.redis.connection.Message
 *  org.springframework.data.redis.connection.MessageListener
 *  org.springframework.stereotype.Service
 */
package com.etas.vaas.detector.component;

import com.etas.vaas.detector.common.Consumer4Kt;
import com.etas.vaas.detector.config.ConsumerDispatcher;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class RedisSubscriber
implements MessageListener {
    private static final Logger log = LoggerFactory.getLogger(RedisSubscriber.class);
    private final Set<String> messageBodySet = new HashSet();
    @Resource
    private ConsumerDispatcher dispatcher;

    @PostConstruct
    void init() {
        log.info("current consuming at group: ");
        for (int i = 0; i < 4; ++i) {
            this.messageBodySet.add(String.valueOf(i));
        }
    }

    public void onMessage(Message message, byte[] pattern) {
        String gidString = new String(message.getBody());
        log.debug("message body: {}", (Object)gidString);
        if (!NumberUtils.isDigits((String)gidString)) {
            log.debug("received a msg but it's not a number");
            return;
        }
        int gid = Integer.parseInt(gidString);
        if (this.messageBodySet.contains(gidString)) {
            this.dispatcher.submitTask(gid, () -> ((Consumer4Kt)this.dispatcher.getConsumer4KtList().get(gid)).consume(gidString));
        } else {
            log.debug("mismatched gid, sending: {}, consuming: {}", (Object)gidString, (Object)gidString);
        }
    }

    public RedisSubscriber() {
    }
}

