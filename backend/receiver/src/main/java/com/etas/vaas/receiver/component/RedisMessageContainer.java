/** SOURCE: Decompiled from receiver.jar | ORIGINAL: com.etas.vaas.receiver.component.RedisMessageContainer | STATUS: Restored */
package com.etas.vaas.receiver.component;

import com.etas.vaas.common.config.RedisKeyConfig;
import jakarta.annotation.Resource;
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
    public RedisMessageListenerContainer messageListenerContainer(
            RedisConnectionFactory redisConnectionFactory,
            RedisSubscriber redisSubscriber) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.setTaskExecutor(Executors.newSingleThreadExecutor());
        String channel = "vaas:debug:device";
        log.info("Sub on channel: {}", channel);
        container.addMessageListener((MessageListener) redisSubscriber, (Topic) new PatternTopic(channel));
        return container;
    }
}
