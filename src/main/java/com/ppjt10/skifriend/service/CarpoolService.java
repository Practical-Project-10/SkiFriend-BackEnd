package com.ppjt10.skifriend.service;

import com.ppjt10.skifriend.dto.CarpoolDto;
import com.ppjt10.skifriend.entity.Carpool;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.repository.CarpoolRepository;
import com.ppjt10.skifriend.time.TimeConversion;
import com.ppjt10.skifriend.validator.CarpoolType;
import com.ppjt10.skifriend.validator.DateValidator;
import com.ppjt10.skifriend.validator.SkiResortType;
import com.ppjt10.skifriend.validator.TimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarpoolService {

    private final CarpoolRepository carpoolRepository;

    @Transactional
    public void createCarpool(String skiResort, CarpoolDto.RequestDto requestDto, User user) {
        CarpoolType.findByCarpoolType(requestDto.getCarpoolType());
        SkiResortType.findBySkiResortType(skiResort);
        DateValidator.validateDateForm(requestDto.getDate());
        TimeValidator.validateTimeForm(requestDto.getTime());
        Carpool carpool = new Carpool(user, requestDto, skiResort);
        carpoolRepository.save(carpool);
    }

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
    //endregion

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

    private CarpoolDto.ResponseDto toCategoryResponseDto(Carpool carpool) {
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
                .status(carpool.isStatus())
                .build();
    }


}
