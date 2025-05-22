package com.example.backendtemplate.service.impl;

import com.example.backendtemplate.entities.user.Role;
import com.example.backendtemplate.entities.user.UserSession;
import com.example.backendtemplate.model.dto.auth.AuthResponseDto;
import com.example.backendtemplate.model.dto.auth.JwtService;
import com.example.backendtemplate.model.dto.auth.AuthUserDetailsService;
import com.example.backendtemplate.model.dto.auth.TokenRequest;
import com.example.backendtemplate.model.request.user.UserLoginRequest;
import com.example.backendtemplate.entities.user.User;
import com.example.backendtemplate.model.request.user.UserRegistrationRequest;
import com.example.backendtemplate.model.response.BaseDetailsResponse;
import com.example.backendtemplate.model.response.SignOutResponse;
import com.example.backendtemplate.model.response.UserSessionResponse;
import com.example.backendtemplate.repository.RoleRepository;
import com.example.backendtemplate.repository.UserRepository;
import com.example.backendtemplate.repository.UserSessionRepository;
import com.example.backendtemplate.service.UserService;
import com.example.backendtemplate.util.MobileUtility;
import com.example.backendtemplate.util.ResponseUtil;
import com.example.backendtemplate.util.constants.LogMessage;
import com.example.backendtemplate.util.constants.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final AuthenticationManager authenticationManager;
    private final AuthUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;
    private final UserSessionRepository userSessionRepository;

    @Value("${jwt.validity}")
    private long jwtTokenValidity;

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
                        .message(MessageUtil.USER_REGISTRATION_FAILED_MSG)
                        .build();
            }
            log.info(LogMessage.USER_REGISTRATION_SUCCESS);
            log.info(LogMessage.USER_REGISTRATION_PREFIX + " [end]");

            return BaseDetailsResponse.<HashMap<String, Object>>builder()
                    .code(ResponseUtil.SUCCESS_CODE)
                    .title(ResponseUtil.SUCCESS)
                    .message(MessageUtil.USER_REGISTRATION_SUCCESS_MSG)
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

            User user = findUser(username, true);

            UserSessionResponse sessionResponse = checkUserAlreadyLoggedIn(user, userLoginRequest.getFingerPrint());

            if (!sessionResponse.isValid()) {
                log.warn("Session response {}", sessionResponse.getMessage());
                return BaseDetailsResponse.<AuthResponseDto>builder()
                        .code(ResponseUtil.FAILED_CODE)
                        .title(ResponseUtil.FAILED)
                        .message(sessionResponse.getMessage())
                        .build();
            }

            return logUser(username, password, user, userLoginRequest.getFingerPrint());


        } catch (NullPointerException e) {
            log.error("{} login process with error: {}", LogMessage.USER, LogMessage.CAN_NOT_FIND_USER);
            return BaseDetailsResponse.<AuthResponseDto>builder()
                    .code(ResponseUtil.FAILED_CODE)
                    .title(ResponseUtil.FAILED)
                    .message("Invalid Username")
                    .build();
        } catch (Exception e) {
            log.error(LogMessage.USER, e, " login" + " with error {}", e.getMessage());
            return null;
        }
    }

    @Override
    public BaseDetailsResponse<SignOutResponse> signOut(String token) {
        log.info(LogMessage.USER + " Sign out" + " [start]");
        try {
            User user = findUser(token, false);

            UserSession session = getUserSession(user.getUserId());

            session.setFingerPrint(null);
            session.setRevoked(true);
            user.setTokenReference(null);
            userRepository.save(user);
            userSessionRepository.save(session);

            log.info("{} Sign out [end]", LogMessage.USER);
            return BaseDetailsResponse.<SignOutResponse>builder()
                    .code(ResponseUtil.SUCCESS_CODE)
                    .title(ResponseUtil.SUCCESS)
                    .message("Successfully logging out")
                    .data(SignOutResponse.builder().isSignOut(true).build())
                    .build();

        } catch (NullPointerException e) {
            log.error("{} sign out process with error: {}", LogMessage.USER, LogMessage.CAN_NOT_FIND_USER);
            return BaseDetailsResponse.<SignOutResponse>builder()
                    .code(ResponseUtil.FAILED_CODE)
                    .title(ResponseUtil.FAILED)
                    .message("Invalid Username")
                    .data(SignOutResponse.builder().isSignOut(false).build())
                    .build();
        } catch (Exception e) {
            log.error(LogMessage.USER, e, " sign out" + " with error {}", e.getMessage());
            return null;
        }
    }

    private User saveNewUser(UserRegistrationRequest userRegistrationRequest) {

        log.info("Saving new user [start]");

        String fullName = userRegistrationRequest.getFirstName() + " " + userRegistrationRequest.getLastName();
        //set default role
        Role defaultRole = roleRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        User user = User.builder()
                .username(userRegistrationRequest.getUsername())
                .fullName(fullName)
                .nic(userRegistrationRequest.getNic())
                .password(passwordEncoder.encode(userRegistrationRequest.getPassword()))
                .phoneNumber(MobileUtility.formatNumber(userRegistrationRequest.getPhoneNumber()))
                .build();

        user.getRoles().add(defaultRole);
        userRepository.save(user);

        log.info("Saving new user [end]");
        return user;
    }

    public BaseDetailsResponse<AuthResponseDto> logUser(String username, String password, User user, String fingerPrint) {
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

        TokenRequest tokenRequest = TokenRequest.builder()
                .username(user.getUsername())
                .role(user.getUsername())
                .build();

        String ref = UUID.randomUUID().toString();
        // Generate JWT token
        String token = jwtService.createJwtToken(tokenRequest, ref);

        //Generate Refresh token
        String refreshToken = jwtService.createRefreshToken(tokenRequest, ref);

        //Build login response
        AuthResponseDto authResponseDto = AuthResponseDto.builder()
                .token(token)
                .refreshToken(refreshToken)
                .build();

        user.setTokenReference(ref);
        userRepository.save(user);

        //save user session
        saveUserSession(user, fingerPrint);

        return BaseDetailsResponse.<AuthResponseDto>builder()
                .code(ResponseUtil.SUCCESS_CODE)
                .title(ResponseUtil.SUCCESS)
                .message("Login Successful")
                .data(authResponseDto)
                .build();

    }

    private User findUser(String username, boolean isLogin) {
        User user;
        if (!isLogin) {
            user = userRepository.findOneByUserId(username);
        } else {
            user = userRepository.findOneByUsername(username);
        }

        if (ObjectUtils.isEmpty(user)) {
            log.error("User not found by given username {}", username);
            throw new NullPointerException();
        }
        log.info("User found by given username {}", username);
        return user;
    }

    private void saveUserSession(User user, String fingerPrint) {
        long validityInMillis = jwtTokenValidity; // 30000
        long validityInSeconds = validityInMillis / 1000;

        UserSession session = getUserSession(user.getUserId());

        if (ObjectUtils.isEmpty(session)) {
            session = new UserSession();
            session.setFingerPrint(fingerPrint);
            session.setUserId(user.getUserId());
        }
        session.setCreatedAt(LocalDateTime.now());
        session.setRevoked(false);
        session.setFingerPrint(fingerPrint);
        session.setExpiresAt(LocalDateTime.now().plus(Duration.ofMinutes(validityInSeconds)));
        userSessionRepository.save(session);
    }

    private UserSessionResponse checkUserAlreadyLoggedIn(User userResponse, String fingerPrint) {
        Optional<UserSession> userSession = userSessionRepository.findByUserId(userResponse.getUserId());

        if (userSession.isPresent()) {
            log.warn("user session not found for user {}", userResponse.getUserId());

            if (userSession.get().getFingerPrint().equals(fingerPrint) && !userSession.get().getExpiresAt().isBefore(LocalDateTime.now())) {
                log.info("user session is alive for same device: {}", userSession.get().getFingerPrint());

                return UserSessionResponse.builder()
                        .isValid(true)
                        .message("User already logged in, can access same browser")
                        .build();
            }

            if (userSession.get().isRevoked()) {
                log.warn("user session is revoked for user {}", userResponse.getUserId());
                return UserSessionResponse.builder()
                        .isValid(false)
                        .message("User session is revoked")
                        .build();
            } else if (!userSession.get().getExpiresAt().isBefore(LocalDateTime.now())) {
                log.warn("user already logged in {}", userResponse.getUserId());
                return UserSessionResponse.builder()
                        .isValid(false)
                        .message("User already logged in")
                        .build();
            } else {
                log.info("user not logged in {}", userResponse.getUserId());
                return UserSessionResponse.builder()
                        .isValid(true)
                        .message("User not logged in")
                        .build();
            }
        } else {
            log.info("user session not found for user {}", userResponse.getUserId());
            return UserSessionResponse.builder()
                    .isValid(true)
                    .message("User session not found")
                    .build();
        }
    }

    private UserSession getUserSession(String userId) {
        UserSession session = userSessionRepository.findOneByUserId(userId);
        if (ObjectUtils.isEmpty(session)) {
            log.warn("user session null");
            throw new NullPointerException("cannot find user session");
        }
        return session;
    }
}
