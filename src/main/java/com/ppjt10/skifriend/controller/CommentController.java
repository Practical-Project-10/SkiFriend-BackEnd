package com.ppjt10.skifriend.controller;


import com.ppjt10.skifriend.dto.CommentDto;
import com.ppjt10.skifriend.security.UserDetailsImpl;
import com.ppjt10.skifriend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class CommentController {
    private final CommentService commentService;

    //region 자유 게시판 게시글 댓글 작성
    @PostMapping("/board/freeBoard/{postId}/comments")
    public void writeFreePostComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CommentDto.RequestDto requestDto,
            @PathVariable Long postId
    ) {
        commentService.writeComment(userDetails, requestDto, postId);
    }
    //endregion

    //region 자유 게시판 게시글 댓글 수정
    @PutMapping("/board/freeBoard/comments/{commentId}")
    public void editFreePostComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CommentDto.RequestDto requestDto,
            @PathVariable Long commentId

    ) {
        commentService.editComment(userDetails, requestDto, commentId);
    }
    //endregion

    //region 자유 게시판 게시글 댓글 삭제
    @DeleteMapping("/board/freeBoard/comments/{commentId}")
    public void deleteFreePostComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(userDetails, commentId);
    }
    //endregion

}
