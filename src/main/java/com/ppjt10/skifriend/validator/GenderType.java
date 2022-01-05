package com.ppjt10.skifriend.validator;

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

    public static void findByGenderType(String gender){
        for(GenderType genderType : GenderType.values()) {
            if(genderType.getGenderType().equals(gender)) {
                return;
            }
        }
        throw new IllegalArgumentException("올바른 Gender Type이 아닙니다.");
    }
}
