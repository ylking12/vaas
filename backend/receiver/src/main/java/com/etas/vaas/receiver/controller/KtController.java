/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.common.dto.KtPackageFrame
 *  com.etas.vaas.receiver.controller.KtController
 *  com.etas.vaas.receiver.service.KtService
 *  jakarta.annotation.Resource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RestController
 *  reactor.core.publisher.Mono
 */
package com.etas.vaas.receiver.controller;

import com.etas.vaas.common.dto.KtPackageFrame;
import com.etas.vaas.receiver.service.KtService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class KtController {
    private static final Logger log = LoggerFactory.getLogger(KtController.class);
    @Resource
    private KtService ktService;

    @PostMapping(value={"/kt-data"})
    public Mono<String> receiveKT710(@RequestBody KtPackageFrame kt710) {
        if (this.ktService.handleKT710Post(kt710)) {
            return Mono.just("ok");
        }
        return Mono.just("not ok");
    }
}

