package com.example.backendtemplate.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Logger;

public class HmacUtility {

    private HmacUtility() {
    }

    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static String generateHmacHash(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hmacSha256 = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hmacSha256) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static boolean verifyHmacHash(String dataString, String key, String hash) {
        if ((dataString == null) || dataString.isEmpty()) {
            logger.warning("Empty data string given");
            return false;
        }
        if ((hash == null) || hash.isEmpty()) {
            logger.warning("Empty hash given");
            return false;
        }
        try {
            String generatedHash = generateHmacHash(dataString, key);
            boolean validSignature = generatedHash.equals(hash);

            if (!validSignature) {
                logger.warning(() -> "Received hash -> " + hash);
                logger.warning(() -> "Generated hash -> " + generatedHash);
            }

            return validSignature;
        } catch (Exception exception) {
            logger.severe(() -> "Error while validating hash -> " + exception.getMessage());
        }
        return false;
    }

    public static String base64Encode(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }

    public static String base64Encode(byte[] input) {
        return Base64.getEncoder().encodeToString(input);
    }

    public static String base64Decode(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        return new String(decodedBytes);
    }
}
