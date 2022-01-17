package com.ppjt10.skifriend.controller;

import com.ppjt10.skifriend.dto.shortsdto.ShortsResponseDto;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.security.UserDetailsImpl;
import com.ppjt10.skifriend.service.ShortsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ShortsController {
    private final ShortsService shortsService;

    //Shorts 조회
//    @GetMapping("/shorts")
//    public ResponseEntity<ShortsResponseDto> getShorts() {
//        return ResponseEntity.ok().body(shortsService.getShorts());
//    }

    //Shorts 작성
    @PostMapping("/shorts")
    public ResponseEntity<ShortsResponseDto> createShorts(@RequestPart(value = "videoFile", required = false) MultipartFile image,
                                                          @RequestPart(value = "title", required = false) String title,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        User user = userDetails.getUser();
        return ResponseEntity.ok().body(shortsService.createShorts(image, title, user));
    }

    //자유 게시판 게시글 수정
    @PutMapping("/shorts/{shortsId}")
    public ResponseEntity<ShortsResponseDto> updateShorts(@RequestPart(value = "title", required = false) String title,
                                                          @PathVariable Long shortsId,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        return ResponseEntity.ok().body(shortsService.updateShorts(title, shortsId, user));
    }

    // 자유 게시판 게시글 삭제
    @DeleteMapping("/shorts/{shortsId}")
    public void deleteShorts(@PathVariable Long shortsId,
                             @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        shortsService.deleteShorts(shortsId, user);
    }
}
