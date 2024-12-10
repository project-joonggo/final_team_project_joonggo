package com.project.joonggo.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class SocialLoginHandler {
    ////////////////////////// 카카오 /////////////////////////////
//    private static final String KAKAO_REST_API_KEY = "ea9c0d226405dacbf737edffbc9299a5";
    private static final String KAKAO_AUTHORIZE_URL = "https://kauth.kakao.com/oauth/authorize";
    private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String KAKAO_REDIRECT_URI = "http://localhost:8089/login/kakao/callback";
    private static final String KAKAO_ACCESS_TOKEN_INFO_URL = "https://kapi.kakao.com/v1/user/access_token_info";
    private static final String KAKAO_USER_INFO = "https://kapi.kakao.com/v2/user/me";

    ////////////////////////// 네이버 /////////////////////////////
//    private static final String NAVER_CLIENT_KEY = "YGxnDI1KuQjMr7eOD1EF";
    private static final String NAVER_AUTHORIZE_URL = "https://nid.naver.com/oauth2.0/authorize";
    private static final String NAVER_SECRET_KEY = "IrEnr4vfYK";
    private static final String NAVER_TOKEN_URL = "https://nid.naver.com/oauth2.0/token";
    private static final String NAVER_REDIRECT_URI = "http://localhost:8089/login/naver/callback";
    private static final String NAVER_USER_INFO = "https://openapi.naver.com/v1/nid/me";

    ///////////////////////// 구글 //////////////////////////////////
//    private static final String GOOGLE_CLIENT_KEY = "557670976871-cp3es3jgh5j5675hn083328ng83ugqti.apps.googleusercontent.com";
    private static final String GOOGLE_SECRET_KEY = "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";
    private static final String GOOGLE_REDIRECT_URI = "http://localhost:8089/login/google/callback";
    private static final String GOOGLE_AUTHORIZE_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";

    // 소셜 로그인 URL 생성
    public String getKakaoLoginPageUrl() {
        return KAKAO_AUTHORIZE_URL + "?response_type=code&client_id=" + KAKAO_REST_API_KEY
                + "&redirect_uri=" + KAKAO_REDIRECT_URI;
    }
    public String getNaverLoginPageUrl() {
        String uuid = UUID.randomUUID().toString();
        return NAVER_AUTHORIZE_URL + "?response_type=code&client_id=" + NAVER_CLIENT_KEY
                + "&state=" + uuid + "&redirect_uri=" + NAVER_REDIRECT_URI;
    }
    public String getGoogleLoginPageUrl() {
        return GOOGLE_AUTHORIZE_URL + "?scope=openid email profile" + "&access_type=offline" + "&include_granted_scopes=true"
                + "&response_type=code" + "&state=state_parameter" + "&redirect_uri=" + GOOGLE_REDIRECT_URI + "&client_id=" + GOOGLE_CLIENT_KEY;
    }

    //---------------------------- 액세스 토큰 value 추출------------------------------//
    public String extractAccessToken(String responseBody, String type){
        if (type.equals("google")){
            try {
                // JSON 파싱
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                return jsonNode.get("access_token").asText();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to parse access token");
            }
        }
        log.info("===========================액세스 토큰 추출 리스폰스바디값 {}", responseBody);
        int start = responseBody.indexOf("\"access_token\":\"") + "\"access_token\":\"".length();
        int end = responseBody.indexOf("\"", start);
        return responseBody.substring(start, end);
    }

    //----------------------------google social login method line------------------------------//
    public String getGoogleAccessToken(String authorizeCode){
        // MultiValueMap을 사용하여 요청 파라미터 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", authorizeCode);
        params.add("client_id", GOOGLE_CLIENT_KEY);
        params.add("client_secret", GOOGLE_SECRET_KEY);
        params.add("redirect_uri", GOOGLE_REDIRECT_URI);
        params.add("grant_type", "authorization_code");

        // RestTemplate 설정
        RestTemplate restTemplate = new RestTemplate();

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // POST 요청 전송
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.exchange(GOOGLE_TOKEN_URL, HttpMethod.POST, entity, String.class);

        // 응답에서 액세스 토큰 추출
        String accessToken = null;
        if (response.getStatusCode() == HttpStatus.OK) {
            // JSON 응답에서 액세스 토큰을 추출
            String responseBody = response.getBody();
            accessToken = extractAccessToken(responseBody, "google");
        }

        return accessToken;
    }

    public String getGoogleUserInfo(String accessToken){
        String userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";

        // 헤더에 액세스 토큰 추가
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // API 요청
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, String.class);

        // 사용자 정보 JSON 반환
        return response.getBody();
    }

    public Map<String, String> parseGoogleUserInfo(String googleUserInfo){
        Map<String, String> userInfo = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(googleUserInfo);

            String userId = jsonNode.get("sub").asText();
            String userName = jsonNode.get("name").asText();
            String givenName = jsonNode.get("given_name").asText();
            String familyName = jsonNode.get("family_name").asText();
            String picture = jsonNode.get("picture").asText();
            String email = jsonNode.get("email").asText();
            String emailVerified = jsonNode.get("email_verified").asText();

            userInfo.put("userId", userId);
            userInfo.put("userName", userName);
            userInfo.put("givenName", givenName);
            userInfo.put("familyName", familyName);
            userInfo.put("picture", picture);
            userInfo.put("email", email);
            userInfo.put("emailVerified", emailVerified);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return userInfo;
    }

    //----------------------------naver social login method line------------------------------//
    public String getNaverAccessToken(String authorizeCode, String authorizeState){
        // MultiValueMap을 사용하여 요청 파라미터 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", NAVER_CLIENT_KEY);
        params.add("client_secret", NAVER_SECRET_KEY);
        params.add("code", authorizeCode);
        params.add("state", authorizeState);

        // RestTemplate 설정
        RestTemplate restTemplate = new RestTemplate();

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // POST 요청 전송
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.exchange(NAVER_TOKEN_URL, HttpMethod.POST, entity, String.class);

        // 응답에서 액세스 토큰 추출
        String accessToken = null;
        if (response.getStatusCode() == HttpStatus.OK) {
            // JSON 응답에서 액세스 토큰을 추출
            String responseBody = response.getBody();
            accessToken = extractAccessToken(responseBody, "naver");
        }

        return accessToken;
    }

    public String getNaverUserInfo(String accessToken) {
        // RestTemplate 설정
        RestTemplate restTemplate = new RestTemplate();

        // HTTP 헤더 설정 (Authorization 헤더에 액세스 토큰 추가)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        // GET 요청 전송
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(NAVER_USER_INFO, HttpMethod.GET, entity, String.class);

        // 응답에서 사용자 정보 추출
        if (response.getStatusCode() == HttpStatus.OK) {
            String responseBody = response.getBody();
            // JSON 응답을 파싱하여 사용자 정보 추출
            return responseBody;
        } else {
            // 오류 발생 시
            return null;
        }
    }

    public Map<String, String> parseNaverUserInfo(String responseBody) {
        Map<String, String> userInfo = new HashMap<>();
        try {
            // JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode responseNode = rootNode.get("response");

            // 사용자 정보 추출
            String userId = responseNode.get("id").asText();
            String nickname = responseNode.get("name").asText();
            String email = responseNode.get("email").asText();
            String birthyear = responseNode.get("birthyear").asText();
            String[] dateParts = responseNode.get("birthday").asText().split("-");
            String birthMonth = dateParts[0];
            String birthDay = dateParts[1];

            userInfo.put("userId", userId);
            userInfo.put("nickname", nickname);
            userInfo.put("email", email);
            userInfo.put("birthYear", birthyear);
            userInfo.put("birthMonth", birthMonth);
            userInfo.put("birthDay", birthDay);

            System.out.println("userId: " + userId);
            System.out.println("nickname: " + nickname);
            System.out.println("email: " + email);
            System.out.println("birthYear: " + birthyear);
            System.out.println("birthMonth: " + birthMonth);
            System.out.println("birthDay: " + birthDay);

            return userInfo;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //----------------------------kakao social login method line------------------------------//
    public String getKakaoAccessToken(String authorizeCode) {
        // MultiValueMap을 사용하여 요청 파라미터 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", KAKAO_REST_API_KEY);
        params.add("redirect_uri", KAKAO_REDIRECT_URI);
        params.add("code", authorizeCode);

        // RestTemplate 설정
        RestTemplate restTemplate = new RestTemplate();

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // POST 요청 전송
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.exchange(KAKAO_TOKEN_URL, HttpMethod.POST, entity, String.class);

        // 응답에서 액세스 토큰 추출
        String accessToken = null;
        if (response.getStatusCode() == HttpStatus.OK) {
            // JSON 응답에서 액세스 토큰을 추출
            String responseBody = response.getBody();
            accessToken = extractAccessToken(responseBody, "kakao");
        }

        return accessToken;
    }

    // 카카오 액세스토큰의 유효성 검증 메서드
    public boolean validateKakaoAccessToken(String accessToken){
        // 카카오 액세스 토큰 유효성 검증 요청
        RestTemplate restTemplate = new RestTemplate();

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        // GET요청 전송
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(KAKAO_ACCESS_TOKEN_INFO_URL, HttpMethod.GET, entity, String.class);

        // 응답 처리
        if(response.getStatusCode() == HttpStatus.OK){
            // 유효한 토큰의 경우 JSON 응답에서 유효성 정보 확인 가능
            String responseBody = response.getBody();
            log.info("토큰 유효성 검증 응답: {}", responseBody);
            return true;
        } else {
            // 유효하지 않은 토큰의 경우
            log.error("토큰 유효성 검증 실패: {}", response.getStatusCode());
            return false;
        }

    }

    public String getKakaoUserInfo(String accessToken) {
        // RestTemplate 설정
        RestTemplate restTemplate = new RestTemplate();

        // HTTP 헤더 설정 (Authorization 헤더에 액세스 토큰 추가)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        // GET 요청 전송
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(KAKAO_USER_INFO, HttpMethod.GET, entity, String.class);

        // 응답에서 사용자 정보 추출
        if (response.getStatusCode() == HttpStatus.OK) {
            String responseBody = response.getBody();
            // JSON 응답을 파싱하여 사용자 정보 추출
            return responseBody;
        } else {
            // 오류 발생 시
            return null;
        }
    }

    public Map<String, String> parseKakaoUserInfo(String responseBody) {
        Map<String, String> userInfo = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);

            // 사용자 정보 추출
            Long userId = rootNode.path("id").asLong();
            String nickname = rootNode.path("properties").path("nickname").asText();
            String email = rootNode.path("kakao_account").path("email").asText();

            userInfo.put("userId", String.valueOf(userId));
            userInfo.put("nickname", nickname);
            userInfo.put("email", email);

            // 추출한 정보 출력
            System.out.println("User ID: " + userId);
            System.out.println("Nickname: " + nickname);
            System.out.println("Email: " + email);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userInfo;
    }

}
