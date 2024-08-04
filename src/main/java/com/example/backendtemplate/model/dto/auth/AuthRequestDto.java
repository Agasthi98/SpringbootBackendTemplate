package com.example.backendtemplate.model.dto.auth;

import lombok.*;


@Getter
@Builder
public class AuthRequestDto {
    private String username;
    private String password;
}
