package com.ppjt10.skifriend.validator;

import java.util.Arrays;

public enum GenderType {
    MALE("남"),
    FEMALE("여");

    private String type;

    GenderType(String type) {
        this.type = type;
    }

    public String getGenderType() {
        return type;
    }

    public static GenderType findByGenderType(String type){
        return Arrays.stream(GenderType.values())
                .filter(e->e.getGenderType().equals(type))
                .findAny()
                .orElseThrow(()-> new IllegalArgumentException("올바른 Gender Type이 아닙니다."));
    }
}
