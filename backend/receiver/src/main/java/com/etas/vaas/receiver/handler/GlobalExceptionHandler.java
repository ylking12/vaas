/** SOURCE: Decompiled from receiver.jar | ORIGINAL: com.etas.vaas.receiver.handler.GlobalExceptionHandler | STATUS: Restored */
package com.etas.vaas.receiver.handler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String MSG_PREFIX = "Validation Failed:";

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<String>> handleBindException(WebExchangeBindException ex) {
        String message = ex.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Params Binding error");
        return Mono.just(ResponseEntity.status(HttpStatus.OK).body(MSG_PREFIX + " " + message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<String>> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .findFirst()
                .orElse("Params Binding error");
        return Mono.just(ResponseEntity.status(HttpStatus.OK).body(MSG_PREFIX + " " + message));
    }

    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<String>> handleServerWebInputException(ServerWebInputException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.OK).body(MSG_PREFIX + " " + ex.getMessage()));
    }
}
