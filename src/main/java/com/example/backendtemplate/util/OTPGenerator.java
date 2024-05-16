package com.example.backendtemplate.util;


import java.security.SecureRandom;

public class OTPGenerator {
    private static final String OTP_CHARACTERS = "0123456789";

    public static String generateOTP(int otpLength) {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder otp = new StringBuilder(otpLength);
        for (int i = 0; i < otpLength; i++) {
            otp.append(OTP_CHARACTERS.charAt(secureRandom.nextInt(OTP_CHARACTERS.length())));
        }
        return otp.toString();
    }
}
