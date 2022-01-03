//package com.ppjt10.skifriend.controller;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.ppjt10.skifriend.dto.CommentDto;
//import com.ppjt10.skifriend.dto.FreePostDto;
//import com.ppjt10.skifriend.dto.SignupDto;
//import com.ppjt10.skifriend.service.FreePostService;
//import org.junit.jupiter.api.*;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestBody;
//
//import java.io.File;
//import java.io.FileInputStream;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//@AutoConfigureMockMvc
//class CommentControllerTest {
//
//    @Autowired
//    MockMvc mockMvc;
//
//    private HttpHeaders headers;
//
//    private static ObjectMapper objectMapper = new ObjectMapper();
//
//    private String token = "";
//
//    @BeforeEach
//    public void setup() {
//
//        headers = new HttpHeaders();
//
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//    }
//
//    @Test
//    @Order(1)
//    @DisplayName("회원 가입")
//    void test1() throws Exception {
//        // given
//        String requestBody = objectMapper.writeValueAsString(user1);
//
//        mockMvc.perform(post("/user/signup")
//                        .content(requestBody)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print());
//    }
//
//    @Test
//    @Order(2)
//    @DisplayName("로그인, JWT 토큰 받기")
//    void test2() throws Exception {
//        // given
//        String requestBody = objectMapper.writeValueAsString(user1Login);
//
//        MockHttpServletResponse response = mockMvc.perform(post("/user/login")
//                        .content(requestBody)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andReturn().getResponse();
//
//        token = response.getHeader(HttpHeaders.AUTHORIZATION);
//
//    }
//
//    @Test
//    @Order(3)
//    @DisplayName("게시물 작성")
//    void test3() throws Exception {
//        //given
//        String request = objectMapper.writeValueAsString(post1);
//
//
//        MockMultipartFile multipartFile1 = new MockMultipartFile("image", "empty.txt", "multipart/form-data", "".getBytes());
//
//        MockMultipartFile multipartFile2 = new MockMultipartFile("requestDto", "", "application/json", request.getBytes());
//        mockMvc.perform(multipart("/board/{skiResort}/freeBoard", "HighOne")
//                        .file(multipartFile1)
//                        .file(multipartFile2)
//                        .contentType("multipart/mixed")
//                        .characterEncoding("UTF-8")
//                        .header(HttpHeaders.AUTHORIZATION, this.token))
//                .andExpect(status().isOk())
//                .andDo(print());
//    }
//
//    @Test
//    @Order(4)
//    @DisplayName("댓글 작성")
//    void test4() throws Exception {
//
//        Long postId = 1L;
//
//        String requestDto = objectMapper.writeValueAsString(comment1);
//
//        mockMvc.perform(post("/board/freeBoard/{postId}/comments", postId)
//                        .header("Authorization", this.token)
//                        .content(requestDto)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print());
//    }
//
//    @Test
//    @Order(5)
//    @DisplayName("댓글 수정")
//    void test5() throws Exception {
//
//        Long commentId = 1L;
//
//        String requestDto = objectMapper.writeValueAsString(comment1);
//
//        mockMvc.perform(put("/board/freeBoard/comments/{commentId}", commentId)
//                        .header("Authorization", this.token)
//                        .content(requestDto)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print());
//
//    }
//
//    @Test
//    @Order(6)
//    @DisplayName("댓글 삭제")
//    void test6() throws Exception {
//
//        Long commentId = 1L;
//
//        mockMvc.perform(delete("/board/freeBoard/comments/{commentId}", commentId)
//                        .header("Authorization", this.token)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print());
//
//    }
//
//    private SignupDto.RequestDto user1 = SignupDto.RequestDto.builder()
//            .username("beomin12")
//            .nickname("버민")
//            .password("asdf12!!")
//            .phoneNum("01078945321")
//            .build();
//
//    private UserControllerTest.TestLoginDto user1Login = UserControllerTest.TestLoginDto.builder()
//            .username("beomin12")
//            .password("asdf12!!")
//            .build();
//
//    private FreePostDto.RequestDto post1 = FreePostDto.RequestDto.builder()
//            .title("버민")
//            .content("내용")
//            .build();
//
//
//    private CommentDto.RequestDto comment1 = CommentDto.RequestDto.builder()
//            .content("comment1")
//            .build();
//
//}