package com.example.backendtemplate.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
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
    private final SecretKey getSecretKey;


    //retrieve username from jwt token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    //retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    //for retrieving any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().verifyWith(getSecretKey).build().parseSignedClaims(token).getPayload();
    }

    //check if the token has expired
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    //generate token for user
    public String generateToken(UserDetails userDetails) {
        return doGenerateToken(userDetails.getUsername());
    }

    //for testing
    public String GenerateToken(String username){
        return doGenerateToken(username);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateRefreshToken(claims, userDetails.getUsername());
    }

    //while creating the token -
    //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
    //2. Sign the JWT using the HS512 algorithm and secret key.
    private String doGenerateToken(String subject) {
        return JWT.create()
                .withSubject(subject)
                .withClaim("role", "app_user")
                .withIssuedAt(Date.from(Instant.now().atZone(ZoneId.systemDefault()).toInstant()))
                .withIssuer("SPRING_APP_BACKEND")
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtConfig.getJwtTokenValidity() * 1000))
                .sign(Algorithm.HMAC512(jwtConfig.getJwtSecret().getBytes()));
    }

    private String doGenerateRefreshToken(Map<String, Object> claims, String subject) {

        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtConfig.getJwtRefreshTokenValidity() * 1000))
                .signWith(getSecretKey)
                .compact();
    }

    public boolean isVerifyExpiration(String token) {
        String logPrefix = "verifyExpiryJwt -> ";
        try {
            Algorithm algorithm = Algorithm.HMAC512(jwtConfig.getJwtSecret().getBytes()); // Replace with your secret key
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);

            // Extract the expiration claim
            Long expirationTime = decodedJWT.getClaim("exp").asLong();
            long currentTime = System.currentTimeMillis() / 1000; // Convert to seconds

            if (expirationTime != null && expirationTime >= currentTime) {
                logger.info(() -> logPrefix + "JWT is not expired.");
                return true;
            } else {
                logger.info(() -> logPrefix + "JWT is expired.");
            }
        } catch (JWTDecodeException e) {
            logger.log(Level.SEVERE, e, () -> logPrefix + "Invalid JWT format ");
        }
        return false;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
