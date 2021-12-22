package com.ppjt10.skifriend.validator;

import java.util.Arrays;

public enum SkiResortType {
    HIGHONE("HighOne"),
    YONGPYONG("YongPyong"),
    WELLIHILLIPARK("WellihilliPark"),
    KONJIAM("Konjiam"),
    VIVALDIPARK("VivaldiPark"),
    PHOENIX("Phoenix");

    private final String skiResortType;

    SkiResortType(String skiResortType) {
        this.skiResortType = skiResortType;
    }

    public String getSkiResortType(){
        return this.skiResortType;
    }

    public static SkiResortType findBySkiResortType(String type){
        return Arrays.stream(SkiResortType.values())
                .filter(e->e.getSkiResortType().equals(type))
                .findAny()
                .orElseThrow(()-> new IllegalArgumentException("올바른 ageRange Type이 아닙니다."));
    }
}
