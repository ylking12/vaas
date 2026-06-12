/*
 * SOURCE: Restored from decompiled vaas-backend.jar
 * STATUS: Restored - OpenAPI annotation added (P8-8.10)
 * DATE: 2026-06-12
 */
package com.etas.vaas.backend;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages={"com.etas.vaas.backend", "com.etas.vaas.common"})
@MapperScan(value={"com.etas.vaas.common.mapper"})
@OpenAPIDefinition(
    info = @Info(
        title = "VaaS Backend API",
        version = "0.15.0",
        description = "城市级道路状态感知与预警系统 - 核心业务 API"
    )
)
public class VaaSBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(VaaSBackendApplication.class, (String[])args);
    }
}

