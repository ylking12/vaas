/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.dev33.satoken.annotation.SaCheckLogin
 *  com.etas.vaas.admin.ApiResponse
 *  com.etas.vaas.admin.controller.LogController
 *  com.etas.vaas.admin.dto.ResponseTemplate
 *  com.etas.vaas.admin.enums.GeneralEnum
 *  com.etas.vaas.admin.service.AdminService
 *  jakarta.annotation.Resource
 *  jakarta.validation.constraints.NotBlank
 *  jakarta.validation.constraints.Pattern
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.validation.annotation.Validated
 *  org.springframework.web.bind.annotation.CrossOrigin
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RequestParam
 *  org.springframework.web.bind.annotation.RestController
 */
package com.etas.vaas.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.etas.vaas.admin.ApiResponse;
import com.etas.vaas.admin.dto.ResponseTemplate;
import com.etas.vaas.admin.enums.GeneralEnum;
import com.etas.vaas.admin.service.AdminService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SaCheckLogin
@Validated
@CrossOrigin
@RestController
@RequestMapping(value={"/log"})
public class LogController {
    private static final Logger log = LoggerFactory.getLogger(LogController.class);
    @Resource
    private AdminService adminService;

    @GetMapping(value={"/device-option"})
    public ResponseTemplate<List<String>> getModelOption() {
        List result = this.adminService.doGetAllDevice();
        return new ResponseTemplate((ApiResponse)GeneralEnum.SUCCESS, (Object)result);
    }

    @GetMapping(value={"/device-debug"})
    public ResponseTemplate<Void> enableDeviceDebug(@RequestParam(value="deviceId") @NotBlank(message="\u8bbe\u5907\u53f7\u4e0d\u80fd\u4e3a\u7a7a") @NotBlank(message="\u8bbe\u5907\u53f7\u4e0d\u80fd\u4e3a\u7a7a") String deviceId, @RequestParam(value="operation") @NotBlank(message="\u64cd\u4f5c\u7b26\u4e0d\u80fd\u4e3a\u7a7a") @Pattern(regexp="^(on|off)$") @NotBlank(message="\u64cd\u4f5c\u7b26\u4e0d\u80fd\u4e3a\u7a7a") @Pattern(regexp="^(on|off)$") String operation) {
        this.adminService.doSwitchDeviceDebug(deviceId, operation);
        return new ResponseTemplate((ApiResponse)GeneralEnum.SUCCESS);
    }
}

