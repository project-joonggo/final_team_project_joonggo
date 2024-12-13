package com.project.joonggo.handler;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject; // JSONObject의 올바른 import 경로
import java.util.HashMap;

@Component
@Slf4j
public class PhoneAuthHandler {

        @Value("${sms.api-key}")
        private String SMS_API_KEY;

        @Value("${sms.api-secret}")
        private String SMS_API_SECRET;

    public void certifiedPhoneNumber(String userPhoneNumber, int randomNumber) {

        String api_key = SMS_API_KEY;
        String api_secret = SMS_API_SECRET;
        Message coolsms = new Message(api_key, api_secret);
        // 4 params(to, from, type, text) must be filled
        HashMap<String, String> params = new HashMap<>();
        // 수신전화번호
        params.put("to", userPhoneNumber);
        // 발신전화번호. 테스트시에는 발신,수신 둘다 본인 번호로 하면 됨
        params.put("from", "01095573830");
        params.put("type", "SMS");
        // 문자 내용 입력
        params.put("text", "SseulTem(쓸템) 인증번호는" + "[" + randomNumber + "]" + "입니다.");
        // application name and version
        params.put("app_version", "test app 1.2");
        try {
            JSONObject obj = (JSONObject) coolsms.send(params);
        } catch (CoolsmsException e) {
        }
    }
}
