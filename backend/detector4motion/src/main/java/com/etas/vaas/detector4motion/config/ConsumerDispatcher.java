/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.detector4motion.config.ConsumerDispatcher
 *  com.etas.vaas.detector4motion.consumer.Consumer4Motion
 *  jakarta.annotation.PostConstruct
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.annotation.Configuration
 */
package com.etas.vaas.detector4motion.config;

import com.etas.vaas.detector4motion.consumer.Consumer4Motion;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsumerDispatcher {
    @Resource
    private ApplicationContext applicationContext;
    private static final int THREAD_COUNT = 5;
    private final ExecutorService[] executors = new ExecutorService[5];
    private final List<Consumer4Motion> consumers4Motion = new ArrayList();

    @PostConstruct
    public void init() {
        for (int i = 0; i < 5; ++i) {
            int finalI = i;
            this.executors[i] = Executors.newSingleThreadExecutor(r -> new Thread(r, "consumer-gid-" + finalI));
            Consumer4Motion consumer = (Consumer4Motion)this.applicationContext.getBean(Consumer4Motion.class);
            this.consumers4Motion.add(consumer);
        }
    }

    public void submitTask(int gid, Runnable task) {
        int threadIndex = gid % 5;
        this.executors[threadIndex].submit(task);
    }

    public List<Consumer4Motion> getConsumers4Motion() {
        return this.consumers4Motion;
    }
}

