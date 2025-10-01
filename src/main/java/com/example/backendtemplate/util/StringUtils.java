package com.example.backendtemplate.util;

import org.springframework.stereotype.Component;

@Component
public class StringUtils {

    public static String reverseString(String input) {
        if (input == null) {
            return null;
        }
        return new StringBuilder(input).reverse().toString();
    }
}
