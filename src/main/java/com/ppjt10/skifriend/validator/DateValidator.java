package com.ppjt10.skifriend.validator;


import java.text.SimpleDateFormat;


public class DateValidator {
    public static void validateDateForm(String date) {
        try {
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
            dateFormater.setLenient(false);
            dateFormater.parse(date);
        } catch (Exception e) {
            throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다");
        }
    }
}
