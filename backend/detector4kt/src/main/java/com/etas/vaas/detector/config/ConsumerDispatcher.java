/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.detector.common.Consumer4Kt
 *  com.etas.vaas.detector.config.ConsumerDispatcher
 *  jakarta.annotation.PostConstruct
 *  jakarta.annotation.Resource
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.annotation.Configuration
 */
package com.etas.vaas.detector.config;

import com.etas.vaas.detector.common.Consumer4Kt;
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
    private static final int THREAD_COUNT = 4;
    private final ExecutorService[] executors = new ExecutorService[4];
    private final List<Consumer4Kt> consumer4KtList = new ArrayList();

    @PostConstruct
    public void init() {
        for (int i = 0; i < 4; ++i) {
            int finalI = i;
            this.executors[i] = Executors.newSingleThreadExecutor(r -> new Thread(r, "consumer-gid-" + finalI));
            Consumer4Kt consumer = (Consumer4Kt)this.applicationContext.getBean(Consumer4Kt.class);
            this.consumer4KtList.add(consumer);
        }
    }

    public void submitTask(int gid, Runnable task) {
        int threadIndex = gid % 4;
        this.executors[threadIndex].submit(task);
    }

    public List<Consumer4Kt> getConsumer4KtList() {
        return this.consumer4KtList;
    }
}

