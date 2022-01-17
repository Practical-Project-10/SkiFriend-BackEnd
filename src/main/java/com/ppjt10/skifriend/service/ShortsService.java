package com.ppjt10.skifriend.service;

import com.ppjt10.skifriend.config.S3Uploader;
import com.ppjt10.skifriend.dto.ShortsResponseDto;
import com.ppjt10.skifriend.dto.freepostdto.FreePostRequestDto;
import com.ppjt10.skifriend.dto.freepostdto.FreePostResponseDto;
import com.ppjt10.skifriend.entity.FreePost;
import com.ppjt10.skifriend.entity.Shorts;
import com.ppjt10.skifriend.entity.SkiResort;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.repository.FreePostRepository;
import com.ppjt10.skifriend.repository.ShortsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShortsService {
//    private final ShortsRepository shortsRepository;
//    private final S3Uploader s3Uploader;
//    private final String imageDirName = "freepost";
//
//    @Transactional
//    public ShortsResponseDto getShorts() {
//    }
//
//    //Shorts 작성
//    @Transactional
//    public ShortsResponseDto createShorts(MultipartFile videoPath,
//                                          String title,
//                                          User user
//    ) {
//        Shorts shorts = new Shorts(user.getId(), title);
//        shortsRepository.save(shorts);
//
//
//        return
//    }
}
