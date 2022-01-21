package com.ppjt10.skifriend.service;

import com.ppjt10.skifriend.config.S3Uploader;
import com.ppjt10.skifriend.dto.shortsdto.ShortsLikeResponseDto;
import com.ppjt10.skifriend.dto.shortsdto.ShortsRequestDto;
import com.ppjt10.skifriend.dto.shortsdto.ShortsResponseDto;
import com.ppjt10.skifriend.entity.Shorts;
import com.ppjt10.skifriend.entity.ShortsLike;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShortsService {
    private final ShortsRepository shortsRepository;
    private final UserRepository userRepository;
    private final RedisRepository redisRepository;
    private final ShortsCommentRepository shortsCommentRepository;
    private final ShortsLikeRepository shortsLikeRepository;
    private final S3Uploader s3Uploader;
    private final String videoDirName = "shorts";

    //Shorts 조회
    @Transactional
    public ShortsResponseDto getShorts(String ip) {
        List<Shorts> shortsList = shortsRepository.findAll();
        long totalNum = shortsList.size();
        long pastRanNum = redisRepository.getRandomNumIp(ip);
        System.out.println("pastRanNum : " + pastRanNum);

        if(totalNum == 0) {
            throw new IllegalArgumentException("Shorts가 하나도 없습니다");
        }

        Shorts shorts;
        long randomNum = (long)(Math.random() * totalNum + 1);
        System.out.println("randomNum: " + randomNum);
        while(randomNum == pastRanNum) {
            randomNum = (long)(Math.random() * totalNum + 1);
            System.out.println("while문 안 randomNum: " + randomNum);
        }
        redisRepository.setRandomNumIp(ip, (int)randomNum);
        shorts = shortsList.get((int)randomNum - 1);
        System.out.println("실행중인 shortsId: " + shorts.getId());
        return generateShortsResponseDto(shorts);

//        long pastRanNum = redisRepository.getRandomNumIp(ip);
//        long totalNum = shortsRepository.count();
//        if(totalNum == 0) {
//            throw new IllegalArgumentException("Shorts가 하나도 없습니다");
//        }
//        Optional<Shorts> shorts;
//        do {
//            long randomNum = (long)(Math.random() * totalNum + 1);
//            while(randomNum == pastRanNum) {
//                randomNum = (long)(Math.random() * totalNum + 1);
//            }
//            redisRepository.setRandomNumIp(ip, (int)randomNum);
//            shorts = shortsRepository.findById(randomNum);
//        } while (!shorts.isPresent());
//
//        return generateShortsResponseDto(shorts.get());
    }

    //Shorts 작성
    @Transactional
    public ShortsResponseDto createShorts(MultipartFile videoFile,
                                          ShortsRequestDto requestDto,
                                          User user
    ) throws IOException {
        String videoUrl = s3Uploader.uploadVideo(videoFile, videoDirName).split("~")[0];
        String thumbNailUrl = s3Uploader.uploadVideo(videoFile, videoDirName).split("~")[1];
        Shorts shorts = new Shorts(user.getId(), requestDto.getTitle(), videoUrl, thumbNailUrl);
        shortsRepository.save(shorts);

        return generateShortsResponseDto(shorts);
    }

    //Shorts 수정
    @Transactional
    public ShortsResponseDto updateShorts(ShortsRequestDto requestDto, Long shortsId, User user) {

        Shorts shorts = shortsRepository.findById(shortsId).orElseThrow(
                () -> new IllegalArgumentException("해당 동영상이 존재하지 않습니다.")
        );

        if (!user.getId().equals(shorts.getUserId())) {
            throw new IllegalArgumentException("게시글을 작성한 유저만 수정이 가능합니다.");
        }

        if (requestDto.getTitle() == null) {
            throw new IllegalArgumentException("제목을 작성해주세요");
        }

        shorts.update(requestDto.getTitle());
        return generateShortsResponseDto(shorts);
    }

    //Shorts 삭제
    @Transactional
    public void deleteShorts(Long shortsId, User user) {
        Shorts shorts = shortsRepository.findById(shortsId).orElseThrow(
                () -> new IllegalArgumentException("해당 동영상이 존재하지 않습니다.")
        );

        if (!user.getId().equals(shorts.getUserId())) {
            throw new IllegalArgumentException("게시글을 작성한 유저만 삭제가 가능합니다.");
        }
        try {
            String oldVideoUrl = URLDecoder.decode(shorts.getVideoPath().replace("https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/", ""), "UTF-8");
            s3Uploader.deleteFromS3(oldVideoUrl);
            String oldThumbNailUrl = URLDecoder.decode(shorts.getThumbNailPath().replace("https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/", ""), "UTF-8");
            s3Uploader.deleteFromS3(oldThumbNailUrl);
        } catch (Exception ignored) {
        }

        shortsCommentRepository.deleteAllByShortsId(shortsId);
        shortsLikeRepository.deleteAllByShortsId(shortsId);
        shortsRepository.deleteById(shortsId);
    }

    // ShortsLikeResponseDto 생성
    private ShortsLikeResponseDto generateShortsLikeResponseDto(ShortsLike shortsLike) {
        return ShortsLikeResponseDto.builder()
                .userId(shortsLike.getUserId())
                .build();
    }


    //ShortsResponseDto 생성
    private ShortsResponseDto generateShortsResponseDto(Shorts shorts) {
        String nickname;
        String profileImg;
        try {
            User user = userRepository.findById(shorts.getUserId()).orElseThrow(
                    () -> new IllegalArgumentException("해당 유저가 존재하지 않습니다.")
            );
            nickname = user.getNickname();
            profileImg = user.getProfileImg();
        } catch (Exception e) {
            nickname = "알 수 없음";
            profileImg = "https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/static/defalt+user+frofile.png";
        }
        List<ShortsLikeResponseDto> shortsLikeResponseDtoList = new ArrayList<>();
        List<ShortsLike> shortsLikeList = shortsLikeRepository.findAllByShorts(shorts);
        for(ShortsLike shortsLike : shortsLikeList) {
            shortsLikeResponseDtoList.add(generateShortsLikeResponseDto(shortsLike));
        }

        return ShortsResponseDto.builder()
                .shortsId(shorts.getId())
                .userId(shorts.getUserId())
                .nickname(nickname)
                .profileImg(profileImg)
                .videoPath(shorts.getVideoPath())
                .thumbNailPath(shorts.getThumbNailPath())
                .title(shorts.getTitle())
                .shortsCommentCnt(shorts.getShortsCommentCnt())
                .shortsLikeCnt(shorts.getShortsLikeCnt())
                .shortsLikeResponseDtoList(shortsLikeResponseDtoList)
                .build();
    }
}

