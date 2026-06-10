/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.common.config.RedisKeyConfig
 *  com.etas.vaas.common.dao.VehicleEventDao
 *  com.etas.vaas.common.dto.MotionFrame
 *  com.etas.vaas.common.service.VehicleEventService
 *  com.etas.vaas.common.utils.JsonUtils
 *  com.etas.vaas.common.utils.RedisUtils
 *  com.etas.vaas.detector4motion.consumer.Consumer4Motion
 *  com.etas.vaas.detector4motion.processor.BumpyProcessor4Motion
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.annotation.Scope
 *  org.springframework.stereotype.Component
 */
package com.etas.vaas.detector4motion.consumer;

import com.etas.vaas.common.config.RedisKeyConfig;
import com.etas.vaas.common.dao.VehicleEventDao;
import com.etas.vaas.common.dto.MotionFrame;
import com.etas.vaas.common.service.VehicleEventService;
import com.etas.vaas.common.utils.JsonUtils;
import com.etas.vaas.common.utils.RedisUtils;
import com.etas.vaas.detector4motion.processor.BumpyProcessor4Motion;
import jakarta.annotation.Resource;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value="prototype")
public class Consumer4Motion {
    private static final Logger log = LoggerFactory.getLogger(Consumer4Motion.class);
    @Resource
    private BumpyProcessor4Motion bumpIdentifier;
    @Resource
    private RedisKeyConfig redisKeyConfig;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private VehicleEventService vehicleEventService;

    public void consume(Integer groupId) {
        log.debug("consumer thread: {}", (Object)Thread.currentThread().getName());
        String listKey = this.redisKeyConfig.getInstance().getMotionQueue() + groupId;
        try {
            long listSize = this.redisUtils.listSize(listKey);
            log.debug("queue size: {}", (Object)listSize);
            if (listSize == 0L) {
                log.debug("queue {}, is empty", (Object)listKey);
                return;
            }
            List listOfRawMotions = this.redisUtils.leftPopWithCount(listKey, listSize);
            for (Object motionObj : listOfRawMotions) {
                String motionString = (String) motionObj;
                MotionFrame frame = (MotionFrame)JsonUtils.parseJson((String)motionString, MotionFrame.class);
                VehicleEventDao bumpEvent = this.bumpIdentifier.identify(frame);
                if (bumpEvent == null) continue;
                log.info("detected bumpy event :{}", (Object)bumpEvent);
                log.info("JSON detected bumpy event :{}", (Object)JsonUtils.toStr((Object)bumpEvent));
                this.vehicleEventService.handleEvent(bumpEvent);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }
}

