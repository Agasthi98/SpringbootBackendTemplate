package com.example.backendtemplate.service.impl;

import com.example.backendtemplate.constants.LogMessage;
import com.example.backendtemplate.entities.UserEntity;
import com.example.backendtemplate.model.request.UserRegistrationRequest;
import com.example.backendtemplate.model.response.BaseDetailsResponse;
import com.example.backendtemplate.repository.UserRepository;
import com.example.backendtemplate.service.UserService;
import com.example.backendtemplate.util.MobileUtility;
import com.example.backendtemplate.util.ResponseCodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public BaseDetailsResponse<HashMap<String, Object>> userRegistration(UserRegistrationRequest userRegistrationRequest) {
        try {
            log.info(LogMessage.USER_REGISTRATION_PREFIX + " [start]");

            UserEntity userResponse = userRepository.findByNic(userRegistrationRequest.getNic());

            if (!ObjectUtils.isEmpty(userResponse)) {
                log.warn(LogMessage.USER_ALREADY_EXIST + " by given nic {}", userRegistrationRequest.getNic());
                return BaseDetailsResponse.<HashMap<String, Object>>builder()
                        .code(ResponseCodeUtil.FAILED_CODE)
                        .title(ResponseCodeUtil.FAILED)
                        .message(LogMessage.USER_ALREADY_EXIST)
                        .build();
            }

            log.info("User doesn't exist by given nic {}", userRegistrationRequest.getNic());

            UserEntity userEntity = saveNewUser(userRegistrationRequest);

            if (ObjectUtils.isEmpty(userEntity)) {
                log.error(LogMessage.USER_REGISTRATION_FAILED);
                return BaseDetailsResponse.<HashMap<String, Object>>builder()
                        .code(ResponseCodeUtil.FAILED_CODE)
                        .title(ResponseCodeUtil.FAILED)
                        .message(LogMessage.USER_REGISTRATION_FAILED)
                        .build();
            }
            log.info(LogMessage.USER_REGISTRATION_SUCCESS);
            log.info(LogMessage.USER_REGISTRATION_PREFIX + " [end]");

            return BaseDetailsResponse.<HashMap<String, Object>>builder()
                    .code(ResponseCodeUtil.SUCCESS_CODE)
                    .title(ResponseCodeUtil.SUCCESS)
                    .message(LogMessage.USER_REGISTRATION_SUCCESS)
                    .build();

        } catch (Exception e) {
            log.error(LogMessage.USER_REGISTRATION_FAILED + " with error {}", e.getMessage() + e);
            return null;
        }
    }

    private UserEntity saveNewUser(UserRegistrationRequest userRegistrationRequest) {

        log.info("Saving new user [start]");

        String fullName = userRegistrationRequest.getFirstName() + " " + userRegistrationRequest.getLastName();

        UserEntity userEntity = UserEntity.builder()
                .username(userRegistrationRequest.getUsername())
                .fullName(fullName)
                .nic(userRegistrationRequest.getNic())
                .password(passwordEncoder.encode(userRegistrationRequest.getPassword()))
                .phoneNumber(MobileUtility.formatNumber(userRegistrationRequest.getPhoneNumber()))
                .build();

        userRepository.save(userEntity);

        log.info("Saving new user [end]");
        return userEntity;
    }
}
