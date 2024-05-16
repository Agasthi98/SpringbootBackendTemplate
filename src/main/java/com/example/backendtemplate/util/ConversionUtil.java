/**
 * Created By Dilsha Prasanna
 * Date : 1/10/2024
 * Time : 3:37 PM
 * Project Name : upay-sso
 */

package com.example.backendtemplate.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class ConversionUtil {

    private ConversionUtil(){}

    public static LocalDateTime convertDateToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
