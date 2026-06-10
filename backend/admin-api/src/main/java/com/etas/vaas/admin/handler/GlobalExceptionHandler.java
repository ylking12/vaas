/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.dev33.satoken.exception.NotLoginException
 *  com.etas.vaas.admin.ApiResponse
 *  com.etas.vaas.admin.dto.ResponseTemplate
 *  com.etas.vaas.admin.enums.GeneralEnum
 *  com.etas.vaas.admin.exception.AdminException
 *  com.etas.vaas.admin.exception.GeneralException
 *  com.etas.vaas.admin.handler.GlobalExceptionHandler
 *  jakarta.validation.ConstraintViolationException
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.HttpStatusCode
 *  org.springframework.http.ResponseEntity
 *  org.springframework.validation.BindingResult
 *  org.springframework.validation.FieldError
 *  org.springframework.web.bind.MethodArgumentNotValidException
 *  org.springframework.web.bind.annotation.ControllerAdvice
 *  org.springframework.web.bind.annotation.ExceptionHandler
 */
package com.etas.vaas.admin.handler;

import cn.dev33.satoken.exception.NotLoginException;
import com.etas.vaas.admin.ApiResponse;
import com.etas.vaas.admin.dto.ResponseTemplate;
import com.etas.vaas.admin.enums.GeneralEnum;
import com.etas.vaas.admin.exception.AdminException;
import com.etas.vaas.admin.exception.GeneralException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice

@SuppressWarnings({"unchecked", "rawtypes"})
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value={MethodArgumentNotValidException.class})
    public ResponseEntity<ResponseTemplate<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : null;
        return ResponseEntity.ok((errorMessage != null ? new ResponseTemplate(Integer.valueOf(-18), errorMessage) : new ResponseTemplate((ApiResponse)GeneralEnum.UNKNOWN_ERROR)));
    }

    @ExceptionHandler(value={ConstraintViolationException.class})
    public ResponseEntity<ResponseTemplate<String>> handleConstraintViolationException(ConstraintViolationException ex) {
        return new ResponseEntity((Object)new ResponseTemplate(Integer.valueOf(-18), ex.getMessage()), (HttpStatusCode)HttpStatus.OK);
    }

    @ExceptionHandler(value={AdminException.class})
    public ResponseEntity<ResponseTemplate<Void>> handleUserExceptions(AdminException ex) {
        return new ResponseEntity((Object)new ResponseTemplate(ex.getApiResponse()), (HttpStatusCode)HttpStatus.OK);
    }

    @ExceptionHandler(value={GeneralException.class})
    public ResponseEntity<ResponseTemplate<Void>> handleUserExceptions(GeneralException ex) {
        return new ResponseEntity((Object)new ResponseTemplate(ex.getApiResponse()), (HttpStatusCode)HttpStatus.OK);
    }

    @ExceptionHandler(value={NotLoginException.class})
    public ResponseEntity<ResponseTemplate<Void>> handleNotLoginExceptions(NotLoginException ex) {
        log.warn("Unauthorized Access: {}", (Object)ex.getMessage());
        return new ResponseEntity((Object)new ResponseTemplate(Integer.valueOf(-401), "\u672a\u6388\u6743\u8bbf\u95ee, \u8bf7\u767b\u5f55"), (HttpStatusCode)HttpStatus.OK);
    }
}

