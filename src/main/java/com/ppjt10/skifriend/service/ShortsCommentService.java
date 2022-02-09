package com.ppjt10.skifriend.service;


import com.ppjt10.skifriend.dto.shortsdto.ShortsCommentResponseDto;
import com.ppjt10.skifriend.entity.*;
import com.ppjt10.skifriend.repository.ShortsCommentRepository;
import com.ppjt10.skifriend.repository.ShortsRepository;
import com.ppjt10.skifriend.repository.UserRepository;
import com.ppjt10.skifriend.time.TimeConversion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShortsCommentService {
    private final ShortsCommentRepository shortsCommentRepository;
    private final ShortsRepository shortsRepository;
    private final UserRepository userRepository;

    // Shorts 댓글 조회
    public List<ShortsCommentResponseDto> getShortsComments(Long shortsId) {
        Shorts shorts = shortsRepository.findById(shortsId).orElseThrow(
                () -> new IllegalArgumentException("해당 Shorts가 존재하지 않습니다")
        );

        List<ShortsCommentResponseDto> shortsCommentResponseDtos = new ArrayList<>();
        List<ShortsComment> shortsCommentList = shortsCommentRepository.findAllByShorts(shorts);
        for (ShortsComment shortsComment : shortsCommentList) {
            shortsCommentResponseDtos.add(generateShortsCommentResponseDto(shortsComment));
        }
        return shortsCommentResponseDtos;
    }

    // Shorts 댓글 작성
    @Transactional
    public void createShortsComment(Long shortsId, String content, User user) {
        Shorts shorts = shortsRepository.findById(shortsId).orElseThrow(
                () -> new IllegalArgumentException("해당 Shorts가 존재하지 않습니다")
        );

        ShortsComment shortsComment = new ShortsComment(user.getId(), shorts, content);
        shortsCommentRepository.save(shortsComment);

        shorts.setShortsCommentCnt(shorts.getShortsCommentCnt() + 1);
    }

    // Shorts 댓글 수정
    @Transactional
    public void updateShortsComment(Long shortsCommentId, String content, User user) {

        ShortsComment shortsComment = shortsCommentRepository.findById(shortsCommentId).orElseThrow(
                () -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다")
        );

        if (!user.getId().equals(shortsComment.getUserId())) {
            throw new IllegalArgumentException("댓글 작성자만 댓글을 수정할 수 있습니다");
        }

        shortsComment.update(content);
    }

    // 자유 게시판 게시글 댓글 삭제
    @Transactional
    public void deleteShortsComment(Long shortsCommentId, User user) {

        ShortsComment shortsComment = shortsCommentRepository.findById(shortsCommentId).orElseThrow(
                () -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다")
        );

        if (!user.getId().equals(shortsComment.getUserId())) {
            throw new IllegalArgumentException("댓글 작성자만 댓글을 삭제할 수 있습니다");
        }

        shortsCommentRepository.deleteById(shortsCommentId);

        Shorts commentShorts = shortsComment.getShorts();

        commentShorts.setShortsCommentCnt(commentShorts.getShortsCommentCnt() - 1);
    }

    private ShortsCommentResponseDto generateShortsCommentResponseDto(ShortsComment shortsComment) {
        String nickname;
        String userImg;
        Optional<User> user = userRepository.findById(shortsComment.getUserId());
        if(user.isPresent()){
            nickname = user.get().getNickname();
            userImg = user.get().getProfileImg();
        } else{
            nickname = "알 수 없음";
            userImg = "https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/static/defalt+user+frofile.png";
        }

        return ShortsCommentResponseDto.builder()
                .userId(shortsComment.getUserId())
                .shortsCommentId(shortsComment.getId())
                .userImg(userImg)
                .nickname(nickname)
                .content(shortsComment.getContent())
                .createdAt(TimeConversion.timePostConversion(shortsComment.getCreateAt()))
                .build();
    }


}
