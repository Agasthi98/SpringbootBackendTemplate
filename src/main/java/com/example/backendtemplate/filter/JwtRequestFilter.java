package com.example.backendtemplate.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.example.backendtemplate.entities.user.User;
import com.example.backendtemplate.enums.Status;
import com.example.backendtemplate.exception.UserDisabledException;
import com.example.backendtemplate.exception.UserNotFoundException;
import com.example.backendtemplate.exception.UserSessionExpiredException;
import com.example.backendtemplate.model.dto.auth.AuthUserDetailsService;
import com.example.backendtemplate.model.dto.auth.TokenBlackListService;
import com.example.backendtemplate.model.response.DefaultResponse;
import com.example.backendtemplate.repository.UserRepository;
import com.example.backendtemplate.util.ResponseUtil;
import com.example.backendtemplate.util.constants.AppConstants;
import com.example.backendtemplate.util.constants.LogMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.backendtemplate.util.constants.AppConstants.MDC_UID_KEY;

@Component
@Order(2)
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logWriter = Logger.getLogger(AppConstants.APP_LOG);
    private final Algorithm getSecretKey;
    private final AuthUserDetailsService authUserDetailsService;
    private final UserRepository userRepository;
    private final TokenBlackListService tokenBlackListService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        String logPrefix = "JWTRequestFilter:[doFilterInternal] -> ";
        try {

            boolean refreshToken = skipRefreshToken(request);

            if (refreshToken) {
                filterChain.doFilter(request, response);
                return;
            }
            String header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            UsernamePasswordAuthenticationToken authentication = getAuthentication(header, request);
            if (Objects.isNull(authentication)) {
                logWriter.info(() -> logPrefix + "Not authenticated. Public request.");
            } else {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } catch (AlgorithmMismatchException e) {
            logWriter.log(Level.WARNING, () -> logPrefix + "Exception: JWT algorithm mismatched");
            DefaultResponse defaultResponse = DefaultResponse.builder().code(ResponseUtil.JWT_TOKEN_VALIDATE_ERROR_CODE).title(ResponseUtil.FAILED).message(ResponseUtil.INVALID_CREDENTIAL).build();
            generateErrorResponse(response, defaultResponse);
        } catch (SignatureVerificationException e) {
            logWriter.log(Level.WARNING, () -> logPrefix + "Exception: JWT signature verification failed");
            DefaultResponse defaultResponse = DefaultResponse.builder().code(ResponseUtil.JWT_TOKEN_VALIDATE_ERROR_CODE).title(ResponseUtil.FAILED).message(ResponseUtil.INVALID_CREDENTIAL).build();
            generateErrorResponse(response, defaultResponse);
        } catch (TokenExpiredException e) {
            logWriter.log(Level.WARNING, () -> logPrefix + "Exception: JWT expired");
            DefaultResponse defaultResponse = DefaultResponse.builder().code(ResponseUtil.JWT_TOKEN_EXPIRED_ERROR_CODE).title(ResponseUtil.FAILED).message(ResponseUtil.INVALID_CREDENTIAL).build();
            generateErrorResponse(response, defaultResponse);
        } catch (InvalidClaimException e) {
            logWriter.log(Level.WARNING, () -> logPrefix + "Exception: JWT claim not valid");
            DefaultResponse defaultResponse = DefaultResponse.builder().code(ResponseUtil.JWT_TOKEN_VALIDATE_ERROR_CODE).title(ResponseUtil.FAILED).message(ResponseUtil.INVALID_CREDENTIAL).build();
            generateErrorResponse(response, defaultResponse);
        } catch (JWTVerificationException e) {
            logWriter.log(Level.WARNING, () -> logPrefix + "Exception: JWT verification failed");
            DefaultResponse defaultResponse = DefaultResponse.builder().code(ResponseUtil.JWT_TOKEN_VALIDATE_ERROR_CODE).title(ResponseUtil.FAILED).message(ResponseUtil.INVALID_CREDENTIAL).build();
            generateErrorResponse(response, defaultResponse);
        } catch (UserSessionExpiredException e){
            logWriter.log(Level.WARNING, () -> logPrefix + "Exception: User already logged out");
            DefaultResponse defaultResponse = DefaultResponse.builder().code(ResponseUtil.JWT_TOKEN_EXPIRED_ERROR_CODE).title(ResponseUtil.FAILED).message(ResponseUtil.USER_ALREADY_LOGGED_OUT).build();
            generateErrorResponse(response, defaultResponse);
        }catch (Exception e) {
            logWriter.log(Level.WARNING, e, () -> logPrefix + "Exception: " + e.getMessage());

            DefaultResponse defaultResponse = DefaultResponse.builder().code(ResponseUtil.JWT_TOKEN_VALIDATE_ERROR_CODE).title(ResponseUtil.FAILED).message(ResponseUtil.INVALID_CREDENTIAL).build();
            generateErrorResponse(response, defaultResponse);
        } finally {
            MDC.remove(MDC_UID_KEY);
            request.removeAttribute("JWTRequestFilter.FILTERED");
        }
    }

    // Reads the JWT from the Authorization header, and then uses JWT to validate the token
    public UsernamePasswordAuthenticationToken getAuthentication(String token, HttpServletRequest request) {
        validateTokenPresence(token);
        String username = getUsernameFromToken(token);
        UserDetails userDetails = loadUserDetails(username);

        validateUser(userDetails, token, request);

        return buildAuthenticationToken(userDetails, request);
    }

    private void validateTokenPresence(String token) {
        if (token == null) {
            logWriter.log(Level.WARNING, () -> LogMessage.REQUEST_FILTER_PREFIX + "Token not found");
            throw new JWTVerificationException("Token not found");
        } else {
            logWriter.info(() -> LogMessage.REQUEST_FILTER_PREFIX + "Token found");
        }
    }

    private String getUsernameFromToken(String token) {
        String username = JWT.require(getSecretKey).build().verify(token.replace(AppConstants.BEARER, "")).getSubject();

        if (username == null) {
            logWriter.log(Level.WARNING, () -> LogMessage.REQUEST_FILTER_PREFIX + "Username not found");
            throw new JWTVerificationException("Username not found");
        }
        logWriter.log(Level.INFO, () -> LogMessage.REQUEST_FILTER_PREFIX + "Username found: " + username);
        return username;
    }

    private UserDetails loadUserDetails(String username) {
        logWriter.log(Level.INFO, () -> LogMessage.REQUEST_FILTER_PREFIX + "Loading user details for username: " + username);
        return authUserDetailsService.loadUserByUsername(username);
    }

    private void validateUser(UserDetails userDetails, String token, HttpServletRequest request) {
        User user = userRepository.findOneByUsername(userDetails.getUsername());

        if (user == null) {
            logWriter.log(Level.WARNING, () -> LogMessage.REQUEST_FILTER_PREFIX + "User not found for given username");
            throw new UserNotFoundException("User not found for given username");
        }

        if (user.getStatus().equals(Status.DISABLED.name())) {
            logWriter.log(Level.WARNING, () -> LogMessage.REQUEST_FILTER_PREFIX + "User disabled.");
            throw new UserDisabledException("User disabled");
        }

        if (StringUtils.isEmpty(user.getTokenReference())) {
            logWriter.log(Level.WARNING, () -> LogMessage.REQUEST_FILTER_PREFIX + "User Already logged out.");
            throw new UserSessionExpiredException("User Already logged out.");
        }

        request.setAttribute("user", user);
        MDC.put(MDC_UID_KEY, user.getUsername());
    }

    private UsernamePasswordAuthenticationToken buildAuthenticationToken(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authenticationToken;
    }


    private boolean skipRefreshToken(HttpServletRequest httpServletRequest) {
        String[] regs = {"/user/refresh-token"};
        Matcher matcher;
        for (String pathExpr : regs) {
            matcher = Pattern.compile(pathExpr).matcher(httpServletRequest.getServletPath());
            if (matcher.find()) {
                logWriter.info("Request: PATH: " + httpServletRequest.getServletPath());
                return true;
            }
        }
        return false;
    }


    public void generateErrorResponse(HttpServletResponse response, DefaultResponse defaultResponse) throws IOException {
        generateErrorResponse(response, defaultResponse, HttpServletResponse.SC_UNAUTHORIZED);
    }

    private void generateErrorResponse(HttpServletResponse response, DefaultResponse defaultResponse, int httpStatus) throws IOException {
        PrintWriter writer = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(httpStatus);
        writer.print(new ObjectMapper().writeValueAsString(defaultResponse));
    }
}
