package com.example.backendtemplate.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

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
    public SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
