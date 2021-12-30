package com.ppjt10.skifriend.service;


import com.ppjt10.skifriend.config.S3Uploader;
import com.ppjt10.skifriend.dto.CarpoolDto;
import com.ppjt10.skifriend.dto.SignupDto;
import com.ppjt10.skifriend.dto.UserDto;
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
import java.util.stream.Collectors;

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


    // 유저 프로필 작성
    @Transactional
    public UserDto.ResponseDto writeUserProfile(MultipartFile profileImg, MultipartFile vacImg, UserDto.ProfileRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));

        // 유효성 검사
        GenderType.findByGenderType(requestDto.getGender());
        AgeRangeType.findByageRangeType(requestDto.getAgeRange());
        CareerType.findByCareerType(requestDto.getCareer());

        // 유저 프로필 작성
        user.wirteProfile(requestDto);

        // 프로필 이미지 저장 및 저장 경로 업데이트
        if (!profileImg.isEmpty()) {
            try {
                String profileImgUrl = s3Uploader.upload(profileImg, profileImgDirName);
                user.setProfileImg(profileImgUrl);
            } catch (Exception e) {
                user.setProfileImg("No Post Image");
            }
        } else {
            user.setProfileImg("No Post Image");
        }

        // 백신 이미지 저장 및 저장 경로 업데이트
        if (!vacImg.isEmpty()) {
            try {
                String vacImgUrl = s3Uploader.upload(vacImg, vacImgDirName);
                user.setVacImg(vacImgUrl);
            } catch (Exception e) {
                user.setVacImg("No Post Image");
            }
        } else {
            user.setVacImg("No Post Image");
        }

        return createUserResponseDto(user);
    }

    // 유저 프로필 조회
    @Transactional
    public UserDto.ResponseDto getUserInfo(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));
        return createUserResponseDto(user);
    }

    // 유저 프로필 수정
    @Transactional
    public UserDto.ResponseDto updateUserInfo(MultipartFile profileImg, MultipartFile vacImg, UserDto.UpdateRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));

        // 기타 유저 정보 등, 이미지를 제외한 정보 업데이트
        user.update(requestDto);

        // 프로필 이미지 저장 및 저장 경로 업데이트
        System.out.println("Original파일이름!!!!!!! : " + profileImg.getOriginalFilename());
        System.out.println("name파일이름!!!!!!! : " + profileImg.getName());
        if (!profileImg.isEmpty()) {
            // 빈 이미지가 아닐때만 기존 이미지 삭제
            if (!user.getProfileImg().equals("No Post Image")) {
                try {
                    String source = URLDecoder.decode(user.getProfileImg().replace("https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/", ""), "UTF-8");
                    s3Uploader.deleteFromS3(source);
                } catch (Exception e) {}
            }

            if(!profileImg.getOriginalFilename().equals("delete")){
                try {
                    String profileImgUrl = s3Uploader.upload(profileImg, profileImgDirName);
                    user.setProfileImg(profileImgUrl);
                } catch (Exception e) {
                    user.setProfileImg("No Post Image");
                }
            } else {
                user.setProfileImg("No Post Image");
            }
        }

        // 백신 이미지 저장 및 저장 경로 업데이트
        if (!vacImg.isEmpty()) {
            // 빈 이미지가 아닐때만 기존 이미지 삭제
            if (!user.getVacImg().equals("No Post Image")) {
                try {
                    String source = URLDecoder.decode(user.getVacImg().replace("https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/", ""), "UTF-8");
                    s3Uploader.deleteFromS3(source);
                } catch (Exception e) {}
            }

            try {
                String vacImgUrl = s3Uploader.upload(vacImg, vacImgDirName);
                user.setVacImg(vacImgUrl);
            } catch (Exception e) {
                user.setVacImg("No Post Image");
            }
        }

        return createUserResponseDto(user);
    }

    // 유저 비밀번호 수정
    @Transactional
    public void updatePassword(UserDto.PasswordDto passwordDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));

        if (passwordEncoder.matches(passwordDto.getPassword(), user.getPassword())) {
            UserInfoValidator.checkPassword(passwordDto.getNewPassword());
            String enPassword = passwordEncoder.encode(passwordDto.getNewPassword());
            user.setPassword(enPassword);
        } else {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    // 유저 탈퇴
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // 내 폰 번호 공개
    @Transactional
    public SignupDto.PhoneNumDto getPhoneNum(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));

        return SignupDto.PhoneNumDto.builder()
                .phoneNumber(user.getPhoneNum())
                .build();
    }

    // 내가 쓴 카풀 게시물 목록 조회
    @Transactional
    public List<CarpoolDto.ResponseDto> findMyCarpools(User user) {
        List<Carpool> carpoolList = carpoolRepository.findAllByUser(user);

        List<CarpoolDto.ResponseDto> carpoolListDto = new ArrayList<>();
        for (Carpool carpool : carpoolList) {
            carpoolListDto.add(createCarpoolResponseDto(carpool));
        }

        return carpoolListDto;
    }

    @Transactional
    public UserDto.OtherResponseDto getOtherProfile(Long longRoomId, Long userId) {
        List<ChatUserInfo> chatUserInfoList = chatUserInfoRepository.findAllByChatRoomId(longRoomId);

        User user = new User();
        for (ChatUserInfo chatUserInfo : chatUserInfoList) {
            if (chatUserInfo.getUser().getId() != userId) {
                user = chatUserInfo.getUser();
            }
        }

        return createOtherReponseDto(user);
    }

    private CarpoolDto.ResponseDto createCarpoolResponseDto(Carpool carpool) {
        return CarpoolDto.ResponseDto.builder()
                .userId(carpool.getUser().getId())
                .postId(carpool.getId())
                .carpoolType(carpool.getCarpoolType())
                .startLocation(carpool.getStartLocation())
                .endLocation(carpool.getEndLocation())
                .date(carpool.getDate())
                .time(carpool.getTime())
                .price(carpool.getPrice())
                .memberNum(carpool.getMemberNum())
                .notice(carpool.getNotice())
                .status(carpool.isStatus())
                .build();
    }

    private UserDto.ResponseDto createUserResponseDto(User user) {
        return UserDto.ResponseDto.builder()
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

    private UserDto.OtherResponseDto createOtherReponseDto(User user) {
        return UserDto.OtherResponseDto.builder()
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
