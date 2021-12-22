package com.ppjt10.skifriend.validator;

import java.util.Arrays;

public enum CarpoolType {
    REQUEST("카풀 요청"),
    OFFER("카풀 제공");

    private final String carpoolType;

    CarpoolType(String carpoolType) {
        this.carpoolType = carpoolType;
    }

    public String getCarpoolType(){
        return this.carpoolType;
    }

    public static CarpoolType findByCarpoolType(String type){
        return Arrays.stream(CarpoolType.values())
                .filter(e->e.getCarpoolType().equals(type))
                .findAny()
                .orElseThrow(()-> new IllegalArgumentException("올바른 Carpool Type이 아닙니다."));
    }
}
