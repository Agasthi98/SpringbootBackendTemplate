package com.example.backendtemplate.config;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class JwtConfig {
    @Value("${jwt.validity}")
    private long jwtTokenValidity;

    @Value("${jwt.refresh.validity}")
    private long jwtRefreshTokenValidity;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public Algorithm getJwtSecret() {
        return Algorithm.HMAC512(jwtSecret.getBytes());
    }
}
