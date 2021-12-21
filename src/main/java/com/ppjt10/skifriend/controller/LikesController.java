package com.ppjt10.skifriend.controller;


import com.ppjt10.skifriend.security.UserDetailsImpl;
import com.ppjt10.skifriend.service.LikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
public class LikesController {
    private final LikesService likesService;

    //region 좋아요 기능
    @PostMapping("/board/{skiResort}/freeBoard/{postId}/likes")
    public void clickLike(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String skiResort,
            @PathVariable Long postId
            ){
        likesService.changeLike(userDetails, skiResort, postId);
    }
    //endregion


}
