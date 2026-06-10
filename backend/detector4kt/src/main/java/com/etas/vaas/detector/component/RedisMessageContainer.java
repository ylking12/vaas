/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.common.config.RedisKeyConfig
 *  com.etas.vaas.detector.component.RedisMessageContainer
 *  com.etas.vaas.detector.component.RedisSubscriber
 *  jakarta.annotation.Resource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.data.redis.connection.MessageListener
 *  org.springframework.data.redis.connection.RedisConnectionFactory
 *  org.springframework.data.redis.listener.PatternTopic
 *  org.springframework.data.redis.listener.RedisMessageListenerContainer
 *  org.springframework.data.redis.listener.Topic
 */
package com.etas.vaas.detector.component;

import com.etas.vaas.common.config.RedisKeyConfig;
import com.etas.vaas.detector.component.RedisSubscriber;
import jakarta.annotation.Resource;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;

@Configuration
public class RedisMessageContainer {
    private static final Logger log = LoggerFactory.getLogger(RedisMessageContainer.class);
    @Resource
    private RedisKeyConfig redisKeyConfig;

    @Bean
    public RedisMessageListenerContainer messageListenerContainer(RedisConnectionFactory redisConnectionFactory, RedisSubscriber redisSubscriber) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.setTaskExecutor((Executor)Executors.newSingleThreadExecutor());
        String channel = this.redisKeyConfig.getInstance().getKtTopic();
        log.info("Sub on channel: {}", (Object)channel);
        container.addMessageListener((MessageListener)redisSubscriber, (Topic)new PatternTopic(channel));
        return container;
    }
}

