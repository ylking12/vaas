/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.uber.h3core.H3Core
 *  org.springframework.context.annotation.Bean
 *  org.springframework.stereotype.Component
 */
package com.etas.vaas.common.config;

import com.uber.h3core.H3Core;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class H3Config {
    @Bean
    public H3Core h3Core() throws IOException {
        return H3Core.newInstance();
    }
}

