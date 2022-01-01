package com.ppjt10.skifriend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class ChatRoomControllerTest {

    @Autowired
    MockMvc mockMvc;

    private HttpHeaders headers;

    private static ObjectMapper objectMapper = new ObjectMapper();

    private String token = "";

    @BeforeEach
    public void setup() {

        headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

    }

    @Test
    @Order(1)
    @DisplayName("회원 가입")
    void test1() throws Exception {
        // given
        String requestBody = objectMapper.writeValueAsString(user1);

        mockMvc.perform(post("/user/signup")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Order(2)
    @DisplayName("로그인, JWT 토큰 받기")
    void test2() throws Exception {
        // given
        String requestBody = objectMapper.writeValueAsString(user1Login);

        MockHttpServletResponse response = mockMvc.perform(post("/user/login")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse();

        token = response.getHeader(HttpHeaders.AUTHORIZATION);

    }

    @Test
    @Order(3)
    @DisplayName("채팅방 만들기")
    void test3() {



    }


}