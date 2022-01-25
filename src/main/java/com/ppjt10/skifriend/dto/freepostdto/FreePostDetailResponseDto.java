package com.ppjt10.skifriend.dto.freepostdto;

import com.ppjt10.skifriend.dto.commentdto.CommentResponseDto;
import com.ppjt10.skifriend.dto.likedto.LikesResponseDto;
import com.ppjt10.skifriend.dto.photodto.PhotoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FreePostDetailResponseDto{
    private Long userId;
    private String userProfile;
    private Long postId;
    private List<PhotoDto> photoList;
    private String nickname;
    private String title;
    private String content;
    private String createdAt;
    private int likeCnt;
    private int commentCnt;
    private List<LikesResponseDto> likesDtoList;
    private List<CommentResponseDto> commentDtoList;
}