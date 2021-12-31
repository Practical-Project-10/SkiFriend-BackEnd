package com.ppjt10.skifriend.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppjt10.skifriend.dto.FreePostDto;
import com.ppjt10.skifriend.repository.FreePostRepository;
import com.ppjt10.skifriend.repository.UserRepository;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
//@WebMvcTest(controllers = FreePostController.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WithMockUser
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FreePostControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    private final String title = "title1";
    private final String content = "content1";
    private final String postImg = "Image";
    private final String skiResortName = "HighOne";
    private String token;



    private  HttpHeaders headers;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Nested
    @DisplayName("게시글 작성")
    class writeFreePost {


        @Test
        @Order(1)

        @DisplayName("게시글 작성 성공")
//        @WithAuthUser(username = "beomin12")
        public void success() throws JsonProcessingException {

            //given
            FreePostDto.RequestDto requestDto = FreePostDto.RequestDto.builder()
                    .title(title)
                    .content(content)
                    .build();

            String requestBody = objectMapper.writeValueAsString(requestDto);

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            //when
            ResponseEntity<FreePostDto.ResponseDto> response = testRestTemplate.postForEntity(
                    "/board/" + skiResortName + "/freeBoard",
                    request,
                    FreePostDto.ResponseDto.class
            );

            //then
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }


//        @Test
//        @Order(2)
//        @DisplayName("게시글 작성 실패")
//        public void fail() {
//
//
//        }










    }


}
