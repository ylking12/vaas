/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.dev33.satoken.stp.StpUtil
 *  com.etas.vaas.admin.dto.LoginRequest
 *  com.etas.vaas.admin.dto.LoginResponse
 *  com.etas.vaas.admin.service.UserService
 *  jakarta.annotation.PostConstruct
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
 *  org.springframework.security.crypto.password.PasswordEncoder
 *  org.springframework.stereotype.Service
 */
package com.etas.vaas.admin.service;

import cn.dev33.satoken.stp.StpUtil;
import com.etas.vaas.admin.dto.LoginRequest;
import com.etas.vaas.admin.dto.LoginResponse;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    @Value(value="${password-path}")
    private String filePath;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Map<String, String> userPassMap = new HashMap();

    @PostConstruct
    void init() {
        log.info("passwd file: {}", (Object)this.filePath);
        this.loadPassFile();
    }

    public LoginResponse doLogin(LoginRequest request) {
        String username = request.getUsername();
        String rawPassword = request.getPassword();
        LoginResponse response = new LoginResponse();
        response.setSuccess(false);
        boolean passwordCorrect = this.passwordEncoder.matches((CharSequence)rawPassword, (String)this.userPassMap.get(username));
        if (passwordCorrect) {
            StpUtil.login((Object)username);
            String token = StpUtil.getTokenValue();
            response.setUsername(username);
            response.setToken(token);
            response.setSuccess(true);
        }
        return response;
    }

    private void loadPassFile() {
        try {
            BufferedReader reader;
            InputStream resourceStream = this.getClass().getClassLoader().getResourceAsStream("passwd");
            if (resourceStream != null) {
                log.info("loading passwd file from resource...");
                reader = new BufferedReader(new InputStreamReader(resourceStream));
            } else {
                log.info("can't find passwd file in resource, loading passwd filepath from config...");
                reader = new BufferedReader(new FileReader(this.filePath));
            }
            try (BufferedReader bufferedReader = reader;){
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length != 2) continue;
                    this.userPassMap.put(parts[0].trim(), parts[1].trim());
                }
                log.info("admin password loaded");
            }
        }
        catch (IOException e) {
            throw new IllegalStateException("\u8bfb\u53d6 passwd \u6587\u4ef6\u5931\u8d25", e);
        }
    }

    public UserService() {
    }
}

