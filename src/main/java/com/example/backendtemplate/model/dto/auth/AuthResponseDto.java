package com.example.backendtemplate.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class AuthResponseDto {
    @JsonProperty("access_token")
    private String accessToken;
}
