package com.ppjt10.skifriend.service;


import com.ppjt10.skifriend.config.S3Uploader;
import com.ppjt10.skifriend.dto.commentdto.CommentResponseDto;
import com.ppjt10.skifriend.dto.freepostdto.FreePostDetailResponseDto;
import com.ppjt10.skifriend.dto.freepostdto.FreePostHotResponseDto;
import com.ppjt10.skifriend.dto.freepostdto.FreePostRequestDto;
import com.ppjt10.skifriend.dto.freepostdto.FreePostResponseDto;
import com.ppjt10.skifriend.dto.likeDto.LikesResponseDto;
import com.ppjt10.skifriend.entity.*;
import com.ppjt10.skifriend.repository.CommentRepository;
import com.ppjt10.skifriend.repository.FreePostRepository;
import com.ppjt10.skifriend.repository.LikesRepository;
import com.ppjt10.skifriend.repository.SkiResortRepository;
import com.ppjt10.skifriend.time.TimeConversion;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FreePostService {
    private final FreePostRepository freePostRepository;
    private final S3Uploader s3Uploader;
    private final String imageDirName = "freepost";
    private final LikesRepository likesRepository;
    private final SkiResortRepository skiResortRepository;
    private final CommentRepository commentRepository;

    // 자유게시글 전체 조회
    @Transactional
    public List<FreePostResponseDto> getFreePosts(String resortName) {
        List<FreePostResponseDto> freePostResponseDtoList = new ArrayList<>();

        SkiResort skiResort = skiResortRepository.findByResortName(resortName).orElseThrow(
                () -> new IllegalArgumentException("해당 이름의 스키장이 존재하지 않습니다.")
        );

        // 해당 스키장의 자유게시글 리스트 가져오기
        List<FreePost> freePostList = freePostRepository.findAllBySkiResortOrderByCreateAtDesc(skiResort);

        //게시글 리스트
        for (FreePost freePost : freePostList) {
            freePostResponseDtoList.add(generateFreePostResponseDto(freePost));
        }

        return freePostResponseDtoList;
    }

    // 자유 게시판 게시글 작성
    @Transactional
    public FreePostResponseDto createFreePosts(MultipartFile image,
                                               FreePostRequestDto requestDto,
                                               String resortName,
                                               User user
    ) {
        SkiResort skiResort = skiResortRepository.findByResortName(resortName).orElseThrow(
                () -> new IllegalArgumentException("해당 이름의 스키장이 존재하지 않습니다.")
        );

        String imageUrl;
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + image.getOriginalFilename());
        if (image != null) {
            try {
                imageUrl = s3Uploader.upload(image, imageDirName);
            } catch (Exception err) {
                System.out.println("이미지 업로드 에러@@@@@@@@@@@@@@@@@@@@@@@@" + err);
                imageUrl = "No Post Image";
            }
        } else {
            imageUrl = "No Post Image";
        }

        FreePost freePost = new FreePost(user, skiResort, requestDto.getTitle(), requestDto.getContent(), imageUrl);

        freePostRepository.save(freePost);

        return generateFreePostResponseDto(freePost);

    }

    // 자유 게시판 게시글 상세 조회
    @Transactional
    public FreePostDetailResponseDto getDetailFreePost(Long postId) {

        FreePost freePost = freePostRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다.")
        );

        List<Likes> likesList = likesRepository.findAllByFreePostId(postId);
        List<LikesResponseDto> likesResponseDtoList = new ArrayList<>();
        for (Likes likes : likesList) {
            likesResponseDtoList.add(generateLikesResponseDto(likes));
        }

        List<Comment> commentList = commentRepository.findAllByFreePostIdOrderByCreateAtDesc(postId);
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
        for (Comment comment : commentList) {
            commentResponseDtoList.add(generateCommentResponseDto(comment));
        }

        return generateFreePostDetailResponseDto(freePost, likesResponseDtoList, commentResponseDtoList);
    }

    // 자유 게시판 게시글 수정
    @Transactional
    public FreePostResponseDto updateFreePost(MultipartFile image,
                                              FreePostRequestDto requestDto,
                                              Long postId,
                                              User user
    ) {
        FreePost freePost = freePostRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다")
        );

        if (!user.getId().equals(freePost.getUser().getId())) {
            throw new IllegalArgumentException("게시글을 작성한 유저만 수정이 가능합니다.");
        }

        String imageUrl = freePost.getImage();
        // 수정하려는 이미지가 빈 값이 아닐 때
        if (image != null) {
            // 이전에 업로드된 이미지가 존재할 경우 삭제
            if (!imageUrl.equals("No Post Image")) {
                try {
                    String oldImageUrl = URLDecoder.decode(imageUrl.replace("https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/", ""), "UTF-8");
                    s3Uploader.deleteFromS3(oldImageUrl);
                } catch (Exception ignored) {
                }
            }

            // 수정하려는 이미지가 delete가 아닐 때
            if (!image.getOriginalFilename().equals("delete")) {
                try {
                    imageUrl = s3Uploader.upload(image, imageDirName);
                } catch (Exception err) {
                    imageUrl = "No Post Image";
                }
            } else {
                imageUrl = "No Post Image";
            }
        }

        freePost.update(requestDto, imageUrl);

        return generateFreePostResponseDto(freePost);
    }

    //자유 게시판 게시글 삭제
    @Transactional
    public void deleteFreePost(Long postId, User user) {

        FreePost freePost = freePostRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다")
        );

        if (!user.getId().equals(freePost.getUser().getId())) {
            throw new IllegalArgumentException("게시글을 작성한 유저만 삭제가 가능합니다.");
        }

        try {
            String oldImageUrl = URLDecoder.decode(freePost.getImage().replace("https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/", ""), "UTF-8");
            s3Uploader.deleteFromS3(oldImageUrl);
        } catch (Exception exception) {
        }

        commentRepository.deleteAllByFreePostId(postId);
        likesRepository.deleteAllByFreePostId(postId);
        freePostRepository.deleteById(postId);
    }

    // HOT게시물 가져오기
    @Transactional
    public List<FreePostHotResponseDto> getHotFreePosts() {
        List<SkiResort> skiResortList = skiResortRepository.findAll();
        List<FreePost> populatedResortPosts = new ArrayList<>();
        for (SkiResort skiResort : skiResortList) {
            FreePost skiResortHotFreePost = extractHotFreePost(skiResort.getResortName());
            if (skiResortHotFreePost != null) {
                populatedResortPosts.add(skiResortHotFreePost);
            }
        }
        List<FreePostHotResponseDto> freePostHotResponseDtoList = new ArrayList<>();
        for (FreePost freePost : populatedResortPosts) {
            freePostHotResponseDtoList.add(generateFreePostHotResponseDto(freePost));
        }
        return freePostHotResponseDtoList;
    }

    // Hot 리조트별 실시간 가장 핫 한 게시물 찾기
    private FreePost extractHotFreePost(String resortName) {
        try {
            SkiResort foundSkiResort = skiResortRepository.findByResortName(resortName).orElseThrow(
                    () -> new IllegalArgumentException("해당하는 skiResort가 없습니다")
            );
            Long skiResortId = foundSkiResort.getId();
            List<Likes> hotLikesList = likesRepository.findAllByModifiedAtAfterAndFreePost_SkiResortId(LocalDateTime.now().minusHours(12), skiResortId);
            HashMap<Long, Integer> duplicatedCount = new HashMap<>();
            for (Likes likes : hotLikesList) {
                Long postId = likes.getFreePost().getId();
                duplicatedCount.put(postId, 0);
                if (duplicatedCount.containsKey(postId)) {
                    duplicatedCount.put(postId, duplicatedCount.get(postId) + 1);
                } else {
                    duplicatedCount.put(postId, 1);
                }
            }
            Long findId = Collections.max(duplicatedCount.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
            return freePostRepository.findById(findId).orElseThrow(() -> new IllegalArgumentException("해당하는 게시물이 존재하지 않습니다"));
        } catch (Exception e) {
            return null;
        }
    }

    // FreePostHotResponseDto 생성
    private FreePostHotResponseDto generateFreePostHotResponseDto(FreePost freePost) {
        return FreePostHotResponseDto.builder()
                .postId(freePost.getId())
                .title(freePost.getTitle())
                .skiResort(freePost.getSkiResort().getResortName())
                .createdAt(TimeConversion.timePostConversion(freePost.getCreateAt()))
                .likeCnt(freePost.getLikeCnt())
                .commentCnt(freePost.getCommentCnt())
                .build();
    }

    // FreePostResponseDto 생성
    private FreePostResponseDto generateFreePostResponseDto(FreePost freePost) {
        return FreePostResponseDto.builder()
                .postId(freePost.getId())
                .userId(freePost.getUser().getId())
                .nickname(freePost.getUser().getNickname())
                .createdAt(TimeConversion.timePostConversion(freePost.getCreateAt()))
                .title(freePost.getTitle())
                .likeCnt(freePost.getLikeCnt())
                .commentCnt(freePost.getCommentCnt())
                .build();
    }

    // LikesResponseDto 생성
    private LikesResponseDto generateLikesResponseDto(Likes likes) {
        return LikesResponseDto.builder()
                .userId(likes.getUser().getId())
                .build();
    }

    // CommentResponseDto 생성
    private CommentResponseDto generateCommentResponseDto(Comment comment) {
        return CommentResponseDto.builder()
                .commentId(comment.getId())
                .nickname(comment.getUser().getNickname())
                .content(comment.getContent())
                .createdAt(TimeConversion.timePostConversion(comment.getCreateAt()))
                .build();
    }

    // FreePostDetailResponseDto 생성
    private FreePostDetailResponseDto generateFreePostDetailResponseDto(FreePost freePost,
                                                                        List<LikesResponseDto> likesResponseDtoList,
                                                                        List<CommentResponseDto> commentResponseDtoList
    ) {
        return FreePostDetailResponseDto.builder()
                .postId(freePost.getId())
                .nickname(freePost.getUser().getNickname())
                .createdAt(TimeConversion.timePostConversion(freePost.getCreateAt()))
                .title(freePost.getTitle())
                .content(freePost.getContent())
                .image(freePost.getImage())
                .likeCnt(freePost.getLikeCnt())
                .commentCnt(freePost.getCommentCnt())
                .likesDtoList(likesResponseDtoList)
                .commentDtoList(commentResponseDtoList)
                .build();
    }
}
