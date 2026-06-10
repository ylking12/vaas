/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.component.RedisSubscriber
 *  com.etas.vaas.backend.controller.web.SSEController
 *  com.etas.vaas.backend.service.web.SSEService
 *  jakarta.annotation.Resource
 *  org.reactivestreams.Publisher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.web.bind.annotation.CrossOrigin
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.RestController
 *  reactor.core.publisher.Flux
 */
package com.etas.vaas.backend.controller.web;

import com.etas.vaas.backend.component.RedisSubscriber;
import com.etas.vaas.backend.service.web.SSEService;
import jakarta.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@CrossOrigin
@RestController
public class SSEController {
    private static final Logger log = LoggerFactory.getLogger(SSEController.class);
    private static final String dateTimePattern = "yyyy-MM-dd HH:mm:ss.SSSSSS";
    @Resource
    SSEService sseService;
    @Resource
    private RedisSubscriber redisSubscriber;

    @GetMapping(value={"/stream_data"}, produces={"text/event-stream"})
    public Flux<Map<String, Object>> streamData() {
        Flux dateTime = Flux.interval((Duration)Duration.ofSeconds(5L)).map(tick -> this.getDateTime());
        Flux eventStream = this.redisSubscriber.getMessageFlux().map(eventJson -> this.sseService.generateEventItem(eventJson));
        return Flux.merge((Publisher[])new Publisher[]{dateTime, eventStream});
    }

    private Map<String, Object> getDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimePattern);
        HashMap<String, Object> output = new HashMap<String, Object>();
        output.put("data", now.format(formatter));
        return output;
    }

    public SSEController(SSEService sseService, RedisSubscriber redisSubscriber) {
        this.sseService = sseService;
        this.redisSubscriber = redisSubscriber;
    }
}

