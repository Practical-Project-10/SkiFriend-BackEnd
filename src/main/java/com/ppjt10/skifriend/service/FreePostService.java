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
import com.ppjt10.skifriend.security.UserDetailsImpl;
import com.ppjt10.skifriend.time.TimeConversion;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FreePostService {
    private final FreePostRepository freePostRepository;
    private final S3Uploader s3Uploader;
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
        String imageUrl;
        try {
            imageUrl = s3Uploader.upload(image, imageDirName);
        }
        catch(Exception err) {
            imageUrl = "No Post Image";
        }


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
            Long postId
    ) {
        FreePost freePost = freePostRepository.findById(postId).orElseThrow(
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
            Long postId
    ) throws IOException {
        FreePost freePost = freePostRepository.findById(postId).orElseThrow(
                ()-> new IllegalArgumentException("해당 게시글이 존재하지 않습니다"));
        if(userDetails.getUser().getId() != freePost.getUser().getId()) {
            throw new IllegalArgumentException("게시글을 작성한 유저만 수정이 가능합니다.");
        }
        String imageUrl;
        try {
            imageUrl = s3Uploader.upload(image, imageDirName);
        } catch(Exception err) {
            imageUrl = "No Post Image";
        }
        try {
            String oldImageUrl = URLDecoder.decode(freePost.getImage().replace("https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/", ""), "UTF-8");
            s3Uploader.deleteFromS3(oldImageUrl);
        } catch(Exception ignored) {}
        freePost.update(requestDto, imageUrl);
    }
    //endregion

    //region 자유 게시판 게시글 삭제
    @Transactional
    public void deleteFreePost(
            UserDetailsImpl userDetails,
            Long postId
    ) throws UnsupportedEncodingException {
        FreePost freePost = freePostRepository.findById(postId).orElseThrow(
                ()-> new IllegalArgumentException("해당 게시글이 존재하지 않습니다"));
        if(userDetails.getUser().getId() != freePost.getUser().getId()) {
            throw new IllegalArgumentException("게시글을 작성한 유저만 삭제가 가능합니다.");
        }
        String oldImageUrl = URLDecoder.decode(freePost.getImage().replace("https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/", ""), "UTF-8");
        s3Uploader.deleteFromS3(oldImageUrl);
        freePostRepository.deleteById(postId);
    }
    //endregion



    //region HOT게시물 가져오기
    @Transactional
    public ResponseEntity<List<FreePostDto.ResortTabDto>> takeHotFreePosts() {
        List<FreePost> populatedResortPosts = new ArrayList<>();
        List<FreePost> highOne = freePostRepository.findAllBySkiResortOrderByLikeCntDesc("highOne");
        extractHotFreePost(populatedResortPosts, highOne);
        List<FreePost> yongPyong = freePostRepository.findAllBySkiResortOrderByLikeCntDesc("yongPyong");
        extractHotFreePost(populatedResortPosts, yongPyong);
        List<FreePost> vivaldi = freePostRepository.findAllBySkiResortOrderByLikeCntDesc("vivaldi");
        extractHotFreePost(populatedResortPosts, vivaldi);
        List<FreePost> phoenix = freePostRepository.findAllBySkiResortOrderByLikeCntDesc("phoenix");
        extractHotFreePost(populatedResortPosts, phoenix);
        List<FreePost> wellihilli = freePostRepository.findAllBySkiResortOrderByLikeCntDesc("wellihilli");
        extractHotFreePost(populatedResortPosts, wellihilli);
        List<FreePost> konJiam = freePostRepository.findAllBySkiResortOrderByLikeCntDesc("konJiam");
        extractHotFreePost(populatedResortPosts, konJiam);
        List<FreePostDto.ResortTabDto> resortTabDtoList  = populatedResortPosts.stream()
                .map(FreePost::toResortTabDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(resortTabDtoList);
    }

    //핫 게시물 nullCheck 후 resort별로 가장 인기있는 게시글 담기
    private void extractHotFreePost(List<FreePost> populatedResortPosts, List<FreePost> skiResort) {
        if (skiResort.size() != 0) {
            populatedResortPosts.add(skiResort.get(0));
        }
    }
    //endregion
}
