package com.ppjt10.skifriend.service;

import com.ppjt10.skifriend.dto.CarpoolDto;
import com.ppjt10.skifriend.entity.Carpool;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.repository.CarpoolRepository;
import com.ppjt10.skifriend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class CarpoolService {

    private final CarpoolRepository carpoolRepository;

    @Transactional
    public void createCarpool(String skiResort, CarpoolDto.RequestDto requestDto, User user) {
        Carpool carpool = new Carpool(user, requestDto, skiResort);
        carpoolRepository.save(carpool);
    }

    @Transactional
    public void updateCarpool(Long carpoolId, CarpoolDto.RequestDto requestDto, Long userid) {
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
}
