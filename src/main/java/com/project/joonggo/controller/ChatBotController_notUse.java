package com.project.joonggo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

@Slf4j
@Controller
@RequiredArgsConstructor            //
@RequestMapping("/chat/*")          // 안쓰면 template/chat 폴더 내 파일 불러오기 요청 안됨
public class ChatBotController_notUse {

    //  chatbot api
    //  APIGW Invokeurl
    //  https://7tk1vpas45.apigw.ntruss.com/custom/v1/16386/18f423687ad5e19fcf88aead17118568d0de03ad02b17caf35ed69ca6a6ad9eb

    //  Secret Key
    //  TVFYT1l0VXZIQkFERnFnaGZTaERJblBQdWxUTWVyR1Q=

    private static String secretkey = "TVFYT1l0VXZIQkFERnFnaGZTaERJblBQdWxUTWVyR1Q=";
    private static String apiUrl = "https://7tk1vpas45.apigw.ntruss.com/custom/v1/16386/18f423687ad5e19fcf88aead17118568d0de03ad02b17caf35ed69ca6a6ad9eb/";

    // bubbles => content, description => details

    @MessageMapping("/sendMessage")
    @SendTo("/topic/public")
    public String sendMessage(@Payload String chatMessage) throws IOException {
        URL url = new URL(apiUrl);

        String message = getReqMessage(chatMessage);
        String encodeBase64String = makeSignature(message, secretkey);

        // api서버 접속 (서버 -> 서버 통신)
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json;UTF-8");
        conn.setRequestProperty("X-NCP-CHATBOT_SIGNATURE", encodeBase64String);

        conn.setDoOutput(true);
        DataOutputStream dtOutStr = new DataOutputStream(conn.getOutputStream());

        dtOutStr.write(message.getBytes("UTF-8"));
        dtOutStr.flush();
        dtOutStr.close();
        int responseCode = conn.getResponseCode();

        BufferedReader br;

        // code 200 : 정상호출
        // 정상호출되면 while 에서 null 여부 체크 후 jsonString 에 값 선언.
        if(responseCode == 200) {
            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(
                                            conn.getInputStream(), "UTF-8"));
            String decodedString;
            String jsonString = "";
            while((decodedString = in.readLine()) != null ) {
                jsonString = decodedString;
            }

            // 받아온 값 세팅
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject json = (JSONObject)jsonParser.parse(jsonString);
                JSONArray bubblesArray = (JSONArray)json.get("bubbles");
                JSONObject bubbles = (JSONObject)bubblesArray.get(0);
                JSONObject data = (JSONObject)bubbles.get("data");
                String description = "";
                description = (String)data.get("description");
                chatMessage = description;
            } catch(Exception e){
                System.out.println("error");
                e.printStackTrace();
            }

            in.close();

        } else {                // error
            chatMessage = conn.getResponseMessage();
        }
        return chatMessage;
    }

    // makeSigniture : 보낼 메세지를 네이버 챗봇에 포맷으로 변경해주는 메서드
    public static String makeSignature(String message, String secretkey) {

        String encodeBase64String = "";

        try {
            byte[] secret_key_bytes = secretkey.getBytes();

            SecretKeySpec signingkey = new SecretKeySpec(secret_key_bytes, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingkey);

            byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
            encodeBase64String = Base64.encodeBase64String(rawHmac);

            return encodeBase64String;

        } catch (Exception e) {
            System.out.println(e);
        }
        return encodeBase64String;
    }

    public static String getReqMessage(String voiceMessage) {

        String requestBody = "";

        try {
            JSONObject obj = new JSONObject();

            long timestamp = new Date().getTime();

            System.out.println("##" + timestamp);

            obj.put("version","v2");
            obj.put("userId", "U47b00b58c90f8e47428af8b7bddc1231heo2");
            obj.put("timestamp", timestamp);

            JSONObject bubbles_obj = new JSONObject();              // 원래 bubbles_obj 로 선언하려했으나, 버전 변경으로 content로 선언. 안되면 롤백 예정

            bubbles_obj.put("type","text");

            JSONObject data_obj = new JSONObject();
            data_obj.put("description", voiceMessage);                  // 원래 description 이었으나 버전 변경으로 details로 변경됨. 안되면 롤백예정

            bubbles_obj.put("type","text");
            bubbles_obj.put("data", data_obj);

            JSONArray bubbles_array = new JSONArray();
            bubbles_array.add(bubbles_obj);

            obj.put("bubbles", bubbles_array);
            obj.put("event","send");

            requestBody = obj.toString();

        } catch(Exception e) {
            System.out.println("### Exception : " + e);
        }
        log.info(">> requestbody > {}", requestBody);
        return requestBody;
    }

    @GetMapping("/chatting")
    public String enterchat() {
        return "/chat/chatting";
    }

}
