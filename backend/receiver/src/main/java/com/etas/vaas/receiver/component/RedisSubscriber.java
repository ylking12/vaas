/** SOURCE: Decompiled from receiver.jar | ORIGINAL: com.etas.vaas.receiver.component.RedisSubscriber | STATUS: Restored */
package com.etas.vaas.receiver.component;

import com.etas.vaas.common.component.DebugDeviceCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class RedisSubscriber implements MessageListener {
    private static final Logger log = LoggerFactory.getLogger(RedisSubscriber.class);

    private final DebugDeviceCache debugDeviceCache;

    public RedisSubscriber(DebugDeviceCache debugDeviceCache) {
        this.debugDeviceCache = debugDeviceCache;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String operationString = new String(message.getBody());
        log.debug("message body: {}", operationString);
        if (operationString.startsWith("add")) {
            debugDeviceCache.addToDebugSet(operationString.split(":")[1]);
            return;
        }
        if (operationString.startsWith("remove")) {
            debugDeviceCache.removeFromDebugSet(operationString.split(":")[1]);
        }
    }
}
