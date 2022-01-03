package com.ppjt10.skifriend.validator;


import java.text.SimpleDateFormat;

public class TimeValidator {
    public static void validateTimeForm(String time) {

        try {
            SimpleDateFormat timeFormater = new SimpleDateFormat("HH:mm");
            timeFormater.setLenient(false);
            timeFormater.parse(time);
        }
        catch(Exception e) {
            throw new IllegalArgumentException("시간 형식이 올바르지 않습니다");
        }

    }



}

