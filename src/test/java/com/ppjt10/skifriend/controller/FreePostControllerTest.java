//package com.ppjt10.skifriend.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.ppjt10.skifriend.dto.commentdto.CommentRequestDto;
//import com.ppjt10.skifriend.dto.freepostdto.FreePostRequestDto;
//import com.ppjt10.skifriend.dto.signupdto.SignupRequestDto;
//
//import org.junit.Before;
//import org.junit.jupiter.api.*;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//@AutoConfigureMockMvc
//class FreePostControllerTest {
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
//    @Before
//    public void setup() {
//
//        headers = new HttpHeaders();
//
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//    }
//
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
//        MockMultipartFile multipartFile1 = new MockMultipartFile("requestDto", "", "application/json", request.getBytes());
//        mockMvc.perform(multipart("/board/{skiResort}/freeBoard", "HighOne")
//                        .file(multipartFile1)
//                        .contentType("multipart/mixed")
//                        .characterEncoding("UTF-8")
//                        .header(HttpHeaders.AUTHORIZATION, this.token))
//                .andExpect(status().isOk())
//                .andDo(print());
//    }
//
//    @Test
//    @Order(4)
//    @DisplayName("게시물 상세조회")
//    void test4() throws Exception {
//        Long postId = 1L;
//
//        mockMvc.perform(get("/board/freeBoard/{postId}/detail", postId)
//                        .header(HttpHeaders.AUTHORIZATION, this.token)
//                )
//                .andExpect(status().isOk())
//                .andDo(print());
//    }
//
//    @Test
//    @Order(5)
//    @DisplayName("게시물 수정")
//    void test5() throws Exception {
//
//        String request = objectMapper.writeValueAsString(post1);
//
//        Long postId = 1L;
//
//        MockMultipartHttpServletRequestBuilder builder =
//                MockMvcRequestBuilders.multipart("/board/freeBoard/{postId}", postId);
//
//        builder.with(request1 -> {
//            request1.setMethod("PUT");
//            return request1;
//        });
//
//        MockMultipartFile multipartFile1 = new MockMultipartFile("requestDto", "", "application/json", request.getBytes());
//        mockMvc.perform(builder.file(multipartFile1)
//                        .header("Authorization", this.token))
//                .andExpect(status().isOk())
//                .andDo(print());
//
//    }
//
//
//    @Test
//    @Order(6)
//    @DisplayName("전체 게시글 조회")
//    void test6() throws Exception {
//
//        String skiResort = "HighOne";
//
//        mockMvc.perform(get("/board/freeBoard/{skiResort}", skiResort)
//                        .param("page", "1")
//                        .param("size", "10")
//                        .header("Authorization", this.token))
//                .andExpect(status().isOk())
//                .andDo(print());
//    }
//
//
//    @Test
//    @Order(7)
//    @DisplayName("핫 게시물 조회")
//    void test7() throws Exception {
//
//        mockMvc.perform(get("/main"))
//                .andExpect(status().isOk())
//                .andDo(print());
//
//
//    }
//
//    @Test
//    @Order(8)
//    @DisplayName("게시물 좋아요")
//    void test8() throws Exception {
//        Long postId = 1L;
//
//        mockMvc.perform(post("/board/freeBoard/{postId}/likes", postId)
//                        .header("Authorization", this.token))
//                .andExpect(status().isOk())
//                .andDo(print());
//    }
//
//    @Test
//    @Order(9)
//    @DisplayName("댓글 작성")
//    void test9() throws Exception {
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
//    @Order(10)
//    @DisplayName("댓글 수정")
//    void test10() throws Exception {
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
//    @Order(11)
//    @DisplayName("댓글 삭제")
//    void test11() throws Exception {
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
//    @Test
//    @Order(12)
//    @DisplayName("게시글 삭제")
//    void test12() throws Exception {
//        Long postId = 1L;
//
//        mockMvc.perform(delete("/board/freeBoard/{postId}", postId)
//                        .header("Authorization", this.token))
//                .andExpect(status().isOk())
//                .andDo(print());
//    }
//
//
//    private SignupRequestDto user1 = SignupRequestDto.builder()
//            .username("beomin121")
//            .nickname("버민1")
//            .password("asdf12!!")
//            .phoneNum("01012341234")
//            .build();
//
//    private UserControllerTest.TestLoginDto user1Login = UserControllerTest.TestLoginDto.builder()
//            .username("beomin121")
//            .password("asdf12!!")
//            .build();
//
//    private FreePostRequestDto post1 = FreePostRequestDto.builder()
//            .title("버민")
//            .content("내용")
//            .build();
//
//    private CommentRequestDto comment1 = CommentRequestDto.builder()
//            .content("comment1")
//            .build();
//
//}