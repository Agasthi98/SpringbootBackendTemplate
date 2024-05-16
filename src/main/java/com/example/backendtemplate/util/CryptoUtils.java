package com.example.backendtemplate.util;

import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class CryptoUtils {

    public static String hashOTP(String otp) {
        String encryptedOTP = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(otp.getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest();
            encryptedOTP = String.format("%064x", new BigInteger(1, digest));
        } catch (NoSuchAlgorithmException var5) {
            log.info("OTP Encryption failed..." + var5.getMessage());
        }
        return encryptedOTP;
    }

}
