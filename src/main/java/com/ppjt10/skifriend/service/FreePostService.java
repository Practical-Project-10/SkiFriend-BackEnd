package com.ppjt10.skifriend.service;


import com.ppjt10.skifriend.config.S3Uploader;
import com.ppjt10.skifriend.dto.commentdto.CommentResponseDto;
import com.ppjt10.skifriend.dto.freepostdto.FreePostDetailResponseDto;
import com.ppjt10.skifriend.dto.freepostdto.FreePostHotResponseDto;
import com.ppjt10.skifriend.dto.freepostdto.FreePostRequestDto;
import com.ppjt10.skifriend.dto.freepostdto.FreePostResponseDto;
import com.ppjt10.skifriend.dto.likedto.LikesResponseDto;
import com.ppjt10.skifriend.dto.photodto.PhotoDto;
import com.ppjt10.skifriend.entity.*;
import com.ppjt10.skifriend.repository.*;
import com.ppjt10.skifriend.time.TimeConversion;
import lombok.RequiredArgsConstructor;
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
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;

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
    public FreePostResponseDto createFreePosts(List<MultipartFile> images,
                                               FreePostRequestDto requestDto,
                                               String resortName,
                                               User user
    ) {
        SkiResort skiResort = skiResortRepository.findByResortName(resortName).orElseThrow(
                () -> new IllegalArgumentException("해당 이름의 스키장이 존재하지 않습니다.")
        );

        FreePost freePost = new FreePost(user.getId(), skiResort, requestDto.getTitle(), requestDto.getContent());
        freePostRepository.save(freePost);

        savePhotoList(images, freePost);

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

        List<Photo> photoIdList = photoRepository.findAllByFreePost(freePost);
        List<PhotoDto> photoDtoList = new ArrayList<>();
        for (Photo photo : photoIdList) {
            photoDtoList.add(generatePhotoDto(photo));
        }

        return generateFreePostDetailResponseDto(freePost, likesResponseDtoList, commentResponseDtoList, photoDtoList);
    }

    // 자유 게시판 게시글 수정
    @Transactional
    public FreePostResponseDto updateFreePost(List<MultipartFile> images,
                                              FreePostRequestDto requestDto,
                                              Long postId,
                                              User user
    ) {
        FreePost freePost = freePostRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다")
        );

        if (!user.getId().equals(freePost.getUserId())) {
            throw new IllegalArgumentException("게시글을 작성한 유저만 수정이 가능합니다.");
        }

        // 삭제할 사진 아이디 리스트
        List<Long> photoIdList = requestDto.getPhotoIdList();
        // 게시물에 저장돼있던 사진 객체 리스트
        List<Photo> photoList = photoRepository.findAllByFreePost(freePost);

        for (Photo photo : photoList) {
            if (photoIdList.contains(photo.getId())) {
                try {
                    String oldImageUrl = URLDecoder.decode(photo.getFilePath().replace("https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/", ""), "UTF-8");
                    s3Uploader.deleteFromS3(oldImageUrl);
                    photoRepository.deleteById(photo.getId());
                } catch (Exception ignored) {
                }
            }
        }

        savePhotoList(images, freePost);

        freePost.update(requestDto);

        return generateFreePostResponseDto(freePost);
    }

    //자유 게시판 게시글 삭제
    @Transactional
    public void deleteFreePost(Long postId, User user) {

        FreePost freePost = freePostRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다")
        );

        if (!user.getId().equals(freePost.getUserId())) {
            throw new IllegalArgumentException("게시글을 작성한 유저만 삭제가 가능합니다.");
        }

        List<Photo> photoList = photoRepository.findAllByFreePost(freePost);

        for (Photo photo : photoList) {
            try {
                String oldImageUrl = URLDecoder.decode(photo.getFilePath().replace("https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/", ""), "UTF-8");
                s3Uploader.deleteFromS3(oldImageUrl);
                photoRepository.deleteById(photo.getId());
            } catch (Exception ignored) {
            }
        }

        commentRepository.deleteAllByFreePostId(postId);
        likesRepository.deleteAllByFreePostId(postId);
        freePostRepository.deleteById(postId);
    }

    // Hot 게시물 조회
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

    // 리조트별 실시간 가장 Hot 게시물 찾기
    private FreePost extractHotFreePost(String resortName) {
        try {
            SkiResort foundSkiResort = skiResortRepository.findByResortName(resortName).orElseThrow(
                    () -> new IllegalArgumentException("해당하는 skiResort가 없습니다")
            );
            Long skiResortId = foundSkiResort.getId();
            List<Likes> hotLikesList = likesRepository.findAllByModifiedAtAfterAndFreePost_SkiResortId(LocalDateTime.now().minusHours(24), skiResortId);
            HashMap<Long, Integer> postLikeCount = new HashMap<>();
            for (Likes likes : hotLikesList) {
                Long postId = likes.getFreePost().getId();
                if (postLikeCount.containsKey(postId)) {
                    postLikeCount.put(postId, postLikeCount.get(postId) + 1);
                } else {
                    postLikeCount.put(postId, 1);
                }
            }
            Long foundMaxLikeCntPostId = Collections.max(postLikeCount.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
            return freePostRepository.findById(foundMaxLikeCntPostId).orElseThrow(() -> new IllegalArgumentException("해당하는 게시물이 존재하지 않습니다"));
        } catch (Exception e) {
            return null;
        }
    }

    // 이미지 리스트 저장하기
    private void savePhotoList(List<MultipartFile> images, FreePost freePost) {
        if (images != null) {
            for (MultipartFile image : images) {
                String imageUrl;
                try {
                    imageUrl = s3Uploader.upload(image, imageDirName);
                } catch (Exception err) {
                    imageUrl = "No Post Image";
                }
                Photo photo = new Photo(image.getOriginalFilename(), imageUrl, freePost);
                photoRepository.save(photo);
            }
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
        Optional<User> user = userRepository.findById(freePost.getUserId());

        String nickname;
        if(user.isPresent()){
            nickname = user.get().getNickname();
        } else{
            nickname = "알 수 없음";
        }

        return FreePostResponseDto.builder()
                .postId(freePost.getId())
                .userId(freePost.getUserId())
                .nickname(nickname)
                .createdAt(TimeConversion.timePostConversion(freePost.getCreateAt()))
                .title(freePost.getTitle())
                .likeCnt(freePost.getLikeCnt())
                .commentCnt(freePost.getCommentCnt())
                .build();
    }

    // LikesResponseDto 생성
    private LikesResponseDto generateLikesResponseDto(Likes likes) {
        return LikesResponseDto.builder()
                .userId(likes.getUserId())
                .build();
    }

    // CommentResponseDto 생성
    private CommentResponseDto generateCommentResponseDto(Comment comment) {
        Optional<User> user = userRepository.findById(comment.getUserId());

        String nickname;
        String userProfile;
        if(user.isPresent()){
            nickname = user.get().getNickname();
            userProfile = user.get().getProfileImg();
        } else{
            nickname = "알 수 없음";
            userProfile = "https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/static/defalt+user+frofile.png";
        }

        return CommentResponseDto.builder()
                .userId(comment.getUserId())
                .commentId(comment.getId())
                .userProfile(userProfile)
                .nickname(nickname)
                .content(comment.getContent())
                .createdAt(TimeConversion.timePostConversion(comment.getCreateAt()))
                .build();
    }

    // PhotoDto 생성
    private PhotoDto generatePhotoDto(Photo photo) {
        return PhotoDto.builder()
                .photoId(photo.getId())
                .photoUrl(photo.getFilePath())
                .build();
    }


    // FreePostDetailResponseDto 생성
    private FreePostDetailResponseDto generateFreePostDetailResponseDto(FreePost freePost,
                                                                        List<LikesResponseDto> likesResponseDtoList,
                                                                        List<CommentResponseDto> commentResponseDtoList,
                                                                        List<PhotoDto> photoDtoList
    ) {
        Optional<User> user = userRepository.findById(freePost.getUserId());

        String nickname;
        String userProfile;
        if(user.isPresent()){
            nickname = user.get().getNickname();
            userProfile = user.get().getProfileImg();
        } else{
            nickname = "알 수 없음";
            userProfile = "https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/static/defalt+user+frofile.png";
        }

        return FreePostDetailResponseDto.builder()
                .userId(freePost.getUserId())
                .userProfile(userProfile)
                .postId(freePost.getId())
                .photoList(photoDtoList)
                .nickname(nickname)
                .createdAt(TimeConversion.timePostConversion(freePost.getCreateAt()))
                .title(freePost.getTitle())
                .content(freePost.getContent())
                .likeCnt(freePost.getLikeCnt())
                .commentCnt(freePost.getCommentCnt())
                .likesDtoList(likesResponseDtoList)
                .commentDtoList(commentResponseDtoList)
                .build();
    }
}
