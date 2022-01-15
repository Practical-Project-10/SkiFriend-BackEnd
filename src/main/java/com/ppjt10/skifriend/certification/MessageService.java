package com.ppjt10.skifriend.certification;

import com.ppjt10.skifriend.dto.signupdto.SignupPhoneNumDto;
import com.ppjt10.skifriend.dto.signupdto.SignupSmsCertificationDto;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.repository.UserRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class MessageService {
    private final SmsRedisRepository smsRedisRepository;
    private final UserRepository userRepository;

    private final String SKIFRIEND = "[스키프렌드] ";

    @Value("${twililo.apikey}")
    private String apiKey;

    @Value("${twililo.apisecret}")
    private String apiSecret;

    @Value("${twililo.fromphone}")
    private String fromPhoneNum;

    // 인증번호 생성하기
    public String getSmsRedisRepository(SignupPhoneNumDto requestDto, User user) {
        if(user.getPhoneNum() != null){
            throw new IllegalArgumentException("이미 전화번호 인증을 완료하셨습니다.");
        }

        User existedUser = userRepository.findByPhoneNum(requestDto.getPhoneNumber());
        if(existedUser != null){
            throw new IllegalArgumentException("이미 가입된 번호입니다.");
        }

        // 랜덤한 인증 번호 생성
        String randomNum = String.valueOf((int) (Math.random() * 9000) + 1000);
        System.out.println("인증번호" + randomNum);

        // 발신 정보 설정
        Twilio.init(apiKey, apiSecret);
        String toPhoneNum = "+" + 82 + requestDto.getPhoneNumber();

        Message message = Message.creator(
                new PhoneNumber(toPhoneNum),
                new PhoneNumber(fromPhoneNum),
                SKIFRIEND + randomNum).create();

        // DB에 발송한 인증번호 저장
        smsRedisRepository.createSmsCertification(requestDto.getPhoneNumber(), randomNum);
//        return randomNum;
        return "문자 전송이 완료되었습니다.";
    }

    // 인증 번호 검증
    @Transactional
    public String checkCertificationNum(SignupSmsCertificationDto requestDto, User user) {
        if (!isVerify(requestDto)) {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }

        if(user.getPhoneNum() != null){
            throw new IllegalArgumentException("이미 전화번호 인증을 완료하셨습니다.");
        }

        String phoneNum = requestDto.getPhoneNumber();

        // 인증 완료 시, Redis Repository에서 인증번호 삭제
        smsRedisRepository.deleteSmsCertification(phoneNum);

        // 유저 전화번호 업데이트
        User verifiedUser = userRepository.findById(user.getId()).orElseThrow(
                () -> new IllegalArgumentException("유저가 없어용")
        );
        verifiedUser.setPhoneNum(requestDto.getPhoneNumber());

        return "인증 완료되었습니다.";
    }

    public void createChatRoomAlert(String phoneNumber, String msg) {
        // 발신 정보 설정
        Twilio.init(apiKey, apiSecret);
        String toPhoneNum = "+" + 82 + phoneNumber;

//        Message message = Message.creator(
//                new PhoneNumber(toPhoneNum),
//                new PhoneNumber(fromPhoneNum),
//                SKIFRIEND + msg).create();

        System.out.println(phoneNumber + "에게 채팅방 생성 알림 전송 : " + msg);
    }

//    public void openPhoneNumAlert(String phoneNumber, String msg) {
//        // 발신 정보 설정
//        Twilio.init(apiKey, apiSecret);
//        String toPhoneNum = "+" + 82 + phoneNumber;
//
//        Message message = Message.creator(
//                new PhoneNumber(toPhoneNum),
//                new PhoneNumber(fromPhoneNum),
//                SKIFRIEND + msg).create();
//
//        System.out.println(phoneNumber + "에게 번호 공개 알림 전송 : " + msg);
//    }

    private boolean isVerify(SignupSmsCertificationDto requestDto) {
        // 해당 휴대폰 번호로 전송된 인증번호가 존재하면
        if (smsRedisRepository.hasKey(requestDto.getPhoneNumber())) {
            // Redis에 저장된 인증번호랑 클라이언트에게 받은 인증번호가 일치하면 true 반환, 일치하지 않으면 false 반환
            return smsRedisRepository.getSmsCertification(requestDto.getPhoneNumber()).equals(requestDto.getRandomNumber());
        }
        return false;
    }
}