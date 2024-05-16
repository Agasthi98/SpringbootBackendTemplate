package com.example.backendtemplate.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ValidationUtil {
    public static final String NIC_PATTERN_REGEX = "^([0-9]{9}[x|X|v|V]|[0-9]{12})$";
    public static final String MOBILE_PATTERN_REGEX = "^[0-9]*$";
    public static final String EMAIL_PATTERN_REGEX = "^(.+)@(.+)$";
    public static final String MOBILE_NO_PATTERN_REGEX = "\\d{11}";
    public static String getOldNicByNewNic(String newNic) {
        log.info("Given new -> " + newNic);
//        Eg: 1 9 9 8 - 7 6 5 - 0 4 3 2  1
//            0 1 2 3   4 5 6   7 8 9 10 11
        String yy = newNic.substring(2, 4);
        String ddd = newNic.substring(4, 7);
        String nnnn = newNic.substring(8);

        log.info("yy -> " + yy);
        log.info("ddd -> " + ddd);
        log.info("nnnn -> " + nnnn);

        String suffix = "_"; // This is important for like query

        String f = yy + ddd + nnnn + suffix;

        log.info("f -> " + f);

        return f;
    }

    public static String getNewNicByOldNic(String oldNic) {
        log.info("Given old -> " + oldNic);
//        Eg: 9 8 - 8 7 6 - 5 4 3 2 v
//            0 1   2 3 4   5 6 7 8 v
        String yy = oldNic.substring(0, 2);
        String ddd = oldNic.substring(2, 5);
        String nnnn = oldNic.substring(5, 9);

        log.info("yy -> " + yy);
        log.info("ddd -> " + ddd);
        log.info("nnnn -> " + nnnn);

        String f = "19" + yy + ddd + "0" + nnnn;

        log.info("f -> " + f);

        return f;
    }
}
