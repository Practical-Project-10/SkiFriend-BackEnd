package com.ppjt10.skifriend.validator;


import java.util.regex.Pattern;

public class TimeValidator {
    public static void validateTimeForm(String time) {
        String patterTime = "^([01][0-9]|2[0-3]):([0-5][0-9])$";
        if(time == null || !Pattern.matches(patterTime, time)) {
            throw new IllegalArgumentException("시간 형식이 올바르지 않습니다");
        }
    }



}

