package com.ppjt10.skifriend.controller;

import com.ppjt10.skifriend.dto.shortsdto.ShortsCommentResponseDto;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.security.UserDetailsImpl;
import com.ppjt10.skifriend.service.ShortsCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ShortsCommentController {
    private final ShortsCommentService shortsCommentService;

    // Shorts 댓글 조회
    @GetMapping("/shorts/{shortsId}")
    public ResponseEntity<List<ShortsCommentResponseDto>> getShortsComments(@PathVariable Long shortsId) {
        return ResponseEntity.ok().body(shortsCommentService.getShortsComments(shortsId));
    }


    // Shorts 댓글 작성
    @PostMapping("/shorts/{shortsId}/comments")
    public void createShortsComment(@PathVariable Long shortsId,
                              @RequestBody String content,
                              @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        shortsCommentService.createShortsComment(shortsId, content, user);
    }


    // Shorts 댓글 수정
    @PutMapping("/shorts/comments/{shortsCommentId}")
    public void updateShortsComment(@PathVariable Long shortsCommentId,
                              @RequestBody String content,
                              @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        shortsCommentService.updateShortsComment(shortsCommentId, content, user);
    }


    // Shorts 댓글 삭제
    @DeleteMapping("/shorts/comments/{shortsCommentId}")
    public void deleteShortsComment(
            @PathVariable Long shortsCommentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        shortsCommentService.deleteShortsComment(shortsCommentId, user);
    }
}
