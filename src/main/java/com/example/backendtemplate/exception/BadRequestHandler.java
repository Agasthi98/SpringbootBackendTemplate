package com.example.backendtemplate.exception;

import com.example.backendtemplate.util.constants.MessageUtil;
import com.example.backendtemplate.model.response.DefaultResponse;
import com.example.backendtemplate.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BadRequestHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("BadRequestHandler-> input validation error");

        Map<String, Object> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        DefaultResponse thirdPartyDefaultResponse = DefaultResponse.builder()
                .code(ResponseUtil.INPUT_VALIDATION_ERROR_CODE)
                .title(ResponseUtil.FAILED)
                .message(MessageUtil.INPUT_VALIDATION_ERROR)
                .data(fieldErrors)
                .build();

        return ResponseEntity.badRequest().body(thirdPartyDefaultResponse);
    }

    // Handle AccessDeniedException (access control errors)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<DefaultResponse> handleAccessDenied(AccessDeniedException ex) {
        log.warn("BadRequestHandler-> Access Denied: {}", ex.getMessage());

        DefaultResponse response = DefaultResponse.builder()
                .code(ResponseUtil.FAILED_CODE)
                .title(ResponseUtil.FAILED)
                .message(MessageUtil.ACCESS_DENIED)
                .data(null)  // You can add more details here if needed
                .build();

        return ResponseEntity.status(403).body(response);  // HTTP 403 Forbidden
    }
}
