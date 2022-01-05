package com.ppjt10.skifriend.controller;

import com.ppjt10.skifriend.dto.freepostdto.FreePostDetailResponseDto;
import com.ppjt10.skifriend.dto.freepostdto.FreePostHotResponseDto;
import com.ppjt10.skifriend.dto.freepostdto.FreePostRequestDto;
import com.ppjt10.skifriend.dto.freepostdto.FreePostResponseDto;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.security.UserDetailsImpl;
import com.ppjt10.skifriend.service.FreePostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FreePostController {
    private final FreePostService freePostService;


    //자유게시판 전체 조회
    @GetMapping("/board/freeBoard/{skiResort}")
    public ResponseEntity<List<FreePostResponseDto>> getFreePosts(@PathVariable String skiResort,
                                                                  @RequestParam int page,
                                                                  @RequestParam int size
    ) {
        page = page - 1;
        return ResponseEntity.ok().body(freePostService.getFreePosts(skiResort, page, size));
    }

    //자유 게시판 게시글 작성
    @PostMapping("/board/{skiResort}/freeBoard")
    public ResponseEntity<FreePostResponseDto> createFreePosts(@RequestPart(value = "image") MultipartFile image,
                                                               @RequestPart(value = "requestDto") FreePostRequestDto requestDto,
                                                               @PathVariable String skiResort,
                                                               @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        return ResponseEntity.ok().body(freePostService.createFreePosts(image, requestDto, skiResort, user));
    }


    //자유 게시판 게시글 상세조회
    @GetMapping("/board/freeBoard/{postId}/detail")
    public ResponseEntity<FreePostDetailResponseDto> getDetailFreePost(@PathVariable Long postId) {
        return ResponseEntity.ok().body(freePostService.getDetailFreePost(postId));
    }


    //자유 게시판 게시글 수정
    @PutMapping("/board/freeBoard/{postId}")
    public ResponseEntity<FreePostResponseDto> updateFreePost(@RequestPart("image") MultipartFile image,
                                                            @RequestPart("requestDto") FreePostRequestDto requestDto,
                                                            @PathVariable Long postId,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        return ResponseEntity.ok().body(freePostService.updateFreePost(image, requestDto, postId, user));
    }

    // 자유 게시판 게시글 삭제
    @DeleteMapping("/board/freeBoard/{postId}")
    public void deleteFreePost(@PathVariable Long postId,
                               @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        freePostService.deleteFreePost(postId, user);
    }

    // 핫 게시물 내려주기
    @GetMapping("/main")
    public ResponseEntity<List<FreePostHotResponseDto>> getHotFreePosts() {
        return ResponseEntity.ok().body(freePostService.getHotFreePosts());
    }
}
