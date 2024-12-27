package com.project.joonggo.controller;

import com.project.joonggo.domain.ChatMessage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@PropertySource("classpath:chatbot.properties")
@Slf4j
@Controller
@RequiredArgsConstructor
//@RequestMapping("/chat/*")          // 안쓰면 template/chat 폴더 내 파일 불러오기 요청 안됨
public class ChatBotController {

    RestTemplate restTemplate = new RestTemplate();

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.model}")
    private String model; // 예: "gpt-3.5-turbo"

    // 준비된 응답을 저장할 Map
    private final Map<String, String> preparedResponses = new HashMap<>();

    @PostConstruct
    public void init() {
        // 초기화 시점에 미리 준비된 응답들을 설정
        preparedResponses.put("안녕", "안녕하세요! 무엇을 도와드릴까요?");
        preparedResponses.put("환불", "환불 정책은 다음과 같습니다:\n1. 제품 수령 후 7일 이내\n2. 미사용 제품에 한해\n3. 원래 구매처에서만 가능");
        preparedResponses.put("배송", "배송 관련 안내입니다:\n- 배송비: 3,000원 (3만원 이상 구매 시 무료)\n- 배송기간: 2-3일 소요");
        preparedResponses.put("위치", "서울특별시 강남구 테헤란로 123 OO빌딩 4층");
        preparedResponses.put("결제", "신용카드, 무통장입금, 휴대폰 결제를 지원합니다.");
        // 필요한 키워드와 응답을 추가할 수 있습니다
    }

    @MessageMapping("/chatbot")
    @SendTo("/topic/chatbot")
    public ChatMessage handleChatbotMessage(ChatMessage message) {
        log.info("Received Message: {}", message.getMessage());

        String preparedResponse = findPreparedResponse(message.getMessage());

        if (preparedResponse != null) {
            log.info("Sending prepared response for keyword");
            return new ChatMessage(
                    message.getMessage(),
                    message.getTimestamp(),
                    preparedResponse,
                    "bot"
            );
        }

        try {
            // API 키와 모델 로그
            log.info("Using API Key (first 5 chars): {}", apiKey.substring(0, 5));
            log.info("Using Model: {}", model);

            // OpenAI API 요청 구성
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", model);

            JSONArray messages = new JSONArray();
            JSONObject messageObj = new JSONObject();
            messageObj.put("role", "user");
            messageObj.put("content", message.getMessage());
            messages.put(messageObj);

            requestBody.put("messages", messages);

            // 요청 바디 로그
            log.info("Request Body: {}", requestBody.toString());

            // API 호출을 위한 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            // 헤더 로그
            log.info("Request Headers: {}", headers);

            // HTTP 요청 생성
            HttpEntity<String> requestEntity = new HttpEntity<>(
                    requestBody.toString(),
                    headers
            );

            // OpenAI API 엔드포인트
            String apiUrl = "https://api.openai.com/v1/chat/completions";

            log.info("Calling OpenAI API at: {}", apiUrl);

            // API 호출
            ResponseEntity<String> response = restTemplate.postForEntity(
                    apiUrl,
                    requestEntity,
                    String.class
            );

            // 응답 로그
            log.info("Response Status: {}", response.getStatusCode());
            log.info("Response Body: {}", response.getBody());

            // 응답 처리
            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject jsonResponse = new JSONObject(response.getBody());
                String botResponse = jsonResponse
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");

                return new ChatMessage(
                        message.getMessage(),
                        message.getTimestamp(),
                        botResponse.trim(),
                        "bot"
                );
            }
        } catch (HttpClientErrorException.TooManyRequests e) {
            log.error("Rate limit exceeded. Response: {}", e.getResponseBodyAsString());
            return new ChatMessage(
                    message.getMessage(),
                    message.getTimestamp(),
                    "현재 요청이 많아 잠시 후 다시 시도해주세요.",
                    "bot"
            );
        } catch (Exception e) {
            log.error("OpenAI API 호출 중 오류 발생:", e);
            log.error("Error class: {}", e.getClass().getName());
            log.error("Error message: {}", e.getMessage());
            return new ChatMessage(
                    message.getMessage(),
                    message.getTimestamp(),
                    "죄송합니다. 응답을 처리하는 중에 오류가 발생했습니다.",
                    "bot"
            );
        }

        return new ChatMessage(
                message.getMessage(),
                message.getTimestamp(),
                "응답을 받지 못했습니다. 잠시 후 다시 시도해주세요.",
                "bot"
        );
    }

    private String findPreparedResponse(String message) {
        // 대소문자 구분 없이 검색하기 위해 소문자로 변환
        String lowerMessage = message.toLowerCase();

        // 모든 준비된 키워드에 대해 확인
        return preparedResponses.entrySet().stream()
                .filter(entry -> lowerMessage.contains(entry.getKey().toLowerCase()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }
}
