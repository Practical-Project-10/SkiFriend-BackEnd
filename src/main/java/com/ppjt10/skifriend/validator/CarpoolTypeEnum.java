package com.ppjt10.skifriend.validator;

public enum CarpoolTypeEnum {
    REQUEST("카풀 요청"),
    OFFER("카풀 제공");

    private final String carpoolType;

    CarpoolTypeEnum(String carpoolType) {
        this.carpoolType = carpoolType;
    }

    public String getCarpoolType(){
        return this.carpoolType;
    }

}
