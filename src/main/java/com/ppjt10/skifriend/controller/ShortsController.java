package com.ppjt10.skifriend.controller;

import com.ppjt10.skifriend.dto.shortsdto.ShortsRequestDto;
import com.ppjt10.skifriend.dto.shortsdto.ShortsResponseDto;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.security.UserDetailsImpl;
import com.ppjt10.skifriend.service.ShortsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ShortsController {
    private final ShortsService shortsService;

    //Shorts 조회
    @GetMapping("/shorts")
    public ResponseEntity<ShortsResponseDto> getShorts(HttpSession session) {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-FORWARDED-FOR");
        if (ip == null)
            ip = req.getRemoteAddr();
        System.out.println("IP 주소 : " + ip);

        System.out.println("세션 아이디 : " + session.getId());
        return ResponseEntity.ok().body(shortsService.getShorts(session));
    }

    //Shorts 작성
    @PostMapping("/shorts")
    public ResponseEntity<ShortsResponseDto> createShorts(@RequestPart(value = "videoFile", required = false) MultipartFile image,
                                                          @RequestPart(value = "requestDto", required = false) ShortsRequestDto requestDto,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        User user = userDetails.getUser();
        return ResponseEntity.ok().body(shortsService.createShorts(image, requestDto, user));
    }

    //Shorts 수정
    @PutMapping("/shorts/{shortsId}")
    public ResponseEntity<ShortsResponseDto> updateShorts(@RequestPart(value = "requestDto", required = false) ShortsRequestDto requestDto,
                                                          @PathVariable Long shortsId,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        return ResponseEntity.ok().body(shortsService.updateShorts(requestDto, shortsId, user));
    }

    //Shorts 삭제
    @DeleteMapping("/shorts/{shortsId}")
    public void deleteShorts(@PathVariable Long shortsId,
                             @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        shortsService.deleteShorts(shortsId, user);
    }
}
