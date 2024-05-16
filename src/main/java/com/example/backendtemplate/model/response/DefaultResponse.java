package com.example.backendtemplate.model.response;

import com.example.backendtemplate.util.ResponseCodeUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Setter
@Getter
@Builder
public class DefaultResponse {
    private String code;
    private String title;
    private String message;
    private Object data;

    public DefaultResponse() {
    }

    public DefaultResponse(String code, String title, String message, Object data) {
        this.code = code;
        this.title = title;
        this.message = message;
        this.data = data;
    }

    public DefaultResponse(String code, String title, String message) {
        this.code = code;
        this.title = title;
        this.message = message;
        this.data = new HashMap<>();
    }

    public static DefaultResponse success(String title, String message, Object data) {
        return new DefaultResponse(ResponseCodeUtil.SUCCESS_CODE, title, message, data);
    }

    public static DefaultResponse success(String title, String message) {
        return new DefaultResponse(ResponseCodeUtil.SUCCESS_CODE, title, message, new HashMap<String, Object>());
    }

    public static DefaultResponse error(String title, String message, Object data) {
        return new DefaultResponse(ResponseCodeUtil.FAILED_CODE, title, message, data);
    }

    public static DefaultResponse error(String title, String message) {
        return new DefaultResponse(ResponseCodeUtil.FAILED_CODE, title, message, new HashMap<String, Object>());
    }

    public static DefaultResponse internalServerError(String title, String message, Object data) {
        return new DefaultResponse(ResponseCodeUtil.INTERNAL_SERVER_ERROR_CODE, title, message, data);
    }

    public static DefaultResponse internalServerError(String title, String message) {
        return new DefaultResponse(ResponseCodeUtil.INTERNAL_SERVER_ERROR_CODE, title, message, new HashMap<String, Object>());
    }
}
