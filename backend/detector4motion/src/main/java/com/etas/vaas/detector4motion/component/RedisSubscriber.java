/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.detector4motion.component.RedisSubscriber
 *  com.etas.vaas.detector4motion.config.ConsumerDispatcher
 *  com.etas.vaas.detector4motion.consumer.Consumer4Motion
 *  jakarta.annotation.PostConstruct
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.data.redis.connection.Message
 *  org.springframework.data.redis.connection.MessageListener
 *  org.springframework.stereotype.Service
 */
package com.etas.vaas.detector4motion.component;

import com.etas.vaas.detector4motion.config.ConsumerDispatcher;
import com.etas.vaas.detector4motion.consumer.Consumer4Motion;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class RedisSubscriber
implements MessageListener {
    private static final Logger log = LoggerFactory.getLogger(RedisSubscriber.class);
    private final Set<Integer> messageBodySet = new HashSet();
    @Resource
    private ConsumerDispatcher dispatcher;

    @PostConstruct
    void init() {
        log.info("current consuming at group: ");
        for (int i = 0; i < 5; ++i) {
            this.messageBodySet.add(i);
        }
    }

    public void onMessage(Message message, byte[] pattern) {
        log.debug("onMessage thread: {}", (Object)Thread.currentThread().getName());
        String messageBody = new String(message.getBody());
        log.debug("message body: {}", (Object)messageBody);
        Integer groupId = Integer.parseInt(messageBody);
        log.debug("consume group is {}", (Object)groupId);
        if (this.messageBodySet.contains(groupId)) {
            this.dispatcher.submitTask(groupId.intValue(), () -> ((Consumer4Motion)this.dispatcher.getConsumers4Motion().get(groupId)).consume(groupId));
        } else {
            log.debug("mismatched gid, sending: {}", (Object)groupId);
        }
    }

    public RedisSubscriber() {
    }
}

