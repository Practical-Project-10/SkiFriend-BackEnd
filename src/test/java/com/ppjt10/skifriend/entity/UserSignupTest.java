//package com.ppjt10.skifriend.entity;
//
//import com.ppjt10.skifriend.dto.SignupDto;
//import com.ppjt10.skifriend.validator.UserInfoValidator;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class UserSignupTest {
//    private SignupDto.RequestDto signupDto;
//
//    @BeforeEach
//    void setup() {
//        signupDto = SignupDto.RequestDto.builder()
//                .username("diddl99")
//                .password("tjddms123!")
//                .phoneNum("01012341234")
//                .nickname("diddl")
//                .build();
//    }
//
//    @Nested
//    @DisplayName("회원가입 요청, 정상 케이스")
//    class SucessSignup {
//        @Test
//        @DisplayName("회원 가입 1")
//        void createUserInfo_Normal() {
//            UserInfoValidator.validateUserInfoInput(
//                    signupDto.getUsername(),
//                    signupDto.getNickname(),
//                    signupDto.getPassword(),
//                    signupDto.getPhoneNum());
//
//            User user = new User(signupDto, signupDto.getPassword());
//
//            assertEquals(user.getUsername(), signupDto.getUsername());
//            assertEquals(user.getPassword(), signupDto.getPassword());
//            assertEquals(user.getPhoneNum(), signupDto.getPhoneNum());
//            assertEquals(user.getNickname(), signupDto.getNickname());
//            assertNull(user.getCareer());
//            assertNull(user.getAgeRange());
//            assertNull(user.getGender());
//            assertNull(user.getSelfIntro());
//        }
//    }
//
//    @Nested
//    @DisplayName("회원가입 요청, 실패 케이스")
//    class CreateUserInfo {
//        @Nested
//        @DisplayName("회원 ID")
//        class Username {
//            @Test
//            @DisplayName("ID가 null일 때")
//            void fail1() {
//                signupDto.setUsername(null);
//
//                Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//                    UserInfoValidator.validateUserInfoInput(
//                            signupDto.getUsername(),
//                            signupDto.getNickname(),
//                            signupDto.getPassword(),
//                            signupDto.getPhoneNum());
//                });
//
//                assertEquals("영문, 숫자 5자리 이상, 특수 문자 사용 불가 합니다.", exception.getMessage());
//            }
//
//            @Test
//            @DisplayName("ID가 한국어로 이루어져 있을 때")
//            void fail2() {
//                signupDto.setUsername("프로카풀러");
//
//                Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//                    UserInfoValidator.validateUserInfoInput(
//                            signupDto.getUsername(),
//                            signupDto.getNickname(),
//                            signupDto.getPassword(),
//                            signupDto.getPhoneNum());
//                });
//
//                assertEquals("영문, 숫자 5자리 이상, 특수 문자 사용 불가 합니다.", exception.getMessage());
//            }
//
//            @Test
//            @DisplayName("ID 길이가 부족할 때")
//            void fail3() {
//                signupDto.setUsername("프로카풀러");
//
//                Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//                    UserInfoValidator.validateUserInfoInput(
//                            signupDto.getUsername(),
//                            signupDto.getNickname(),
//                            signupDto.getPassword(),
//                            signupDto.getPhoneNum());
//                });
//
//                assertEquals("영문, 숫자 5자리 이상, 특수 문자 사용 불가 합니다.", exception.getMessage());
//            }
//        }
//
//        @Nested
//        @DisplayName("회원 PASSWORD")
//        class Password {
//            @Test
//            @DisplayName("PASSWORD가 null일 때")
//            void fail1() {
//                signupDto.setPassword(null);
//
//                Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//                    UserInfoValidator.validateUserInfoInput(
//                            signupDto.getUsername(),
//                            signupDto.getNickname(),
//                            signupDto.getPassword(),
//                            signupDto.getPhoneNum());
//                });
//
//                assertEquals("특수문자 영어 숫자 포함, 최소 8자 이상이어야 합니다.", exception.getMessage());
//            }
//
//            @Test
//            @DisplayName("Password 길이가 부족할 때")
//            void fail2() {
//                signupDto.setPassword("tjddms1");
//
//                Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//                    UserInfoValidator.validateUserInfoInput(
//                            signupDto.getUsername(),
//                            signupDto.getNickname(),
//                            signupDto.getPassword(),
//                            signupDto.getPhoneNum());
//                });
//
//                assertEquals("특수문자 영어 숫자 포함, 최소 8자 이상이어야 합니다.", exception.getMessage());
//            }
//
//            @Test
//            @DisplayName("Password 특수문자 미포함일때")
//            void fail3() {
//                signupDto.setPassword("tjddms112");
//
//                Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//                    UserInfoValidator.validateUserInfoInput(
//                            signupDto.getUsername(),
//                            signupDto.getNickname(),
//                            signupDto.getPassword(),
//                            signupDto.getPhoneNum());
//                });
//
//                assertEquals("특수문자 영어 숫자 포함, 최소 8자 이상이어야 합니다.", exception.getMessage());
//            }
//        }
//
//        @Nested
//        @DisplayName("회원 PHONENUM")
//        class PhoneNum {
//            @Test
//            @DisplayName("PHONENUM가 null일 때")
//            void fail1() {
//                signupDto.setPhoneNum(null);
//
//                Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//                    UserInfoValidator.validateUserInfoInput(
//                            signupDto.getUsername(),
//                            signupDto.getNickname(),
//                            signupDto.getPassword(),
//                            signupDto.getPhoneNum());
//                });
//
//                assertEquals("휴대전화 번호를 정확히 입력해주세요.", exception.getMessage());
//            }
//
//            @Test
//            @DisplayName("PHONENUM 전화번호 형식이 안 맞을때")
//            void fail2() {
//                signupDto.setPhoneNum("010-1234-1234");
//
//                Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//                    UserInfoValidator.validateUserInfoInput(
//                            signupDto.getUsername(),
//                            signupDto.getNickname(),
//                            signupDto.getPassword(),
//                            signupDto.getPhoneNum());
//                });
//
//                assertEquals("휴대전화 번호를 정확히 입력해주세요.", exception.getMessage());
//            }
//        }
//
//        @Nested
//        @DisplayName("회원 NICKNAME")
//        class Nickname {
//            @Test
//            @DisplayName("NICKNAME가 null일 때")
//            void fail1() {
//                signupDto.setNickname(null);
//
//                Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//                    UserInfoValidator.validateUserInfoInput(
//                            signupDto.getUsername(),
//                            signupDto.getNickname(),
//                            signupDto.getPassword(),
//                            signupDto.getPhoneNum());
//                });
//
//                assertEquals("닉네임 값이 없습니다.", exception.getMessage());
//            }
//        }
//    }
//}
