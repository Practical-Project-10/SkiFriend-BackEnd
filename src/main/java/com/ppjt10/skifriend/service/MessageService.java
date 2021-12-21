package com.ppjt10.skifriend.service;

import java.util.HashMap;
import java.util.Random;

import org.json.simple.JSONObject;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    public String sendSMS(String phonNumber) {
        String api_key = "NCSUGJUECHMH2RNC";
        String api_secret = "4JRE9YGPVRNPYZ8C9A5IQGIDYSI2USTD";

        Message coolsms = new Message(api_key, api_secret);
        HashMap<String, String> params = new HashMap<String, String>();

        String randomNum = createRandomNumber();

        params.put("to", phonNumber);
        params.put("from", "07080651899");
        params.put("type", "SMS");
        params.put("text", randomNum);
        params.put("app_version", "test app 1.2");

        try {
            JSONObject obj = (JSONObject) coolsms.send(params);
            System.out.println(obj.toString());
        } catch (CoolsmsException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
        }

        return "문자 전송이 완료되었습니다.";
    }

    public String createRandomNumber() {
        Random rand = new Random();
        String randomNum = "";
        for (int i = 0; i < 4; i++) {
            String random = Integer.toString(rand.nextInt(10));
            randomNum += random;
        }

        return randomNum;
    }
}
