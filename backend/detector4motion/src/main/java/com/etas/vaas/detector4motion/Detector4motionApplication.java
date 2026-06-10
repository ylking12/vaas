/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.detector4motion.Detector4motionApplication
 *  org.mybatis.spring.annotation.MapperScan
 *  org.springframework.boot.SpringApplication
 *  org.springframework.boot.autoconfigure.SpringBootApplication
 *  org.springframework.scheduling.annotation.EnableScheduling
 */
package com.etas.vaas.detector4motion;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages={"com.etas.vaas.detector4motion", "com.etas.vaas.common"})
@MapperScan(value={"com.etas.vaas.common.mapper"})
public class Detector4motionApplication {
    public static void main(String[] args) {
        SpringApplication.run(Detector4motionApplication.class, (String[])args);
    }
}

