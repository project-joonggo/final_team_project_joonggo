package com.project.joonggo.controller;


import com.project.joonggo.service.BoardService;
import com.project.joonggo.service.LoginService;
import com.project.joonggo.service.NotificationService;
import com.project.joonggo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:8089")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/payment")
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final BoardService boardService;
    private final NotificationService notificationService;

    @Value("${api.key}")
    private String apiKey;

    @Value("${api.secret}")
    private String apiSecret;

    @Autowired
    private RestTemplate restTemplate;



    @PostMapping("/success")
    public ResponseEntity<Map<String, String>> handlePaymentSuccess(@RequestBody Map<String, Object> paymentInfo, Principal principal) {
        String impUid = (String) paymentInfo.get("impUid");
        String merchantUid = (String) paymentInfo.get("merchantUid");
        int amount = (Integer) paymentInfo.get("amount");
        Long boardId = Long.parseLong(paymentInfo.get("boardId").toString());
        String productName = (String) paymentInfo.get("productName");


        Long userNum = Long.valueOf(principal.getName());

        log.info(">>> userNum >> {}", userNum);


        boolean isSaved = paymentService.savePaymentInfo(impUid, merchantUid, amount, boardId, productName, userNum);

        Map<String, String> response = new HashMap<>();
        if (isSaved) {
            boardService.updateTradeFlag(boardId); // 거래완료시 게시글 상태 업데이트
            log.info("결제 정보 저장 완료 - Merchant UID: {}, Amount: {}, Board ID: {}, Product Name: {} ,impUid : {} , userNum : {}" ,merchantUid, amount, boardId, productName, impUid, userNum);

            // 판매자에게 알림 보내기
            Long sellerId = boardService.getSellerIdByBoardId(boardId);  // 상품의 판매자 ID를 가져옵니다.
            String notificationMessage = "귀하의 상품 '" + productName + "'이(가) 판매되었습니다!";
            notificationService.saveNotification(sellerId, notificationMessage, boardId);  // 판매자에게 알림 전송


            response.put("message", "결제 정보 저장 완료");
            response.put("status", "success");
            response.put("redirectUrl","/payment/history");
            return ResponseEntity.ok(response);  // 200 OK 응답과 함께 JSON 반환
        } else {
            response.put("message", "결제 정보 저장 실패");
            response.put("status", "failure");
            return ResponseEntity.status(500).body(response);  // 500 Internal Server Error 응답과 함께 JSON 반환
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<Map<String, String>> handleWebhook(@RequestBody Map<String, Object> webhookData) {
        log.info("Received webhook data: {}", webhookData);

        String impUid = (String) webhookData.get("imp_uid");
        String merchantUid = (String) webhookData.get("merchant_uid");
        String status = (String) webhookData.get("status");

        // 금액을 webhookData에서 직접 가져오지 않고, 아임포트 API에서 조회
        int paidAmount = getAmountFromImpUid(impUid);
        log.info(">>> paidAmount {}  >>>> " , paidAmount);


        log.info("impUid >>> {} , merUid >>> {} , status >>> {} " , impUid,merchantUid,status);

        if ("paid".equals(status)) {
            // 결제 완료 처리
            boolean isValid = verifyPayment(impUid, paidAmount,merchantUid);
            if (isValid) {
                // 결제 성공 처리
                return ResponseEntity.ok(Map.of("status", "success", "message", "결제 성공"));
            } else {
                return ResponseEntity.status(400).body(Map.of("status", "failure", "message", "결제 검증 실패"));
            }
        } else if ("cancelled".equals(status)) {
            // 결제 취소 처리
            cancelPayment(impUid,merchantUid, paidAmount);
            return ResponseEntity.ok(Map.of("status", "success", "message", "결제 취소 처리 완료"));
        } else {
            return ResponseEntity.status(400).body(Map.of("status", "failure", "message", "알 수 없는 상태"));
        }
    }

    // impUid로 결제 금액 조회
    private int getAmountFromImpUid(String impUid) {
        // 아임포트 결제 내역 조회
        String accessToken = getAccessToken();  // 토큰 발급 메소드
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken);

        String url = "https://api.iamport.kr/payments/" + impUid;
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 아임포트 결제 내역 조회
        ResponseEntity<Map> paymentResponse = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (paymentResponse.getStatusCodeValue() == 200) {
            Map<String, Object> paymentData = paymentResponse.getBody();
            if (paymentData != null && paymentData.containsKey("response")) {
                Map<String, Object> response = (Map<String, Object>) paymentData.get("response");
                return (Integer) response.get("amount");  // 실제 결제 금액
            }
        }
        return 0;  // 만약 조회 실패 시, 기본값으로 0 반환
    }


    // 아임포트에서 결제 정보를 확인하여 실제 금액과 일치하는지 검증
    private boolean verifyPayment(String impUid, int paidAmount, String merchantUid) {
        // 아임포트 API 토큰 발급
        String accessToken = getAccessToken();

        // 결제 내역 조회
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken);

        String url = "https://api.iamport.kr/payments/" + impUid;
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 아임포트 결제 내역 조회
        ResponseEntity<Map> paymentResponse = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (paymentResponse.getStatusCodeValue() == 200) {
            Map<String, Object> paymentData = paymentResponse.getBody();
            if (paymentData != null && paymentData.containsKey("response")) {
                Map<String, Object> response = (Map<String, Object>) paymentData.get("response");
                int actualPaidAmount = (Integer) response.get("amount");  // 실제 결제된 금액
                String merchantUidFromApi = (String) response.get("merchant_uid");  // API에서 받은 merchant_uid

                // 결제 금액과 주문 정보 확인
                return paidAmount == actualPaidAmount && merchantUid.equals(merchantUidFromApi);
            }
        }
        return false;
    }

    @PostMapping("/refund")
    public ResponseEntity<Map<String, String>> handleRefund(@RequestBody Map<String, Object> refundInfo) {
        String impUid = (String) refundInfo.get("impUid");
        String merchantUid = (String) refundInfo.get("merchantUid");
        int paidAmount = (Integer) refundInfo.get("paidAmount");

        log.info(">> refund >>> {} , {} , {} " ,impUid,merchantUid,paidAmount);

        try {
            // 환불 처리
            boolean isRefunded = cancelPayment(impUid, merchantUid, paidAmount);
            paymentService.updatePaymentStatus(impUid, paidAmount);
            if (isRefunded) {
                log.info("환불 처리 성공 - impUid: {}, merchantUid: {}", impUid, merchantUid);

                String currentStatus = getPaymentStatus(impUid);
                log.info("현재 결제 상태: {}", currentStatus);

                // 결제 상태가 'cancelled'일 경우에만 웹훅 호출
                if ("cancelled".equals(currentStatus)) {
                    Map<String, Object> webhookData = Map.of(
                            "imp_uid", impUid,
                            "merchant_uid", merchantUid,
                            "status", "cancelled"  // 결제 상태를 "cancelled"로 설정
                    );
                    ResponseEntity<Map<String, String>> webhookResponse = handleWebhook(webhookData);  // 웹훅 메서드 호출
                    if (webhookResponse.getStatusCode().is2xxSuccessful()) {
                        return ResponseEntity.ok(Map.of("status", "success", "message", "환불 처리 및 웹훅 호출 완료","redirectUrl", "/board/list")); // mypage만들면 거기로가자
                    } else {
                        return ResponseEntity.status(500).body(Map.of("status", "failure", "message", "웹훅 호출 실패"));
                    }
                } else {
                    // 결제 상태가 cancelled가 아닐 경우
                    return ResponseEntity.status(500).body(Map.of("status", "failure", "message", "환불 처리 후 상태가 'cancelled'가 아님"));
                }
            } else {
                return ResponseEntity.status(500).body(Map.of("status", "failure", "message", "환불 처리 실패"));
            }
        } catch (Exception e) {
            log.error("환불 처리 중 오류 발생", e);
            return ResponseEntity.status(500).body(Map.of("status", "failure", "message", "환불 처리 실패"));
        }
    }

    // 아임포트 결제 취소 API 호출
    private boolean cancelPayment(String impUid, String merchantUid, int paidAmount) {
        try {
            // 아임포트 API 토큰 발급
            String accessToken = getAccessToken();

            log.info(">>> accessToken > {}", accessToken);

            // 결제 취소 요청
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            log.info(" caninfo >>> {}, {} ,{} " , impUid,merchantUid,paidAmount);

            // 결제 취소 요청을 위한 바디 설정
            Map<String, Object> body = new HashMap<>();
            body.put("imp_uid", impUid);
            body.put("merchant_uid", merchantUid);
            body.put("amount", paidAmount);
            body.put("reason", "상품 불만족");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            log.info("Request headers: {}", headers);
            log.info("Request body: {}", body);

            log.info(">>> ent >>> {}", entity);

            try {
                Thread.sleep(2000);
               }
            catch (InterruptedException e)
               {
                Thread.currentThread().interrupt();
              }

            // 결제 취소 요청
            ResponseEntity<Map> cancelResponse = restTemplate.exchange(
                    "https://api.iamport.kr/payments/cancel", HttpMethod.POST, entity, Map.class
            );

            log.info(">>>> cancelResponse >>> {}", cancelResponse);

            if (cancelResponse.getStatusCodeValue() == 200) {
                // 환불 처리 완료
                Map<String, Object> responseBody = cancelResponse.getBody();
                log.info("responseBody >>> {],", responseBody);
                if (responseBody != null && responseBody.containsKey("response")) {
                    Map<String, Object> response = (Map<String, Object>) responseBody.get("response");
                    System.out.println("환불 처리 완료: " + response);
                    return true;
                }
            } else {
                // 환불 실패 처리 (응답 상태 코드가 200이 아니면)
                System.out.println("환불 처리 실패: " + cancelResponse.getBody());
            }
        } catch (Exception e) {
            // 예외 발생 시 처리
            e.printStackTrace();
        }
        return false;
    }
    // 아임포트 API 토큰 발급
    private String getAccessToken() {
        int retries = 5;
        while (retries > 0) {
            try {
                // 요청 본문 및 헤더 설정
                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type", "application/json");

                Map<String, String> body = Map.of(
                        "imp_key", apiKey,
                        "imp_secret", apiSecret
                );

                HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

                // 요청 보내기
                ResponseEntity<Map> response = restTemplate.exchange(
                        "https://api.iamport.kr/users/getToken", HttpMethod.POST, entity, Map.class
                );
                log.info(">>>> responce >> {}" , response);

                // 응답 처리
                if (response.getStatusCodeValue() == 200) {
                    Map<String, Object> responseBody = response.getBody();
                    if (responseBody != null && responseBody.containsKey("response")) {
                        Map<String, Object> responseObj = (Map<String, Object>) responseBody.get("response");
                        log.info(">>> 토큰발급됐냐? >>> {}" , responseObj);
                        return (String) responseObj.get("access_token");
                    }
                }
                log.info("토큰발급 왜 안댐?");
                throw new RuntimeException("API 토큰 발급 실패");

            } catch (Exception e) {
                retries--;
                if (retries == 0) {
                    throw new RuntimeException("토큰 발급 재시도 실패", e);
                }
                try {
                    Thread.sleep(2000);  // 2초 후 재시도
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return null;
    }


    // 아임포트 결제 상태 확인
    private String getPaymentStatus(String impUid) {
        String accessToken = getAccessToken();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken);

        String url = "https://api.iamport.kr/payments/" + impUid;
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 아임포트 결제 내역 조회
        ResponseEntity<Map> paymentResponse = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (paymentResponse.getStatusCodeValue() == 200) {
            Map<String, Object> paymentData = paymentResponse.getBody();
            if (paymentData != null && paymentData.containsKey("response")) {
                Map<String, Object> response = (Map<String, Object>) paymentData.get("response");
                return (String) response.get("status");  // 결제 상태 반환
            }
        }
        return "unknown";  // 상태를 확인할 수 없는 경우
    }

}


