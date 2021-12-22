package com.ppjt10.skifriend.controller;

import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.security.UserDetailsImpl;
import com.ppjt10.skifriend.service.CarpoolService;
import com.ppjt10.skifriend.dto.CarpoolDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CarpoolController {
    private final CarpoolService carpoolService;

    //카풀 게시물 작성
    @PostMapping("/board/carpool/{skiResort}")
    public void createCarpool(@PathVariable String skiResort,
                              @RequestBody CarpoolDto.RequestDto requestDto,
                              @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        carpoolService.createCarpool(skiResort, requestDto, user);
    }

    //카풀 게시뭏 수정
    @PutMapping("/board/carpool/{carpoolId}")
    public void updateCarpool(@PathVariable Long carpoolId,
                              @RequestBody CarpoolDto.RequestDto requestDto,
                              @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        carpoolService.updateCarpool(carpoolId, requestDto); // 유저 아이디랑 게시글을 작성한 아이디랑 같은지 비교하는 파트 추가 필요
    }

    //카풀 게시글 삭제
    @DeleteMapping("/board/carpool/{carpoolId}")
    public void deleteCarpool(@PathVariable Long carpoolId,
                              @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        carpoolService.deleteCarpool(carpoolId);
    }
}
