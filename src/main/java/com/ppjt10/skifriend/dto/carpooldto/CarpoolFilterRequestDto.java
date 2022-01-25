package com.ppjt10.skifriend.dto.carpooldto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CarpoolFilterRequestDto{
    private String carpoolType;
    private String memberNum;
    private String startLocation;
    private String endLocation;
    private String date;
    private Boolean status;
}