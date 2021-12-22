package com.ppjt10.skifriend.validator;


import java.util.regex.Pattern;

public class DateValidator {
    public static void validateDateForm(String date) {
        String patterDate = "^((19|20)\\d\\d)?([- /.])?(0[1-9]|1[012])([- /.])?(0[1-9]|[12][0-9]|3[01])$";

        if(date == null || !Pattern.matches(patterDate, date)) {
            throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다");
        }
    }

}
