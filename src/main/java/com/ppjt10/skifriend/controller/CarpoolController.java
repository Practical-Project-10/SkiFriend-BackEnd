package com.ppjt10.skifriend.controller;

import com.ppjt10.skifriend.dto.carpooldto.CarpoolBannerDto;
import com.ppjt10.skifriend.dto.carpooldto.CarpoolFilterRequestDto;
import com.ppjt10.skifriend.dto.carpooldto.CarpoolRequestDto;
import com.ppjt10.skifriend.dto.carpooldto.CarpoolResponseDto;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.security.UserDetailsImpl;
import com.ppjt10.skifriend.service.CarpoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class CarpoolController {

    private final CarpoolService carpoolService;

    // 카풀 게시물 조회
    @GetMapping("/board/carpool/{skiResort}")
    public ResponseEntity<List<CarpoolResponseDto>> getCarpools(@PathVariable String skiResort,
                                                                @RequestParam int page,
                                                                @RequestParam int size
    ) {
        page = page - 1;
        return ResponseEntity.ok().body(carpoolService.getCarpools(skiResort, page, size));
    }

    // 카풀 게시물 작성
    @PostMapping("/board/carpool/{skiResort}")
    public ResponseEntity<CarpoolResponseDto> createCarpool(@PathVariable String skiResort,
                                                            @RequestBody CarpoolRequestDto requestDto,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        return ResponseEntity.ok().body(carpoolService.createCarpool(skiResort, requestDto, user));
    }

    // 카풀 게시물 수정
    @PutMapping("/board/carpool/{carpoolId}")
    public ResponseEntity<CarpoolResponseDto> updateCarpool(@PathVariable Long carpoolId,
                                                            @RequestBody CarpoolRequestDto requestDto,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        return ResponseEntity.ok().body(carpoolService.updateCarpool(carpoolId, requestDto, user));
    }

    // 카풀 게시글 삭제
    @DeleteMapping("/board/carpool/{carpoolId}")
    public void deleteCarpool(@PathVariable Long carpoolId,
                              @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        carpoolService.deleteCarpool(carpoolId, user);
    }

    // 카풀 모집 완료 상태로 변경
    @PostMapping("/board/carpool/{carpoolId}/status")
    public void changeStatus(@PathVariable Long carpoolId,
                             @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        carpoolService.changeStatus(carpoolId, user);
    }

    // 카풀 카테고리 분류
    @PostMapping("/board/carpool/{skiResort}/category")
    public ResponseEntity<List<CarpoolResponseDto>> sortCategories(@PathVariable String skiResort,
                                                                   @RequestBody CarpoolFilterRequestDto requestDto
    ) {
        return ResponseEntity.ok().body(carpoolService.sortCarpools(skiResort, requestDto));
    }

    // 배너 가져오기
    @GetMapping("/board/carpool/{skiResort}/banner")
    public ResponseEntity<CarpoolBannerDto> getBanner(@PathVariable String skiResort) {
        return ResponseEntity.ok().body(carpoolService.getBanner(skiResort));
    }
}
