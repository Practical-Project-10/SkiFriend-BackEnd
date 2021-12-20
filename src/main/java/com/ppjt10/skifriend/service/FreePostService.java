package com.ppjt10.skifriend.service;


import com.ppjt10.skifriend.dto.CommentDto;
import com.ppjt10.skifriend.dto.FreePostDto;
import com.ppjt10.skifriend.dto.LikesDto;
import com.ppjt10.skifriend.entity.Comment;
import com.ppjt10.skifriend.entity.FreePost;
import com.ppjt10.skifriend.entity.Likes;
import com.ppjt10.skifriend.repository.CommentRepository;
import com.ppjt10.skifriend.repository.FreePostRepository;
import com.ppjt10.skifriend.security.UserDetailsImpl;
import com.ppjt10.skifriend.time.TimeConversion;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FreePostService {
    private final FreePostRepository freePostRepository;
//    private final S3Uploader s3Uploader;
//    private final LikesRepository likesRepository;
    private final CommentRepository commentRepository;

    private final String imageDirName = "static";

    //region 자유 게시판 게시글 작성
    @Transactional
    public void uploadFreePosts(
            UserDetailsImpl userDetails,
            String skiResort,
            @ModelAttribute("requestDto") FreePostDto.RequestDto requestDto
    ) {
        if(userDetails == null) {
            throw new IllegalArgumentException("회원가입 후 이용해주세요.");
        }
        String imageUrl = s3Uploader.upload(requestDto.getImage(), imageDirName);

        FreePost freePost = FreePost.builder()
                .user(userDetails.getUser())
                .skiResort(skiResort)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .Image(imageUrl)
                .build();
        freePostRepository.save(freePost);
    }
    //endregion

    //region 자유 게시판 게시글 상세 조회
    @Transactional
    public ResponseEntity<FreePostDto.ResponseDto> getFreePost(
            String skiResort,
            Long postId
    ) {
        FreePost freePost = freePostRepository.findByIdAndSkiResort(postId, skiResort);
        List<LikesDto.ResponseDto> likesResponseDtoList = freePost.getLikeList().stream()
                .map(Likes::toResponseDto)
                .collect(Collectors.toList());
        List<CommentDto.ResponseDto> commentResponseDtoList = freePost.getCommentList().stream()
                .map(Comment::toResponseDto)
                .collect(Collectors.toList());
        FreePostDto.ResponseDto freeResponseDto = FreePostDto.ResponseDto.builder()
                .postId(postId)
                .nickname(freePost.getUser().getNickname())
                .createdAt(TimeConversion.timeConversion(freePost.getCreateAt()))
                .title(freePost.getTitle())
                .content(freePost.getContent())
                .image(freePost.getImage())
                .likesDtoList(likesResponseDtoList)
                .commentDtoList(commentResponseDtoList)
                .build();
        return ResponseEntity.ok().body(freeResponseDto);
    }
    //endregion

    //region 자유 게시판 게시글 수정
    public void modifyFreePost(
            FreePostDto.RequestDto requestDto,
            String skiResort,
            Long postId
            ) {
        FreePost freePost = freePostRepository.findByIdAndSkiResort(postId, skiResort);
        freePost.update(requestDto);

    }
    //endregion

    //region 자유 게시판 게시글 삭제
    public void deleteFreePost(Long postId, String skiResort) {
        freePostRepository.deleteByIdAndSkiResort(postId, skiResort);
    }
    //endregion

    //region 자유 게시판 게시글 댓글 작성
    @Transactional
    public void writeComment(
            UserDetailsImpl userDetails,
            CommentDto.RequestDto requestDto,
            String skiResort,
            Long postId
    ) {
        if(userDetails == null) {
            throw new IllegalArgumentException("회원가입 후 이용해주세요.");
        }
        FreePost freePost = freePostRepository.findByIdAndSkiResort(postId, skiResort);

        Comment comment = Comment.builder()
                .freePost(freePost)
                .user(userDetails.getUser())
                .content(requestDto.getContent())
                .build();

        commentRepository.save(comment);
    }
    //endregion

    //region 자유 게시판 게시글 댓글 수정
    @Transactional
    public void editComment(UserDetailsImpl userDetails, CommentDto.RequestDto requestDto, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다")
        );
        if(userDetails.getUser().getId() != comment.getUser().getId()) {
            throw new IllegalArgumentException("댓글 작성자만 댓글을 수정할 수 있습니다");
        }
        comment.update(requestDto);
    }
    //endregion

    //region 자유 게시판 게시글 댓글 삭제
    @Transactional
    public void deleteComment(UserDetailsImpl userDetails, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다")
        );
        if(userDetails.getUser().getId() != comment.getUser().getId()) {
            throw new IllegalArgumentException("댓글 작성자만 댓글을 삭제할 수 있습니다");
        }
        commentRepository.deleteById(commentId);
    }
    //endregion
}
