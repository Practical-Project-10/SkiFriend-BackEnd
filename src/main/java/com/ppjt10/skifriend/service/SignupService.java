package com.ppjt10.skifriend.service;

import com.ppjt10.skifriend.dto.SignupDto;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.repository.UserRepository;
import com.ppjt10.skifriend.validator.UserInfoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SignupService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

//    @Transactional
//    public void createUser(MultipartFile profileImg, MultipartFile vacImg, UserDto.RequestDto requestDto) throws IOException {
//        String username = requestDto.getUsername();
//        String nickname = requestDto.getNickname();
//        String password = requestDto.getPassword();
//        String phoneNumber = requestDto.getPhoneNum();
//
//        checkIsPhoneNum(phoneNumber);
//        checkIsNickname(nickname);
//        checkIsId(username);
//
//        // 유효성 검사
//        UserInfoValidator.validateUserInfoInput(username, nickname, password, phoneNumber, requestDto.getSelfIntro());
//        GenderType.findByGenderType(requestDto.getGender());
//        AgeRangeType.findByageRangeType(requestDto.getAgeRange());
//        CareerType.findByCareerType(requestDto.getCareer());
//
//        // 민감 정보 암호화
//        String enPassword = passwordEncoder.encode(password);
//        User user = new User(requestDto, enPassword);
//
//        // 프로필 이미지 저장 및 저장 경로 User Entity에 set
//        try {
//            String profileImgUrl = s3Uploader.upload(profileImg, profileImgDirName);
//            user.setProfileImg(profileImgUrl);
//        } catch (Exception e) {
//            user.setProfileImg("이미지 미설정");
//        }
//
//        // 백신 이미지 저장 및 저장 경로 User Entity에 set
//        try {
//            String vacImgUrl = s3Uploader.upload(vacImg, vacImgDirName);
//            user.setVacImg(vacImgUrl);
//        } catch (Exception e) {
//            user.setVacImg("이미지 미설정");
//        }
//
//        userRepository.save(user);
//    }

    // 아이디 중복 체크
    @Transactional
    public void checkIsId(String username) {
        Optional<User> isUsername = userRepository.findByUsername(username);
        if (isUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 아이디가 존재합니다.");
        }
    }

    // 닉네임 중복 체크
    @Transactional
    public void checkIsNickname(String nickname) {
        Optional<User> isNickname = userRepository.findByNickname(nickname);
        if (isNickname.isPresent()) {
            throw new IllegalArgumentException("중복된 닉네임이 존재합니다.");
        }
    }

    // 임시 1차 회원가입용 함수
    @Transactional
    public void signup(SignupDto.RequestDto requestDto) {
        String username = requestDto.getUsername();
        String nickname = requestDto.getNickname();
        String password = requestDto.getPassword();
        String phoneNumber = requestDto.getPhoneNum();

        //checkIsPhoneNum(phoneNumber);
        checkIsNickname(nickname);
        checkIsId(username);

        // 유효성 검사
        UserInfoValidator.validateUserInfoInput(username, nickname, password, phoneNumber);

        // 민감 정보 암호화
        String enPassword = passwordEncoder.encode(password);
        User user = new User(requestDto, enPassword);

        userRepository.save(user);
    }

    private void checkIsPhoneNum(String phoneNum) {
        Optional<User> isPhoneNum = userRepository.findByPhoneNum(phoneNum);
        if (isPhoneNum.isPresent()) {
            throw new IllegalArgumentException("중복된 핸드폰 번호가 존재합니다.");
        }
    }
}
