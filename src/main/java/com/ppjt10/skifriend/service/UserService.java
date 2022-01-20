package com.ppjt10.skifriend.service;


import com.ppjt10.skifriend.config.S3Uploader;
import com.ppjt10.skifriend.dto.carpooldto.CarpoolResponseDto;
import com.ppjt10.skifriend.dto.shortsdto.ShortsMyResponseDto;
import com.ppjt10.skifriend.dto.signupdto.SignupPhoneNumDto;
import com.ppjt10.skifriend.dto.userdto.UserProfileOtherDto;
import com.ppjt10.skifriend.dto.userdto.UserProfileUpdateDto;
import com.ppjt10.skifriend.dto.userdto.UserResponseDto;
import com.ppjt10.skifriend.entity.*;
import com.ppjt10.skifriend.repository.*;
import com.ppjt10.skifriend.validator.CareerType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ChatUserInfoRepository chatUserInfoRepository;
    private final CarpoolRepository carpoolRepository;
    private final ShortsRepository shortsRepository;
    private final S3Uploader s3Uploader;
    private final String profileImgDirName = "Profile";
    private final String defaultImg = "https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/static/defalt+user+frofile.png";

    // 유저 프로필 조회
    @Transactional
    public UserResponseDto getUserProfile(User user) {
        return generateUserResponseDto(user);
    }

    // 유저 프로필 수정
    @Transactional
    public UserResponseDto updateUserProfile(MultipartFile profileImg, UserProfileUpdateDto requestDto, User user) {

        User dbUser = userRepository.findById(user.getId()).orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        // CareerType.findByCareerType(requestDto.getCareer());

        // 기타 유저 정보 등, 이미지를 제외한 정보 업데이트
        dbUser.update(requestDto);

        // 프로필 이미지 저장 및 저장 경로 업데이트
        if (profileImg != null) {
            // 빈 이미지가 아닐때만 기존 이미지 삭제
            if (!dbUser.getProfileImg().equals(defaultImg)) {
                try {
                    String source = URLDecoder.decode(dbUser.getProfileImg().replace("https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/", ""), "UTF-8");
                    s3Uploader.deleteFromS3(source);
                } catch (Exception e) {
                }
            }

            if (!profileImg.getOriginalFilename().equals("delete")) {
                try {
                    String profileImgUrl = s3Uploader.upload(profileImg, profileImgDirName);
                    dbUser.setProfileImg(profileImgUrl);
                } catch (Exception e) {
                    dbUser.setProfileImg(defaultImg);
                }
            } else {
                dbUser.setProfileImg(defaultImg);
            }
        }

        return generateUserResponseDto(dbUser);
    }

    // 유저 탈퇴
    @Transactional
    public String deleteUser(User user) {
        Long userId = user.getId();
        userRepository.deleteById(userId);
        List<Carpool> carpoolList = carpoolRepository.findAllByUserId(userId);
        for (Carpool carpool : carpoolList) {
            carpool.setStatus(false);
        }

        List<ChatUserInfo> chatUserInfoList = chatUserInfoRepository.findAllByUserId(userId);
        for (ChatUserInfo chatUserInfo : chatUserInfoList) {
            chatUserInfo.getChatRoom().setActive(false);
        }

        return "회원탈퇴 되었습니다.";
    }

    // 내 폰 번호 공개
    @Transactional
    public SignupPhoneNumDto getPhoneNum(User user) {

        return SignupPhoneNumDto.builder()
                .phoneNumber(user.getPhoneNum())
                .build();
    }

    // 내가 쓴 카풀 게시물 목록 조회
    @Transactional
    public List<CarpoolResponseDto> getMyCarpools(User user) {
        List<Carpool> carpoolList = carpoolRepository.findAllByUserId(user.getId());

        List<CarpoolResponseDto> carpoolListDto = new ArrayList<>();
        for (Carpool carpool : carpoolList) {
            carpoolListDto.add(generateCarpoolResponseDto(carpool));
        }

        return carpoolListDto;
    }

    // 해당 채팅방에서 상대유저의 프로필 조회
    @Transactional
    public UserProfileOtherDto getOtherProfile(Long roomId, User user) {
        List<ChatUserInfo> chatUserInfoList = chatUserInfoRepository.findAllByChatRoomId(roomId);

        Long otherId;
        if (chatUserInfoList.get(0).getUserId().equals(user.getId())) {
            otherId = chatUserInfoList.get(1).getUserId();
        } else {
            otherId = chatUserInfoList.get(0).getUserId();
        }
        User other = userRepository.findById(otherId).orElseThrow(
                () -> new IllegalArgumentException("유저가 없어용")
        );
        return generateOtherResponseDto(other);
    }

    // 내가 쓴 Shorts 목록 페이지 조회
    @Transactional
    public List<ShortsMyResponseDto> getMyShorts(User user) {
        List<Shorts> shortsList = shortsRepository.findAllByUserId(user.getId());

        List<ShortsMyResponseDto> shortsMyResponseDtoList = new ArrayList<>();
        for (Shorts shorts : shortsList) {
            shortsMyResponseDtoList.add(generateShortsMyResponseDto(shorts));
        }
        return shortsMyResponseDtoList;
    }

    private CarpoolResponseDto generateCarpoolResponseDto(Carpool carpool) {
        String nickname;
        try {
            User user = userRepository.findById(carpool.getUserId()).orElseThrow(
                    () -> new IllegalArgumentException("해당 유저가 존재하지 않습니다.")
            );
            nickname = user.getNickname();
        } catch (Exception e) {
            nickname = "알 수 없음";
        }
        return CarpoolResponseDto.builder()
                .userId(carpool.getUserId())
                .postId(carpool.getId())
                .nickname(nickname)
                .createdAt(carpool.getCreateAt().toString())
                .carpoolType(carpool.getCarpoolType())
                .startLocation(carpool.getStartLocation())
                .endLocation(carpool.getEndLocation())
                .skiResort(carpool.getSkiResort().getResortName())
                .title(carpool.getTitle())
                .date(carpool.getDate())
                .time(carpool.getTime())
                .price(carpool.getPrice())
                .memberNum(carpool.getMemberNum())
                .notice(carpool.getNotice())
                .status(carpool.isStatus())
                .build();
    }

    private UserResponseDto generateUserResponseDto(User user) {
        return UserResponseDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .phoneNum(user.getPhoneNum())
                .nickname(user.getNickname())
                .profileImg(user.getProfileImg())
                .gender(user.getGender())
                .ageRange(user.getAgeRange())
                .career(user.getCareer())
                .selfIntro(user.getSelfIntro())
                .certification(user.getPhoneNum() != null)
                .build();
    }

    private UserProfileOtherDto generateOtherResponseDto(User user) {
        return UserProfileOtherDto.builder()
                .nickname(user.getNickname())
                .profileImg(user.getProfileImg())
                .gender(user.getGender())
                .ageRange(user.getAgeRange())
                .career(user.getCareer())
                .selfIntro(user.getSelfIntro())
                .build();
    }

    private ShortsMyResponseDto generateShortsMyResponseDto(Shorts shorts) {
        return ShortsMyResponseDto.builder()
                .shortsId(shorts.getId())
                .title(shorts.getTitle())
                .videoPath(shorts.getVideoPath())
                .build();
    }
}
