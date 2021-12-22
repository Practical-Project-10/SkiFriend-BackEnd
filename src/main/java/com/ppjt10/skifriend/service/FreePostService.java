package com.ppjt10.skifriend.service;


import com.ppjt10.skifriend.config.S3Uploader;
import com.ppjt10.skifriend.dto.CommentDto;
import com.ppjt10.skifriend.dto.FreePostDto;
import com.ppjt10.skifriend.dto.LikesDto;
import com.ppjt10.skifriend.entity.Comment;
import com.ppjt10.skifriend.entity.FreePost;
import com.ppjt10.skifriend.entity.Likes;
import com.ppjt10.skifriend.repository.CommentRepository;
import com.ppjt10.skifriend.repository.FreePostRepository;
import com.ppjt10.skifriend.repository.LikesRepository;
import com.ppjt10.skifriend.security.UserDetailsImpl;
import com.ppjt10.skifriend.time.TimeConversion;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.NotActiveException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FreePostService {
    private final FreePostRepository freePostRepository;
    private final S3Uploader s3Uploader;
    private final LikesRepository likesRepository;
    private final CommentRepository commentRepository;

    private final String imageDirName = "freepost";

    //region 자유 게시판 게시글 작성
    @Transactional
    public void uploadFreePosts(
            UserDetailsImpl userDetails,
            MultipartFile image,
            String skiResort,
            FreePostDto.RequestDto requestDto
    ) throws IOException {
        if(userDetails == null) {
            throw new IllegalArgumentException("회원가입 후 이용해주세요.");
        }
        String imageUrl = s3Uploader.upload(image, imageDirName);

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
        FreePost freePost = freePostRepository.findByIdAndSkiResort(postId, skiResort).orElseThrow(
                ()-> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

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
    @Transactional
    public void modifyFreePost(
            UserDetailsImpl userDetails,
            FreePostDto.RequestDto requestDto,
            MultipartFile image,
            String skiResort,
            Long postId
            ) throws IOException {
        FreePost freePost = freePostRepository.findByIdAndSkiResort(postId, skiResort).orElseThrow(
                ()-> new IllegalArgumentException("해당 게시글이 존재하지 않습니다"));
        if(userDetails.getUser().getId() != freePost.getUser().getId()) {
            throw new IllegalArgumentException("게시글을 작성한 유저만 수정이 가능합니다.");
        }
        String oldImageUrl = URLDecoder.decode(freePost.getImage().replace("https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/", ""), "UTF-8");
        s3Uploader.deleteFromS3(oldImageUrl);
        String imageUrl = s3Uploader.upload(image, imageDirName);
        freePost.update(requestDto, imageUrl);
    }
    //endregion

    //region 자유 게시판 게시글 삭제
    @Transactional
    public void deleteFreePost(UserDetailsImpl userDetails,
                               Long postId,
                               String skiResort) {
        FreePost freePost = freePostRepository.findByIdAndSkiResort(postId, skiResort).orElseThrow(
                ()-> new IllegalArgumentException("해당 게시글이 존재하지 않습니다"));
        if(userDetails.getUser().getId() != freePost.getUser().getId()) {
            throw new IllegalArgumentException("게시글을 작성한 유저만 삭제가 가능합니다.");
        }
        freePostRepository.deleteById(postId);
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
        FreePost freePost = freePostRepository.findByIdAndSkiResort(postId, skiResort).orElseThrow(
                ()-> new IllegalArgumentException("해당 게시글이 존재하지 않습니다"));

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
    public void editComment(UserDetailsImpl userDetails,
                            CommentDto.RequestDto requestDto,
                            Long commentId)
    {
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

    //region HOT게시물 가져오기
    @Transactional
    public ResponseEntity<List<FreePostDto.ResortTabDto>> takeHotFreePosts() {

        List<FreePost> highOne = freePostRepository.findAllBySkiResortOrderByLikeCntDesc("highOne");
        System.out.println(highOne);
        List<FreePost> yongPyong = freePostRepository.findAllBySkiResortOrderByLikeCntDesc("yongPyong");
        List<FreePost> vivalDi = freePostRepository.findAllBySkiResortOrderByLikeCntDesc("vivalDi");
        List<FreePost> phoenix = freePostRepository.findAllBySkiResortOrderByLikeCntDesc("phoenix");
        List<FreePost> wellihilli = freePostRepository.findAllBySkiResortOrderByLikeCntDesc("wellihilli");
        List<FreePost> konJiam = freePostRepository.findAllBySkiResortOrderByLikeCntDesc("konJiam");

        List<FreePost> populatedResortPosts = new ArrayList<>();
        populatedResortPosts.add(highOne.get(0));
        populatedResortPosts.add(yongPyong.get(0));
        populatedResortPosts.add(vivalDi.get(0));
        populatedResortPosts.add(phoenix.get(0));
        populatedResortPosts.add(wellihilli.get(0));
        populatedResortPosts.add(konJiam.get(0));

        List<FreePostDto.ResortTabDto> resortTabDtoList  = populatedResortPosts.stream()
                .map(FreePost::toResortTabDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(resortTabDtoList);
    }


    //endregion
}
