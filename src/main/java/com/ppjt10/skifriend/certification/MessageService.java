package com.ppjt10.skifriend.certification;

import com.ppjt10.skifriend.dto.signupdto.SignupPhoneNumDto;
import com.ppjt10.skifriend.dto.signupdto.SignupSmsCertificationDto;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MessageService {
    private final SmsRedisRepository smsRedisRepository;

    private final String SKIFRIEND = "[스키프렌드]";

    @Value("${twililo.apikey}")
    private String apiKey;

    @Value("${twililo.apisecret}")
    private String apiSecret;

    @Value("${twililo.fromphone}")
    private String fromPhoneNum;

    // 인증번호 전송하기
    public String getSmsRedisRepository(SignupPhoneNumDto requestDto) {

        // 랜덤한 인증 번호 생성
        String randomNum = String.valueOf((int) (Math.random() * 9000) + 1000);

        // 발신 정보 설정
        Twilio.init(apiKey, apiSecret);
        String toPhoneNum = "+" + 82 + requestDto.getPhoneNumber();

        Message message = Message.creator(
                new PhoneNumber(toPhoneNum),
                new PhoneNumber(fromPhoneNum),
                SKIFRIEND + randomNum).create();

        // DB에 발송한 인증번호 저장
        smsRedisRepository.createSmsCertification(requestDto.getPhoneNumber(), randomNum);

        return "문자 전송이 완료되었습니다. 인증번호는 " + randomNum + " 입니다.";
    }

    // 인증 번호 검증
    public String checkCertificationNum(SignupSmsCertificationDto requestDto) {
        if (!isVerify(requestDto)) {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }

        // 인증 완료 시, Redis Repository에서 인증번호 삭제
        smsRedisRepository.deleteSmsCertification(requestDto.getPhoneNumber());

        return "인증 완료되었습니다.";
    }

    public void createChatRoomAlert(String phoneNumber, String msg) {
        // 발신 정보 설정
        Twilio.init(apiKey, apiSecret);
        String toPhoneNum = "+" + 82 + phoneNumber;

        Message message = Message.creator(
                new PhoneNumber(toPhoneNum),
                new PhoneNumber(fromPhoneNum),
                SKIFRIEND + msg).create();

        System.out.println(phoneNumber + "에게 채팅방 생성 알림 전송 : " + msg);
    }

    private boolean isVerify(SignupSmsCertificationDto requestDto) {
        // 해당 휴대폰 번호로 전송된 인증번호가 존재하면
        if (smsRedisRepository.hasKey(requestDto.getPhoneNumber())) {
            // Redis에 저장된 인증번호랑 클라이언트에게 받은 인증번호가 일치하면 true 반환, 일치하지 않으면 false 반환
            return smsRedisRepository.getSmsCertification(requestDto.getPhoneNumber()).equals(requestDto.getRandomNumber());
        }
        return false;
    }
}