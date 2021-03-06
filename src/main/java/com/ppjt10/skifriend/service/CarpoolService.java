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
import com.ppjt10.skifriend.repository.UserRepository;
import com.ppjt10.skifriend.time.TimeConversion;
import com.ppjt10.skifriend.validator.CarpoolType;
import com.ppjt10.skifriend.validator.DateValidator;
import com.ppjt10.skifriend.validator.TimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarpoolService {

    private final CarpoolRepository carpoolRepository;
    private final SkiResortRepository skiResortRepository;
    private final UserRepository userRepository;

    //카풀 전체 조회
    @Transactional
    public List<CarpoolResponseDto> getCarpools(String skiResortName, int page, int size) {
        SkiResort skiResort = skiResortRepository.findByResortName(skiResortName).orElseThrow(
                () -> new IllegalArgumentException("해당 이름의 스키장이 존재하지 않습니다.")
        );

        //해당 스키장의 카풀 정보 리스트 가져오기
        Pageable pageable = PageRequest.of(page, size);
        Page<Carpool> carpoolList = carpoolRepository.findAllBySkiResortOrderByCreateAtDesc(skiResort, pageable);

        //Carpool 리스트
        List<CarpoolResponseDto> carpoolResponseDtoList = new ArrayList<>();
        for (Carpool carpool : carpoolList) {
            carpoolResponseDtoList.add(generateCarpoolResponseDto(carpool));
        }

        return carpoolResponseDtoList;
    }

    //카풀 게시글 작성
    @Transactional
    public CarpoolResponseDto createCarpool(String resortName, CarpoolRequestDto requestDto, User user) {
//        if (user.getAgeRange() == null || user.getGender() == null) {
//            throw new IllegalArgumentException("추가 동의 항목이 필요합니다.");
//        }

        if (user.getPhoneNum() == null) {
            throw new IllegalArgumentException("전화번호 인증이 필요한 서비스입니다.");
        }

        SkiResort skiResort = skiResortRepository.findByResortName(resortName).orElseThrow(
                () -> new IllegalArgumentException("해당 이름의 스키장이 존재하지 않습니다.")
        );

        if (requestDto.getStartLocation().equals(skiResort.getResortName())) {
            List<String> endLocations = Arrays.asList(requestDto.getEndLocation().split(" "));
            if (endLocations.get(1).equals("전체")) {
                requestDto.setEndLocation(endLocations.get(0));
            }
        } else {
            List<String> startLocations = Arrays.asList(requestDto.getStartLocation().split(" "));
            if (startLocations.get(1).equals("전체")) {
                requestDto.setStartLocation(startLocations.get(0));
            }
        }

        CarpoolType.findByCarpoolType(requestDto.getCarpoolType());
        DateValidator.validateDateForm(requestDto.getDate());
        TimeValidator.validateTimeForm(requestDto.getTime());

        Carpool carpool = new Carpool(user.getId(), requestDto, skiResort);
        carpoolRepository.save(carpool);

        return generateCarpoolResponseDto(carpool);
    }

    //카풀 게시글 수정
    @Transactional
    public CarpoolResponseDto updateCarpool(Long carpoolId, CarpoolRequestDto requestDto, User user) {
        Carpool carpool = carpoolRepository.findById(carpoolId).orElseThrow(
                () -> new IllegalArgumentException("해당 아이디의 카풀이 존재하지 않습니다.")
        );

        if (!carpool.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("작성자만 상태를 변경할 수 있습니다.");
        }

        CarpoolType.findByCarpoolType(requestDto.getCarpoolType());
        DateValidator.validateDateForm(requestDto.getDate());
        TimeValidator.validateTimeForm(requestDto.getTime());

        carpool.update(requestDto);
        return generateCarpoolResponseDto(carpool);
    }

    //카풀 게시글 삭제
    @Transactional
    public void deleteCarpool(Long carpoolId, User user) {
        Carpool carpool = carpoolRepository.findById(carpoolId).orElseThrow(
                () -> new IllegalArgumentException("해당 아이디의 카풀이 존재하지 않습니다.")
        );

        if (!carpool.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("작성자만 상태를 변경할 수 있습니다.");
        }

        carpoolRepository.deleteById(carpoolId);
    }

    //카풀 카테고리 분류
    @Transactional
    public List<CarpoolResponseDto> sortCarpools(String resortName, CarpoolFilterRequestDto requestDto) {
        SkiResort skiResort = skiResortRepository.findByResortName(resortName).orElseThrow(
                () -> new IllegalArgumentException("해당 이름의 스키장이 존재하지 않습니다.")
        );

        try {
            List<String> endLocations = Arrays.asList(requestDto.getEndLocation().split(" "));
            if (endLocations.get(1).equals("전체")) {
                requestDto.setEndLocation(endLocations.get(0));
            }
        } catch (Exception ignored) {
        }

        try {
            List<String> startLocations = Arrays.asList(requestDto.getStartLocation().split(" "));
            if (startLocations.get(1).equals("전체")) {
                requestDto.setStartLocation(startLocations.get(0));
            }
        } catch (Exception ignored) {
        }

        List<Carpool> sortedCategories;
        if (!requestDto.getStatus()) {
            sortedCategories =
                    carpoolRepository.findAllBySkiResortResortNameAndCarpoolTypeContainingAndStartLocationContainingAndEndLocationContainingAndDateContainingAndMemberNumIsContainingOrderByCreateAtDesc
                            (
                                    skiResort.getResortName(),
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
                                    skiResort.getResortName(),
                                    requestDto.getCarpoolType(), //빈 값은 "" 으로
                                    requestDto.getStartLocation(), //빈 값은 "" 으로
                                    requestDto.getEndLocation(), //빈 값은 "" 으로
                                    requestDto.getDate(), //빈 값은 "" 으로
                                    requestDto.getMemberNum(), // 빈 값은 숫자 맥스로
                                    requestDto.getStatus()
                            );
        }

        List<CarpoolResponseDto> carpoolResponseDtoList = new ArrayList<>();
        for (Carpool carpool : sortedCategories) {
            carpoolResponseDtoList.add(generateCarpoolResponseDto(carpool));
        }
        return carpoolResponseDtoList;
    }

    //카풀 상태 변경
    @Transactional
    public CarpoolResponseDto changeStatus(Long carpoolId, User user) {
        Carpool carpool = carpoolRepository.findById(carpoolId).orElseThrow(
                () -> new IllegalArgumentException("해당 아이디의 카풀이 존재하지 않습니다.")
        );

        if (!carpool.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("작성자만 상태를 변경할 수 있습니다.");
        }

        if (!carpool.isStatus()) {
            LocalDateTime currentTime = LocalDateTime.now();
            String textCarpoolTime = carpool.getDate() + " " + carpool.getTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime carpoolTime = LocalDateTime.parse(textCarpoolTime, formatter);
            Long timeDiff = Duration.between(carpoolTime, currentTime).getSeconds();
            if (timeDiff > 0) {
                throw new IllegalArgumentException("카풀 모집 마감시간이 지났습니다");
            }
            carpool.setStatus(true);
        } else {
            carpool.setStatus(false);
        }

        return generateCarpoolResponseDto(carpool);
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
        Optional<User> user = userRepository.findById(carpool.getUserId());

        String nickname;
        if(user.isPresent()){
            nickname = user.get().getNickname();
        } else{
            nickname = "알 수 없음";
        }

        return CarpoolResponseDto.builder()
                .postId(carpool.getId())
                .userId(carpool.getUserId())
                .nickname(nickname)
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

