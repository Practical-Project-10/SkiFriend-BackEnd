package com.ppjt10.skifriend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppjt10.skifriend.dto.CommentDto;
import com.ppjt10.skifriend.dto.FreePostDto;
import com.ppjt10.skifriend.dto.SignupDto;
import com.ppjt10.skifriend.repository.FreePostRepository;
import com.ppjt10.skifriend.service.FreePostService;
import lombok.val;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class FreePostControllerTest {

    @Autowired
    MockMvc mockMvc;

    private HttpHeaders headers;

    private static ObjectMapper objectMapper = new ObjectMapper();

    private String token = "";

    @Before
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
    @DisplayName("게시물 작성")
    void test3() throws Exception {
        //given
        String request = objectMapper.writeValueAsString(post1);

        File file = new File("/Users/beomin/Desktop/file.txt");
        FileInputStream fileInputStream = new FileInputStream(file);

        MockMultipartFile multipartFile1 = new MockMultipartFile("image", file.getName(), "multipart/form-data", "".getBytes());

        MockMultipartFile multipartFile2 = new MockMultipartFile("requestDto", "", "application/json", request.getBytes());
        mockMvc.perform(multipart("/board/{skiResort}/freeBoard", "HighOne")
                        .file(multipartFile1)
                        .file(multipartFile2)
                        .contentType("multipart/mixed")
                        .characterEncoding("UTF-8")
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                        .andExpect(status().isOk())
                        .andDo(print());
    }

    @Test
    @Order(4)
    @DisplayName("게시물 상세조회")
    void test4() throws Exception {
        Long postId = 1L;

        mockMvc.perform(get("/board/freeBoard/{postId}/detail", postId)
                        .header(HttpHeaders.AUTHORIZATION, this.token)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Order(5)
    @DisplayName("게시물 수정")
    void test5() throws Exception {

        String request = objectMapper.writeValueAsString(post1);

        Long postId = 1L;

        File file = new File("/Users/beomin/Desktop/file.txt");

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/board/freeBoard/{postId}", postId);

        builder.with(request1 -> {
            request1.setMethod("PUT");
            return request1;
        });

        FileInputStream fileInputStream = new FileInputStream(file);

        MockMultipartFile multipartFile1 = new MockMultipartFile("image", file.getName(), "multipart/form-data", fileInputStream);

        MockMultipartFile multipartFile2 = new MockMultipartFile("requestDto", "", "application/json", request.getBytes());
        mockMvc.perform(builder.file(multipartFile1).file(multipartFile2)
                        .header("Authorization", this.token))
                        .andExpect(status().isOk());

    }


    @Test
    @Order(6)
    @DisplayName("전체 게시글 조회")
    void test6() throws Exception {

        String skiResort = "HighOne";

        mockMvc.perform(get("/board/freeBoard/{skiResort}", skiResort)
                        .param("page", "1")
                        .param("size", "10")
                .header("Authorization", this.token))
                .andExpect(status().isOk())
                .andDo(print());
    }



    @Test
    @Order(7)
    @DisplayName("핫 게시물 조회")
    void test7() throws Exception {

        mockMvc.perform(get("/main"))
                .andExpect(status().isOk())
                .andDo(print());


    }

    @Test
    @Order(8)
    @DisplayName("게시물 좋아요")
    void test8() throws Exception {
        Long postId = 1L;

        mockMvc.perform(post("/board/freeBoard/{postId}/likes", postId)
                .header("Authorization", this.token))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Order(9)
    @DisplayName("게시글 삭제")
    void test9() throws Exception {
        Long postId = 1L;

        mockMvc.perform(delete("/board/freeBoard/{postId}", postId)
                        .header("Authorization", this.token))
                .andExpect(status().isOk())
                .andDo(print());
    }


    private SignupDto.RequestDto user1 = SignupDto.RequestDto.builder()
            .username("beomin12")
            .nickname("버민")
            .password("asdf12!!")
            .phoneNum("01078945321")
            .build();

    private UserControllerTest.TestLoginDto user1Login = UserControllerTest.TestLoginDto.builder()
            .username("beomin12")
            .password("asdf12!!")
            .build();

    private FreePostDto.RequestDto post1 = FreePostDto.RequestDto.builder()
            .title("버민")
            .content("내용")
            .build();

    private CommentDto.RequestDto comment1 = CommentDto.RequestDto.builder()
            .content("comment1")
            .build();
}