package com.ppjt10.skifriend.service;

import com.ppjt10.skifriend.dto.UserDto;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.repository.UserRepository;
import com.ppjt10.skifriend.validator.UserInfoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void createUser(UserDto.RequestDto requestDto) {
        String username = requestDto.getUsername();
        String nickname = requestDto.getNickname();
        String password = requestDto.getPassword();

        // 중복 검사
        checkDuplicatoin(username, nickname);

        // 유효성 검사
        UserInfoValidator.validateUserInfoInput(username, nickname, password);

        // 민감 정보 암호화
        String enPassword = passwordEncoder.encode(requestDto.getPassword());

        User user = new User(requestDto, enPassword);

        // 이미지 저장

        // 이미지 경로값 Entity에 Set해주기


        userRepository.save(user);
    }

    private void checkDuplicatoin(String username, String nickname) {
        Optional<User> isUsername = userRepository.findByUsername(username);
        Optional<User> isNickname = userRepository.findByNickname(nickname);
        if (isUsername.isPresent() || isNickname.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }
    }
}
