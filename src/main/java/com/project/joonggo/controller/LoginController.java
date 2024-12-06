package com.project.joonggo.controller;

import com.project.joonggo.domain.UserVO;
import com.project.joonggo.handler.SocialLoginHandler;
import com.project.joonggo.service.LoginService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Slf4j
@RequestMapping("/login/*")
@RequiredArgsConstructor
@Controller
public class LoginController {
    private final LoginService loginService;
    private final SocialLoginHandler socialLoginHandler;
    private static final int SIGN_FLAG_DEFAULT = 0;
    private static final int SIGN_FLAG_KAKAO = 1;
    private static final int SIGN_FLAG_NAVER = 2;
    private static final int SIGN_FLAG_GOOGLE = 3;

    //회원가입 페이지 이동
    @GetMapping("/join")
    public String join(Model model){
        // 기본 유저의 소셜가입구분을 0으로 (0:기본값, 1:카카오, 2:네이버, 3:구글)
        model.addAttribute("signflag", SIGN_FLAG_DEFAULT);
        return "/user/join";
    }

    //회원가입 요청
    @PostMapping("/join")
    public String join(UserVO userVO){
        //userVO.setPwd(passwordEncoder.encode(userVO.getPwd()));
        int isOk = loginService.insert(userVO);
        return "/user/login";
    }

    //로그인 페이지 이동
    @GetMapping("/page")
    public String login(Model model) {
        String kakaoLoginPageUrl = socialLoginHandler.getKakaoLoginPageUrl();
        String naverLoginPageUrl = socialLoginHandler.getNaverLoginPageUrl();
        String googleLoginPageUrl = socialLoginHandler.getGoogleLoginPageUrl();

        model.addAttribute("kakaoLoginPageUrl", kakaoLoginPageUrl);
        model.addAttribute("naverLoginPageUrl", naverLoginPageUrl);
        model.addAttribute("googleLoginPageUrl", googleLoginPageUrl);

        return "/user/login";
    }

    //로그인 요청
    @PostMapping("/enter")
    public String login(UserVO userVO, HttpSession session, Model model) {
        // DB에서 user_id로 사용자 조회
        UserVO loginUser = loginService.findUserByIdAndSignFlag(userVO.getUserId(), SIGN_FLAG_DEFAULT);

        if (loginUser == null) {
            // 사용자 ID가 없는 경우
            model.addAttribute("error", "존재하지 않는 사용자입니다.");
            return "/user/login"; // 다시 로그인 페이지로
        }

        if (!userVO.getPassword().equals(loginUser.getPassword())) {
            // 비밀번호가 틀린 경우
            model.addAttribute("error", "비밀번호가 틀렸습니다.");
            return "/user/login"; 
        }

        // 로그인 성공: 세션에 사용자 정보 저장
        session.setAttribute("loginUser", loginUser);
        log.info("로그인 성공: {}", loginUser);

        // 메인 페이지로 리다이렉트
        return "redirect:/";
    }

    //////////////////////////
    /* 구글 소셜 로그인 line */
    ////////////////////////
    @GetMapping("/google/callback")
    public String googleCallback(@RequestParam("code") String authorizeCode, HttpSession session, Model model){
        log.info("===================구글 인가코드========={}", authorizeCode);

        String accessToken = socialLoginHandler.getGoogleAccessToken(authorizeCode);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 구글 액세스 토큰 >>>>>>>>>>>>>>>>>> {}", accessToken);

        String googleUserInfo = socialLoginHandler.getGoogleUserInfo(accessToken);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 구글유저 정보 >>>>>{}", googleUserInfo);

        Map<String, String> userInfo = socialLoginHandler.parseGoogleUserInfo(googleUserInfo);
        String googleId = userInfo.get("userId");
        String googleName = userInfo.get("userName");
        String givenName = userInfo.get("givenName");
        String familyName = userInfo.get("familyName");
        String picture = userInfo.get("picture");
        String email = userInfo.get("email");
        String emailVerified = userInfo.get("emailVerified");

        UserVO loginUser = loginService.findUserByIdAndSignFlag(googleId, SIGN_FLAG_GOOGLE);

        System.out.println("----------------------------"+googleId);
        System.out.println("----------------------------"+SIGN_FLAG_GOOGLE);
        System.out.println("----------------------------"+loginUser);

        if (loginUser != null) {
            // 회원이 존재하면 로그인 처리
            session.setAttribute("loginUser", loginUser);
            log.info("소셜 로그인 성공: {}", loginUser);

        } else {
            // 회원이 없으면 회원가입 처리
            model.addAttribute("socialEmail", email);
            model.addAttribute("signflag", SIGN_FLAG_GOOGLE);
            model.addAttribute("socialUserId", googleId);
            return "/user/join";
        }

        return "redirect:/";
    }

