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
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FreePostController {
    private final FreePostService freePostService;

    // 핫 게시물 내려주기
    @GetMapping("/main")
    public ResponseEntity<List<FreePostDto.HotResponseDto>> takeHotFreePosts() {

        List<FreePostDto.HotResponseDto> responseDtos = freePostService.takeHotFreePosts();

        return ResponseEntity.ok().body(responseDtos);
    }

    //자유게시판 전체 조회
    @GetMapping("/board/freeBoard/{skiResort}")
    public ResponseEntity<List<FreePostDto.AllResponseDto>> getFreePosts(
            @PathVariable String skiResort,
            @RequestParam int page,
            @RequestParam int size
    ) {
        page = page - 1;

        List<FreePostDto.AllResponseDto> allResponseDtoList = freePostService.getFreePosts(skiResort, page, size);

        return ResponseEntity.ok()
                .body(allResponseDtoList);
    }

    //region 자유 게시판 게시글 작성
    @PostMapping("/board/{skiResort}/freeBoard")
    public ResponseEntity<FreePostDto.AllResponseDto> writeFreePosts(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @PathVariable String skiResort,
            @RequestPart(value = "requestDto", required = false) FreePostDto.RequestDto requestDto
    ) throws IOException {
        System.out.println("requestDto: " + requestDto);
        System.out.println("img: " + image);
        FreePostDto.AllResponseDto responseDto = freePostService.uploadFreePosts(userDetails, image, skiResort, requestDto);

        return ResponseEntity.ok().body(responseDto);
    }
    //endregion

    //region 자유 게시판 게시글 상세조회
    @GetMapping("/board/freeBoard/{postId}/detail")
    public ResponseEntity<FreePostDto.ResponseDto> readFreePost(
            @PathVariable Long postId
    ) {

        FreePostDto.ResponseDto responseDto = freePostService.getFreePost(postId);

        return ResponseEntity.ok().body(responseDto);
    }
    //endregion

    //region 자유 게시판 게시글 수정
    @PutMapping("/board/freeBoard/{postId}")
    public ResponseEntity<FreePostDto.AllResponseDto> editFreePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestPart("image") MultipartFile image,
            @RequestPart("requestDto") FreePostDto.RequestDto requestDto
    ) throws IOException {

        FreePostDto.AllResponseDto responseDto = freePostService.modifyFreePost(userDetails, requestDto, image, postId);

        return ResponseEntity.ok().body(responseDto);
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
