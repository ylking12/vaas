/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.dev33.satoken.annotation.SaCheckLogin
 *  com.etas.vaas.admin.controller.UserController
 *  com.etas.vaas.admin.dto.LoginRequest
 *  com.etas.vaas.admin.dto.LoginResponse
 *  com.etas.vaas.admin.dto.ResponseTemplate
 *  com.etas.vaas.admin.service.UserService
 *  jakarta.annotation.Resource
 *  jakarta.validation.Valid
 *  org.springframework.validation.annotation.Validated
 *  org.springframework.web.bind.annotation.CrossOrigin
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.etas.vaas.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.etas.vaas.admin.dto.LoginRequest;
import com.etas.vaas.admin.dto.LoginResponse;
import com.etas.vaas.admin.dto.ResponseTemplate;
import com.etas.vaas.admin.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@CrossOrigin
@RestController
@RequestMapping(value={"/user"})
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping(value={"/login"})
    public ResponseTemplate<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = this.userService.doLogin(request);
        return response.isSuccess() ? new ResponseTemplate(Integer.valueOf(200), "\u767b\u5f55\u6210\u529f", (Object)response) : new ResponseTemplate(Integer.valueOf(403), "\u7528\u6237\u540d\u6216\u5bc6\u7801\u9519\u8bef", (Object)response);
    }

    @GetMapping(value={"/test"})
    public ResponseTemplate<String> test() {
        return new ResponseTemplate(Integer.valueOf(200), "yes", (Object)"yes");
    }

    @SaCheckLogin
    @GetMapping(value={"/get-routes"})
    public ResponseTemplate<List<String>> fakeRoute() {
        return new ResponseTemplate(Integer.valueOf(201), "success", new ArrayList());
    }
}

