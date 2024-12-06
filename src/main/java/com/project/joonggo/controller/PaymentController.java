package com.project.joonggo.controller;


import com.project.joonggo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:8089")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/payment")
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/success")
    public ResponseEntity<Map<String, String>> handlePaymentSuccess(@RequestBody Map<String, Object> paymentInfo) {
        String merchantUid = (String) paymentInfo.get("merchantUid");
        int amount = (Integer) paymentInfo.get("amount");
        Long boardId = Long.parseLong(paymentInfo.get("boardId").toString());
        String productName = (String) paymentInfo.get("productName");

        boolean isSaved = paymentService.savePaymentInfo(merchantUid, amount, boardId, productName);

        Map<String, String> response = new HashMap<>();
        if (isSaved) {
            log.info("결제 정보 저장 완료 - Merchant UID: {}, Amount: {}, Board ID: {}, Product Name: {}", merchantUid, amount, boardId, productName);
            response.put("message", "결제 정보 저장 완료");
            response.put("status", "success");
            return ResponseEntity.ok(response);  // 200 OK 응답과 함께 JSON 반환
        } else {
            response.put("message", "결제 정보 저장 실패");
            response.put("status", "failure");
            return ResponseEntity.status(500).body(response);  // 500 Internal Server Error 응답과 함께 JSON 반환
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyPayment(@RequestBody Map<String, Object> paymentVerificationInfo) {
        String impUid = (String) paymentVerificationInfo.get("imp_uid");
        String merchantUid = (String) paymentVerificationInfo.get("merchant_uid");
        int paidAmount = (Integer) paymentVerificationInfo.get("amount");

        // 아임포트 API에서 결제 검증을 위한 토큰 발급
        String accessToken = getAccessToken();

        log.info("accessToken>> {}", accessToken);

        // 결제 내역 조회
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken);  // API 토큰

        String url = "https://api.iamport.kr/payments/" + impUid;
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 아임포트 결제 내역 조회
        ResponseEntity<Map> paymentResponse = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (paymentResponse.getStatusCodeValue() == 200) {
            Map<String, Object> paymentData = paymentResponse.getBody();
            if (paymentData != null && paymentData.containsKey("response")) {
                Map<String, Object> response = (Map<String, Object>) paymentData.get("response");
                int actualPaidAmount = (Integer) response.get("amount");

                // 결제 금액 검증
                if (actualPaidAmount == paidAmount) {
                    log.info("결제 검증 성공 - impUid: {}, merchantUid: {}", impUid, merchantUid);
                    Map<String, String> verificationResponse = new HashMap<>();
                    verificationResponse.put("message", "결제 검증 성공");
                    verificationResponse.put("status", "success");
                    return ResponseEntity.ok(verificationResponse);
                } else {
                    log.error("결제 금액 불일치 - 예상 금액: {}, 실제 금액: {}", paidAmount, actualPaidAmount);
                    Map<String, String> verificationResponse = new HashMap<>();
                    verificationResponse.put("message", "결제 금액 불일치");
                    verificationResponse.put("status", "failure");
                    return ResponseEntity.status(400).body(verificationResponse); // 400 Bad Request
                }
            } else {
                log.error("결제 정보 조회 실패 - impUid: {}", impUid);
                Map<String, String> verificationResponse = new HashMap<>();
                verificationResponse.put("message", "결제 정보 조회 실패");
                verificationResponse.put("status", "failure");
                return ResponseEntity.status(400).body(verificationResponse);
            }
        } else {
            log.error("결제 내역 조회 실패 - impUid: {}", impUid);
            Map<String, String> verificationResponse = new HashMap<>();
            verificationResponse.put("message", "결제 내역 조회 실패");
            verificationResponse.put("status", "failure");
            return ResponseEntity.status(400).body(verificationResponse);
        }
    }

    // 아임포트 API 토큰 발급
    private String getAccessToken() {
        String apiKey = "4726065027433512";
        String apiSecret = "QnFZhqiZglSppH5JZDbqJdvSpZ3aLXmhLrFx3AkVRLgmV6WQl5IaaEaCiNd1lO4edImhLEdChsHu0Jfh";

        // 토큰 발급을 위한 요청
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        Map<String, String> body = new HashMap<>();
        body.put("imp_key", apiKey);
        body.put("imp_secret", apiSecret);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange("https://api.iamport.kr/users/getToken", HttpMethod.POST, entity, Map.class);

        if (response.getStatusCodeValue() == 200) {
            Map<String, Object> responseBody = response.getBody();

            // 응답이 null이 아니고 "response" 키가 존재하는지 확인
            if (responseBody != null && responseBody.containsKey("response")) {
                Map<String, Object> responseObj = (Map<String, Object>) responseBody.get("response");
                return (String) responseObj.get("access_token");
            } else {
                throw new RuntimeException("API 응답에서 access_token을 찾을 수 없습니다.");
            }
        } else {
            throw new RuntimeException("API 토큰 발급 실패 - 응답 상태: " + response.getStatusCodeValue());
        }
    }

}
