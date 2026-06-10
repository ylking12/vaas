package com.etas.vaas.admin.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(value={"com.etas.vaas.common.mapper"})
public class MybatisPlusConfig {
    // MyBatis-Plus configuration (pagination interceptor removed - needs mybatis-plus extension)
}
