package com.ppjt10.skifriend.controller;

import com.ppjt10.skifriend.dto.FreePostDto;
import com.ppjt10.skifriend.service.FreePostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HomeController {
    private final FreePostService freePostService;

    @GetMapping("/main")
    public ResponseEntity<List<FreePostDto.ResortTabDto>> takeHotFreePosts() {
        return freePostService.takeHotFreePosts();
    }
}
