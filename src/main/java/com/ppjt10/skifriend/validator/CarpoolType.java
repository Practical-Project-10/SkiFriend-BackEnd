package com.ppjt10.skifriend.validator;

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

    public static void findByCarpoolType(String carpool){
        for(CarpoolType carpoolType : CarpoolType.values()) {
            if(!carpoolType.getCarpoolType().equals(carpool)) {
                throw new IllegalArgumentException("올바른 Carpool Type이 아닙니다.");
            }
        }
    }
}
