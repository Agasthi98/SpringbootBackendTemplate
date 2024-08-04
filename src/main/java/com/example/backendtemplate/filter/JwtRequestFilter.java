package com.example.backendtemplate.filter;

import com.auth0.jwt.exceptions.*;
import com.example.backendtemplate.config.JwtConfig;
import com.example.backendtemplate.exception.UserSessionExpiredException;
import com.example.backendtemplate.model.dto.AuthUserDetailsService;
import com.example.backendtemplate.model.response.DefaultResponse;
import com.example.backendtemplate.util.constants.AppConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

@Component
@Order(2)
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtConfig jwtConfig;
    private final SecretKey getSecretKey;
    private final AuthUserDetailsService authUserDetailsService;
    private static final Logger logWriter = Logger.getLogger(AppConstants.APP_LOG);


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String logPrefix = "jwtFilter: ";

        String token = request.getHeader("Authorization");

        if (!StringUtils.hasLength(token) || !token.startsWith("Bearer ")) {
            logWriter.info(() -> logPrefix + "Auth token not found");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            logWriter.info(() -> logPrefix + "Algorithm " + getSecretKey.getAlgorithm());

            filterChain.doFilter(request, response);

        } catch (AlgorithmMismatchException e) {
            logWriter.severe(() -> logPrefix + "Exception: JWT algorithm mismatched");
            DefaultResponse defaultResponse = buildResponse(HttpServletResponse.SC_UNAUTHORIZED,AppConstants.FAILED_TITLE,AppConstants.INVALID_CREDENTIALS);
            generateErrorResponse(response, defaultResponse);
        } catch (SignatureVerificationException e) {
            logWriter.severe(() -> logPrefix + "Exception: JWT signature verification failed");
            DefaultResponse defaultResponse = buildResponse(HttpServletResponse.SC_UNAUTHORIZED,AppConstants.FAILED_TITLE,AppConstants.INVALID_CREDENTIALS);
            generateErrorResponse(response, defaultResponse);
        } catch (TokenExpiredException e) {
            logWriter.severe(() -> logPrefix + "Exception: JWT expired");
            DefaultResponse defaultResponse = buildResponse(HttpServletResponse.SC_UNAUTHORIZED,AppConstants.FAILED_TITLE,"session_timeout");

            generateErrorResponse(response, defaultResponse);
        } catch (InvalidClaimException e) {
            logWriter.severe(() -> logPrefix + "Exception: JWT claim not valid");
            DefaultResponse defaultResponse = buildResponse(HttpServletResponse.SC_UNAUTHORIZED,AppConstants.FAILED_TITLE,AppConstants.INVALID_CREDENTIALS);
            generateErrorResponse(response, defaultResponse);
        } catch (JWTVerificationException e) {
            logWriter.severe(() -> logPrefix + "Exception: JWT verification failed");
            DefaultResponse defaultResponse = buildResponse(HttpServletResponse.SC_UNAUTHORIZED,AppConstants.FAILED_TITLE,AppConstants.INVALID_CREDENTIALS);
            generateErrorResponse(response, defaultResponse);
        } catch (UserSessionExpiredException e) {
            logWriter.severe(() -> logPrefix + "Exception: user Session Expired, " + e.getMessage());
            DefaultResponse defaultResponse = buildResponse(HttpServletResponse.SC_UNAUTHORIZED,AppConstants.FAILED_TITLE,"Session timeout");

            generateErrorResponse(response, defaultResponse, HttpServletResponse.SC_PAYMENT_REQUIRED);
        } catch (Exception e) {
            logWriter.severe(() -> logPrefix + "doFilterInternal-> Exception: " + e.getMessage());
            DefaultResponse defaultResponse = buildResponse(HttpServletResponse.SC_UNAUTHORIZED,AppConstants.FAILED_TITLE,AppConstants.INVALID_CREDENTIALS);
            generateErrorResponse(response, defaultResponse);
        } finally {
            MDC.remove(AppConstants.MDC_UID_KEY);
            MDC.remove(AppConstants.MDC_IAM_TOKEN);
            MDC.remove(AppConstants.MDC_APIM_TOKEN);
        }

    }

    private void generateErrorResponse(HttpServletResponse response, DefaultResponse defaultResponse) throws IOException {
        generateErrorResponse(response, defaultResponse, HttpServletResponse.SC_UNAUTHORIZED);
    }

    private void generateErrorResponse(HttpServletResponse response, DefaultResponse defaultResponse, int status) throws IOException {
        PrintWriter writer = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        writer.print(new ObjectMapper().writeValueAsString(defaultResponse));
    }

    private DefaultResponse buildResponse(int code,String title,String message){
        return DefaultResponse.builder()
                .code(String.valueOf(code))
                .title(title)
                .message(message)
                .build();

    }
}
