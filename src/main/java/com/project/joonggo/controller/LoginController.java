package com.project.joonggo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.joonggo.domain.UserVO;
import com.project.joonggo.service.LoginService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequestMapping("/login/*")
@RequiredArgsConstructor
@Controller
public class LoginController {
    private final LoginService loginService;

    private static final String KAKAO_REST_API_KEY = "ea9c0d226405dacbf737edffbc9299a5";
    private static final String KAKAO_AUTHORIZE_URL = "https://kauth.kakao.com/oauth/authorize";
    private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String KAKAO_REDIRECT_URI = "http://localhost:8089/login/kakao/callback";
    private static final String KAKAO_ACCESS_TOKEN_INFO_URL = "https://kapi.kakao.com/v1/user/access_token_info";
    private static final String KAKAO_USER_INFO = "https://kapi.kakao.com/v2/user/me";

    @GetMapping("/join")
    public String join(){
        return "/user/join";
    }

    @PostMapping("/join")
    public String join(UserVO userVO){
        //userVO.setPwd(passwordEncoder.encode(userVO.getPwd()));
        int isOk = loginService.insert(userVO);
        return "/user/login";
    }

    @GetMapping("/page")
    public String login(Model model) {
        String kakaoLoginPageUrl = KAKAO_AUTHORIZE_URL + "?response_type=code&client_id=" + KAKAO_REST_API_KEY
                + "&redirect_uri=" + KAKAO_REDIRECT_URI;

        model.addAttribute("kakaoLoginPageUrl", kakaoLoginPageUrl);
        return "/user/login";
    }

    @PostMapping("/enter")
    public String login(UserVO userVO, HttpSession session, Model model) {
        // DB에서 user_id로 사용자 조회
        UserVO loginUser = loginService.findUserById(userVO.getUserId());

        if (loginUser == null) {
            // 사용자 ID가 없는 경우
            model.addAttribute("error", "존재하지 않는 사용자입니다.");
            return "/user/login"; // 다시 로그인 페이지로
        }

        if (!userVO.getPassword().equals(loginUser.getPassword())) {
            model.addAttribute("error", "비밀번호가 틀렸습니다.");
            return "/user/login"; // 다시 로그인 페이지로
        }

        // 로그인 성공: 세션에 사용자 정보 저장
        session.setAttribute("loginUser", loginUser);
        log.info("로그인 성공: {}", loginUser);

        // 메인 페이지로 리다이렉트
        return "redirect:/";
    }

    //////////////////////////
    /* 카카오 소셜 로그인 line */
    /////////////////////////
    @GetMapping("/kakao/callback")
    public String callback(@RequestParam("code") String authorizeCode, HttpSession session, Model model) {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 인가코드 >>>>>>>>>>>>>>>>>> {}", authorizeCode);

        // 인가 코드로 카카오 토큰 요청
        String accessToken = getKakaoAccessToken(authorizeCode);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 액세스 토큰 >>>>>>>>>>>>>>>>>> {}", accessToken);

        // 이후 토큰을 통해 필요한 데이터를 처리
        // 토큰 유효성 검증
        boolean isTokenValid = validateKakaoAccessToken(accessToken);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 토큰 유효성 >>>>>>>>>>>>>>>>>> {}", isTokenValid);

        String kakaoUserInfo = getKakaoUserInfo(accessToken);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 카카오 유저 정보 >>>>>{}", kakaoUserInfo);

        String kakaoEmail = parseKakaoUserInfo(kakaoUserInfo);

        // 이메일로 회원이 존재하는지 확인
        // DB에서 user_id로 사용자 조회
        UserVO loginUser = loginService.findUserById(kakaoEmail);

        if (loginUser != null) {
            // 회원이 존재하면 로그인 처리
            session.setAttribute("loginUser", loginUser);
            log.info("소셜 로그인 성공: {}", loginUser);

        } else {
            // 회원이 없으면 회원가입 처리
            model.addAttribute("kakaoEmail", kakaoEmail); // 이메일을 모델에 담아 회원가입 페이지로 전달
            return "/user/join";
        }

        return "redirect:/";
    }

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
            accessToken = extractKakaoAccessToken(responseBody);
        }

        return accessToken;
    }

    // 카카오액세스 토큰 추출
    public String extractKakaoAccessToken(String responseBody){
        log.info("===========================카카오액세스 토큰 추출 리스폰스바디값 {}", responseBody);
        int start = responseBody.indexOf("\"access_token\":\"") + "\"access_token\":\"".length();
        int end = responseBody.indexOf("\"", start);
        return responseBody.substring(start, end);
    }

    // 카카오 액세스토큰의 유효성 검증 메서드
    private boolean validateKakaoAccessToken(String accessToken){
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

    public String parseKakaoUserInfo(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);

            // 사용자 정보 추출
            Long userId = rootNode.path("id").asLong();
            String nickname = rootNode.path("properties").path("nickname").asText();
            String email = rootNode.path("kakao_account").path("email").asText();

            // 추출한 정보 출력
            System.out.println("User ID: " + userId);
            System.out.println("Nickname: " + nickname);
            System.out.println("Email: " + email);

            return email;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
