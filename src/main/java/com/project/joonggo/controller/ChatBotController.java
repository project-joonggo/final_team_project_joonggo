package com.project.joonggo.controller;

import com.project.joonggo.domain.BoardVO;
import com.project.joonggo.domain.ChatMessage;
import com.project.joonggo.domain.UserVO;
import com.project.joonggo.service.BoardService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    private final SimpMessagingTemplate messagingTemplate;

    RestTemplate restTemplate = new RestTemplate();

    private final BoardService boardService;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.model}")
    private String model; // 예: "gpt-3.5-turbo"

    // 준비된 응답을 저장할 Map
    private final Map<String, String> preparedResponses = new HashMap<>();

    @PostConstruct
    public void init() {
        // 초기화 시점에 미리 준비된 응답들을 설정
        preparedResponses.put("회사위치", "인천 남동구 인주대로 593 엔타스빌딩 12층 입니다.");
        preparedResponses.put("안녕", "안녕하세요! 무엇을 도와드릴까요?");
        preparedResponses.put("환불", "환불 정책은 다음과 같습니다:\n1. 제품 수령 후 7일 이내\n2. 미사용 제품에 한해\n3. 원래 구매처에서만 가능");
//        preparedResponses.put("게시판", "게시판으로 이동하는 링크를 드리겠습니다.\n<a href='/board/list' class='chat-button'>게시판으로 이동하기</a>");
        // 필요한 키워드와 응답을 추가 가능
    }

    @MessageMapping("/chatbot")
    @SendTo("/topic/chatbot")
    public ChatMessage handleChatbotMessage(ChatMessage message) {

        log.info("Received Message: {}", message.getMessage());

        // 주소 관련 키워드 체크
        if (message.getMessage().contains("오늘 날씨")
//                || message.getMessage().contains("주변")
//                || message.getMessage().contains("근처")
        ) {
            if (message.getUserAddress() != null && !message.getUserAddress().isEmpty()) {

                // 데이터 잘라넣기 : 인천 미추홀구 ..... => 인천 미추홀구
                String userAddress = message.getUserAddress();
                String[] addr = userAddress.split(" ");    // [인천], [미추홀구], [...]
                String searchAddress = addr[0] + " " + addr[1];   // 인천 미추홀구

                String response = String.format(
                        "회원님의 주소 '%s' 기준으로 검색하겠습니다.",
//                        message.getUserAddress()
                        searchAddress
                );

                // 웹소켓으로 중간 메시지 전송
                messagingTemplate.convertAndSend("/topic/chatbot",
                        new ChatMessage(
                                message.getMessage(),
                                message.getTimestamp(),
                                response,
                                "bot",
//                                message.getUserAddress()
                                searchAddress
                        )
                );

                message.setMessage( searchAddress + " " +  message.getMessage() );
            } else {
                return new ChatMessage(
                        message.getMessage(),
                        message.getTimestamp(),
                        "로그인 후 이용 가능한 서비스입니다.",
                        "bot",
                        null
                );
            }
        }

        if(message.getMessage().contains("내 주소") || message.getMessage().contains("내 위치")) {
            if (message.getUserAddress() != null && !message.getUserAddress().isEmpty()) {

                String userAddress = message.getUserAddress();

                return new ChatMessage(
                        message.getMessage(),
                        message.getTimestamp(),
                        "회원님의 주소는 '" + userAddress + "' 입니다.",
                        "bot",
                        userAddress
                );
            } else {
                return new ChatMessage(
                        message.getMessage(),
                        message.getTimestamp(),
                        "로그인 후 이용 가능한 서비스입니다.",
                        "bot",
                        null
                );
            }
        }

        if (message.getMessage().contains("관련") && message.getMessage().contains("게시판")) {
            // 검색어 추출 (예: "고양이에 대한 상품 게시판을 보여줘" -> "고양이")
            String keyword = extractKeyword(message.getMessage());

            if (keyword != null) {
                String response = String.format(
                        "검색어 '%s'에 대한 게시판으로 이동하는 링크입니다.<br><br>" +
                                "<a href='/board/list?keyword=%s' class='chat-button'>게시판으로 이동하기</a>",
                        keyword, keyword
                );

                return new ChatMessage(
                        message.getMessage(),
                        message.getTimestamp(),
                        response,
                        "bot",
                        null
                );
            }
        }

            if(message.getMessage().contains("인기상품")) {
                BoardVO popularProduct = boardService.getPopularProduct();
                String category = popularProduct.getCategory();
                String boardName = popularProduct.getBoardName();
                long boardId = popularProduct.getBoardId();

                String response = String.format(
                        "오늘의 인기상품은 %s 항목의 '%s' 게시판 입니다.<br>"
                        + "해당 상품으로 이동하시겠습니까?<br>"
                        + "<a href='/board/detail?boardId=%d' class='chat-button'>이동</a>",
                        category, boardName, boardId
                );
                return new ChatMessage(
                        message.getMessage(),
                        message.getTimestamp(),
                        response,
                        "bot",
                        null
                );
            }

        String preparedResponse = findPreparedResponse(message.getMessage());
        if (preparedResponse != null) {
            log.info("Sending prepared response for keyword");
            return new ChatMessage(
                    message.getMessage(),
                    message.getTimestamp(),
                    preparedResponse,
                    "bot",
                    message.getUserAddress()
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
                        "bot",
                        message.getUserAddress()
                );
            }
        } catch (HttpClientErrorException.TooManyRequests e) {
            log.error("Rate limit exceeded. Response: {}", e.getResponseBodyAsString());
            return new ChatMessage(
                    message.getMessage(),
                    message.getTimestamp(),
                    "현재 요청이 많아 잠시 후 다시 시도해주세요.",
                    "bot",
                    message.getUserAddress()
            );
        } catch (Exception e) {
            log.error("OpenAI API 호출 중 오류 발생:", e);
            log.error("Error class: {}", e.getClass().getName());
            log.error("Error message: {}", e.getMessage());
            return new ChatMessage(
                    message.getMessage(),
                    message.getTimestamp(),
                    "죄송합니다. 응답을 처리하는 중에 오류가 발생했습니다.",
                    "bot",
                    message.getUserAddress()
            );
        }

        return new ChatMessage(
                message.getMessage(),
                message.getTimestamp(),
                "응답을 받지 못했습니다. 잠시 후 다시 시도해주세요.",
                "bot",
                message.getUserAddress()
        );
    }

    private String extractKeyword(String message) {
        // "에 대한", "관련", "상품" 등의 키워드 제거

        String[] extractWord = message.split(" ");
        String keyword = extractWord[0];

        // 공백 정리 후 첫 번째 단어 반환
        return keyword;
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
