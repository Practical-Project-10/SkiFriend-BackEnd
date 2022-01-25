package com.ppjt10.skifriend.controller;

import com.ppjt10.skifriend.dto.shortsdto.ShortsLikeResponseDto;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.security.UserDetailsImpl;
import com.ppjt10.skifriend.service.ShortsLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ShortsLikeController {
    private final ShortsLikeService shortsLikeService;

    @PostMapping("/shorts/{shortsId}/like")
    public ResponseEntity<List<ShortsLikeResponseDto>> changeShortsLike(@PathVariable Long shortsId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok().body(shortsLikeService.changeShortsLike(shortsId, user));
    }
}
