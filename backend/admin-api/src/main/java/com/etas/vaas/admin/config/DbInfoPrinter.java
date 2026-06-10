/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.admin.config.DbInfoPrinter
 *  jakarta.annotation.PostConstruct
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.etas.vaas.admin.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.sql.Connection;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DbInfoPrinter {
    private static final Logger log = LoggerFactory.getLogger(DbInfoPrinter.class);
    @Resource
    private DataSource dataSource;
    private String dbName;

    @PostConstruct
    public void printDbName() throws Exception {
        try (Connection connection = this.dataSource.getConnection();){
            String catalog;
            this.dbName = catalog = connection.getCatalog();
            log.info("\u542f\u52a8\u8fde\u63a5\u7684\u6570\u636e\u5e93\u540d: {}", (Object)catalog);
        }
    }

    public String getDbName() {
        return this.dbName;
    }
}

