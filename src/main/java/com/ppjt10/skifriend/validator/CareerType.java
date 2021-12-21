package com.ppjt10.skifriend.validator;

import java.util.Arrays;

public enum CareerType {
    BEGINNER("초보"),
    TWOTHREEYEAR("1~3년"),
    THREEFIVEYEAR("3~5년"),
    AFTERFIVEYEAR("5년 이상");

    private String type;

    CareerType(String type) {
        this.type = type;
    }

    public String getCareerType() {
        return type;
    }

    public static CareerType findByCareerType(String type){
        return Arrays.stream(CareerType.values())
                .filter(e->e.getCareerType().equals(type))
                .findAny()
                .orElseThrow(()-> new IllegalArgumentException("올바른 ageRange Type이 아닙니다."));
    }
}
