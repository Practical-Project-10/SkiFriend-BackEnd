package com.ppjt10.skifriend.service;


import com.ppjt10.skifriend.config.S3Uploader;
import com.ppjt10.skifriend.dto.UserDto;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.repository.UserRepository;
import com.ppjt10.skifriend.validator.AgeRangeType;
import com.ppjt10.skifriend.validator.CareerType;
import com.ppjt10.skifriend.validator.GenderType;
import com.ppjt10.skifriend.validator.UserInfoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Uploader s3Uploader;
    private final String profileImgDirName = "Profile";
    private final String vacImgDirName = "Vaccine";

    @Transactional
    public void createUser(MultipartFile profileImg, MultipartFile vacImg, UserDto.RequestDto requestDto) throws IOException {

        String username = requestDto.getUsername();
        String nickname = requestDto.getNickname();
        String password = requestDto.getPassword();

        // 중복 검사
        checkDuplicatoin(username, nickname);

        // 유효성 검사
        UserInfoValidator.validateUserInfoInput(username, nickname, password);
        GenderType.findByGenderType(requestDto.getGender()).getGenderType();
        AgeRangeType.findByageRangeType(requestDto.getAgeRange()).getageRangeType();
        CareerType.findByCareerType(requestDto.getCareer()).getCareerType();

        // 민감 정보 암호화
        String enPassword = passwordEncoder.encode(requestDto.getPassword());
        User user = new User(requestDto, enPassword);

        // 프로필 이미지 저장 및 저장 경로 User Entity에 set
        try {
            String profileImgUrl = s3Uploader.upload(profileImg, profileImgDirName);
            user.setProfileImg(profileImgUrl);
        } catch (Exception e) {
            user.setProfileImg("이미지 미설정");
        }

        // 백신 이미지 저장 및 저장 경로 User Entity에 set
        try {
            String vacImgUrl = s3Uploader.upload(vacImg, vacImgDirName);
            user.setVacImg(vacImgUrl);
        } catch (Exception e) {
            user.setVacImg("이미지 미설정");
        }

        userRepository.save(user);
    }

    private void checkDuplicatoin(String username, String nickname) {
        Optional<User> isUsername = userRepository.findByUsername(username);
        Optional<User> isNickname = userRepository.findByNickname(nickname);
        if (isUsername.isPresent() || isNickname.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }
    }


    @Transactional
    public UserDto.ResponseDto getUserInfo(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));
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

    @Transactional
    public UserDto.ResponseDto updateUserInfo(MultipartFile profileImg, MultipartFile vacImg, UserDto.updateRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));

        // 비밀번호, 기타 유저 정보 등, 이미지를 제외한 정보 업데이트
        String enPassword = passwordEncoder.encode(requestDto.getPassword());
        user.update(requestDto, enPassword);

        // 프로필 이미지 저장 및 저장 경로 업데이트
        try {
            String source = URLDecoder.decode(user.getProfileImg().replace("https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/", ""), "UTF-8");
            s3Uploader.deleteFromS3(source);
            String profileImgUrl = s3Uploader.upload(profileImg, profileImgDirName);
            user.setProfileImg(profileImgUrl);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            user.setProfileImg("이미지 미설정");
        }

        // 백신 이미지 저장 및 저장 경로 업데이트
        try {
            String source = URLDecoder.decode(user.getVacImg().replace("https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/", ""), "UTF-8");
            s3Uploader.deleteFromS3(source);
            String vacImgUrl = s3Uploader.upload(vacImg, vacImgDirName);
            user.setVacImg(vacImgUrl);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            user.setVacImg("이미지 미설정");
        }

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

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public UserDto.phoneNumDto getPhoneNum(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));

        return UserDto.phoneNumDto.builder()
                .phoneNumber(user.getPhoneNum())
                .build();
    }
}
