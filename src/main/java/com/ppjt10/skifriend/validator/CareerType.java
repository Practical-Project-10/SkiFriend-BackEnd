package com.ppjt10.skifriend.validator;

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

    public static void findByCareerType(String career){
        for(CareerType careerType : CareerType.values()) {
            if(careerType.getCareerType().equals(career)) {
                return;
            }
        }
        throw new IllegalArgumentException("올바른 Career Type이 아닙니다.");
    }

}
