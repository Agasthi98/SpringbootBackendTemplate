package com.example.backendtemplate.model.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignOutResponse {
    private boolean isSignOut;
}
