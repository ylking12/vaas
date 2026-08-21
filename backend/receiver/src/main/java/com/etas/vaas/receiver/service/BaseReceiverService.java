/** SOURCE: Decompiled from receiver.jar | ORIGINAL: com.etas.vaas.receiver.service.BaseReceiverService | STATUS: Restored */
package com.etas.vaas.receiver.service;

import com.etas.vaas.common.log.DeviceLogger;
import com.etas.vaas.common.utils.RedisUtils;
import jakarta.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseReceiverService {
    private static final Logger log = LoggerFactory.getLogger(BaseReceiverService.class);

    protected final int countInterval;
    protected final String dataType;

    @Resource
    private DeviceLogger deviceLogger;

    @Resource
    private RedisUtils redisUtils;

    protected final ConcurrentHashMap<String, Integer> heartbeatMap = new ConcurrentHashMap<>();

    protected BaseReceiverService(int countInterval, String dataType) {
        this.countInterval = countInterval;
        this.dataType = dataType;
    }

    public int getCountInterval() {
        return countInterval;
    }

    protected void setHeartbeat(String deviceId) {
        int count = heartbeatMap.compute(deviceId, (key, value) -> value == null ? 1 : value + 1);
        if (count % countInterval != 0) {
            return;
        }
        deviceLogger.debug(deviceId, "setting heartbeat of {} data", dataType);
        log.trace("setting heartbeat of {} data", dataType);
        redisUtils.setHashValue("vaas:heartbeat:" + dataType, deviceId,
                String.valueOf(System.currentTimeMillis()));
        heartbeatMap.put(deviceId, 0);
    }
}
