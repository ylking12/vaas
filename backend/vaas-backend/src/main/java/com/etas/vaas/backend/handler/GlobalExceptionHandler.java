/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.handler.GlobalExceptionHandler
 *  com.etas.vaas.backend.utils.ExceptionUtil
 *  com.etas.vaas.backend.vo.VaaSResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.annotation.Order
 *  org.springframework.http.HttpStatus
 *  org.springframework.web.bind.annotation.ExceptionHandler
 *  org.springframework.web.bind.annotation.ResponseStatus
 *  org.springframework.web.bind.annotation.RestControllerAdvice
 */
package com.etas.vaas.backend.handler;

import com.etas.vaas.backend.utils.ExceptionUtil;
import com.etas.vaas.backend.vo.VaaSResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(value=-1)
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value={Exception.class})
    public VaaSResponse<String> exceptionHandler(Exception exception) {
        log.error("exceptionHandle {}\n{}", exception.getMessage(), ExceptionUtil.stacktraceToString(exception));
        return VaaSResponse.error((String)exception.getMessage());
    }
}

