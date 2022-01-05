package com.ppjt10.skifriend.service;

import com.ppjt10.skifriend.dto.signupdto.SignupRequestDto;
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

    // 아이디 중복 체크
    @Transactional
    public void checkId(String username) {
        Optional<User> isUsername = userRepository.findByUsername(username);
        if (isUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 아이디가 존재합니다.");
        }
    }

    // 닉네임 중복 체크
    @Transactional
    public void checkNickname(String nickname) {
        Optional<User> isNickname = userRepository.findByNickname(nickname);
        if (isNickname.isPresent()) {
            throw new IllegalArgumentException("중복된 닉네임이 존재합니다.");
        }
    }

    // 임시 1차 회원가입용 함수
    @Transactional
    public void signup(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String nickname = requestDto.getNickname();
        String password = requestDto.getPassword();
        String phoneNumber = requestDto.getPhoneNum();

        //checkPhoneNum(phoneNumber);
        checkNickname(nickname);
        checkId(username);

        // 유효성 검사
        UserInfoValidator.validateUserInfoInput(username, nickname, phoneNumber);
        UserInfoValidator.validatePassword(password);

        // 민감 정보 암호화
        String enPassword = passwordEncoder.encode(password);
        User user = new User(requestDto, enPassword);

        userRepository.save(user);
    }

    public void checkPhoneNum(String phoneNum) {
        Optional<User> isPhoneNum = userRepository.findByPhoneNum(phoneNum);
        if (isPhoneNum.isPresent()) {
            throw new IllegalArgumentException("중복된 핸드폰 번호가 존재합니다.");
        }
    }
}
