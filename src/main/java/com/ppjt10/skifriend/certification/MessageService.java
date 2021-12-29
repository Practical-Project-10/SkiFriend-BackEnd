package com.ppjt10.skifriend.certification;

import com.ppjt10.skifriend.dto.SignupDto;
import com.ppjt10.skifriend.dto.UserDto;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class MessageService {
    private final SmsCertification smsCertification;
    private final UserRepository userRepository;

    @Value("${coolsms.apikey}")
    private String apiKey;

    @Value("${coolsms.apisecret}")
    private String apiSecret;

    @Value("${coolsms.fromnumber}")
    private String fromNumber;

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

    private HashMap<String, String> makeParams(String to, String randomNum) {
        HashMap<String, String> params = new HashMap<>();
        params.put("from", fromNumber);
        params.put("type", "SMS");
        params.put("app_version", "test app 1.2");
        params.put("to", to);
        params.put("text", randomNum);
        return params;
    }

    // 인증번호 전송하기
    public String sendSMS(String phoneNumber) {

        // 중복 검사
        // checkDuplicatoin(phoneNumber);

        // 랜덤한 인증 번호 생성
        String randomNum = createRandomNumber();
        System.out.println(randomNum);

        // 발신 정보 설정
//        HashMap<String, String> params = makeParams(phoneNumber, randomNum);
//
//        try {
//            JSONObject obj = (JSONObject) coolsms.send(params);
//            System.out.println(obj.toString());
//        } catch (CoolsmsException e) {
//            System.out.println(e.getMessage());
//            System.out.println(e.getCode());
//        }

        // DB에 발송한 인증번호 저장
        smsCertification.createSmsCertification(phoneNumber, randomNum);

        return "문자 전송이 완료되었습니다.";
    }

    public void createChatRoomAlert(String phoneNumber, String msg) {
        // 발신 정보 설정
//        HashMap<String, String> params = makeParams(phoneNumber, msg);
//
//        try {
//            JSONObject obj = (JSONObject) coolsms.send(params);
//            System.out.println(obj.toString());
//        } catch (CoolsmsException e) {
//            System.out.println(e.getMessage());
//            System.out.println(e.getCode());
//        }
        System.out.println(phoneNumber + "에게 채팅방 생성 알림 전송 : " + msg);
    }

    private void checkDuplicatoin(String phoneNum) {
        Optional<User> isPhoneNum = userRepository.findByPhoneNum(phoneNum);
        if (isPhoneNum.isPresent()) {
            throw new IllegalArgumentException("이미 가입된 전화번호입니다.");
        }
    }

    // 인증 번호 검증
    public String verifySms(SignupDto.SmsCertificationDto requestDto) {
        if (isVerify(requestDto)) {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }
        smsCertification.deleteSmsCertification(requestDto.getPhoneNumber());

        return "인증 완료되었습니다.";
    }

    private boolean isVerify(SignupDto.SmsCertificationDto requestDto) {
        return !(smsCertification.hasKey(requestDto.getPhoneNumber()) &&
                smsCertification.getSmsCertification(requestDto.getPhoneNumber())
                        .equals(requestDto.getRandomNumber()));
    }
}
