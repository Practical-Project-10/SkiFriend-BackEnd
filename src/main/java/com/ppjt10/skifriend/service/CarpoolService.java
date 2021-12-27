package com.ppjt10.skifriend.service;

import com.ppjt10.skifriend.dto.CarpoolDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarpoolService {

    private final CarpoolRepository carpoolRepository;
    private final SkiResortRepository skiResortRepository;

    //카풀 게시글 작성
    @Transactional
    public void createCarpool(String skiResortName, CarpoolDto.RequestDto requestDto, User user) {
        CarpoolType.findByCarpoolType(requestDto.getCarpoolType());

        SkiResort skiResort = skiResortRepository.findByResortName(skiResortName).orElseThrow(
                () -> new IllegalArgumentException("해당 이름의 스키장이 존재하지 않습니다.")
        );

        DateValidator.validateDateForm(requestDto.getDate());
        TimeValidator.validateTimeForm(requestDto.getTime());
        Carpool carpool = new Carpool(user, requestDto, skiResort);

        carpoolRepository.save(carpool);
    }

    //카풀 게시글 수정
    @Transactional
    public void updateCarpool(Long carpoolId, CarpoolDto.RequestDto requestDto, Long userid) {
        CarpoolType.findByCarpoolType(requestDto.getCarpoolType());
        DateValidator.validateDateForm(requestDto.getDate());
        TimeValidator.validateTimeForm(requestDto.getTime());

        Carpool carpool = carpoolRepository.findById(carpoolId).orElseThrow(
                () -> new IllegalArgumentException("해당 아이디의 카풀이 존재하지 않습니다.")
        );

        if(carpool.getUser().getId() != userid){
            throw new IllegalArgumentException("작성자만 상태를 변경할 수 있습니다.");
        }

        carpool.update(requestDto);
    }

    //카풀 게시글 삭제
    @Transactional
    public void deleteCarpool(Long carpoolId, Long userid) {
        Carpool carpool = carpoolRepository.findById(carpoolId).orElseThrow(
                () -> new IllegalArgumentException("해당 아이디의 카풀이 존재하지 않습니다.")
        );

        if(carpool.getUser().getId() != userid){
            throw new IllegalArgumentException("작성자만 상태를 변경할 수 있습니다.");
        }

        carpoolRepository.deleteById(carpoolId);
    }

    //region 카풀 카테고리 분류
    @Transactional
    public ResponseEntity<Page<CarpoolDto.ResponseDto>> sortCarpools(
            CarpoolDto.CategoryRequestDto categoryRequestDto,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Carpool> sortedCategories =
                carpoolRepository.findAllByCarpoolTypeContainingAndStartLocationContainingAndEndLocationContainingAndDateAndMemberNumIsLessThanEqual
        (
                categoryRequestDto.getCarpoolType(), //빈 값은 "" 으로
                categoryRequestDto.getStartLocation(), //빈 값은 "" 으로
                categoryRequestDto.getEndLocation(), //빈 값은 "" 으로
                categoryRequestDto.getDate(), //빈 값은 "" 으로
                categoryRequestDto.getMaxMemberNum(), // 빈 값은 숫자 맥스로
                pageable
        );
        List<CarpoolDto.ResponseDto> categoryResponseDto = sortedCategories.stream()
                .map(e->toCategoryResponseDto(e))
                .collect(Collectors.toList());
        Page<CarpoolDto.ResponseDto> categoryResponseDtoPage = new PageImpl<>(categoryResponseDto, pageable, sortedCategories.getTotalElements());
        return ResponseEntity.ok().body(categoryResponseDtoPage);
    }

    private CarpoolDto.ResponseDto toCategoryResponseDto(Carpool carpool) {
        return CarpoolDto.ResponseDto.builder()
                .postId(carpool.getId())
                .userId(carpool.getUser().getId())
                .nickname(carpool.getUser().getNickname())
                .createdAt(TimeConversion.timeConversion(carpool.getCreateAt()))
                .carpoolType(carpool.getCarpoolType())
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

    //카풀 상태 변경
    @Transactional
    public void changeStatus(Long carpoolId, Long userid) {
        Carpool carpool = carpoolRepository.findById(carpoolId).orElseThrow(
                () -> new IllegalArgumentException("해당 아이디의 카풀이 존재하지 않습니다.")
        );

        if(carpool.getUser().getId() != userid){
            throw new IllegalArgumentException("작성자만 상태를 변경할 수 있습니다.");
        }

        carpool.changeStatus();
    }


    //카풀 전체 조회
    public List<CarpoolDto.ResponseDto> getCarpools(String skiResort, int page, int size) {
            List<CarpoolDto.ResponseDto> carpoolResponseDtoList = new ArrayList<>();
            //해당 스키장의 카풀 정보 리스트 가져오기
            Page<Carpool> carpoolPage = carpoolRepository.findAllBySkiResort(skiResort, PageRequest.of(page, size));

            //Carpool 리스트
            if (carpoolPage.hasContent()) {
                for (Carpool carpool : carpoolPage.toList()) {
                    carpoolResponseDtoList.add(generateCarpoolResponseDto(carpool));
                }
            }
            return carpoolResponseDtoList;
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

