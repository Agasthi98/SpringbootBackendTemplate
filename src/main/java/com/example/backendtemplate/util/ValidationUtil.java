package com.example.backendtemplate.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ValidationUtil {
    private ValidationUtil() {
    }
    public static final String NIC_PATTERN_REGEX = "^([0-9]{9}[x|X|v|V]|[0-9]{12})$";
    public static final String MOBILE_PATTERN_REGEX = "^[0-9]*$";
    public static final String EMAIL_PATTERN_REGEX = "^(.+)@(.+)$";
    public static final String MOBILE_NO_PATTERN_REGEX = "\\d{11}";
    public static final String USER_PASSWORD_PATTERN_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    public static final String PHONE_NUMBER_PATTERN_REGEX = "^[0-9]{10}$";
    public static final String USERNAME_PATTERN_REGEX = "^[a-z0-9]+$";
}
