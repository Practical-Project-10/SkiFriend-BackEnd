package com.ppjt10.skifriend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppjt10.skifriend.dto.SignupDto;
import com.ppjt10.skifriend.dto.carpooldto.CarpoolRequestDto;
import com.ppjt10.skifriend.dto.carpooldto.CarpoolResponseDto;
import lombok.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
class CarpoolControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private TestRestTemplate restTemplate;

    private HttpHeaders headers;

    private static ObjectMapper objectMapper = new ObjectMapper();

    private String token = "";

    private SignupDto.RequestDto user1 = SignupDto.RequestDto.builder()
            .username("beomin12")
            .nickname("버민")
            .password("asdf12!!")
            .phoneNum("01078945321")
            .build();

    private TestLoginDto user1Login = TestLoginDto.builder()
            .username("beomin12")
            .password("asdf12!!")
            .build();

    private final List<CarpoolResponseDto> createdCarpools = new ArrayList<>();

    @BeforeEach
    public void setup() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    @Order(1)
    @DisplayName("회원 가입")
    void test1() throws JsonProcessingException {
        // given
        String requestBody = objectMapper.writeValueAsString(user1);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        // when
        ResponseEntity<Object> response = restTemplate.postForEntity(
                "/user/signup",
                request,
                Object.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @Order(2)
    @DisplayName("로그인, JWT 토큰 받기")
    void test2() throws JsonProcessingException {
        // given
        String requestBody = objectMapper.writeValueAsString(user1Login);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        // when
        ResponseEntity<Object> response = restTemplate.postForEntity(
                "/user/login",
                request,
                Object.class);

        // then
        token = response.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(3)
    @DisplayName("카풀 게시물 생성 성공")
    void test3() throws JsonProcessingException {

        headers.set("Authorization", token);

        CarpoolRequestDto carpoolRequest = CarpoolRequestDto.builder()
                .carpoolType("카풀 제공")
                .title("한자리 급구")
                .startLocation("서울")
                .endLocation("하이원")
                .date("2021-12-01")
                .time("17:00")
                .price(10000)
                .memberNum(4)
                .notice("주의")
                .build();

        String requestBody = objectMapper.writeValueAsString(carpoolRequest);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        String skiResort = "HighOne";
        ResponseEntity<CarpoolResponseDto> response = restTemplate.postForEntity(
                "/board/carpool/" + skiResort,
                request,
                CarpoolResponseDto.class);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        CarpoolResponseDto carpoolResponse = response.getBody();
        assertNotNull(carpoolResponse);
        assertEquals(carpoolResponse.getPostId(), 1L);
        assertEquals(carpoolResponse.getNickname(), user1.getNickname());
        assertEquals(carpoolResponse.getTitle(), carpoolRequest.getTitle());
        assertEquals(carpoolResponse.getSkiResort(), skiResort);
        assertEquals(carpoolResponse.getNotice(), carpoolRequest.getNotice());
        createdCarpools.add(carpoolResponse);
    }

    @Test
    @Order(4)
    @DisplayName("카풀 게시물 조회")
    void test4() throws Exception {

        String skiResort = "HighOne";

        mockMvc.perform(get("/board/carpool/{skiResort}", skiResort)
                        .param("page", "1")
                        .param("size", "10")
                        .header("Authorization", this.token))
                .andExpect(status().isOk())
                .andDo(print());
    }

//    void test4() throws JsonProcessingException {
//
//        headers.set("Authorization", token);
//        HttpEntity<Object> request = new HttpEntity<>(headers);
//
//        String skiResort = "HighOne";
//        ResponseEntity<CarpoolDto.ResponseDto[]> response = restTemplate.getForEntity(
//                "/board/carpool"+skiResort+"?page=" + 1 + "&size=" + 10,
//                CarpoolDto.ResponseDto[].class);
//
//        //then
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        CarpoolDto.ResponseDto[] responseDtos = response.getBody();
//        assert responseDtos != null;
//        Arrays.stream(responseDtos).forEach(System.out::println);
//        assertNotNull(responseDtos);
//        assertEquals(createdCarpools.size(), responseDtos.length);
//        for (CarpoolDto.ResponseDto carpoolResponseDto : responseDtos) {
//            CarpoolDto.ResponseDto createdCarpool = createdCarpools.stream()
//                    .filter(carpool -> carpoolResponseDto.getPostId().equals(carpool.getPostId()))
//                    .findAny()
//                    .orElse(null);
//
//            assertNotNull(createdCarpool);
//            assertEquals(createdCarpool.getCarpoolType(), carpoolResponseDto.getCarpoolType());
//            assertEquals(createdCarpool.getTitle(), carpoolResponseDto.getTitle());
//            assertEquals(createdCarpool.getNotice(), carpoolResponseDto.getNotice());
//        }
//    }

    @Test
    @Order(5)
    @DisplayName("카풀 게시물 수정")
    void test5() throws JsonProcessingException {

        headers.set("Authorization", token);

        CarpoolRequestDto carpoolUpdateRequest = CarpoolRequestDto.builder()
                .carpoolType("카풀 제공")
                .title("한자리 급구")
                .startLocation("대구")
                .endLocation("하이원")
                .date("2021-12-01")
                .time("17:00")
                .price(10000)
                .memberNum(4)
                .notice("주의하세요!")
                .build();

        String requestBody = objectMapper.writeValueAsString(carpoolUpdateRequest);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        String skiResort = "HighOne";
        long carpoolId = 1L;
        ResponseEntity<CarpoolResponseDto> response = restTemplate.exchange(
                "/board/carpool/" + carpoolId,
                HttpMethod.PUT,
                request,
                CarpoolResponseDto.class);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        CarpoolResponseDto carpoolResponse = response.getBody();
        assertNotNull(carpoolResponse);
        assertEquals(carpoolResponse.getPostId(), 1L);
        assertEquals(carpoolResponse.getNickname(), user1.getNickname());
        assertEquals(carpoolResponse.getTitle(), carpoolUpdateRequest.getTitle());
        assertEquals(carpoolResponse.getSkiResort(), skiResort);
        assertEquals(carpoolResponse.getNotice(), carpoolUpdateRequest.getNotice());
        createdCarpools.add(carpoolResponse);
    }


    @Test
    @Order(6)
    @DisplayName("카풀 게시물 삭제")
    void test6() {

        headers.set("Authorization", token);
        HttpEntity<String> request = new HttpEntity<>(headers);

        long carpoolId = 1L;
        ResponseEntity<CarpoolResponseDto> response = restTemplate.exchange(
                "/board/carpool/" + carpoolId,
                HttpMethod.DELETE,
                request,
                CarpoolResponseDto.class);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(7)
    @DisplayName("carpoolType 유효성 검사")
    void test7() throws JsonProcessingException {

        headers.set("Authorization", token);

        CarpoolRequestDto carpoolRequest = CarpoolRequestDto.builder()
                .carpoolType("카풀")
                .title("한자리 급구")
                .startLocation("서울")
                .endLocation("하이원")
                .date("2021-12-01")
                .time("17:00")
                .price(10000)
                .memberNum(4)
                .notice("주의")
                .build();

        String requestBody = objectMapper.writeValueAsString(carpoolRequest);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        String skiResort = "HighOne";
        ResponseEntity<CarpoolResponseDto> response = restTemplate.postForEntity(
                "/board/carpool/" + skiResort,
                request,
                CarpoolResponseDto.class);

        //then
        assertTrue(
                response.getStatusCode() == HttpStatus.BAD_REQUEST
                        || response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @Test
    @Order(8)
    @DisplayName("title null")
    void test8() throws JsonProcessingException {

        headers.set("Authorization", token);

        CarpoolRequestDto carpoolRequest = CarpoolRequestDto.builder()
                .carpoolType("카풀 제공")
                .startLocation("서울")
                .endLocation("하이원")
                .date("2021-12-01")
                .time("17:00")
                .price(10000)
                .memberNum(4)
                .notice("주의")
                .build();

        String requestBody = objectMapper.writeValueAsString(carpoolRequest);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        String skiResort = "HighOne";
        ResponseEntity<CarpoolResponseDto> response = restTemplate.postForEntity(
                "/board/carpool/" + skiResort,
                request,
                CarpoolResponseDto.class);

        //then
        assertTrue(
                response.getStatusCode() == HttpStatus.BAD_REQUEST
                        || response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @Test
    @Order(9)
    @DisplayName("time 형식 다름")
    void test9() throws JsonProcessingException {

        headers.set("Authorization", token);

        CarpoolRequestDto carpoolRequest = CarpoolRequestDto.builder()
                .carpoolType("카풀 제공")
                .title("한자리 급구")
                .startLocation("서울")
                .endLocation("하이원")
                .date("2021-12-01")
                .time("17시00분")
                .price(10000)
                .memberNum(4)
                .notice("주의")
                .build();

        String requestBody = objectMapper.writeValueAsString(carpoolRequest);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        String skiResort = "HighOne";
        ResponseEntity<CarpoolResponseDto> response = restTemplate.postForEntity(
                "/board/carpool/" + skiResort,
                request,
                CarpoolResponseDto.class);

        //then
        assertTrue(
                response.getStatusCode() == HttpStatus.BAD_REQUEST
                        || response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @Test
    @Order(10)
    @DisplayName("date 형식 다름")
    void test10() throws JsonProcessingException {

        headers.set("Authorization", token);

        CarpoolRequestDto carpoolRequest = CarpoolRequestDto.builder()
                .carpoolType("카풀 제공")
                .title("한자리 급구")
                .startLocation("서울")
                .endLocation("하이원")
                .date("2021년12월01일")
                .time("17시00분")
                .price(10000)
                .memberNum(4)
                .notice("주의")
                .build();

        String requestBody = objectMapper.writeValueAsString(carpoolRequest);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        String skiResort = "HighOne";
        ResponseEntity<CarpoolResponseDto> response = restTemplate.postForEntity(
                "/board/carpool/" + skiResort,
                request,
                CarpoolResponseDto.class);

        //then
        assertTrue(
                response.getStatusCode() == HttpStatus.BAD_REQUEST
                        || response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static class TestLoginDto {
        private String username;
        private String password;
    }
}