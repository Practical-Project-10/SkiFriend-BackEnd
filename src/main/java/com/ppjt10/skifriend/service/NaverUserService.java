package com.ppjt10.skifriend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppjt10.skifriend.dto.signupdto.SignupSocialDto;
import com.ppjt10.skifriend.dto.userdto.UserLoginResponseDto;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.repository.UserRepository;
import com.ppjt10.skifriend.security.UserDetailsImpl;
import com.ppjt10.skifriend.security.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.UUID;

@RequiredArgsConstructor
@Service //서비스로 등록 -> Bean등록
public class NaverUserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    @Transactional
    public SignupSocialDto naverLogin(String code) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getAccessToken(code, "http://localhost:3000/user/naver/callback");

        // 3. 필요시에 회원가입
        User naverUser = registerNaverUserIfNeeded(accessToken);

        // 4. 로그인 JWT 토큰 발행
        String token = jwtTokenCreate(naverUser);

        UserLoginResponseDto userLoginResponseDto = UserLoginResponseDto.builder()
                .userId(naverUser.getId())
                .nickname(naverUser.getNickname())
                .isProfile(true)
                .build();

        return SignupSocialDto.builder()
                .token(token)
                .userLoginResponseDto(userLoginResponseDto)
                .build();
    }

//    @Transactional
//    public UserLoginResponseDto naverAddUserProfile(String code, Long userId) throws JsonProcessingException {
//        // 업데이트 필요성 체크
//        User user = userRepository.findById(userId).orElseThrow(
//                () -> new IllegalArgumentException("유저가 없어용")
//        );
//
//        UserLoginResponseDto userLoginResponseDto;
//        if (user.getAgeRange() == null) {
//            // 1. "인가 코드"로 "액세스 토큰" 요청
//            String accessToken = getAccessToken(code, "http://localhost:3000/user/naver/callback/properties");
//
//            // 2. 유저 정보 업데이트
//            userLoginResponseDto = updateUserProfile(accessToken, user);
//        } else {
//            userLoginResponseDto = UserLoginResponseDto.builder()
//                    .userId(user.getId())
//                    .nickname(user.getNickname())
//                    .isProfile(true)
//                    .build();
//        }
//        return userLoginResponseDto;
//    }

    private String getAccessToken(String code, String redirect_uri) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        final String state = new BigInteger(130, new SecureRandom()).toString();
        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("state", state);
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> naverTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                naverTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    // 젤 처음 로그인 시, 회원 가입 안되어 있으면 회원 가입 시켜주기
    private User registerNaverUserIfNeeded(String accessToken) throws JsonProcessingException {
        JsonNode jsonNode = getNaverUserInfo(accessToken);

        // DB 에 중복된 Kakao Id 가 있는지 확인
        String naverId = String.valueOf(jsonNode.get("response").get("id").asLong());
        User naverUser = userRepository.findByUsername(naverId)
                .orElse(null);

        // 회원가입
        if (naverUser == null) {
            String naverNick = jsonNode.get("response").get("nickname").asText();
            String gender = jsonNode.get("response").get("gender").asText();
            String ageRange = jsonNode.get("response").get("age").asText();

            // password: random UUID
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);

            naverUser = new User(naverId, naverNick, encodedPassword, gender, ageRange);
            userRepository.save(naverUser);
        }

        return naverUser;
    }

    // 유저 프로필 등록 (나이대, 성별)
//    private UserLoginResponseDto updateUserProfile(String accessToken, User user) throws JsonProcessingException {
//        JsonNode jsonNode = getNaverUserInfo(accessToken);
//
//        String ageRange = jsonNode.get("kakao_account").get("age_range").asText();
//        String gender = jsonNode.get("kakao_account").get("gender").asText();
//
//        user.updateKakaoProfile(ageRange, gender);
//
//        boolean isProfile = false;
//        if (user.getPhoneNum() != null) {
//            isProfile = true;
//        }
//
//        return UserLoginResponseDto.builder()
//                .userId(user.getId())
//                .nickname(user.getNickname())
//                .isProfile(isProfile)
//                .build();
//    }

    // 카카오에서 동의 항목 가져오기
    private JsonNode getNaverUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> naverUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST,
                naverUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(responseBody);
    }

    // JWT 토큰 생성
    private String jwtTokenCreate(User naverUser) {
        String TOKEN_TYPE = "BEARER";

        UserDetails userDetails = new UserDetailsImpl(naverUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails1 = ((UserDetailsImpl) authentication.getPrincipal());
        final String token = JwtTokenUtils.generateJwtToken(userDetails1);
        return TOKEN_TYPE + " " + token;
    }
}
