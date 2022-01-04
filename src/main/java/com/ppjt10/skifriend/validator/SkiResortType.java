package com.ppjt10.skifriend.validator;

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
}
