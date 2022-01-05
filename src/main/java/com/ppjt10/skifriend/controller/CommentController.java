package com.ppjt10.skifriend.controller;

import com.ppjt10.skifriend.dto.commentdto.CommentRequestDto;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.security.UserDetailsImpl;
import com.ppjt10.skifriend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class CommentController {
    private final CommentService commentService;

    // 자유 게시판 게시글 댓글 작성
    @PostMapping("/board/freeBoard/{postId}/comments")
    public void createComment(@PathVariable Long postId,
                              @RequestBody CommentRequestDto requestDto,
                              @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        commentService.createComment(postId, requestDto, user);
    }


    // 자유 게시판 게시글 댓글 수정
    @PutMapping("/board/freeBoard/comments/{commentId}")
    public void updateComment(@PathVariable Long commentId,
                              @RequestBody CommentRequestDto requestDto,
                              @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        commentService.updateComment(commentId, requestDto, user);
    }


    // 자유 게시판 게시글 댓글 삭제
    @DeleteMapping("/board/freeBoard/comments/{commentId}")
    public void deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        commentService.deleteComment(commentId, user);
    }

}
