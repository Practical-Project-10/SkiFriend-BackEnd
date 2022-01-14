package com.ppjt10.skifriend.dto.freepostdto;

import com.ppjt10.skifriend.dto.commentdto.CommentResponseDto;
import com.ppjt10.skifriend.dto.likeDto.LikesResponseDto;
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
    private Long postId;
    private String nickname;
    private String title;
    private String content;
    private String image;
    private String createdAt;
    private int likeCnt;
    private int commentCnt;
    private List<LikesResponseDto> likesDtoList;
    private List<CommentResponseDto> commentDtoList;
}