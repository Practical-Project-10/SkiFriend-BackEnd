package com.ppjt10.skifriend.service;


import com.ppjt10.skifriend.config.S3Uploader;
import com.ppjt10.skifriend.dto.carpooldto.CarpoolResponseDto;
import com.ppjt10.skifriend.dto.signupdto.SignupPhoneNumDto;
import com.ppjt10.skifriend.dto.userdto.*;
import com.ppjt10.skifriend.entity.Carpool;
import com.ppjt10.skifriend.entity.ChatUserInfo;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.repository.CarpoolRepository;
import com.ppjt10.skifriend.repository.ChatUserInfoRepository;
import com.ppjt10.skifriend.repository.UserRepository;
import com.ppjt10.skifriend.validator.AgeRangeType;
import com.ppjt10.skifriend.validator.CareerType;
import com.ppjt10.skifriend.validator.GenderType;
import com.ppjt10.skifriend.validator.UserInfoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private final S3Uploader s3Uploader;
    private final String profileImgDirName = "Profile";
    private final String vacImgDirName = "Vaccine";
    private final String defaultImg = "https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/static/defalt+user+frofile.png";

    // 유저 프로필 작성
    @Transactional
    public UserResponseDto createUserProfile(MultipartFile profileImg, MultipartFile vacImg, UserProfileRequestDto requestDto, User user) {

        // 유효성 검사
        GenderType.findByGenderType(requestDto.getGender());
        AgeRangeType.findByAgeRangeType(requestDto.getAgeRange());
        CareerType.findByCareerType(requestDto.getCareer());

        // 유저 프로필 작성
        user.createUserProfile(requestDto);

        // 프로필 이미지 저장 및 저장 경로 업데이트
        if (profileImg != null) {
            try {
                String profileImgUrl = s3Uploader.upload(profileImg, profileImgDirName);
                user.setProfileImg(profileImgUrl);
            } catch (Exception e) {
                user.setProfileImg("https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/static/6950b535-5658-4604-8039-dd9d4e3a1119profile+picture.png");
            }
        } else {
            user.setProfileImg("https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/static/6950b535-5658-4604-8039-dd9d4e3a1119profile+picture.png");
        }

        // 백신 이미지 저장 및 저장 경로 업데이트
        if (vacImg != null) {
            try {
                String vacImgUrl = s3Uploader.upload(vacImg, vacImgDirName);
                user.setVacImg(vacImgUrl);
            } catch (Exception e) {
                user.setVacImg("No Post Image");
            }
        } else {
            user.setVacImg("No Post Image");
        }

        return generateUserResponseDto(user);
    }

    // 유저 프로필 조회
    @Transactional
    public UserResponseDto getUserProfile(User user) {
        return generateUserResponseDto(user);
    }

    // 유저 프로필 수정
    @Transactional
    public UserResponseDto updateUserProfile(MultipartFile profileImg, MultipartFile vacImg, UserProfileUpdateDto requestDto, User user) {

        // 기타 유저 정보 등, 이미지를 제외한 정보 업데이트
        user.update(requestDto);

        // 프로필 이미지 저장 및 저장 경로 업데이트
        if (profileImg != null) {
            // 빈 이미지가 아닐때만 기존 이미지 삭제
            if (!user.getProfileImg().equals(defaultImg)) {
                try {
                    String source = URLDecoder.decode(user.getProfileImg().replace("https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/", ""), "UTF-8");
                    s3Uploader.deleteFromS3(source);
                } catch (Exception e) {
                }
            }

            if (!profileImg.getOriginalFilename().equals("delete")) {
                try {
                    String profileImgUrl = s3Uploader.upload(profileImg, profileImgDirName);
                    user.setProfileImg(profileImgUrl);
                } catch (Exception e) {
                    user.setProfileImg(defaultImg);
                }
            } else {
                user.setProfileImg(defaultImg);
            }
        }

        // 백신 이미지 저장 및 저장 경로 업데이트
        if (vacImg != null) {
            // 빈 이미지가 아닐때만 기존 이미지 삭제
            if (!user.getVacImg().equals("No Post Image")) {
                try {
                    String source = URLDecoder.decode(user.getVacImg().replace("https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/", ""), "UTF-8");
                    s3Uploader.deleteFromS3(source);
                } catch (Exception e) {
                }
            }

            try {
                String vacImgUrl = s3Uploader.upload(vacImg, vacImgDirName);
                user.setVacImg(vacImgUrl);
            } catch (Exception e) {
                user.setVacImg("No Post Image");
            }
        }

        return generateUserResponseDto(user);
    }

    // 유저 비밀번호 수정
    @Transactional
    public void updatePassword(UserPasswordUpdateDto passwordDto, User user) {

        // 기존 비밀번호랑 일치하면 비밀번호 업데이트
        if (passwordEncoder.matches(passwordDto.getPassword(), user.getPassword())) {
            // 새 비밀번호 유효성 검사
            UserInfoValidator.validatePassword(passwordDto.getNewPassword());

            // 새 비밀번호 암호화
            String enPassword = passwordEncoder.encode(passwordDto.getNewPassword());

            user.updatePassword(enPassword);
        } else {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    // 유저 탈퇴
    @Transactional
    public String deleteUser(User user) {
        userRepository.deleteById(user.getId());

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
        List<Carpool> carpoolList = carpoolRepository.findAllByUser(user);

        List<CarpoolResponseDto> carpoolListDto = new ArrayList<>();
        for (Carpool carpool : carpoolList) {
            carpoolListDto.add(generateCarpoolResponseDto(carpool));
        }

        return carpoolListDto;
    }

    @Transactional
    public UserProfileOtherDto getOtherProfile(Long longRoomId, User user) {
        List<ChatUserInfo> chatUserInfoList = chatUserInfoRepository.findAllByChatRoomId(longRoomId);

        User other;
        if (chatUserInfoList.get(0).getUser().getId().equals(user.getId())) {
            other = chatUserInfoList.get(1).getUser();
        } else {
            other = chatUserInfoList.get(0).getUser();
        }

        return generateOtherReponseDto(other);
    }

    private CarpoolResponseDto generateCarpoolResponseDto(Carpool carpool) {
        return CarpoolResponseDto.builder()
                .userId(carpool.getUser().getId())
                .postId(carpool.getId())
                .nickname(carpool.getUser().getNickname())
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
                .username(user.getUsername())
                .phoneNum(user.getPhoneNum())
                .nickname(user.getNickname())
                .profileImg(user.getProfileImg())
                .vacImg(user.getVacImg())
                .gender(user.getGender())
                .ageRange(user.getAgeRange())
                .career(user.getCareer())
                .selfIntro(user.getSelfIntro())
                .build();
    }

    private UserProfileOtherDto generateOtherReponseDto(User user) {
        return UserProfileOtherDto.builder()
                .nickname(user.getNickname())
                .profileImg(user.getProfileImg())
                .vacImg(user.getVacImg())
                .gender(user.getGender())
                .ageRange(user.getAgeRange())
                .career(user.getCareer())
                .selfIntro(user.getSelfIntro())
                .build();
    }
}
