package com.ppjt10.skifriend.validator;

import java.util.Arrays;

public enum AgeRangeType {
    TEENS("10대"),
    TWENTIES("20대"),
    THIRTIES("30대"),
    AFTERFORTIES("40대 이상");

    private String type;

    AgeRangeType(String type) {
        this.type = type;
    }

    public String getageRangeType() {
        return type;
    }

    public static AgeRangeType findByageRangeType(String type){
        return Arrays.stream(AgeRangeType.values())
                .filter(e->e.getageRangeType().equals(type))
                .findAny()
                .orElseThrow(()-> new IllegalArgumentException("올바른 ageRange Type이 아닙니다."));
    }
}
