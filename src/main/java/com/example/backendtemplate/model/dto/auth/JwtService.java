package com.example.backendtemplate.model.dto.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.backendtemplate.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Transient;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class JwtService implements Serializable {

    private static final Logger logger = Logger.getLogger("APP_LOG");
    private final transient JwtConfig jwtConfig;
    private final transient Algorithm getSecretKey;


    public String createJwtToken(TokenRequest tokenRequest) {
        return JWT.create()
                .withSubject(tokenRequest.getUsername())
                .withClaim("role", tokenRequest.getRole())
                .withIssuedAt(Date.from(tokenRequest.getNow().atZone(ZoneId.systemDefault()).toInstant()))
                .withIssuer("SPRING_BACKEND")
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtConfig.getJwtTokenValidity() * 1000L))
                .sign(getSecretKey);
    }

    public boolean isValidToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(getSecretKey).build();
            DecodedJWT decodedJWT = verifier.verify(token);

            // Extract the expiration claim
            Long expirationTime = decodedJWT.getClaim("exp").asLong();
            long currentTime = System.currentTimeMillis() / 1000; // Convert to seconds

            if ((expirationTime != null) && (expirationTime >= currentTime)) {
                logger.log(Level.INFO, "JWT is not expired");
                return true;
            } else {
                logger.log(Level.INFO, "JWT is expired");
            }
        } catch (JWTDecodeException e) {
            logger.log(Level.WARNING, "Invalid JWT format");
            // Handle the decoding exception here
        } catch (TokenExpiredException e) {
            logger.log(Level.WARNING, "Token is expired");
        }
        return false;
    }

    public String createRefreshToken(TokenRequest tokenRequest) {
        return JWT.create()
                .withSubject(tokenRequest.getUsername())
                .withClaim("role", tokenRequest.getRole())
                .withIssuedAt(Date.from(tokenRequest.getNow().atZone(ZoneId.systemDefault()).toInstant()))
                .withIssuer("SPRING_BACKEND")
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtConfig.getJwtRefreshTokenValidity() * 1000L))
                .sign(getSecretKey);

    }

    public String getUsernameFromToken(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        logger.info("username  " + decodedJWT.getSubject());
        return decodedJWT.getSubject();
    }
}
