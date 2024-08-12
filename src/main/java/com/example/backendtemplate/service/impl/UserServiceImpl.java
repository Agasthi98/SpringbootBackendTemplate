package com.example.backendtemplate.service.impl;

import com.example.backendtemplate.model.dto.auth.AuthResponseDto;
import com.example.backendtemplate.model.dto.auth.JwtService;
import com.example.backendtemplate.model.dto.auth.AuthUserDetailsService;
import com.example.backendtemplate.model.dto.auth.TokenRequest;
import com.example.backendtemplate.model.request.user.UserLoginRequest;
import com.example.backendtemplate.entities.user.User;
import com.example.backendtemplate.model.request.UserRegistrationRequest;
import com.example.backendtemplate.model.response.BaseDetailsResponse;
import com.example.backendtemplate.repository.UserRepository;
import com.example.backendtemplate.service.UserService;
import com.example.backendtemplate.util.MobileUtility;
import com.example.backendtemplate.util.ResponseUtil;
import com.example.backendtemplate.util.constants.LogMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final AuthenticationManager authenticationManager;
    private final AuthUserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Override
    public BaseDetailsResponse<HashMap<String, Object>> userRegistration(UserRegistrationRequest userRegistrationRequest) {
        try {
            log.info(LogMessage.USER + " registration" + " [start]");

            User userResponse = userRepository.findByNic(userRegistrationRequest.getNic());

            if (!ObjectUtils.isEmpty(userResponse)) {
                log.warn(LogMessage.USER_ALREADY_EXIST + " by given nic {}", userRegistrationRequest.getNic());
                return BaseDetailsResponse.<HashMap<String, Object>>builder()
                        .code(ResponseUtil.FAILED_CODE)
                        .title(ResponseUtil.FAILED)
                        .message(LogMessage.USER_ALREADY_EXIST)
                        .build();
            }

            log.info("User doesn't exist by given nic {}", userRegistrationRequest.getNic());

            User user = saveNewUser(userRegistrationRequest);

            if (ObjectUtils.isEmpty(user)) {
                log.error(LogMessage.USER_REGISTRATION_FAILED);
                return BaseDetailsResponse.<HashMap<String, Object>>builder()
                        .code(ResponseUtil.FAILED_CODE)
                        .title(ResponseUtil.FAILED)
                        .message(LogMessage.USER_REGISTRATION_FAILED)
                        .build();
            }
            log.info(LogMessage.USER_REGISTRATION_SUCCESS);
            log.info(LogMessage.USER_REGISTRATION_PREFIX + " [end]");

            return BaseDetailsResponse.<HashMap<String, Object>>builder()
                    .code(ResponseUtil.SUCCESS_CODE)
                    .title(ResponseUtil.SUCCESS)
                    .message(LogMessage.USER_REGISTRATION_SUCCESS)
                    .build();

        } catch (Exception e) {
            log.error(LogMessage.USER_REGISTRATION_FAILED + " with error {}", e.getMessage() + e);
            return null;
        }
    }

    @Override
    public BaseDetailsResponse<AuthResponseDto> login(UserLoginRequest userLoginRequest) {
        try {
            log.info(LogMessage.USER + " login" + " [start]");
            final String username = userLoginRequest.getUsername();
            final String password = userLoginRequest.getPassword();

            User user = userRepository.findOneByUsername(username);

            if (ObjectUtils.isEmpty(user)) {
                log.error("Invalid Username: {}", username);
                return BaseDetailsResponse.<AuthResponseDto>builder()
                        .code(ResponseUtil.FAILED_CODE)
                        .title(ResponseUtil.FAILED)
                        .message("Invalid Username")
                        .build();
            } else {
                return logUser(username, password, user);
            }

        } catch (Exception e) {
            log.error(LogMessage.USER, e, " login" + " with error {}", e.getMessage());
            return null;
        }
    }

    private User saveNewUser(UserRegistrationRequest userRegistrationRequest) {

        log.info("Saving new user [start]");

        String fullName = userRegistrationRequest.getFirstName() + " " + userRegistrationRequest.getLastName();

        User user = User.builder()
                .username(userRegistrationRequest.getUsername())
                .fullName(fullName)
                .nic(userRegistrationRequest.getNic())
                .password(passwordEncoder.encode(userRegistrationRequest.getPassword()))
                .phoneNumber(MobileUtility.formatNumber(userRegistrationRequest.getPhoneNumber()))
                .build();

        userRepository.save(user);

        log.info("Saving new user [end]");
        return user;
    }

    public BaseDetailsResponse<AuthResponseDto> logUser(String username, String password, User user) {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime resetTime = user.getUpdatedDateTime().plusMinutes(5);
        Duration duration = Duration.between(currentTime, resetTime);
        long minutes = Math.abs(duration.toMinutes());

        try {
            if (currentTime.isAfter(resetTime)) {
                user.setLoginAttempts(0);
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            } else {
                if (user.getLoginAttempts() < 3) {
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
                } else {
                    throw new BadCredentialsException("Throw bad credentials exception");
                }
            }
        } catch (BadCredentialsException e) {
            if (user.getLoginAttempts() < 3) {
                user.setLoginAttempts(user.getLoginAttempts() + 1);
                userRepository.save(user);

                log.error("Invalid Username or Password");
                return BaseDetailsResponse.<AuthResponseDto>builder()
                        .code(ResponseUtil.FAILED_CODE)
                        .title(ResponseUtil.FAILED)
                        .message("Invalid Username or Password")
                        .build();
            } else {
                log.warn("login attempts exceeded, Try again after 5 minutes..");
                if (duration.getSeconds() < 60) {
                    long remainingSeconds = duration.getSeconds();
                    return BaseDetailsResponse.<AuthResponseDto>builder()
                            .code(ResponseUtil.FAILED_CODE)
                            .title(ResponseUtil.FAILED)
                            .message("Login attempts exceeded, try again after " + remainingSeconds + " seconds..")
                            .build();
                } else {

                    return BaseDetailsResponse.<AuthResponseDto>builder()
                            .code(ResponseUtil.FAILED_CODE)
                            .title(ResponseUtil.FAILED)
                            .message("Login attempts exceeded, try again after " + minutes + " minutes..")
                            .build();
                }
            }
        }

        /**
         * Reset the login attempts & update record
         */
        user.setLoginAttempts(0);
        userRepository.save(user);

        TokenRequest tokenRequest = TokenRequest.builder()
                .username(user.getUsername())
                .role(user.getUsername())
                .build();

        // Generate JWT token
        String token = jwtService.createJwtToken(tokenRequest);

        //Generate Refresh token
        String refreshToken = jwtService.createRefreshToken(tokenRequest);

        //Build login response
        AuthResponseDto authResponseDto = AuthResponseDto.builder()
                .token(token)
                .refreshToken(refreshToken)
                .build();

        return BaseDetailsResponse.<AuthResponseDto>builder()
                .code(ResponseUtil.SUCCESS_CODE)
                .title(ResponseUtil.SUCCESS)
                .message("Login Successful")
                .data(authResponseDto)
                .build();

    }
}
