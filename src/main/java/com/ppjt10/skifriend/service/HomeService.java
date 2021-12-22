package com.ppjt10.skifriend.service;

import com.ppjt10.skifriend.dto.BoardListResponseDto;
import com.ppjt10.skifriend.dto.CarpoolDto;
import com.ppjt10.skifriend.dto.FreePostDto;
import com.ppjt10.skifriend.entity.Carpool;
import com.ppjt10.skifriend.entity.FreePost;
import com.ppjt10.skifriend.repository.CarpoolRepository;
import com.ppjt10.skifriend.repository.FreePostRepository;
import com.ppjt10.skifriend.time.TimeConversion;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {
    private final CarpoolRepository carpoolRepository;
    private final FreePostRepository freePostRepository;

    public BoardListResponseDto getBoards(String skiResort,
                                                int page,
                                                int size
    ) {
        List<CarpoolDto.ResponseDto> carpoolResponseDtoList = new ArrayList<>();
        List<FreePostDto.AllResponseDto> freePostResponseDtoList = new ArrayList<>();
        //해당 스키장의 카풀 정보 리스트 가져오기
        Page<Carpool> carpoolPage = carpoolRepository.findAllBySkiResort(skiResort, PageRequest.of(page, size));
        Page<FreePost> freePostPage = freePostRepository.findAllBySkiResort(skiResort, PageRequest.of(page, size));
        //Carpool 리스트
        if (carpoolPage.hasContent()) {
            for (Carpool carpool : carpoolPage.toList()) {
                carpoolResponseDtoList.add(generateCarpoolResponseDto(carpool));
            }
        }
        if (freePostPage.hasContent()){
            for (FreePost freePost : freePostPage.toList()){
                freePostResponseDtoList.add(generateFreePostResponseDto(freePost));
            }
        }
        return BoardListResponseDto.builder()
                .carpoolResponseDto(carpoolResponseDtoList)
                .freePostAllResponseDto(freePostResponseDtoList)
                .build();
    }

    private CarpoolDto.ResponseDto generateCarpoolResponseDto(Carpool carpool) {
        return CarpoolDto.ResponseDto.builder()
                .postId(carpool.getId())
                .userId(carpool.getUser().getId())
                .nickname(carpool.getUser().getNickname())
                .createdAt(TimeConversion.timeConversion(carpool.getCreateAt()))
                .carpoolType(carpool.getCarpoolType())
                .startLocation(carpool.getStartLocation())
                .endLocation(carpool.getEndLocation())
                .skiResort(carpool.getSkiResort())
                .date(carpool.getDate())
                .time(carpool.getTime())
                .price(carpool.getPrice())
                .memberNum(carpool.getMemberNum())
                .notice(carpool.getNotice())
                .build();
    }

    private FreePostDto.AllResponseDto generateFreePostResponseDto(FreePost freePost){
        return FreePostDto.AllResponseDto.builder()
                .postId(freePost.getId())
                .userId(freePost.getUser().getId())
                .nickname(freePost.getUser().getNickname())
                .createdAt(TimeConversion.timeConversion(freePost.getCreateAt()))
                .title(freePost.getTitle())
                .likeCnt(freePost.getLikeCnt())
                .commentCnt(freePost.getCommentCnt())
                .build();
    }
}