    //////////////////////////
    /* 네이버 소셜 로그인 line */
    /////////////////////////
    @GetMapping("/naver/callback")
    public String naverCallback(@RequestParam("code") String authorizeCode, @RequestParam("state") String authorizeState, HttpSession session, Model model){
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>> 네이버 인가코드 >>>>> {}", authorizeCode);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>> 네이버 state >>>>>> {}", authorizeState);

        String accessToken = socialLoginHandler.getNaverAccessToken(authorizeCode, authorizeState);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 네이버 액세스 토큰 >>>>>>>>>>>>>>>>>> {}", accessToken);

        String naverUserInfo = socialLoginHandler.getNaverUserInfo(accessToken);
        Map<String, String> parseNaverUserInfoResult =  socialLoginHandler.parseNaverUserInfo(naverUserInfo);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 네이버 유저 정보 >>>>>{}", parseNaverUserInfoResult);

        String naverId = parseNaverUserInfoResult.get("userId");
        String nickName = parseNaverUserInfoResult.get("nickname");
        String email = parseNaverUserInfoResult.get("email");
        String birthYear = parseNaverUserInfoResult.get("birthYear");
        String birthMonth = parseNaverUserInfoResult.get("birthMonth");
        String birthDay = parseNaverUserInfoResult.get("birthDay");

        UserVO loginUser = loginService.findUserByIdAndSignFlag(naverId, SIGN_FLAG_NAVER);

        if (loginUser != null) {
            // 회원이 존재하면 로그인 처리
            session.setAttribute("loginUser", loginUser);
            log.info("소셜 로그인 성공: {}", loginUser);

        } else {
            // 회원이 없으면 회원가입 처리
            model.addAttribute("socialEmail", email);
            model.addAttribute("signflag", SIGN_FLAG_NAVER);
            model.addAttribute("socialUserId", naverId);
            model.addAttribute("birthYear", birthYear);
            model.addAttribute("birthMonth", birthMonth);
            model.addAttribute("birthDay", birthDay);
            return "/user/join";
        }

        return "redirect:/";
    }

    //////////////////////////
    /* 카카오 소셜 로그인 line */
    /////////////////////////
    @GetMapping("/kakao/callback")
    public String kakaoCallback(@RequestParam("code") String authorizeCode, HttpSession session, Model model) {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 카카오 인가코드 >>>>>>>>>>>>>>>>>> {}", authorizeCode);

        // 인가 코드로 카카오 토큰 요청
        String accessToken = socialLoginHandler.getKakaoAccessToken(authorizeCode);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 카카오 액세스 토큰 >>>>>>>>>>>>>>>>>> {}", accessToken);

        // 이후 토큰을 통해 필요한 데이터를 처리
        // 토큰 유효성 검증
        boolean isTokenValid = socialLoginHandler.validateKakaoAccessToken(accessToken);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 카카오 토큰 유효성 >>>>>>>>>>>>>>>>>> {}", isTokenValid);

        String kakaoUserInfo = socialLoginHandler.getKakaoUserInfo(accessToken);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 카카오 유저 정보 >>>>>{}", kakaoUserInfo);

        Map<String, String> userInfo = socialLoginHandler.parseKakaoUserInfo(kakaoUserInfo);
        log.info("parsekakaoUserInfo>>>>>>>>>>{}", userInfo);

        String kakaoId = userInfo.get("userId");
        String kakaoEmail = userInfo.get("email");

        // 이메일로 회원이 존재하는지 확인
        // DB에서 user_id로 사용자 조회
        UserVO loginUser = loginService.findUserByIdAndSignFlag(kakaoId, SIGN_FLAG_KAKAO);

        if (loginUser != null) {
            // 회원이 존재하면 로그인 처리
            session.setAttribute("loginUser", loginUser);
            log.info("소셜 로그인 성공: {}", loginUser);

        } else {
            // 회원이 없으면 회원가입 처리
            model.addAttribute("socialEmail", kakaoEmail);
            model.addAttribute("signflag", SIGN_FLAG_KAKAO);
            model.addAttribute("socialUserId", kakaoId);
            return "/user/join";
        }

        return "redirect:/";
    }

}
