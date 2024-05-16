package com.example.backendtemplate.util;

import org.springframework.stereotype.Component;

@Component
public class MobileUtility {
    public static String formatContactNumber(String mobile) {
        if (mobile.charAt(0) == '9' && mobile.length() == 11) {
            mobile = mobile.substring(2);
        }
        final String firstTwoDigits = mobile.substring(0, 1);
        if (!firstTwoDigits.equalsIgnoreCase("0")) {
            mobile = "0" + mobile;
        }
        return mobile;
    }

    public static String formatNumber(String mobile) {
        if (mobile.charAt(0) == '0' && mobile.length() == 10) {
            mobile = mobile.substring(1);
        }
        final String firstTwoDigits = mobile.substring(0, 2);
        if (!firstTwoDigits.equalsIgnoreCase("94")) {
            mobile = "94" + mobile;
        }
        return mobile;
    }

    public String formatNumberToNormal(String mobile) {

        final String firstTwoDigits = mobile.substring(0, 2);
        if (firstTwoDigits.equalsIgnoreCase("94")) {
            mobile = "0" + mobile.substring(mobile.length() - 9);
        }
        return mobile;
    }
}
