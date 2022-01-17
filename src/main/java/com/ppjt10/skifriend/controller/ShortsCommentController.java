//package com.ppjt10.skifriend.controller;
//
//
//import com.ppjt10.skifriend.dto.commentdto.CommentRequestDto;
//import com.ppjt10.skifriend.entity.User;
//import com.ppjt10.skifriend.security.UserDetailsImpl;
//import com.ppjt10.skifriend.service.ShortsCommentService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequiredArgsConstructor
//public class ShortsCommentController {
//    private final ShortsCommentService shortsCommentService;
//
//    // Shorts 댓글 작성
//    @PostMapping("/board/freeBoard/{postId}/comments")
//    public void createShortsComment(@PathVariable Long postId,
//                              @RequestBody CommentRequestDto requestDto,
//                              @AuthenticationPrincipal UserDetailsImpl userDetails
//    ) {
//        User user = userDetails.getUser();
//        shortsCommentService.createShortsComment(postId, requestDto, user);
//    }
//
//
//    // Shorts 댓글 수정
//    @PutMapping("/board/freeBoard/comments/{commentId}")
//    public void updateShortsComment(@PathVariable Long commentId,
//                              @RequestBody CommentRequestDto requestDto,
//                              @AuthenticationPrincipal UserDetailsImpl userDetails
//    ) {
//        User user = userDetails.getUser();
//        shortsCommentService.updateShortsComment(commentId, requestDto, user);
//    }
//
//
//    // Shorts 댓글 삭제
//    @DeleteMapping("/board/freeBoard/comments/{commentId}")
//    public void deleteShortsComment(
//            @PathVariable Long commentId,
//            @AuthenticationPrincipal UserDetailsImpl userDetails
//    ) {
//        User user = userDetails.getUser();
//        shortsCommentService.deleteShortsComment(commentId, user);
//    }
//}
