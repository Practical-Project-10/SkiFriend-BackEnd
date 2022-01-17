package com.ppjt10.skifriend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppjt10.skifriend.dto.signupdto.SignupSocialDto;
import com.ppjt10.skifriend.dto.userdto.UserResponseDto;
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

        UserResponseDto userResponseDto = UserResponseDto.builder()
                .userId(naverUser.getId())
                .username(naverUser.getUsername())
                .nickname(naverUser.getNickname())
                .profileImg(naverUser.getProfileImg())
                .ageRange(naverUser.getAgeRange())
                .gender(naverUser.getGender())
                .certification(naverUser.getPhoneNum() != null)
                .build();

        return SignupSocialDto.builder()
                .token(token)
                .userResponseDto(userResponseDto)
//                .userLoginResponseDto(userLoginResponseDto)
                .build();
    }

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

        // DB 에 중복된 Naver Id 가 있는지 확인
        String naverId = jsonNode.get("response").get("id").asText();
        User naverUser = userRepository.findByUsername(naverId)
                .orElse(null);

        // 회원가입
        if (naverUser == null) {
            String naverNick = jsonNode.get("response").get("nickname").asText();
            String gender = jsonNode.get("response").get("gender").asText();
            if(gender.equals("F")){
                gender = "여";
            } else {
                gender = "남";
            }

            String ageRange = jsonNode.get("response").get("age").asText();
            String userAge = ageRange.split("-")[0];
            if (Integer.parseInt(userAge) >= 20){
                userAge += "대";
            } else {
                userAge = "청소년";
            }

            // password: random UUID
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);

            naverUser = new User(naverId, naverNick, encodedPassword, gender, userAge);
            userRepository.save(naverUser);
        }

        return naverUser;
    }

    // 카카오에서 동의 항목 가져오기
    private JsonNode getNaverUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/xml;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> naverUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
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

