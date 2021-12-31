package com.ppjt10.skifriend.service;


import com.ppjt10.skifriend.config.S3Uploader;
import com.ppjt10.skifriend.dto.CommentDto;
import com.ppjt10.skifriend.dto.FreePostDto;
import com.ppjt10.skifriend.dto.LikesDto;
import com.ppjt10.skifriend.entity.Comment;
import com.ppjt10.skifriend.entity.FreePost;
import com.ppjt10.skifriend.entity.Likes;
import com.ppjt10.skifriend.entity.SkiResort;
import com.ppjt10.skifriend.repository.CommentRepository;
import com.ppjt10.skifriend.repository.FreePostRepository;
import com.ppjt10.skifriend.repository.LikesRepository;
import com.ppjt10.skifriend.repository.SkiResortRepository;
import com.ppjt10.skifriend.security.UserDetailsImpl;
import com.ppjt10.skifriend.time.TimeConversion;
import com.ppjt10.skifriend.validator.SkiResortType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FreePostService {
    private final FreePostRepository freePostRepository;
    private final S3Uploader s3Uploader;
    private final String imageDirName = "freepost";
    private final LikesRepository likesRepository;
    private final SkiResortRepository skiResortRepository;
    private final CommentRepository commentRepository;

    //region 자유 게시판 게시글 작성
    @Transactional
    public FreePostDto.AllResponseDto uploadFreePosts(
            UserDetailsImpl userDetails,
            MultipartFile image,
            String resortName,
            FreePostDto.RequestDto requestDto
    ) throws IOException {
//        SkiResortType.findBySkiResortType(skiResort);
        SkiResort skiResort = skiResortRepository.findByResortName(resortName).orElseThrow(
                () -> new IllegalArgumentException("해당 이름의 스키장이 존재하지 않습니다.")
        );

        if (userDetails == null) {
            throw new IllegalArgumentException("회원가입 후 이용해주세요.");
        }

        String imageUrl;
        if(!image.isEmpty()) {
            try {
                imageUrl = s3Uploader.upload(image, imageDirName);
            } catch (Exception err) {
                imageUrl = "No Post Image";
            }
        } else  {
            imageUrl = "No Post Image";
        }

        FreePost freePost = new FreePost(
                userDetails.getUser(),
                skiResort,
                requestDto.getTitle(),
                requestDto.getContent(),
                imageUrl
        );

        freePostRepository.save(freePost);

        return generateFreePostResponseDto(freePost);

    }
    //endregion

    //region 자유 게시판 게시글 상세 조회
    @Transactional
    public FreePostDto.ResponseDto getFreePost(
            Long postId
    ) {
        FreePost freePost = freePostRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        List<Likes> likes = likesRepository.findAllByFreePostId(postId);

        List<LikesDto.ResponseDto> likesResponseDtoList = likes.stream()
                .map(e -> toLikesResponseDto(e))
                .collect(Collectors.toList());

        List<Comment> comments = commentRepository.findAllByFreePostId(postId);

        List<CommentDto.ResponseDto> commentResponseDtoList = comments.stream()
                .map(e -> toCommentResponseDto(e))
                .collect(Collectors.toList());

        FreePostDto.ResponseDto freeResponseDto = FreePostDto.ResponseDto.builder()
                .postId(postId)
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

        return freeResponseDto;
    }

    private LikesDto.ResponseDto toLikesResponseDto(Likes likes) {
        return LikesDto.ResponseDto.builder()
                .userId(likes.getUser().getId())
                .build();
    }

    private CommentDto.ResponseDto toCommentResponseDto(Comment comment) {
        return CommentDto.ResponseDto.builder()
                .commentId(comment.getId())
                .nickname(comment.getUser().getNickname())
                .content(comment.getContent())
                .createdAt(TimeConversion.timePostConversion(comment.getCreateAt()))
                .build();
    }
    //endregion

    //region 자유 게시판 게시글 수정
    @Transactional
    public FreePostDto.AllResponseDto modifyFreePost(
            UserDetailsImpl userDetails,
            FreePostDto.RequestDto requestDto,
            MultipartFile image,
            Long postId
    ) throws IOException {
        FreePost freePost = freePostRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다"));

        if (userDetails.getUser().getId() != freePost.getUser().getId()) {
            throw new IllegalArgumentException("게시글을 작성한 유저만 수정이 가능합니다.");
        }

        String imageUrl = freePost.getImage();
        if(!image.isEmpty()) {
            if(!imageUrl.equals("No Post Image")) {
                try {
                    String oldImageUrl = URLDecoder.decode(imageUrl.replace("https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/", ""), "UTF-8");
                    s3Uploader.deleteFromS3(oldImageUrl);
                } catch (Exception ignored) {}
            }

            if(!image.getOriginalFilename().equals("delete")) {
                try {
                    imageUrl = s3Uploader.upload(image, imageDirName);
                } catch (Exception err) {
                    imageUrl = "No Post Image";
                }
            } else{
                imageUrl = "No Post Image";
            }
        }

        freePost.update(requestDto, imageUrl);

        return generateFreePostResponseDto(freePost);
    }
    //endregion

    //region 자유 게시판 게시글 삭제
    @Transactional
    public void deleteFreePost(
            UserDetailsImpl userDetails,
            Long postId
    ) throws UnsupportedEncodingException {
        FreePost freePost = freePostRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다"));
        if (userDetails.getUser().getId() != freePost.getUser().getId()) {
            throw new IllegalArgumentException("게시글을 작성한 유저만 삭제가 가능합니다.");
        }
        String oldImageUrl = URLDecoder.decode(freePost.getImage().replace("https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/", ""), "UTF-8");
        s3Uploader.deleteFromS3(oldImageUrl);
        commentRepository.deleteAllByFreePostId(postId);
        likesRepository.deleteAllByFreePostId(postId);
        freePostRepository.deleteById(postId);
    }
    //endregion

    //region HOT게시물 가져오기
    @Transactional
    public List<FreePostDto.HotResponseDto> takeHotFreePosts() {
        FreePost highOne = extractHotFreePost(SkiResortType.HIGHONE.getSkiResortType());
        FreePost yongPyong = extractHotFreePost(SkiResortType.YONGPYONG.getSkiResortType());
        FreePost vivaldi = extractHotFreePost(SkiResortType.VIVALDIPARK.getSkiResortType());
        FreePost phoenix = extractHotFreePost(SkiResortType.PHOENIX.getSkiResortType());
        FreePost wellihilli = extractHotFreePost(SkiResortType.WELLIHILLIPARK.getSkiResortType());
        FreePost konJiam = extractHotFreePost(SkiResortType.KONJIAM.getSkiResortType());
        List<FreePost> populatedResortPosts = new ArrayList<>();
        exceptionProcessHotFreePost(populatedResortPosts, highOne);
        exceptionProcessHotFreePost(populatedResortPosts, yongPyong);
        exceptionProcessHotFreePost(populatedResortPosts, vivaldi);
        exceptionProcessHotFreePost(populatedResortPosts, phoenix);
        exceptionProcessHotFreePost(populatedResortPosts, wellihilli);
        exceptionProcessHotFreePost(populatedResortPosts, konJiam);
        List<FreePostDto.HotResponseDto> resortTabDtoList = populatedResortPosts.stream()
                .map(e -> toHotResponseDto(e))
                .collect(Collectors.toList());
        return resortTabDtoList;
    }

    // Hot 리조트별 실시간 가장 핫 한 게시물 찾기
    private FreePost extractHotFreePost(String skiResort) {
        try {
            SkiResort foundSkiResort = skiResortRepository.findByResortName(skiResort).orElseThrow(
                    () -> new IllegalArgumentException("해당하는 skiResort가 없습니다")
            );
            Long skiResortId = foundSkiResort.getId();
            List<Likes> hotLikesList = likesRepository.findAllByModifiedAtAfterAndFreePost_SkiResortId(LocalDateTime.now().minusHours(3), skiResortId);
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

    //실시간 Resort 별 Hot 게시물 에러처리
    private void exceptionProcessHotFreePost(List<FreePost> populatedResortPosts, FreePost skiResort) {
        if (skiResort != null) {
            populatedResortPosts.add(skiResort);
        }
    }

    private FreePostDto.HotResponseDto toHotResponseDto(FreePost freePost) {
        return FreePostDto.HotResponseDto.builder()
                .postId(freePost.getId())
                .title(freePost.getTitle())
                .skiResort(freePost.getSkiResort().getResortName())
                .createdAt(TimeConversion.timePostConversion(freePost.getCreateAt()))
                .likeCnt(freePost.getLikeCnt())
                .commentCnt(freePost.getCommentCnt())
                .build();
    }

    //자유게시글 전체 조회
    public List<FreePostDto.AllResponseDto> getFreePosts(String skiResortName, int page, int size) {

        List<FreePostDto.AllResponseDto> freePostResponseDtoList = new ArrayList<>();

        SkiResort skiResort = skiResortRepository.findByResortName(skiResortName).orElseThrow(
                () -> new IllegalArgumentException("해당 이름의 스키장이 존재하지 않습니다.")
        );

        //해당 스키장의 자유게시글 리스트 가져오기
        Page<FreePost> freePostPage = freePostRepository.findAllBySkiResortOrderByCreateAtDesc(
                skiResort,
                PageRequest.of(page, size)
        );

        //게시글 리스트
        if (freePostPage.hasContent()) {
            for (FreePost freePost : freePostPage.toList()) {
                freePostResponseDtoList.add(generateFreePostResponseDto(freePost));
            }
        }
        return freePostResponseDtoList;
    }

    private FreePostDto.AllResponseDto generateFreePostResponseDto(FreePost freePost) {
        return FreePostDto.AllResponseDto.builder()
                .postId(freePost.getId())
                .userId(freePost.getUser().getId())
                .nickname(freePost.getUser().getNickname())
                .createdAt(TimeConversion.timePostConversion(freePost.getCreateAt()))
                .title(freePost.getTitle())
                .likeCnt(freePost.getLikeCnt())
                .commentCnt(freePost.getCommentCnt())
                .build();
    }
    //endregion
}
