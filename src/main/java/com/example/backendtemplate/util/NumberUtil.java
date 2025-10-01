package com.example.backendtemplate.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class NumberUtil {

    public static String getBirthDateFromNIC(String nic) {
        String yearStr;
        int dayOfYear;

        if (nic.length() == 10) { // old NIC format
            yearStr = "19" + nic.substring(0, 2);
            dayOfYear = Integer.parseInt(nic.substring(2, 5));
        } else if (nic.length() == 12) { // new NIC format
            yearStr = nic.substring(0, 4);
            dayOfYear = Integer.parseInt(nic.substring(4, 7));
        } else {
            throw new IllegalArgumentException("Invalid NIC format");
        }

        // Female check
        if (dayOfYear > 500) {
            dayOfYear -= 500;
        }

        int year = Integer.parseInt(yearStr);

        // Construct date
        return LocalDate.ofYearDay(year, dayOfYear).toString();
    }
}
