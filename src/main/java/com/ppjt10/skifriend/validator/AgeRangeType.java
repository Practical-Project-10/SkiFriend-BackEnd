package com.ppjt10.skifriend.validator;

public enum AgeRangeType {
    TEENS("10대"),
    TWENTIES("20대"),
    THIRTIES("30대"),
    AFTERFORTIES("40대 이상");

    private String type;

    AgeRangeType(String type) {
        this.type = type;
    }

    public String getAgeRangeType() {
        return type;
    }

    public static void findByAgeRangeType(String ageRange){
        for(AgeRangeType ageRangeType : AgeRangeType.values()) {
            if(ageRangeType.getAgeRangeType().equals(ageRange)) {
                return;
            }
        }
        throw new IllegalArgumentException("올바른 AgeRange Type이 아닙니다.");
    }
}
