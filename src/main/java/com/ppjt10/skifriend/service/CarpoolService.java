package com.ppjt10.skifriend.service;

import com.ppjt10.skifriend.dto.carpooldto.CarpoolBannerDto;
import com.ppjt10.skifriend.dto.carpooldto.CarpoolFilterRequestDto;
import com.ppjt10.skifriend.dto.carpooldto.CarpoolRequestDto;
import com.ppjt10.skifriend.dto.carpooldto.CarpoolResponseDto;
import com.ppjt10.skifriend.entity.Carpool;
import com.ppjt10.skifriend.entity.SkiResort;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.repository.CarpoolRepository;
import com.ppjt10.skifriend.repository.SkiResortRepository;
import com.ppjt10.skifriend.time.TimeConversion;
import com.ppjt10.skifriend.validator.CarpoolType;
import com.ppjt10.skifriend.validator.DateValidator;
import com.ppjt10.skifriend.validator.TimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarpoolService {

    private final CarpoolRepository carpoolRepository;
    private final SkiResortRepository skiResortRepository;

    //카풀 전체 조회
    @Transactional
    public List<CarpoolResponseDto> getCarpools(String skiResortName) {
        List<CarpoolResponseDto> carpoolResponseDtoList = new ArrayList<>();
        SkiResort skiResort = skiResortRepository.findByResortName(skiResortName).orElseThrow(
                () -> new IllegalArgumentException("해당 이름의 스키장이 존재하지 않습니다.")
        );
        //해당 스키장의 카풀 정보 리스트 가져오기
        List<Carpool> carpoolList = carpoolRepository.findAllBySkiResortOrderByCreateAtDesc(skiResort);

        //Carpool 리스트
        for (Carpool carpool : carpoolList) {
            carpoolResponseDtoList.add(generateCarpoolResponseDto(carpool));
        }

        return carpoolResponseDtoList;
    }

    //카풀 게시글 작성
    @Transactional
    public CarpoolResponseDto createCarpool(String resortName, CarpoolRequestDto requestDto, User user) {
        CarpoolType.findByCarpoolType(requestDto.getCarpoolType());
        DateValidator.validateDateForm(requestDto.getDate());
        TimeValidator.validateTimeForm(requestDto.getTime());

        SkiResort skiResort = skiResortRepository.findByResortName(resortName).orElseThrow(
                () -> new IllegalArgumentException("해당 이름의 스키장이 존재하지 않습니다.")
        );

        Carpool carpool = new Carpool(user, requestDto, skiResort);
        carpoolRepository.save(carpool);

        return generateCarpoolResponseDto(carpool);
    }

    //카풀 게시글 수정
    @Transactional
    public CarpoolResponseDto updateCarpool(Long carpoolId, CarpoolRequestDto requestDto, User user) {
        CarpoolType.findByCarpoolType(requestDto.getCarpoolType());
        DateValidator.validateDateForm(requestDto.getDate());
        TimeValidator.validateTimeForm(requestDto.getTime());

        Carpool carpool = carpoolRepository.findById(carpoolId).orElseThrow(
                () -> new IllegalArgumentException("해당 아이디의 카풀이 존재하지 않습니다.")
        );

        if (!carpool.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("작성자만 상태를 변경할 수 있습니다.");
        }

        carpool.update(requestDto);
        return generateCarpoolResponseDto(carpool);
    }

    //카풀 게시글 삭제
    @Transactional
    public void deleteCarpool(Long carpoolId, User user) {
        Carpool carpool = carpoolRepository.findById(carpoolId).orElseThrow(
                () -> new IllegalArgumentException("해당 아이디의 카풀이 존재하지 않습니다.")
        );

        if (!carpool.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("작성자만 상태를 변경할 수 있습니다.");
        }

        carpoolRepository.deleteById(carpoolId);
    }

    //카풀 카테고리 분류
    @Transactional
    public List<CarpoolResponseDto> sortCarpools(String resortName, CarpoolFilterRequestDto requestDto) {
        List<Carpool> sortedCategories;
        if (!requestDto.getStatus()) {
            sortedCategories =
                    carpoolRepository.findAllBySkiResortResortNameAndCarpoolTypeContainingAndStartLocationContainingAndEndLocationContainingAndDateContainingAndMemberNumIsContainingOrderByCreateAtDesc
                            (
                                    resortName,
                                    requestDto.getCarpoolType(), //빈 값은 "" 으로
                                    requestDto.getStartLocation(), //빈 값은 "" 으로
                                    requestDto.getEndLocation(), //빈 값은 "" 으로
                                    requestDto.getDate(), //빈 값은 "" 으로
                                    requestDto.getMemberNum() // 빈 값은 숫자 맥스로
                            );
        } else {
            sortedCategories =
                    carpoolRepository.findAllBySkiResortResortNameAndCarpoolTypeContainingAndStartLocationContainingAndEndLocationContainingAndDateContainingAndMemberNumIsContainingAndStatusOrderByCreateAtDesc
                            (
                                    resortName,
                                    requestDto.getCarpoolType(), //빈 값은 "" 으로
                                    requestDto.getStartLocation(), //빈 값은 "" 으로
                                    requestDto.getEndLocation(), //빈 값은 "" 으로
                                    requestDto.getDate(), //빈 값은 "" 으로
                                    requestDto.getMemberNum(), // 빈 값은 숫자 맥스로
                                    requestDto.getStatus()
                            );
        }

        List<CarpoolResponseDto> carpoolResponseDtoList = new ArrayList<>();
        for(Carpool carpool : sortedCategories) {
            carpoolResponseDtoList.add(generateCarpoolResponseDto(carpool));
        }
        return carpoolResponseDtoList;
    }

    //카풀 상태 변경
    @Transactional
    public void changeStatus(Long carpoolId, User user) {
        Carpool carpool = carpoolRepository.findById(carpoolId).orElseThrow(
                () -> new IllegalArgumentException("해당 아이디의 카풀이 존재하지 않습니다.")
        );

        if (!carpool.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("작성자만 상태를 변경할 수 있습니다.");
        }
        carpool.setStatus();
    }

    //배너정보 내려주기
    @Transactional
    public CarpoolBannerDto getBanner(String resortName) {
        SkiResort skiResort = skiResortRepository.findByResortName(resortName).orElseThrow(
                () -> new IllegalArgumentException("해당 리조트가 존재하지 않습니다.")
        );
        return CarpoolBannerDto.builder()
                .resortImg(skiResort.getResortImg())
                .build();
    }

    //CarpoolResponseDto 생성
    private CarpoolResponseDto generateCarpoolResponseDto(Carpool carpool) {
        return CarpoolResponseDto.builder()
                .postId(carpool.getId())
                .userId(carpool.getUser().getId())
                .nickname(carpool.getUser().getNickname())
                .createdAt(TimeConversion.timePostConversion(carpool.getCreateAt()))
                .carpoolType(carpool.getCarpoolType())
                .title(carpool.getTitle())
                .startLocation(carpool.getStartLocation())
                .endLocation(carpool.getEndLocation())
                .skiResort(carpool.getSkiResort().getResortName())
                .date(carpool.getDate())
                .time(carpool.getTime())
                .price(carpool.getPrice())
                .memberNum(carpool.getMemberNum())
                .notice(carpool.getNotice())
                .status(carpool.isStatus())
                .build();
    }
}

