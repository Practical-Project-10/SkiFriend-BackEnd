package com.ppjt10.skifriend.service;

import com.ppjt10.skifriend.config.S3Uploader;
import com.ppjt10.skifriend.dto.shortsdto.ShortsResponseDto;
import com.ppjt10.skifriend.entity.Shorts;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URLDecoder;
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

    @Transactional
    public ShortsResponseDto getShorts(HttpSession session) {
        long pastRanNum = redisRepository.getRandomNumSessionId(session.getId());
        long totalNum = shortsRepository.count();
        if(totalNum == 0) {
            throw new IllegalArgumentException("Shorts가 하나도 없습니다");
        }
        Optional<Shorts> shorts;
        do {
            long randomNum = (long)(Math.random() * totalNum + 1);
            while(randomNum == pastRanNum) {
                randomNum = (long)(Math.random() * totalNum + 1);
            }
            redisRepository.setRandomNumSessionId(session.getId(), (int)randomNum);
            shorts = shortsRepository.findById(randomNum);
        } while (!shorts.isPresent());

        return generateShortsResponseDto(shorts.get());
    }

    //Shorts 작성
    @Transactional
    public ShortsResponseDto createShorts(MultipartFile videoPath,
                                          String title,
                                          User user
    ) throws IOException {
        String videoUrl = s3Uploader.upload(videoPath, videoDirName);
        Shorts shorts = new Shorts(user.getId(), title, videoUrl);
        shortsRepository.save(shorts);


        return generateShortsResponseDto(shorts);
    }

    //Shorts 수정
    @Transactional
    public ShortsResponseDto updateShorts(String title, Long shortsId, User user) {

        Shorts shorts = shortsRepository.findById(shortsId).orElseThrow(
                () -> new IllegalArgumentException("해당 동영상이 존재하지 않습니다.")
        );

        if (!user.getId().equals(shorts.getUserId())) {
            throw new IllegalArgumentException("게시글을 작성한 유저만 수정이 가능합니다.");
        }

        if (title == null) {
            throw new IllegalArgumentException("제목을 작성해주세요");
        }

        shorts.update(title);
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
            String oldImageUrl = URLDecoder.decode(shorts.getVideoPath().replace("https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/", ""), "UTF-8");
            s3Uploader.deleteFromS3(oldImageUrl);
        } catch (Exception ignored) {
        }

        shortsCommentRepository.deleteAllByShortsId(shortsId);
        shortsLikeRepository.deleteAllByShortsId(shortsId);
        shortsRepository.deleteById(shortsId);
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
        return ShortsResponseDto.builder()
                .shortsId(shorts.getId())
                .userId(shorts.getUserId())
                .nickname(nickname)
                .profileImg(profileImg)
                .videoPath(shorts.getVideoPath())
                .title(shorts.getTitle())
                .shortsCommentCnt(shorts.getShortsCommentCnt())
                .shortsLikeCnt(shorts.getShortsLikeCnt())
                .build();
    }
}

