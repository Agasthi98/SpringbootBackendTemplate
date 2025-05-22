package com.example.backendtemplate.model.request.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserLoginRequest {
    @NotEmpty(message = "username shouldn't be empty")
    private String username;
    @NotEmpty(message = "password shouldn't be empty")
    private String password;
    private String fingerPrint;
}
