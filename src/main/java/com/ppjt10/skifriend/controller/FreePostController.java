package com.ppjt10.skifriend.controller;



import com.ppjt10.skifriend.dto.FreePostDto;
import com.ppjt10.skifriend.security.UserDetailsImpl;
import com.ppjt10.skifriend.service.FreePostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@RestController
@RequiredArgsConstructor
public class FreePostController {
    private final FreePostService freePostService;

    //region 자유 게시판 게시글 작성
    @PostMapping("/board/{skiResort}/freeBoard")
    public void writeFreePosts(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestPart("image") MultipartFile image,
            @PathVariable String skiResort,
            @RequestPart("requestDto") FreePostDto.RequestDto requestDto
    ) throws IOException {
        freePostService.uploadFreePosts(userDetails, image, skiResort, requestDto);
        System.out.println(requestDto.getContent());
    }
    //endregion

    //region 자유 게시판 게시글 상세조회
    @GetMapping("/board/freeBoard/{postId}")
    public ResponseEntity<FreePostDto.ResponseDto> readFreePost(
            @PathVariable Long postId
    ) {
        return freePostService.getFreePost(postId);
    }
    //endregion

    //region 자유 게시판 게시글 수정
    @PutMapping("/board/freeBoard/{postId}")
    public void editFreePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestPart("image") MultipartFile image,
            @RequestPart("requestDto") FreePostDto.RequestDto requestDto
    ) throws IOException {
        freePostService.modifyFreePost(userDetails, requestDto, image, postId);

    }



    //endregion

    //region 자유 게시판 게시글 삭제
    @DeleteMapping("/board/freeBoard/{postId}")
    public void deleteFreePost(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long postId
    ) throws UnsupportedEncodingException {
        freePostService.deleteFreePost(userDetails, postId);
    }
    //endregion

}
