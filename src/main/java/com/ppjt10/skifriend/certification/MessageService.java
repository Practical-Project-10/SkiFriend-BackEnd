package com.ppjt10.skifriend.certification;

import com.ppjt10.skifriend.dto.signupdto.SignupPhoneNumDto;
import com.ppjt10.skifriend.dto.signupdto.SignupSmsCertificationDto;
import com.ppjt10.skifriend.service.SignupService;
import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class MessageService {
    private final SmsRedisRepository smsRedisRepository;
    private final SignupService signupService;

    @Value("${coolsms.apikey}")
    private String apiKey;

    @Value("${coolsms.apisecret}")
    private String apiSecret;

    @Value("${coolsms.fromnumber}")
    private String fromPhoneNum;

    private final Message coolsms = new Message(apiKey, apiSecret);

    private String createRandomNumber() {
        Random rand = new Random();

        String randomNum = "";
        for (int i = 0; i < 4; i++) {
            String random = Integer.toString(rand.nextInt(10));
            randomNum += random;
        }

        return randomNum;
    }

    // 인증번호 전송하기
    public String getSmsRedisRepository(SignupPhoneNumDto requestDto) {

        // 중복 검사
//        signupService.checkPhoneNum(requestDto.getPhoneNumber());

        // 랜덤한 인증 번호 생성
        String randomNum = createRandomNumber();
        System.out.println(randomNum);

        // 발신 정보 설정
        HashMap<String, String> params = makeParams(requestDto.getPhoneNumber(), randomNum);

        try {
            coolsms.send(params);
        } catch (CoolsmsException e) {
            System.out.println(e.getMessage());
        }

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
//        HashMap<String, String> params = makeParams(phoneNumber, msg);
//
//        try {
//            coolsms.send(params);
//        } catch (CoolsmsException e) {
//            System.out.println(e.getMessage());
//        }

        System.out.println(phoneNumber + "에게 채팅방 생성 알림 전송 : " + msg);
    }

    private HashMap<String, String> makeParams(String toPhoneNum, String randomNum) {
        HashMap<String, String> params = new HashMap<>();
        params.put("from", fromPhoneNum);
        params.put("type", "SMS");
        params.put("app_version", "test app 1.2");
        params.put("to", toPhoneNum);
        params.put("text", randomNum);
        return params;
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
